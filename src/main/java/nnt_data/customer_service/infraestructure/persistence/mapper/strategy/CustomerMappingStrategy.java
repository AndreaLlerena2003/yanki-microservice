package nnt_data.customer_service.infraestructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;

public interface CustomerMappingStrategy {
    CustomerEntity toEntity(Customer customer);
    Customer toDomain(CustomerEntity entity);
    boolean supports(Customer.TypeEnum type);
}
