package nnt_data.customer_service.domain.validation;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import reactor.core.publisher.Mono;

public interface CustomerValidator {
    Mono<Void> ensureUniqueFields(Mono<Customer> customerMono);
    Mono<Void> validateSubtype(Mono<Customer> customerMono);
}
