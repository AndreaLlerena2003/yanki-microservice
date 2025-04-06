package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.BusinessCustomer;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;
/**
 * Estrategia de mapeo para clientes de negocios, extendiendo la estrategia base.
 * Convierte entre `BusinessCustomer` y `CustomerEntity`.
 */

@Component
public class BusinessCustomerMappingStrategy extends BaseCustomerMappingStrategy<BusinessCustomer> {

    public BusinessCustomerMappingStrategy() {
        super(Customer.TypeEnum.BUSINESS, BusinessCustomer.class);
    }

    @Override
    protected void mapSpecificFields(BusinessCustomer source, CustomerEntity target) {
        target.setRuc(source.getRuc());
    }

    @Override
    protected void mapSpecificFields(CustomerEntity source, BusinessCustomer target) {
        target.setRuc(source.getRuc());
    }
}

