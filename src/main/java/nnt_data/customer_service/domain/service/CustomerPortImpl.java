package nnt_data.customer_service.domain.service;

import lombok.RequiredArgsConstructor;
import nnt_data.customer_service.application.port.CustomerPort;
import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.domain.exception.CustomerNotFoundException;
import nnt_data.customer_service.infraestructure.persistence.mapper.CustomerMapper;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.repository.CustomerRepository;
import nnt_data.customer_service.domain.validation.CustomerValidator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
/**
 * Implementación del puerto de servicio para la entidad `Customer`.
 * Utiliza `Mono` de Reactor para manejar las operaciones de manera reactiva.
 *
 * Funcionalidades:
 * - Crear un nuevo cliente: Convierte la entidad a dominio, valida campos únicos y guarda en el repositorio.
 * - Actualizar un cliente existente: Busca el cliente por ID, actualiza los datos y guarda en el repositorio.
 * - Obtener un cliente por ID: Busca el cliente por ID y lo convierte a dominio.
 * - Eliminar un cliente por ID: Busca el cliente por ID y lo elimina del repositorio.
 *
 * Manejo de excepciones:
 * - `CustomerNotFoundException` para indicar que un cliente no fue encontrado.
 */
@Service
@RequiredArgsConstructor
public class CustomerPortImpl implements CustomerPort {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;

    @Override
    public Mono<Customer> createCustomer(CustomerEntity customerEntity) {
        return customerMapper.toDomain(customerEntity)
                .flatMap(customer -> customerValidator.ensureUniqueFields(Mono.just(customer))
                        .thenReturn(customerEntity)
                )
                .flatMap(customerRepository::insert)
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(existingCustomer -> {
                    customerEntity.setId(id);
                    return customerRepository.save(customerEntity);
                })
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> getCustomerById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteCustomerById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customer -> customerRepository.deleteById(id));
    }
}
