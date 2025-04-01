package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerValidationStrategy {
    Mono<Void> validateUniqueFields(Customer customer);
    boolean supports(Customer customer);
}
/**
 * Interfaz para estrategias de validación de clientes.
 * Define métodos para validar campos únicos y verificar el tipo de cliente.
 */