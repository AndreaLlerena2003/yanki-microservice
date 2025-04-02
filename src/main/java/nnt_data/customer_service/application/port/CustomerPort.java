package nnt_data.customer_service.application.port;

import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * Interfaz CustomerPort para operaciones de clientes.
 *
 * - createCustomer: Crea un nuevo cliente.
 * - updateCustomer: Actualiza un cliente existente por su ID.
 * - getCustomerById: Recupera un cliente por su ID.
 * - deleteCustomerById: Elimina un cliente por su ID.
 *
 * Utiliza Mono de Reactor para manejar las operaciones de manera reactiva.
 */
public interface CustomerPort {
    Mono<Customer> createCustomer(CustomerEntity customerEntity);
    Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity);
    Mono<Customer> getCustomerById(String id);
    Mono<Void> deleteCustomerById(String id);
    Flux<Customer> findAll();
}
