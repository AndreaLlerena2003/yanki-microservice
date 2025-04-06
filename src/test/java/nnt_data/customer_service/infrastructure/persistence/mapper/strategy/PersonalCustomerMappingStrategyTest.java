package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.entity.PersonalCustomer;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class PersonalCustomerMappingStrategyTest {

    @InjectMocks
    private PersonalCustomerMappingStrategy strategy;

    private PersonalCustomer personalCustomer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        personalCustomer = new PersonalCustomer();
        personalCustomer.setId("P123");
        personalCustomer.setName("Ana García");
        personalCustomer.setEmail("ana.garcia@example.com");
        personalCustomer.setPhone("555-1234-567");
        personalCustomer.setAddress("Calle Residencial 123");
        personalCustomer.setSubtype(CustomerSubtype.REGULAR);
        personalCustomer.setDni("12345678");
        personalCustomer.setType(Customer.TypeEnum.PERSONAL);

        customerEntity = new CustomerEntity();
        customerEntity.setId("P456");
        customerEntity.setName("Juan Pérez");
        customerEntity.setEmail("juan.perez@example.com");
        customerEntity.setPhone("555-9876-543");
        customerEntity.setAddress("Av. Residencial 456");
        customerEntity.setSubtype(CustomerSubtype.VIP);
        customerEntity.setDni("87654321");
        customerEntity.setType(Customer.TypeEnum.PERSONAL);
    }

    @Test
    void toEntity_shouldMapPersonalCustomerToEntity() {
        CustomerEntity result = strategy.toEntity(personalCustomer);
        assertEquals(personalCustomer.getId(), result.getId());
        assertEquals(personalCustomer.getName(), result.getName());
        assertEquals(personalCustomer.getEmail(), result.getEmail());
        assertEquals(personalCustomer.getPhone(), result.getPhone());
        assertEquals(personalCustomer.getAddress(), result.getAddress());
        assertEquals(personalCustomer.getSubtype(), result.getSubtype());
        assertEquals(Customer.TypeEnum.PERSONAL, result.getType());
        assertEquals(personalCustomer.getDni(), result.getDni());
    }

    @Test
    void toDomain_shouldMapEntityToPersonalCustomer() {
        Customer result = strategy.toDomain(customerEntity);
        assertTrue(result instanceof PersonalCustomer);
        PersonalCustomer personalResult = (PersonalCustomer) result;
        assertEquals(customerEntity.getId(), personalResult.getId());
        assertEquals(customerEntity.getName(), personalResult.getName());
        assertEquals(customerEntity.getEmail(), personalResult.getEmail());
        assertEquals(customerEntity.getPhone(), personalResult.getPhone());
        assertEquals(customerEntity.getAddress(), personalResult.getAddress());
        assertEquals(customerEntity.getSubtype(), personalResult.getSubtype());
        assertEquals(Customer.TypeEnum.PERSONAL, personalResult.getType());
        assertEquals(customerEntity.getDni(), personalResult.getDni());
    }

    @Test
    void supports_withPersonalType_shouldReturnTrue() {
        boolean result = strategy.supports(Customer.TypeEnum.PERSONAL);
        assertTrue(result);
    }

    @Test
    void supports_withNonPersonalType_shouldReturnFalse() {
        boolean result = strategy.supports(Customer.TypeEnum.BUSINESS);
        assertFalse(result);
    }

    @Test
    void toEntity_withNullDni_shouldHandleGracefully() {
        personalCustomer.setDni(null);
        CustomerEntity result = strategy.toEntity(personalCustomer);
        assertNull(result.getDni());
        assertEquals(personalCustomer.getId(), result.getId());
    }

    @Test
    void toDomain_withNullDni_shouldHandleGracefully() {
        customerEntity.setDni(null);
        PersonalCustomer result = (PersonalCustomer) strategy.toDomain(customerEntity);
        assertNull(result.getDni());
        assertEquals(customerEntity.getId(), result.getId());
    }

    @Test
    void toEntity_withNonPersonalCustomer_shouldThrowClassCastException() {
        Customer businessCustomer = new Customer();
        businessCustomer.setType(Customer.TypeEnum.BUSINESS);
        assertThrows(ClassCastException.class,
                () -> strategy.toEntity(businessCustomer));
    }

    @Test
    void bidirectionalMapping_shouldPreserveAllData() {
        CustomerEntity entity = strategy.toEntity(personalCustomer);
        PersonalCustomer mappedBack = (PersonalCustomer) strategy.toDomain(entity);
        assertEquals(personalCustomer.getId(), mappedBack.getId());
        assertEquals(personalCustomer.getName(), mappedBack.getName());
        assertEquals(personalCustomer.getEmail(), mappedBack.getEmail());
        assertEquals(personalCustomer.getPhone(), mappedBack.getPhone());
        assertEquals(personalCustomer.getAddress(), mappedBack.getAddress());
        assertEquals(personalCustomer.getSubtype(), mappedBack.getSubtype());
        assertEquals(personalCustomer.getType(), mappedBack.getType());
        assertEquals(personalCustomer.getDni(), mappedBack.getDni());
    }
}
