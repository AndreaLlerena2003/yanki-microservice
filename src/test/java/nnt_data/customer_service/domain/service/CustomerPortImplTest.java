package nnt_data.customer_service.domain.service;

import nnt_data.customer_service.domain.exception.CustomerNotFoundException;
import nnt_data.customer_service.domain.validation.CustomerValidator;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.infrastructure.persistence.mapper.CustomerMapper;
import nnt_data.customer_service.infrastructure.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerPortImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CustomerValidator customerValidator;
    @InjectMocks
    private CustomerPortImpl customerPort;
    private CustomerEntity customerEntity;
    private Customer customer;
    private final String CUSTOMER_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        customerEntity = new CustomerEntity();
        customerEntity.setId(CUSTOMER_ID);
        customerEntity.setName("Juan Perez");
        customerEntity.setEmail("juan.perez@example.com");
        customerEntity.setPhone("555-123-4567");
        customerEntity.setAddress("Calle Principal 123, Ciudad");
        customerEntity.setType(Customer.TypeEnum.PERSONAL);
        customerEntity.setSubtype(CustomerSubtype.REGULAR);

        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName("Juan Perez");
        customer.setEmail("juan.perez@example.com");
        customer.setPhone("555-123-4567");
        customer.setAddress("Calle Principal 123, Ciudad");
        customer.setType(Customer.TypeEnum.PERSONAL);
        customer.setSubtype(CustomerSubtype.REGULAR);

    }

    @Test
    @DisplayName("Debe crear un cliente cuandos los datos son válidos")
    void createCustomer_ValidData_ShouldCreateCustomer() {
        // Arrange
        when(customerMapper.toDomain(any(CustomerEntity.class))).thenReturn(Mono.just(customer));
        when(customerValidator.ensureUniqueFields(any())).thenReturn(Mono.empty());
        when(customerValidator.validateSubtype(any())).thenReturn(Mono.empty());
        when(customerRepository.insert(any(CustomerEntity.class))).thenReturn(Mono.just(customerEntity));
        // Act
        Mono<Customer> result = customerPort.createCustomer(customerEntity);
        //Assert
        StepVerifier.create(result)
                .expectNextMatches(c ->
                        c.getId().equals(CUSTOMER_ID) &&
                                c.getName().equals("Juan Perez") &&
                                c.getEmail().equals("juan.perez@example.com") &&
                                c.getType().equals(Customer.TypeEnum.PERSONAL) &&
                                c.getSubtype().equals(CustomerSubtype.REGULAR)
                )
                .verifyComplete();

        verify(customerMapper, times(2)).toDomain(any(CustomerEntity.class));
        verify(customerValidator).ensureUniqueFields(any());
        verify(customerValidator).validateSubtype(any());
        verify(customerRepository).insert(any(CustomerEntity.class));

    }

    @Test
    @DisplayName("Debe actualizar un cliente cuando existe")
    void updateCustomer_ExistingCustomer_ShouldUpdateCustomer() {
        //Arrange
        CustomerEntity updatedEntity = new CustomerEntity();
        updatedEntity.setId(CUSTOMER_ID);
        updatedEntity.setName("Juan Pérez Actualizado");
        updatedEntity.setEmail("juan.perez.updated@example.com");
        updatedEntity.setPhone("555-987-6543");
        updatedEntity.setAddress("Avenida Nueva 456, Ciudad");
        updatedEntity.setType(Customer.TypeEnum.PERSONAL);
        updatedEntity.setSubtype(CustomerSubtype.REGULAR);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(CUSTOMER_ID);
        updatedCustomer.setName("Juan Pérez Actualizado");
        updatedCustomer.setEmail("juan.perez.updated@example.com");
        updatedCustomer.setPhone("555-987-6543");
        updatedCustomer.setAddress("Avenida Nueva 456, Ciudad");
        updatedCustomer.setType(Customer.TypeEnum.PERSONAL);
        updatedCustomer.setSubtype(CustomerSubtype.REGULAR);

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customerEntity));
        when(customerValidator.validateSubtype(any())).thenReturn(Mono.empty());
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(Mono.just(updatedEntity));
        when(customerMapper.toDomain(any(CustomerEntity.class))).thenReturn(Mono.just(updatedCustomer));

        // Act
        Mono<Customer> result = customerPort.updateCustomer(CUSTOMER_ID, updatedEntity);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(c ->
                        c.getId().equals(CUSTOMER_ID) &&
                                c.getName().equals("Juan Pérez Actualizado") &&
                                c.getEmail().equals("juan.perez.updated@example.com") &&
                                c.getType().equals(Customer.TypeEnum.PERSONAL) &&
                                c.getSubtype().equals(CustomerSubtype.REGULAR)
                )
                .verifyComplete();

        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerMapper, times(2)).toDomain(any(CustomerEntity.class));
        verify(customerValidator).validateSubtype(any());
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Debe retornar un cliente cuando existe el ID")
    void getCustomerById_ExistingId_ShouldReturnCustomer() {
        // Arrange
        // Asegúrate que el objeto customer tenga exactamente estos valores
        customer.setId(CUSTOMER_ID);
        customer.setName("Juan Pérez");
        customer.setEmail("juan.perez@example.com");

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customerEntity));
        when(customerMapper.toDomain(customerEntity)).thenReturn(Mono.just(customer));

        // Act
        Mono<Customer> result = customerPort.getCustomerById(CUSTOMER_ID);

        // Assert
        StepVerifier.create(result)
                .expectNext(customer) // Usa directamente el objeto customer en lugar de la comparación de campos
                .verifyComplete();

        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    @DisplayName("Debe lanzar CustomerNotFoundException cuando no existe el ID")
    void getCustomerById_NonExistingId_ShouldThrowException() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(customerRepository.findById(nonExistingId)).thenReturn(Mono.empty());

        // Act
        Mono<Customer> result = customerPort.getCustomerById(nonExistingId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof CustomerNotFoundException &&
                                throwable.getMessage().equals("Customer not found")
                )
                .verify();

        verify(customerRepository).findById(nonExistingId);
        verify(customerMapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Debe eliminar un cliente cuando existe el ID")
    void deleteCustomerById_ExistingId_ShouldDeleteCustomer() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customerEntity));
        when(customerRepository.deleteById(CUSTOMER_ID)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = customerPort.deleteCustomerById(CUSTOMER_ID);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerRepository).deleteById(CUSTOMER_ID);
    }

    @Test
    @DisplayName("Debe lanzar CustomerNotFoundException al intentar eliminar un cliente que no existe")
    void deleteCustomerById_NonExistingId_ShouldThrowException() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(customerRepository.findById(nonExistingId)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = customerPort.deleteCustomerById(nonExistingId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof CustomerNotFoundException &&
                                throwable.getMessage().equals("Customer not found")
                )
                .verify();

        verify(customerRepository).findById(nonExistingId);
        verify(customerRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Debe retornar todos los clientes")
    void findAll_ShouldReturnAllCustomers() {
        // Arrange
        CustomerEntity customerEntity2 = new CustomerEntity();
        customerEntity2.setId("customer-id-2");
        customerEntity2.setName("María López");
        customerEntity2.setEmail("maria.lopez@example.com");
        customerEntity2.setPhone("555-123-7890");
        customerEntity2.setAddress("Calle Secundaria 789, Ciudad");
        customerEntity2.setType(Customer.TypeEnum.BUSINESS);
        customerEntity2.setSubtype(CustomerSubtype.REGULAR);

        Customer customer2 = new Customer();
        customer2.setId("customer-id-2");
        customer2.setName("María López");
        customer2.setEmail("maria.lopez@example.com");
        customer2.setPhone("555-123-7890");
        customer2.setAddress("Calle Secundaria 789, Ciudad");
        customer2.setType(Customer.TypeEnum.BUSINESS);
        customer2.setSubtype(CustomerSubtype.REGULAR);

        when(customerRepository.findAll()).thenReturn(Flux.just(customerEntity, customerEntity2));
        when(customerMapper.toDomain(customerEntity)).thenReturn(Mono.just(customer));
        when(customerMapper.toDomain(customerEntity2)).thenReturn(Mono.just(customer2));

        // Act
        Flux<Customer> result = customerPort.findAll();

        // Assert
        StepVerifier.create(result)
                .expectNext(customer)
                .expectNext(customer2)
                .verifyComplete();

        verify(customerRepository).findAll();
        verify(customerMapper).toDomain(customerEntity);
        verify(customerMapper).toDomain(customerEntity2);
    }

    @Test
    @DisplayName("Debe retornar un flujo vacío cuando no hay clientes")
    void findAll_NoCustomers_ShouldReturnEmptyFlux() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Flux.empty());

        // Act
        Flux<Customer> result = customerPort.findAll();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository).findAll();
        verify(customerMapper, never()).toDomain(any(CustomerEntity.class));
    }

}
