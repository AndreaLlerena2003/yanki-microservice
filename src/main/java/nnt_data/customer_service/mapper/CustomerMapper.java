package nnt_data.customer_service.mapper;

import nnt_data.customer_service.document.CustomerEntity;
import nnt_data.customer_service.model.Customer;
import reactor.core.publisher.Mono;

public interface CustomerMapper {
    Mono<CustomerEntity> toEntity(Customer customer);
    Mono<Customer> toDomain(CustomerEntity customerEntity);
}
