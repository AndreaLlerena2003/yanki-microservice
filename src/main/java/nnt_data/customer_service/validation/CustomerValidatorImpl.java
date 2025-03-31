package nnt_data.customer_service.validation;

import lombok.AllArgsConstructor;
import nnt_data.customer_service.exception.CustomerUniqueFieldException;
import nnt_data.customer_service.model.BusinessCustomer;
import nnt_data.customer_service.model.Customer;
import nnt_data.customer_service.model.PersonalCustomer;
import nnt_data.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@AllArgsConstructor
public class CustomerValidatorImpl implements CustomerValidator{

    private final CustomerRepository customerRepository;

    @Override
    public Mono<Void> ensureUniqueFields(Mono<Customer> customerMono) {
        return customerMono.flatMap(customer ->
                isEmailNotUnique(customer.getEmail())
                        .flatMap(emailNotUnique -> {
                            if (emailNotUnique) {
                                return Mono.error(new CustomerUniqueFieldException("Email must be unique"));
                            }
                            if (customer instanceof PersonalCustomer personalCustomer) {
                                return isDniNotUnique(personalCustomer.getDni())
                                        .flatMap(dniNotUnique -> {
                                            if (dniNotUnique) {
                                                return Mono.error(new CustomerUniqueFieldException(
                                                        "DNI must be unique for personal customers"));
                                            }
                                            return Mono.empty();
                                        });
                            } else if (customer instanceof BusinessCustomer businessCustomer) {
                                return isRucNotUnique(businessCustomer.getRuc())
                                        .flatMap(rucNotUnique -> {
                                            if (rucNotUnique) {
                                                return Mono.error(new CustomerUniqueFieldException(
                                                        "RUC must be unique for business customers"));
                                            }
                                            return Mono.empty();
                                        });
                            }
                            return Mono.empty();
                        })
        );
    }

    private Mono<Boolean> isEmailNotUnique(String email) {
        if (email == null) {
            return Mono.just(false);
        }
        return customerRepository.existsByEmail(email);
    }

    private Mono<Boolean> isDniNotUnique(String dni) {
        if (dni == null) {
            return Mono.just(false);
        }
        return customerRepository.existsByDni(dni);
    }

    private Mono<Boolean> isRucNotUnique(String ruc) {
        if (ruc == null) {
            return Mono.just(false);
        }
        return customerRepository.existsByRuc(ruc);
    }


}
