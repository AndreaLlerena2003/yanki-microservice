package nnt_data.customer_service.domain.validation;

import lombok.AllArgsConstructor;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.domain.validation.strategy.CustomerValidationStrategy;
import nnt_data.customer_service.entity.CustomerSubtype;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementación del validador de clientes.
 * Utiliza estrategias de validación para asegurar que los campos únicos de un cliente sean válidos.
 */
@Component
@AllArgsConstructor
public class CustomerValidatorImpl implements CustomerValidator{


    private final List<CustomerValidationStrategy> validationStrategies;

    @Override
    public Mono<Void> ensureUniqueFields(Mono<Customer> customerMono) {
        return customerMono.flatMap(customer ->
                validationStrategies.stream()
                        .filter(strategy -> strategy.supports(customer))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No validation strategy found"))
                        .validateUniqueFields(customer)
        );
    }

    @Override
    public Mono<Void> validateSubtype(Mono<Customer> customerMono) {
        return  customerMono.flatMap(customer ->
                    validationStrategies.stream()
                            .filter(strategy -> strategy.supports(customer))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No validation strategy found"))
                            .validateSubtype(customer.getSubtype())
                );
    }


}
