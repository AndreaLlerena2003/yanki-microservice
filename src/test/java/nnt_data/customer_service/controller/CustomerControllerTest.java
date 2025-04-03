package nnt_data.customer_service.controller;

import nnt_data.customer_service.domain.service.CustomerPortImpl;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.infrastructure.persistence.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerPortImpl customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ServerWebExchange exchange;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId("123");
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
    }

    @Test
    void createCustomer_Success() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId("123");
        customerEntity.setName("Test Customer");
        when(customerMapper.toEntity(any(Customer.class))).thenReturn(Mono.just(customerEntity));
        when(customerService.createCustomer(any(CustomerEntity.class))).thenReturn(Mono.just(testCustomer));
        Mono<ResponseEntity<Customer>> result = customerController.createCustomer(Mono.just(testCustomer), exchange);
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    Customer body = responseEntity.getBody();
                    return responseEntity.getStatusCode().is2xxSuccessful() &&
                            body != null &&
                            "123".equals(body.getId()) &&
                            "Test Customer".equals(body.getName());
                })
                .verifyComplete();

        verify(customerMapper, times(1)).toEntity(any(Customer.class));
        verify(customerService, times(1)).createCustomer(any(CustomerEntity.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerService.deleteCustomerById(anyString())).thenReturn(Mono.empty());
        Mono<ResponseEntity<Void>> result = customerController.deleteCustomer("123", exchange);
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().is2xxSuccessful())
                .verifyComplete();

        verify(customerService, times(1)).deleteCustomerById("123");
    }

    @Test
    void deleteCustomer_NotFound() {
        when(customerService.deleteCustomerById(anyString())).thenReturn(Mono.error(new RuntimeException("Not found")));
        Mono<ResponseEntity<Void>> result = customerController.deleteCustomer("456", exchange);
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().is4xxClientError())
                .verifyComplete();

        verify(customerService, times(1)).deleteCustomerById("456");
    }

    @Test
    void getCustomerById_Success() {
        when(customerService.getCustomerById(anyString())).thenReturn(Mono.just(testCustomer));
        Mono<ResponseEntity<Customer>> result = customerController.getCustomerById("123", exchange);
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    Customer body = responseEntity.getBody();
                    return responseEntity.getStatusCode().is2xxSuccessful() &&
                            body != null &&
                            "123".equals(body.getId()) &&
                            "Test Customer".equals(body.getName());
                })
                .verifyComplete();

        verify(customerService, times(1)).getCustomerById("123");
    }

    @Test
    void getCustomerById_NotFound() {
        when(customerService.getCustomerById(anyString())).thenReturn(Mono.empty());
        Mono<ResponseEntity<Customer>> result = customerController.getCustomerById("456", exchange);
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(customerService, times(1)).getCustomerById("456");
    }

    @Test
    void getCustomers_Success() {
        Customer customer2 = new Customer();
        customer2.setId("456");
        customer2.setName("Another Customer");

        when(customerService.findAll()).thenReturn(Flux.just(testCustomer, customer2));
        Mono<ResponseEntity<Flux<Customer>>> result = customerController.getCustomers(exchange);

        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().is2xxSuccessful())
                .verifyComplete();

        StepVerifier.create(result
                        .flatMapMany(responseEntity -> {
                            Flux<Customer> body = responseEntity.getBody();
                            return body != null ? body : Flux.empty();
                        }))
                .expectNext(testCustomer, customer2)
                .verifyComplete();
        verify(customerService, times(1)).findAll();
    }

    @Test
    void updateCustomer_Success() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId("123");
        updatedCustomer.setName("Updated Name");
        updatedCustomer.setEmail("updated@example.com");

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId("123");
        customerEntity.setName("Updated Name");
        customerEntity.setEmail("updated@example.com");


        when(customerMapper.toEntity(any(Customer.class))).thenReturn(Mono.just(customerEntity));
        when(customerService.updateCustomer(anyString(), any(CustomerEntity.class))).thenReturn(Mono.just(updatedCustomer));


        Mono<ResponseEntity<Customer>> result = customerController.updateCustomer("123", Mono.just(updatedCustomer), exchange);


        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> {
                    Customer body = responseEntity.getBody();
                    return responseEntity.getStatusCode().is2xxSuccessful() &&
                            body != null &&
                            "123".equals(body.getId()) &&
                            "Updated Name".equals(body.getName()) &&
                            "updated@example.com".equals(body.getEmail());
                })
                .verifyComplete();

        verify(customerMapper, times(1)).toEntity(any(Customer.class));
        verify(customerService, times(1)).updateCustomer(eq("123"), any(CustomerEntity.class));
    }
}