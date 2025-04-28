package nnt_data.yanki_service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nnt_data.yanki_service.entity.Transaction;
import nnt_data.yanki_service.entity.TransactionYanki;
import nnt_data.yanki_service.entity.YankiTransactionRequest;
import nnt_data.yanki_service.infrastructure.persistence.mapper.TransactionYankiMapper;
import nnt_data.yanki_service.infrastructure.persistence.repository.TransactionYankiRepository;
import nnt_data.yanki_service.infrastructure.persistence.repository.UserYankiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionYankiService {
    private final TransactionYankiRepository transactionYankiRepository;
    private final UserYankiRepository userYankiRepository;
    private final KafkaService kafkaService;
    private final TransactionYankiMapper transactionYankiMapper;
    private final ReactiveRedisTemplate<String, TransactionYanki> redisTemplate;

    @Value("${redis.ttl.transactions:PT24H}")
    private Duration transactionCacheTtl;

    @Value("${kafka.topics.transaction-request:transaction-requests}")
    private String transactionRequestTopic;

    @Value("${kafka.topics.transaction-response:transaction-responses}")
    private String transactionResponseTopic;

    @Value("${kafka.timeout:30}")
    private long kafkaTimeout;

    private String generateTransactionCacheKey(String transactionId) {
        return "transaction:" + transactionId;
    }

    private Mono<TransactionYanki> cacheTransaction(TransactionYanki transaction) {
        return redisTemplate.opsForValue()
                .set(generateTransactionCacheKey(transaction.getId()),
                        transaction,
                        transactionCacheTtl)
                .thenReturn(transaction);
    }

    private Mono<TransactionYanki> getFromCache(String transactionId) {
        return redisTemplate.opsForValue()
                .get(generateTransactionCacheKey(transactionId));
    }

    private Mono<Void> validateTransaction(TransactionYanki transaction) {
        if (transaction.getTelefonoOrigen().equals(transaction.getTelefonoDestino())) {
            return Mono.error(new IllegalArgumentException(
                    "El teléfono origen y destino no pueden ser iguales"));
        }
        return Mono.empty();
    }

    private Mono<YankiTransactionRequest> prepareTransactionRequest(
            String originDebitCard,
            String destinyDebitCard,
            TransactionYanki transaction) {
        Transaction bankTransaction = new Transaction()
                .type(Transaction.TypeEnum.DEPOSIT)
                .amount(transaction.getMonto())
                .transactionMode(Transaction.TransactionModeEnum.INTER_ACCOUNT)
                .isByCreditCard(true);

        YankiTransactionRequest request = new YankiTransactionRequest()
                .debitCardIdOrigin(originDebitCard)
                .debitCardIdDestiny(destinyDebitCard)
                .transaction(bankTransaction);

        return Mono.just(request);
    }

    public Mono<TransactionYanki> createTransaction(Mono<TransactionYanki> transactionYankiMono) {
        return transactionYankiMono
                .flatMap(transaction -> validateTransaction(transaction)
                        .then(processTransaction(transaction)))
                .doOnSuccess(transaction ->
                        log.info("Transacción creada exitosamente: {}", transaction.getId()))
                .doOnError(error ->
                        log.error("Error al crear la transacción: {}", error.getMessage()));
    }

    private Mono<TransactionYanki> processTransaction(TransactionYanki transaction) {
        return userYankiRepository.findByTelefono(transaction.getTelefonoOrigen())
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Usuario origen no encontrado")))
                .flatMap(userOrigen -> {
                    if (transaction.getType() == TransactionYanki.TypeEnum.SPENT
                            && userOrigen.getTarjetaAsociada() == null) {
                        return Mono.error(new IllegalArgumentException(
                                "Se requiere una tarjeta asociada para realizar gastos"));
                    }

                    return validateDestinationUser(transaction, userOrigen);
                });
    }

    private Mono<TransactionYanki> validateDestinationUser(
            TransactionYanki transaction,
            nnt_data.yanki_service.infrastructure.persistence.entity.UserYankiEntity userOrigen) {
        return userYankiRepository.findByTelefono(transaction.getTelefonoDestino())
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Usuario destino no encontrado")))
                .flatMap(userDestino -> {
                    if (userDestino.getTarjetaAsociada() == null) {
                        return Mono.error(new IllegalArgumentException(
                                "Usuario destino requiere una tarjeta asociada"));
                    }

                    return prepareTransactionRequest(
                            userOrigen.getTarjetaAsociada(),
                            userDestino.getTarjetaAsociada(),
                            transaction)
                            .flatMap(request -> processKafkaTransaction(request, transaction));
                });
    }

    private Mono<TransactionYanki> processKafkaTransaction(
            YankiTransactionRequest request,
            TransactionYanki transaction) {
        return kafkaService.sendAndReceive(
                        transactionRequestTopic,
                        transactionResponseTopic,
                        request,
                        Transaction.class,
                        Duration.ofSeconds(kafkaTimeout)
                )
                .onErrorResume(TimeoutException.class, error ->
                        Mono.error(new RuntimeException(
                                "Tiempo de espera agotado para la transacción bancaria")))
                .then(transactionYankiMapper.toEntity(transaction))
                .flatMap(transactionYankiRepository::save)
                .flatMap(transactionYankiMapper::toDomain)
                .flatMap(this::cacheTransaction);
    }

    public Mono<TransactionYanki> findTransactionById(String transactionId) {
        return getFromCache(transactionId)
                .switchIfEmpty(
                        transactionYankiRepository.findById(transactionId)
                                .flatMap(transactionYankiMapper::toDomain)
                                .flatMap(this::cacheTransaction)
                );
    }
}