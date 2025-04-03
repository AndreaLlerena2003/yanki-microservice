package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.BusinessCustomer;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.infrastructure.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BusinessCustomerValidationStrategyTest {

    @Mock
    private CustomerRepository customerRepository;
    private BusinessCustomerValidationStrategy validationStrategy;

    @BeforeEach
    void setUp() {
        validationStrategy = new BusinessCustomerValidationStrategy(customerRepository);
    }

    @Test
    void supports_withBusinessCustomer_shouldReturnTrue(){
        BusinessCustomer businessCustomer = mock(BusinessCustomer.class);
        assertTrue(validationStrategy.supports(businessCustomer));
    }

    @Test
    void supports_withOtherCustomer_shouldReturnFalse(){
        Customer customer = mock(Customer.class);
        assertFalse(validationStrategy.supports(customer));
    }

    @Test
    void validateUniqueFields_withNonExistingEmailAndRuc_shouldReturnEmptyMono(){
        String email = "business@example.com";
        String ruc = "12345678901";
        BusinessCustomer businessCustomer = mock(BusinessCustomer.class);
        when(businessCustomer.getEmail()).thenReturn(email);
        when(businessCustomer.getRuc()).thenReturn(ruc);
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        when(customerRepository.existsByRuc(ruc)).thenReturn(Mono.just(false));
        StepVerifier.create(validationStrategy.validateUniqueFields(businessCustomer))
                .verifyComplete();
    }

    @Test
    void validateUniqueFields_withExistingEmailAndRuc_shouldReturnError(){
        String email = "business@example.com";
        String ruc = "12345678901";
        BusinessCustomer businessCustomer = mock(BusinessCustomer.class);
        when(businessCustomer.getEmail()).thenReturn(email);
        when(businessCustomer.getRuc()).thenReturn(ruc);
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(true));
        when(customerRepository.existsByRuc(ruc)).thenReturn(Mono.just(false));
        StepVerifier.create(validationStrategy.validateUniqueFields(businessCustomer))
                .expectErrorMatches(error -> error.getMessage().equals("Email must be unique"))
                .verify();
    }

    @Test
    void validateUniqueFields_withExistingRuc_shouldReturnError() {
        String email = "business@example.com";
        String ruc = "12345678901";
        BusinessCustomer businessCustomer = mock(BusinessCustomer.class);
        when(businessCustomer.getEmail()).thenReturn(email);
        when(businessCustomer.getRuc()).thenReturn(ruc);

        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        when(customerRepository.existsByRuc(ruc)).thenReturn(Mono.just(true));
        StepVerifier.create(validationStrategy.validateUniqueFields(businessCustomer))
                .expectErrorMatches(error ->
                        error.getMessage().equals("RUC must be unique"))
                .verify();
    }

    @Test
    void validateSubtype_withNull_shouldReturnError() {
        StepVerifier.create(validationStrategy.validateSubtype(null))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("El cliente debe especificar un subtipo"))
                .verify();
    }

    @Test
    void validateSubtype_withREGULAR_shouldReturnEmptyMono() {
        StepVerifier.create(validationStrategy.validateSubtype(CustomerSubtype.REGULAR))
                .verifyComplete();
    }

    @Test
    void validateSubtype_withPYME_shouldReturnEmptyMono() {
        StepVerifier.create(validationStrategy.validateSubtype(CustomerSubtype.PYME))
                .verifyComplete();
    }

    @Test
    void validateSubtype_withInvalidSubtype_shouldReturnError() {
        CustomerSubtype invalidSubtype = CustomerSubtype.VIP;
        StepVerifier.create(validationStrategy.validateSubtype(invalidSubtype))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Un cliente empresarial solo puede tener subtipos REGULAR o PYME"))
                .verify();
    }




}
