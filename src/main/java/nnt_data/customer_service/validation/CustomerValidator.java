package nnt_data.customer_service.validation;
import nnt_data.customer_service.model.Customer;
import reactor.core.publisher.Mono;

public interface CustomerValidator {
    Mono<Void> ensureUniqueFields(Mono<Customer> customerMono);
}
