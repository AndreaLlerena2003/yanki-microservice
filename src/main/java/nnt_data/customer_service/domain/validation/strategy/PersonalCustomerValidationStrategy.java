package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.entity.PersonalCustomer;
import nnt_data.customer_service.infrastructure.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Estrategia de validación para clientes personales, extendiendo la estrategia base.
 * Valida campos únicos como el email y el DNI.
 */
@Component
public class PersonalCustomerValidationStrategy extends BaseValidationStrategy implements CustomerValidationStrategy {

    public PersonalCustomerValidationStrategy(CustomerRepository customerRepository) {
        super(customerRepository);
    }

    @Override
    public Mono<Void> validateUniqueFields(Customer customer) {
        PersonalCustomer personalCustomer = (PersonalCustomer) customer;
        return validateEmail(customer.getEmail())
                .then(validateField(
                        personalCustomer.getDni(),
                        "DNI",
                        customerRepository.existsByDni(personalCustomer.getDni())
                ));
    }

    @Override
    public boolean supports(Customer customer) {
        return customer instanceof PersonalCustomer;
    }

    @Override
    public Mono<Void> validateSubtype(CustomerSubtype subtype) {
        if (subtype == null) {
            return Mono.error(new IllegalArgumentException(
                    "Un cliente debe especificar un subtipo"));
        }
        boolean isValid = CustomerSubtype.REGULAR.equals(subtype) || CustomerSubtype.VIP.equals(subtype);
        if (!isValid) {
            return Mono.error(new IllegalArgumentException(
                    "Un cliente personal solo puede tener subtipos REGULAR o VIP"));
        }
        return Mono.empty();
    }
}
