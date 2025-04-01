package nnt_data.customer_service.application.port;

import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerPort {
    Mono<Customer> createCustomer(CustomerEntity customerEntity);
    Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity);
    Mono<Customer> getCustomerById(String id);
    Mono<Void> deleteCustomerById(String id);
}
