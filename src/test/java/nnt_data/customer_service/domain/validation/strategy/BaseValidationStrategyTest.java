package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.domain.exception.CustomerUniqueFieldException; // Falta esta importación
import nnt_data.customer_service.infrastructure.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseValidationStrategyTest {
    @Mock
    private CustomerRepository customerRepository;
    private BaseValidationStrategy baseValidationStrategy;

    @BeforeEach
    void setUp() {
        baseValidationStrategy = new BaseValidationStrategy(customerRepository);
    }

    @Test
    void validateField_withNullValue_shouldReturnEmptyMono(){
        String value = null;
        String fieldName = "Email";
        Mono<Boolean> existsCheck = Mono.just(true);
        StepVerifier.create(baseValidationStrategy.validateField(value, fieldName, existsCheck))
                .verifyComplete();
    }

    @Test
    void validateField_withExistingValue_shouldReturnError() {
        String value = "test@example.com";
        String fieldName = "Email";
        Mono<Boolean> existsCheck = Mono.just(true);
        StepVerifier.create(baseValidationStrategy.validateField(value, fieldName, existsCheck))
                .expectErrorMatches(error ->
                        error instanceof CustomerUniqueFieldException &&
                                error.getMessage().equals("Email must be unique"))
                .verify();
    }

    @Test
    void validateField_withNonExistingValue_shouldReturnEmptyMono() {
        String value = "test@example.com";
        String fieldName = "Email";
        Mono<Boolean> existsCheck = Mono.just(false);
        StepVerifier.create(baseValidationStrategy.validateField(value, fieldName, existsCheck))
                .verifyComplete();
    }

    @Test
    void validateEmail_withNullEmail_shouldReturnEmptyMono() {
        String email = null;
        StepVerifier.create(baseValidationStrategy.validateEmail(email))
                .verifyComplete();
    }

    @Test
    void validateEmail_withExistingEmail_shouldReturnError() {
        String email = "test@example.com";
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(true));
        StepVerifier.create(baseValidationStrategy.validateEmail(email))
                .expectErrorMatches(error ->
                        error instanceof CustomerUniqueFieldException &&
                                error.getMessage().equals("Email must be unique"))
                .verify();
    }

    @Test
    void validateEmail_withNonExistingEmail_shouldReturnEmptyMono() {
        String email = "test@example.com";
        when(customerRepository.existsByEmail(email)).thenReturn(Mono.just(false));
        StepVerifier.create(baseValidationStrategy.validateEmail(email))
                .verifyComplete();
    }
}