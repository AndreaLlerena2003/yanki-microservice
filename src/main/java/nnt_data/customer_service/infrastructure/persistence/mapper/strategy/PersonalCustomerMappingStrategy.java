package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.entity.PersonalCustomer;
import org.springframework.stereotype.Component;
/**
 * Estrategia de mapeo para clientes personales, extendiendo la estrategia base.
 * Convierte entre `PersonalCustomer` y `CustomerEntity`.
 */



@Component

public class PersonalCustomerMappingStrategy extends BaseCustomerMappingStrategy<PersonalCustomer> {

    public PersonalCustomerMappingStrategy() {
        super(Customer.TypeEnum.PERSONAL, PersonalCustomer.class);
    }


    @Override
    protected void mapSpecificFields(PersonalCustomer source, CustomerEntity target) {
        target.setDni(source.getDni());
    }

    @Override
    protected void mapSpecificFields(CustomerEntity source, PersonalCustomer target) {
        target.setDni(source.getDni());
    }
}
