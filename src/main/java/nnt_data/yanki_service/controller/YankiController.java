package nnt_data.yanki_service.controller;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import nnt_data.yanki_service.domain.service.TransactionYankiService;
import nnt_data.yanki_service.domain.service.UserYankiService;
import nnt_data.yanki_service.entity.AsociacionTarjeta;
import nnt_data.yanki_service.entity.TransactionYanki;
import nnt_data.yanki_service.entity.UserYanki;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import nnt_data.yanki_service.api.YankiApi;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class YankiController implements YankiApi {

    private final UserYankiService userYankiService;
    private final TransactionYankiService transactionYankiService;


    /**
     * POST /yanki/tarjetas : Asociar tarjeta de débito
     * Asocia una tarjeta de débito al monedero Yanki
     *
     * @param asociacionTarjeta (required)
     * @param exchange
     * @return Tarjeta asociada correctamente (status code 201)
     * or Datos de entrada inválidos (status code 400)
     * or Usuario no encontrado (status code 404)
     * or La tarjeta ya está asociada a otro monedero (status code 409)
     */
    @Override
    public Mono<ResponseEntity<AsociacionTarjeta>> asociarTarjeta(Mono<AsociacionTarjeta> asociacionTarjeta, ServerWebExchange exchange) {
        return asociacionTarjeta
                .flatMap(at -> userYankiService.asociateYankiUserToDebitCard(at.getUserYankiId(), at.getDebitCardId())
                        .map(userYanki -> {
                            AsociacionTarjeta asociacion = new AsociacionTarjeta();
                            asociacion.setUserYankiId(userYanki.getId());
                            asociacion.setDebitCardId(at.getDebitCardId());
                            return asociacion;
                        })
                )
                .map(asociacion -> ResponseEntity.status(HttpStatus.CREATED).body(asociacion))
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .onErrorResume(DuplicateKeyException.class, e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                });
    }

    /**
     * POST /yanki/monederos/transacciones : Realizar una transferencia entre monederos
     * Permite enviar dinero desde un monedero a otro usando el número de teléfono
     *
     * @param transactionYanki (required)
     * @param exchange
     * @return Transferencia realizada correctamente (status code 201)
     * or Datos de entrada inválidos (status code 400)
     * or Algún monedero no encontrado (status code 404)
     * or Saldo insuficiente para realizar la operación (status code 422)
     */
    @Override
    public Mono<ResponseEntity<TransactionYanki>> realizarTransferencia(Mono<TransactionYanki> transactionYanki, ServerWebExchange exchange) {
        return transactionYankiService.createTransaction(transactionYanki)
                .map(transaction -> ResponseEntity.status(HttpStatus.CREATED).body(transaction))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(Exception.class, e -> {
                    if (e.getMessage().contains("saldo insuficiente")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build());
                    }
                    if (e.getMessage().contains("no encontrado")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    /**
     * POST /yanki/usuarios : Registrar nuevo usuario
     * Registra un nuevo usuario en el sistema de monedero móvil Yanki
     *
     * @param userYanki (required)
     * @param exchange
     * @return Usuario registrado correctamente (status code 201)
     * or Datos de entrada inválidos (status code 400)
     * or El usuario ya existe (status code 409)
     */
    @Override
    public Mono<ResponseEntity<UserYanki>> registrarUsuario(Mono<UserYanki> userYanki, ServerWebExchange exchange) {
        return userYankiService.createYankiUser(userYanki)
                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser)) // 201 Created
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .onErrorResume(DuplicateKeyException.class, e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                });
    }
}
