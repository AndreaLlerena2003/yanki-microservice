package nnt_data.customer_service.mapper;

import nnt_data.customer_service.document.CustomerEntity;
import nnt_data.customer_service.exception.CustomerMappingException;
import nnt_data.customer_service.model.BusinessCustomer;
import nnt_data.customer_service.model.Customer;
import nnt_data.customer_service.model.PersonalCustomer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class CustomerMapperImpl implements CustomerMapper{

    @Override
    public Mono<CustomerEntity> toEntity(Customer customer) {
        if (customer == null) {
            return Mono.error(new CustomerMappingException("Cannot map null customer"));
        }
        return Mono.just(createCustomerEntity(customer));
    }

    @Override
    public Mono<Customer> toDomain(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return Mono.error(new CustomerMappingException("Cannot map null customer"));
        }

        return Mono.defer(() -> {
            switch (customerEntity.getType()) {
                case PERSONAL:
                    return Mono.just(createPersonalCustomer(customerEntity));
                case BUSINESS:
                    return Mono.just(createBusinessCustomer(customerEntity));
                default:
                    return Mono.error(new IllegalArgumentException("Unknown customer type: " + customerEntity.getType()));
            }
        });
    }


    private CustomerEntity createCustomerEntity(Customer customer) {
        CustomerEntity customerEntity = new CustomerEntity();
        mapCommonFields(customer, customerEntity);
        mapTypeSpecificFields(customer, customerEntity);
        return customerEntity;
    }

    private void mapCommonFields(Customer customer, CustomerEntity customerEntity) {
        customerEntity.setId(customer.getId());
        customerEntity.setName(customer.getName());
        customerEntity.setLastname(customer.getLastname());
        customerEntity.setEmail(customer.getEmail());
        customerEntity.setPhone(customer.getPhone());
        customerEntity.setAddress(customer.getAddress());
    }

    private void mapTypeSpecificFields(Customer customer, CustomerEntity customerEntity) {
        if (customer instanceof PersonalCustomer personalCustomer) {
            customerEntity.setType(Customer.TypeEnum.PERSONAL);
            customerEntity.setDni(personalCustomer.getDni());
        } else if (customer instanceof BusinessCustomer businessCustomer) {
            customerEntity.setType(Customer.TypeEnum.BUSINESS);
            customerEntity.setRuc(businessCustomer.getRuc());
        }
    }

    private PersonalCustomer createPersonalCustomer(CustomerEntity customerEntity) {
        PersonalCustomer personalCustomer = new PersonalCustomer();
        mapCommonCustomerFields(customerEntity, personalCustomer);
        personalCustomer.setDni(customerEntity.getDni());
        personalCustomer.setType(Customer.TypeEnum.PERSONAL);

        return personalCustomer;
    }

    private BusinessCustomer createBusinessCustomer(CustomerEntity customerEntity) {
        BusinessCustomer businessCustomer = new BusinessCustomer();
        mapCommonCustomerFields(customerEntity, businessCustomer);
        businessCustomer.setRuc(customerEntity.getRuc());
        businessCustomer.setType(Customer.TypeEnum.BUSINESS);
        return businessCustomer;
    }

    private void mapCommonCustomerFields(CustomerEntity customerEntity, Customer customer) {
        customer.setId(customerEntity.getId());
        customer.setName(customerEntity.getName());
        customer.setLastname(customerEntity.getLastname());
        customer.setEmail(customerEntity.getEmail());
        customer.setPhone(customerEntity.getPhone());
        customer.setAddress(customerEntity.getAddress());
    }

}
