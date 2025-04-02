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
public class BusinessCustomerMappingStrategy extends BaseCustomerMappingStrategy {

    @Override
    public CustomerEntity toEntity(Customer customer) {
        BusinessCustomer businessCustomer = (BusinessCustomer) customer;
        CustomerEntity entity = new CustomerEntity();
        mapCommonFields(customer, entity);
        entity.setType(Customer.TypeEnum.BUSINESS);
        entity.setRuc(businessCustomer.getRuc());
        return entity;
    }

    @Override
    public Customer toDomain(CustomerEntity entity) {
        BusinessCustomer customer = new BusinessCustomer();
        mapCommonFields(entity, customer);
        customer.setRuc(entity.getRuc());
        customer.setType(Customer.TypeEnum.BUSINESS);
        return customer;
    }

    @Override
    public boolean supports(Customer.TypeEnum type) {
        return Customer.TypeEnum.BUSINESS.equals(type);
    }
}
