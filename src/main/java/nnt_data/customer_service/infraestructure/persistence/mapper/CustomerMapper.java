package nnt_data.customer_service.infraestructure.persistence.mapper;

import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;
/**
 * Interfaz para el mapeo reactivo entre `Customer` y `CustomerEntity`.
 * Define métodos para convertir de dominio a entidad y viceversa usando `Mono`.
 */
public interface CustomerMapper {
    Mono<CustomerEntity> toEntity(Customer customer);
    Mono<Customer> toDomain(CustomerEntity customerEntity);
}
