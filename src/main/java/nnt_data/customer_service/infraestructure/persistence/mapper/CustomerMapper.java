package nnt_data.customer_service.infraestructure.persistence.mapper;

import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerMapper {
    Mono<CustomerEntity> toEntity(Customer customer);
    Mono<Customer> toDomain(CustomerEntity customerEntity);
}
