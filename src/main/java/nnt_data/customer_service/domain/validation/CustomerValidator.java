package nnt_data.customer_service.domain.validation;
import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerValidator {
    Mono<Void> ensureUniqueFields(Mono<Customer> customerMono);
}
