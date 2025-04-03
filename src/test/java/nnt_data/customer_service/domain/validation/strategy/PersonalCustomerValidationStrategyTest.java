package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.entity.PersonalCustomer;
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
public class PersonalCustomerValidationStrategyTest {

    @Mock
    private CustomerRepository customerRepository;

    private PersonalCustomerValidationStrategy validationStrategy;

    @BeforeEach
    void setUp() {
        validationStrategy = new PersonalCustomerValidationStrategy(customerRepository);
    }

    @Test
    void supports_withPersonalCustomer_shouldReturnTrue(){
        PersonalCustomer personalCustomer = mock(PersonalCustomer.class);
        assertTrue(validationStrategy.supports(personalCustomer));
    }

    @Test
    void supports_withOtherCustomer_shouldReturnFalse(){
        Customer customer = mock(Customer.class);
        assertFalse(validationStrategy.supports(customer));
    }

    @Test
    void validateUniqueFields_withNonExistingEmailAndDni_shouldReturnEmptyMono() {
        String email = "personal@example.com";
        String dni = "12345678";
        PersonalCustomer personalCustomer = mock(PersonalCustomer.class);
        when(personalCustomer.getEmail()).thenReturn(email);
        when(personalCustomer.getDni()).thenReturn(dni);

        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(false));

        StepVerifier.create(validationStrategy.validateUniqueFields(personalCustomer))
                .verifyComplete();
    }

    @Test
    void validateUniqueFields_withExistingEmail_shouldReturnError() {
        String email = "personal@example.com";
        String dni = "12345678";
        PersonalCustomer personalCustomer = mock(PersonalCustomer.class);
        when(personalCustomer.getEmail()).thenReturn(email);
        when(personalCustomer.getDni()).thenReturn(dni);
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(true));
        when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(false));
        StepVerifier.create(validationStrategy.validateUniqueFields(personalCustomer))
                .expectErrorMatches(error ->
                        error.getMessage().equals("Email must be unique"))
                .verify();
    }

    @Test
    void validateUniqueFields_withExistingDni_shouldReturnError() {
        String email = "personal@example.com";
        String dni = "12345678";
        PersonalCustomer personalCustomer = mock(PersonalCustomer.class);
        when(personalCustomer.getEmail()).thenReturn(email);
        when(personalCustomer.getDni()).thenReturn(dni);
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        when(customerRepository.existsByDni(dni)).thenReturn(Mono.just(true));
        StepVerifier.create(validationStrategy.validateUniqueFields(personalCustomer))
                .expectErrorMatches(error ->
                        error.getMessage().equals("DNI must be unique"))
                .verify();
    }

    @Test
    void validateSubtype_withNull_shouldReturnError() {
        StepVerifier.create(validationStrategy.validateSubtype(null))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Un cliente debe especificar un subtipo"))
                .verify();
    }

    @Test
    void validateSubtype_withREGULAR_shouldReturnEmptyMono() {
        StepVerifier.create(validationStrategy.validateSubtype(CustomerSubtype.REGULAR))
                .verifyComplete();
    }

    @Test
    void validateSubtype_withVIP_shouldReturnEmptyMono() {
        StepVerifier.create(validationStrategy.validateSubtype(CustomerSubtype.VIP))
                .verifyComplete();
    }

    @Test
    void validateSubtype_withInvalidSubtype_shouldReturnError() {
        CustomerSubtype invalidSubtype = CustomerSubtype.PYME;
        StepVerifier.create(validationStrategy.validateSubtype(invalidSubtype))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Un cliente personal solo puede tener subtipos REGULAR o VIP"))
                .verify();
    }
}
