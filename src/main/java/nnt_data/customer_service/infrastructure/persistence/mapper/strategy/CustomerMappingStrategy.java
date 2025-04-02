package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
/**
 * Interfaz para estrategias de mapeo entre `Customer` y `CustomerEntity`.
 * Define métodos para convertir entre entidades y dominios, y verificar el tipo de cliente.
 */
public interface CustomerMappingStrategy {
    CustomerEntity toEntity(Customer customer);
    Customer toDomain(CustomerEntity entity);
    boolean supports(Customer.TypeEnum type);
}
