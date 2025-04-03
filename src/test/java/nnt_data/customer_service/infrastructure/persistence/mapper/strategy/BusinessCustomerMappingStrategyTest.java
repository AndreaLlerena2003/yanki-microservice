package nnt_data.customer_service.infrastructure.persistence.mapper.strategy;

import nnt_data.customer_service.entity.BusinessCustomer;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BusinessCustomerMappingStrategyTest {

    @InjectMocks
    private BusinessCustomerMappingStrategy strategy;

    private BusinessCustomer businessCustomer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        businessCustomer = new BusinessCustomer();
        businessCustomer.setId("B123");
        businessCustomer.setName("Empresa XYZ");
        businessCustomer.setEmail("contacto@empresa-xyz.com");
        businessCustomer.setPhone("555-BUSINESS");
        businessCustomer.setAddress("Av. Empresarial 789");
        businessCustomer.setSubtype(CustomerSubtype.REGULAR);
        businessCustomer.setRuc("20123456789");
        businessCustomer.setType(Customer.TypeEnum.BUSINESS);

        customerEntity = new CustomerEntity();
        customerEntity.setId("B456");
        customerEntity.setName("Corporación ABC");
        customerEntity.setEmail("info@corporacion-abc.com");
        customerEntity.setPhone("555-CORP");
        customerEntity.setAddress("Calle Corporativa 123");
        customerEntity.setSubtype(CustomerSubtype.REGULAR);
        customerEntity.setRuc("20987654321");
        customerEntity.setType(Customer.TypeEnum.BUSINESS);
    }

    @Test
    void toEntity_shouldMapBusinessCustomerToEntity() {
        CustomerEntity result = strategy.toEntity(businessCustomer);
        assertEquals(businessCustomer.getId(), result.getId());
        assertEquals(businessCustomer.getName(), result.getName());
        assertEquals(businessCustomer.getEmail(), result.getEmail());
        assertEquals(businessCustomer.getPhone(), result.getPhone());
        assertEquals(businessCustomer.getAddress(), result.getAddress());
        assertEquals(businessCustomer.getSubtype(), result.getSubtype());
        assertEquals(Customer.TypeEnum.BUSINESS, result.getType());
        assertEquals(businessCustomer.getRuc(), result.getRuc());
    }

    @Test
    void toDomain_shouldMapEntityToBusinessCustomer() {
        Customer result = strategy.toDomain(customerEntity);
        assertTrue(result instanceof BusinessCustomer);
        BusinessCustomer businessResult = (BusinessCustomer) result;
        assertEquals(customerEntity.getId(), businessResult.getId());
        assertEquals(customerEntity.getName(), businessResult.getName());
        assertEquals(customerEntity.getEmail(), businessResult.getEmail());
        assertEquals(customerEntity.getPhone(), businessResult.getPhone());
        assertEquals(customerEntity.getAddress(), businessResult.getAddress());
        assertEquals(customerEntity.getSubtype(), businessResult.getSubtype());
        assertEquals(Customer.TypeEnum.BUSINESS, businessResult.getType());
        assertEquals(customerEntity.getRuc(), businessResult.getRuc());
    }

    @Test
    void supports_withBusinessType_shouldReturnTrue() {
        boolean result = strategy.supports(Customer.TypeEnum.BUSINESS);
        assertTrue(result);
    }

    @Test
    void supports_withNonBusinessType_shouldReturnFalse() {
        boolean result = strategy.supports(Customer.TypeEnum.PERSONAL);
        assertFalse(result);
    }

    @Test
    void toEntity_withNullRuc_shouldHandleGracefully() {
        businessCustomer.setRuc(null);
        CustomerEntity result = strategy.toEntity(businessCustomer);
        assertNull(result.getRuc());
        assertEquals(businessCustomer.getId(), result.getId());
    }

    @Test
    void toDomain_withNullRuc_shouldHandleGracefully() {
        customerEntity.setRuc(null);
        BusinessCustomer result = (BusinessCustomer) strategy.toDomain(customerEntity);
        assertNull(result.getRuc());
        assertEquals(customerEntity.getId(), result.getId());
    }

    @Test
    void toEntity_withNonBusinessCustomer_shouldThrowClassCastException() {
        Customer regularCustomer = new Customer();
        regularCustomer.setType(Customer.TypeEnum.PERSONAL);
        assertThrows(ClassCastException.class, () -> {
            strategy.toEntity(regularCustomer);
        });
    }

    @Test
    void bidirectionalMapping_shouldPreserveAllData() {
        CustomerEntity entity = strategy.toEntity(businessCustomer);
        BusinessCustomer mappedBack = (BusinessCustomer) strategy.toDomain(entity);
        assertEquals(businessCustomer.getId(), mappedBack.getId());
        assertEquals(businessCustomer.getName(), mappedBack.getName());
        assertEquals(businessCustomer.getEmail(), mappedBack.getEmail());
        assertEquals(businessCustomer.getPhone(), mappedBack.getPhone());
        assertEquals(businessCustomer.getAddress(), mappedBack.getAddress());
        assertEquals(businessCustomer.getSubtype(), mappedBack.getSubtype());
        assertEquals(businessCustomer.getType(), mappedBack.getType());
        assertEquals(businessCustomer.getRuc(), mappedBack.getRuc());
    }
}