package nnt_data.customer_service.domain.validation.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.PersonalCustomer;
import nnt_data.customer_service.infraestructure.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
}
