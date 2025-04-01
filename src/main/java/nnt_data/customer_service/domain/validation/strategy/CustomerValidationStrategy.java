package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerValidationStrategy {
    Mono<Void> validateUniqueFields(Customer customer);
    boolean supports(Customer customer);
}
