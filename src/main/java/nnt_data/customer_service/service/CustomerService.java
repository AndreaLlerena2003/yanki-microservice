package nnt_data.customer_service.service;

import nnt_data.customer_service.document.CustomerEntity;
import nnt_data.customer_service.model.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<Customer> createCustomer(CustomerEntity customerEntity);
    Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity);
    Mono<Customer> getCustomerById(String id);
    Mono<Void> deleteCustomerById(String id);
}
