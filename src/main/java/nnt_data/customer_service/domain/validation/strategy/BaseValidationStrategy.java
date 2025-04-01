package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.domain.exception.CustomerUniqueFieldException;
import nnt_data.customer_service.infraestructure.persistence.repository.CustomerRepository;
import reactor.core.publisher.Mono;
/**
 * Estrategia base para validaciones de campos únicos en clientes.
 */
public class BaseValidationStrategy {
    protected final CustomerRepository customerRepository;

    protected BaseValidationStrategy(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    protected Mono<Void> validateField(String value, String fieldName,
                                       Mono<Boolean> existsCheck) {
        if (value == null) {
            return Mono.empty();
        }
        return existsCheck
                .flatMap(exists -> exists
                        ? Mono.error(new CustomerUniqueFieldException(
                        String.format("%s must be unique", fieldName)))
                        : Mono.empty());
    }

    protected Mono<Void> validateEmail(String email) {
        return validateField(email, "Email", customerRepository.existsByEmail(email));
    }
}
