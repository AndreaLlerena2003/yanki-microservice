package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import reactor.core.publisher.Mono;

public interface CustomerValidationStrategy {
    Mono<Void> validateUniqueFields(Customer customer);
    boolean supports(Customer customer);
    Mono<Void> validateSubtype(CustomerSubtype subtype);
}
