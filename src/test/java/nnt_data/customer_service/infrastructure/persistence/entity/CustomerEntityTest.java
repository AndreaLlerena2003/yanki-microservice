package nnt_data.customer_service.infrastructure.persistence.entity;


import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerEntityTest {

    @Test
    void testCustomerEntityGettersAndSetters() {
        CustomerEntity customerEntity = new CustomerEntity();
        String id = "12345";
        String name = "Test Customer";
        String email = "test@example.com";
        String phone = "123456789";
        String address = "Test Address";
        Customer.TypeEnum type = Customer.TypeEnum.PERSONAL;
        CustomerSubtype subtype = CustomerSubtype.VIP;
        String dni = "87654321";
        String ruc = "10876543210";

        customerEntity.setId(id);
        customerEntity.setName(name);
        customerEntity.setEmail(email);
        customerEntity.setPhone(phone);
        customerEntity.setAddress(address);
        customerEntity.setType(type);
        customerEntity.setSubtype(subtype);
        customerEntity.setDni(dni);
        customerEntity.setRuc(ruc);


        assertEquals(id, customerEntity.getId());
        assertEquals(name, customerEntity.getName());
        assertEquals(email, customerEntity.getEmail());
        assertEquals(phone, customerEntity.getPhone());
        assertEquals(address, customerEntity.getAddress());
        assertEquals(type, customerEntity.getType());
        assertEquals(subtype, customerEntity.getSubtype());
        assertEquals(dni, customerEntity.getDni());
        assertEquals(ruc, customerEntity.getRuc());
    }

    @Test
    void testEqualsAndHashCode() {

        CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setId("12345");
        customerEntity1.setEmail("test@example.com");

        CustomerEntity customerEntity2 = new CustomerEntity();
        customerEntity2.setId("12345");
        customerEntity2.setEmail("test@example.com");

        CustomerEntity customerEntity3 = new CustomerEntity();
        customerEntity3.setId("54321");
        customerEntity3.setEmail("different@example.com");

        assertEquals(customerEntity1, customerEntity2);
        assertEquals(customerEntity1.hashCode(), customerEntity2.hashCode());

        assertNotEquals(customerEntity1, customerEntity3);
        assertNotEquals(customerEntity1.hashCode(), customerEntity3.hashCode());
    }

    @Test
    void testToString() {

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId("12345");
        customerEntity.setName("Test Customer");
        customerEntity.setEmail("test@example.com");


        String toString = customerEntity.toString();

        assertTrue(toString.contains("12345"));
        assertTrue(toString.contains("Test Customer"));
        assertTrue(toString.contains("test@example.com"));
    }

}
