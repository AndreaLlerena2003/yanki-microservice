package nnt_data.customer_service.controller;
import lombok.RequiredArgsConstructor;
import nnt_data.customer_service.api.CustomersApi;
import nnt_data.customer_service.infraestructure.persistence.mapper.CustomerMapper;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.domain.service.CustomerPortImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {
    private final CustomerPortImpl customerService;
    private final CustomerMapper customerMapper;

    @Override
    public Mono<ResponseEntity<Customer>> createCustomer(Mono<Customer> customer, ServerWebExchange exchange) {
        return customer
                .flatMap(customerMapper::toEntity)
                .flatMap(customerService::createCustomer)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(String customerId, ServerWebExchange exchange) {
        return customerService.deleteCustomerById(customerId)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<Customer>> getCustomerById(String customerId, ServerWebExchange exchange) {
        return customerService.getCustomerById(customerId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
    }

    /**
     * GET /customers : Obtener todos los clientes
     * Recupera la lista completa de clientes registrados en el sistema
     *
     * @param exchange
     * @return Lista de clientes recuperada con éxito (status code 200)
     * or No autorizado (status code 401)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<Flux<Customer>>> getCustomers(ServerWebExchange exchange) {
        return CustomersApi.super.getCustomers(exchange);
    }


    @Override
    public Mono<ResponseEntity<Customer>> updateCustomer(String customerId, Mono<Customer> customer, ServerWebExchange exchange) {
        return customer
                .flatMap(customerMapper::toEntity)
                .flatMap(customerEntity -> customerService.updateCustomer(customerId, customerEntity))
                .map(ResponseEntity::ok);
    }



}
