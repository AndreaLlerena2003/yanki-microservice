package nnt_data.customer_service.domain.validation;

import nnt_data.customer_service.domain.validation.strategy.CustomerValidationStrategy;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerValidatorImplTest {

    private CustomerValidatorImpl customerValidator;

    @Mock
    private CustomerValidationStrategy matchingStrategy;

    @Mock
    private CustomerValidationStrategy nonMatchingStrategy;

    @Mock
    private Customer customer;

    private List<CustomerValidationStrategy> strategies;

    @BeforeEach
    void setUp() {
        strategies = new ArrayList<>();
        strategies.add(nonMatchingStrategy);
        strategies.add(matchingStrategy);
        customerValidator = new CustomerValidatorImpl(strategies);

        lenient().when(nonMatchingStrategy.supports(any(Customer.class))).thenReturn(false);
        lenient().when(matchingStrategy.supports(any(Customer.class))).thenReturn(true);
    }

    @Test
    void ensureUniqueFields_withSupportedCustomer_shouldValidateFields() {
        when(matchingStrategy.validateUniqueFields(customer)).thenReturn(Mono.empty());
        StepVerifier.create(customerValidator.ensureUniqueFields(Mono.just(customer)))
                .verifyComplete();
        verify(matchingStrategy).validateUniqueFields(customer);
    }

    @Test
    void ensureUniqueFields_withValidationError_shouldPropagateError() {
        RuntimeException validationError = new RuntimeException("Validation failed");
        when(matchingStrategy.validateUniqueFields(customer)).thenReturn(Mono.error(validationError));

        StepVerifier.create(customerValidator.ensureUniqueFields(Mono.just(customer)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof RuntimeException;
                    assert error.getMessage().equals("Validation failed");
                })
                .verify();

        verify(matchingStrategy).validateUniqueFields(customer);
    }

    @Test
    void ensureUniqueFields_withNoMatchingStrategy_shouldThrowException() {
        strategies.clear();
        strategies.add(nonMatchingStrategy);
        customerValidator = new CustomerValidatorImpl(strategies);
        StepVerifier.create(customerValidator.ensureUniqueFields(Mono.just(customer)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof IllegalArgumentException;
                    assert error.getMessage().equals("No validation strategy found");
                })
                .verify();

        verify(nonMatchingStrategy).supports(customer);
        verify(nonMatchingStrategy, never()).validateUniqueFields(any());
    }

    @Test
    void validateSubtype_withSupportedCustomer_shouldValidateSubtype() {
        CustomerSubtype subtype = CustomerSubtype.REGULAR;
        when(customer.getSubtype()).thenReturn(subtype);
        when(matchingStrategy.validateSubtype(subtype)).thenReturn(Mono.empty());
        StepVerifier.create(customerValidator.validateSubtype(Mono.just(customer)))
                .verifyComplete();

        verify(matchingStrategy).validateSubtype(subtype);
    }

    @Test
    void validateSubtype_withInvalidSubtype_shouldPropagateError() {
        CustomerSubtype subtype = CustomerSubtype.PYME;
        when(customer.getSubtype()).thenReturn(subtype);
        IllegalArgumentException subtypeError = new IllegalArgumentException("Invalid subtype");
        when(matchingStrategy.validateSubtype(subtype)).thenReturn(Mono.error(subtypeError));
        StepVerifier.create(customerValidator.validateSubtype(Mono.just(customer)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof IllegalArgumentException;
                    assert error.getMessage().equals("Invalid subtype");
                })
                .verify();

        verify(matchingStrategy).validateSubtype(subtype);
    }

    @Test
    void validateSubtype_withNoMatchingStrategy_shouldThrowException() {
        strategies.clear();
        strategies.add(nonMatchingStrategy);
        customerValidator = new CustomerValidatorImpl(strategies);
        StepVerifier.create(customerValidator.validateSubtype(Mono.just(customer)))
                .expectErrorSatisfies(error -> {
                    assert error instanceof IllegalArgumentException;
                    assert error.getMessage().equals("No validation strategy found");
                })
                .verify();
        verify(nonMatchingStrategy).supports(customer);
        verify(nonMatchingStrategy, never()).validateSubtype(any());
    }
}
