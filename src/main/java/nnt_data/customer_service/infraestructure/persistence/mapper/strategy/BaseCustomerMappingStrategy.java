package nnt_data.customer_service.infraestructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;

public abstract  class BaseCustomerMappingStrategy implements CustomerMappingStrategy {
    protected void mapCommonFields(Customer source, CustomerEntity target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
    }

    protected void mapCommonFields(CustomerEntity source, Customer target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
    }
}
