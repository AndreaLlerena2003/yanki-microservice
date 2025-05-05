package nnt_data.yanki_service.domain.service;

import lombok.extern.slf4j.Slf4j;
import nnt_data.yanki_service.entity.DebitCardValidationRequest;
import nnt_data.yanki_service.entity.DebitCardValidationResponse;
import nnt_data.yanki_service.entity.UserYanki;
import nnt_data.yanki_service.infrastructure.persistence.mapper.UserYankiMapper;
import nnt_data.yanki_service.infrastructure.persistence.repository.UserYankiRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class UserYankiService {

    private final UserYankiRepository userYankiRepository;
    private final UserYankiMapper userYankiMapper;
    private final KafkaService kafkaService;
    private final ReactiveRedisTemplate<String, UserYanki> userRedisTemplate;

    @Value("${redis.ttl.users:PT24H}")
    private Duration userCacheTtl;

    @Value("${kafka.topics.debit-card-validation-request:debit-card-validation-requests}")
    private String debitCardValidationRequestTopic;

    @Value("${kafka.topics.debit-card-validation-response:debit-card-validation-responses}")
    private String debitCardValidationResponseTopic;

    public UserYankiService(
            UserYankiRepository userYankiRepository,
            UserYankiMapper userYankiMapper,
            KafkaService kafkaService,
            @Qualifier("userRedisTemplate") ReactiveRedisTemplate<String, UserYanki> userRedisTemplate) {
        this.userYankiRepository = userYankiRepository;
        this.userYankiMapper = userYankiMapper;
        this.kafkaService = kafkaService;
        this.userRedisTemplate = userRedisTemplate;
    }

    private String generateUserCacheKey(String userId) {
        return "user:" + userId;
    }

    private Mono<UserYanki> cacheUser(UserYanki user) {
        return userRedisTemplate.opsForValue()
                .set(generateUserCacheKey(user.getId()), user, userCacheTtl)
                .thenReturn(user);
    }

    private Mono<Void> invalidateUserCache(String userId) {
        return userRedisTemplate.opsForValue()
                .delete(generateUserCacheKey(userId))
                .then();
    }

    public Mono<UserYanki> createYankiUser(Mono<UserYanki> userYankiMono) {
        return userYankiMono
                .flatMap(userYankiMapper::toEntity)
                .flatMap(userYankiRepository::save)
                .flatMap(userYankiMapper::toDomain)
                .flatMap(this::cacheUser)
                .doOnSuccess(user -> log.info("Usuario Yanki creado exitosamente: {}", user.getId()))
                .doOnError(error -> log.error("Error al crear usuario Yanki", error));
    }

    public Mono<UserYanki> findUserById(String userId) {
        return userRedisTemplate.opsForValue()
                .get(generateUserCacheKey(userId))
                .switchIfEmpty(
                        userYankiRepository.findById(userId)
                                .flatMap(userYankiMapper::toDomain)
                                .flatMap(this::cacheUser)
                )
                .doOnSuccess(user -> log.debug("Usuario encontrado: {}", userId))
                .doOnError(error -> log.error("Error al buscar usuario: {}", userId, error));
    }

    public Mono<UserYanki> asociateYankiUserToDebitCard(String yankiUserId, String debitCardId) {
        DebitCardValidationRequest request = new DebitCardValidationRequest(debitCardId);
        log.info("Procesando solicitud de asociación de tarjeta para userId: {}, cardId: {}",
                yankiUserId, debitCardId);

        return validateDebitCard(request)
                .flatMap(response -> updateUserWithDebitCard(yankiUserId, debitCardId))
                .doOnSuccess(user -> log.info("Tarjeta {} asociada exitosamente al usuario {}",
                        debitCardId, yankiUserId))
                .doOnError(error -> log.error("Error al asociar tarjeta", error));
    }

    private Mono<DebitCardValidationResponse> validateDebitCard(DebitCardValidationRequest request) {
        return kafkaService.sendAndReceive(
                        debitCardValidationRequestTopic,
                        debitCardValidationResponseTopic,
                        request,
                        DebitCardValidationResponse.class,
                        Duration.ofSeconds(10)
                )
                .doOnNext(response -> log.debug("Respuesta de validación de tarjeta recibida: {}", response))
                .flatMap(response -> {
                    if (!response.getIsValid()) {
                        return Mono.error(new IllegalArgumentException(
                                "Tarjeta no válida: " + response.getMessage()));
                    }
                    return Mono.just(response);
                });
    }

    private Mono<UserYanki> updateUserWithDebitCard(String yankiUserId, String debitCardId) {
        return findUserById(yankiUserId)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Usuario no encontrado: " + yankiUserId)))
                .flatMap(user -> {
                    user.setTarjetaAsociada(debitCardId);
                    return userYankiMapper.toEntity(user)
                            .flatMap(userYankiRepository::save)
                            .flatMap(userYankiMapper::toDomain)
                            .flatMap(this::cacheUser);
                });
    }

    public Mono<Boolean> existById(String userId) {
        return userYankiRepository.existsById(userId);
    }
}