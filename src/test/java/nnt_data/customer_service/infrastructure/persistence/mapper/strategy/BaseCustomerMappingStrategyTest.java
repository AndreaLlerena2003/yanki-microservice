package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.entity.PersonalCustomer; // Importamos la clase concreta
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BaseCustomerMappingStrategyTest {

    private static class PersonalCustomerMappingStrategy extends BaseCustomerMappingStrategy<PersonalCustomer> {

        public PersonalCustomerMappingStrategy() {
            super(Customer.TypeEnum.PERSONAL, PersonalCustomer.class);
        }

        @Override
        protected void mapSpecificFields(PersonalCustomer source, CustomerEntity target) {
        }

        @Override
        protected void mapSpecificFields(CustomerEntity source, PersonalCustomer target) {
        }
    }

    private BaseCustomerMappingStrategy<PersonalCustomer> strategy;
    private PersonalCustomer customer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        strategy = new PersonalCustomerMappingStrategy();
        customer = new PersonalCustomer();
        customer.setId("123");
        customer.setName("Juan Pérez");
        customer.setEmail("juan@example.com");
        customer.setPhone("555-123-4567");
        customer.setAddress("Calle Principal 123");
        customer.setSubtype(CustomerSubtype.REGULAR);
        customer.setType(Customer.TypeEnum.PERSONAL);


        customerEntity = new CustomerEntity();
        customerEntity.setId("456");
        customerEntity.setName("María García");
        customerEntity.setEmail("maria@example.com");
        customerEntity.setPhone("555-987-6543");
        customerEntity.setAddress("Avenida Central 456");
        customerEntity.setSubtype(CustomerSubtype.PYME);
        customerEntity.setType(Customer.TypeEnum.PERSONAL);
    }

    @Test
    void toEntity_shouldMapAllCommonFields() {
        CustomerEntity entity = strategy.toEntity(customer);

        assertEquals(customer.getId(), entity.getId());
        assertEquals(customer.getName(), entity.getName());
        assertEquals(customer.getEmail(), entity.getEmail());
        assertEquals(customer.getPhone(), entity.getPhone());
        assertEquals(customer.getAddress(), entity.getAddress());
        assertEquals(customer.getSubtype(), entity.getSubtype());
        assertEquals(Customer.TypeEnum.PERSONAL, entity.getType());
    }

    @Test
    void toDomain_shouldMapAllCommonFields() {
        Customer domainObj = strategy.toDomain(customerEntity);
        assertTrue(domainObj instanceof PersonalCustomer);
        assertEquals(customerEntity.getId(), domainObj.getId());
        assertEquals(customerEntity.getName(), domainObj.getName());
        assertEquals(customerEntity.getEmail(), domainObj.getEmail());
        assertEquals(customerEntity.getPhone(), domainObj.getPhone());
        assertEquals(customerEntity.getAddress(), domainObj.getAddress());
        assertEquals(customerEntity.getSubtype(), domainObj.getSubtype());
        assertEquals(Customer.TypeEnum.PERSONAL, domainObj.getType());
    }

    @Test
    void supports_shouldReturnTrueForSupportedType() {
        assertTrue(strategy.supports(Customer.TypeEnum.PERSONAL));
        assertFalse(strategy.supports(Customer.TypeEnum.BUSINESS));
    }

    @Test
    void toEntity_withNullValues_shouldHandleGracefully() {
        PersonalCustomer nullCustomer = new PersonalCustomer();
        nullCustomer.setId("789");
        nullCustomer.setName("Pedro Nulo");
        nullCustomer.setPhone("555-null");
        nullCustomer.setSubtype(null);
        nullCustomer.setType(Customer.TypeEnum.PERSONAL);

        CustomerEntity entity = strategy.toEntity(nullCustomer);

        assertEquals(nullCustomer.getId(), entity.getId());
        assertEquals(nullCustomer.getName(), entity.getName());
        assertNull(entity.getEmail());
        assertEquals(nullCustomer.getPhone(), entity.getPhone());
        assertNull(entity.getAddress());
        assertNull(entity.getSubtype());
        assertEquals(Customer.TypeEnum.PERSONAL, entity.getType());
    }

    @Test
    void bidirectional_shouldPreserveData() {
        CustomerEntity entity = strategy.toEntity(customer);
        Customer mappedBack = strategy.toDomain(entity);
        assertTrue(mappedBack instanceof PersonalCustomer);

        assertEquals(customer.getId(), mappedBack.getId());
        assertEquals(customer.getName(), mappedBack.getName());
        assertEquals(customer.getEmail(), mappedBack.getEmail());
        assertEquals(customer.getPhone(), mappedBack.getPhone());
        assertEquals(customer.getAddress(), mappedBack.getAddress());
        assertEquals(customer.getSubtype(), mappedBack.getSubtype());
        assertEquals(customer.getType(), mappedBack.getType());
    }
}