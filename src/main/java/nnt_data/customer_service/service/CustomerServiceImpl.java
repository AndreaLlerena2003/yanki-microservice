package nnt_data.customer_service.service;

import lombok.RequiredArgsConstructor;
import nnt_data.customer_service.document.CustomerEntity;
import nnt_data.customer_service.exception.CustomerNotFoundException;
import nnt_data.customer_service.mapper.CustomerMapper;
import nnt_data.customer_service.model.Customer;
import nnt_data.customer_service.repository.CustomerRepository;
import nnt_data.customer_service.validation.CustomerValidator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;

    @Override
    public Mono<Customer> createCustomer(CustomerEntity customerEntity) {
        return customerMapper.toDomain(customerEntity)
                .flatMap(customer -> customerValidator.ensureUniqueFields(Mono.just(customer))
                        .thenReturn(customerEntity)
                )
                .flatMap(customerRepository::insert)
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(existingCustomer -> {
                    customerEntity.setId(id);
                    return customerRepository.save(customerEntity);
                })
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> getCustomerById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteCustomerById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customer -> customerRepository.deleteById(id));
    }
}
