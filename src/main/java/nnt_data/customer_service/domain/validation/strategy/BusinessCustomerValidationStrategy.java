package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.BusinessCustomer;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Estrategia de validación para clientes de negocios, extendiendo la estrategia base.
 * Valida campos únicos como el email y el RUC.
 */
@Component
public class BusinessCustomerValidationStrategy extends BaseValidationStrategy implements CustomerValidationStrategy {

    public BusinessCustomerValidationStrategy(CustomerRepository customerRepository) {
        super(customerRepository);
    }

    @Override
    public Mono<Void> validateUniqueFields(Customer customer) {
        BusinessCustomer businessCustomer = (BusinessCustomer) customer;
        return validateEmail(customer.getEmail())
                .then(validateField(
                        businessCustomer.getRuc(),
                        "RUC",
                        customerRepository.existsByRuc(businessCustomer.getRuc())
                ));
    }

    @Override
    public boolean supports(Customer customer) {
        return customer instanceof BusinessCustomer;
    }
}
