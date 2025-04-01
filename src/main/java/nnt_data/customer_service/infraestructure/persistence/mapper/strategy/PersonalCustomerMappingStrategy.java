package nnt_data.customer_service.infraestructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.PersonalCustomer;
import org.springframework.stereotype.Component;

@Component
public class PersonalCustomerMappingStrategy extends BaseCustomerMappingStrategy {

    @Override
    public CustomerEntity toEntity(Customer customer) {
        PersonalCustomer personalCustomer = (PersonalCustomer) customer;
        CustomerEntity entity = new CustomerEntity();
        mapCommonFields(customer, entity);
        entity.setType(Customer.TypeEnum.PERSONAL);
        entity.setDni(personalCustomer.getDni());
        return entity;
    }

    @Override
    public Customer toDomain(CustomerEntity entity) {
        PersonalCustomer customer = new PersonalCustomer();
        mapCommonFields(entity, customer);
        customer.setDni(entity.getDni());
        customer.setType(Customer.TypeEnum.PERSONAL);
        return customer;
    }

    @Override
    public boolean supports(Customer.TypeEnum type) {
        return Customer.TypeEnum.PERSONAL.equals(type);
    }


}
