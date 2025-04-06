package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import lombok.AllArgsConstructor;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
/**
 * Estrategia base para el mapeo de campos comunes entre `Customer` y `CustomerEntity`.
 */

@AllArgsConstructor
public abstract class BaseCustomerMappingStrategy<T extends Customer> implements CustomerMappingStrategy {

    private final Customer.TypeEnum supportedType;
    private final Class<T> customerClass;

    @Override
    public CustomerEntity toEntity(Customer customer) {
        T typedCustomer = customerClass.cast(customer);
        CustomerEntity entity = new CustomerEntity();
        mapCommonFields(customer, entity);
        entity.setType(supportedType);
        mapSpecificFields(typedCustomer, entity);
        return entity;
    }

    @Override
    public Customer toDomain(CustomerEntity entity) {
        T customer;
        try {
            customer = customerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear una instancia de " + customerClass.getSimpleName(), e);
        }
        mapCommonFields(entity, customer);
        customer.setType(supportedType);
        mapSpecificFields(entity, customer);
        return customer;
    }

    @Override
    public boolean supports(Customer.TypeEnum type) {
        return supportedType.equals(type);
    }

    protected void mapCommonFields(Customer source, CustomerEntity target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
        target.setSubtype(source.getSubtype());
    }

    protected void mapCommonFields(CustomerEntity source, Customer target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
        target.setSubtype(source.getSubtype());
    }

    protected abstract void mapSpecificFields(T source, CustomerEntity target);
    protected abstract void mapSpecificFields(CustomerEntity source, T target);
}