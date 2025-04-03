package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseCustomerMappingStrategyTest {

    private static class TestCustomerMappingStrategy extends BaseCustomerMappingStrategy {

        @Override
        public CustomerEntity toEntity(Customer customer) {
            CustomerEntity entity = new CustomerEntity();
            mapCommonFields(customer, entity);
            return entity;
        }

        @Override
        public Customer toDomain(CustomerEntity entity) {
            Customer customer = new Customer();
            mapCommonFields(entity, customer);
            return customer;
        }

        @Override
        public boolean supports(Customer.TypeEnum type) {
            return true;
        }
    }


    private BaseCustomerMappingStrategy strategy;
    private Customer customer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        strategy = new TestCustomerMappingStrategy();
        customer = new Customer();
        customer.setId("123");
        customer.setName("Juan Pérez");
        customer.setEmail("juan@example.com");
        customer.setPhone("555-123-4567");
        customer.setAddress("Calle Principal 123");
        customer.setSubtype(CustomerSubtype.REGULAR);
        customerEntity = new CustomerEntity();
        customerEntity.setId("456");
        customerEntity.setName("María García");
        customerEntity.setEmail("maria@example.com");
        customerEntity.setPhone("555-987-6543");
        customerEntity.setAddress("Avenida Central 456");
        customerEntity.setSubtype(CustomerSubtype.PYME);
    }

    @Test
    void mapCommonFields_fromCustomerToEntity_shouldMapAllFields() {
        CustomerEntity target = new CustomerEntity();
        ((TestCustomerMappingStrategy) strategy).toEntity(customer);
        CustomerEntity entity = ((TestCustomerMappingStrategy) strategy).toEntity(customer);
        assertEquals(customer.getId(), entity.getId());
        assertEquals(customer.getName(), entity.getName());
        assertEquals(customer.getEmail(), entity.getEmail());
        assertEquals(customer.getPhone(), entity.getPhone());
        assertEquals(customer.getAddress(), entity.getAddress());
        assertEquals(customer.getSubtype(), entity.getSubtype());
    }

    @Test
    void mapCommonFields_fromEntityToCustomer_shouldMapAllFields() {
        Customer domain = ((TestCustomerMappingStrategy) strategy).toDomain(customerEntity);
        assertEquals(customerEntity.getId(), domain.getId());
        assertEquals(customerEntity.getName(), domain.getName());
        assertEquals(customerEntity.getEmail(), domain.getEmail());
        assertEquals(customerEntity.getPhone(), domain.getPhone());
        assertEquals(customerEntity.getAddress(), domain.getAddress());
        assertEquals(customerEntity.getSubtype(), domain.getSubtype());
    }

    @Test
    void mapCommonFields_withNullValues_shouldHandleGracefully() {
        // Configurar cliente con algunos valores nulos
        Customer nullCustomer = new Customer();
        nullCustomer.setId("789");
        nullCustomer.setName("Pedro Nulo");
        nullCustomer.setPhone("555-null");
        nullCustomer.setSubtype(null);
        CustomerEntity entity = ((TestCustomerMappingStrategy) strategy).toEntity(nullCustomer);
        assertEquals(nullCustomer.getId(), entity.getId());
        assertEquals(nullCustomer.getName(), entity.getName());
        assertEquals(null, entity.getEmail());
        assertEquals(nullCustomer.getPhone(), entity.getPhone());
        assertEquals(null, entity.getAddress());
        assertEquals(null, entity.getSubtype());
    }

    @Test
    void mapCommonFields_bidirectional_shouldPreserveData() {
        CustomerEntity entity = ((TestCustomerMappingStrategy) strategy).toEntity(customer);
        Customer mappedBack = ((TestCustomerMappingStrategy) strategy).toDomain(entity);
        assertEquals(customer.getId(), mappedBack.getId());
        assertEquals(customer.getName(), mappedBack.getName());
        assertEquals(customer.getEmail(), mappedBack.getEmail());
        assertEquals(customer.getPhone(), mappedBack.getPhone());
        assertEquals(customer.getAddress(), mappedBack.getAddress());
        assertEquals(customer.getSubtype(), mappedBack.getSubtype());
    }

}
