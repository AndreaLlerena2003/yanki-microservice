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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CustomerPortImpl.class);

    @Override
    public Mono<Customer> createCustomer(CustomerEntity customerEntity) {
        log.debug("Datos recibidos para crear cliente: {}", customerEntity);
        return customerMapper.toDomain(customerEntity)
                .flatMap(customer -> customerValidator.ensureUniqueFields(Mono.just(customer))
                        .thenReturn(customerEntity)
                )
                .flatMap(customerRepository::insert)
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> updateCustomer(String id, CustomerEntity customerEntity) {
        log.debug("Datos de actualización: {}", customerEntity);
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
        log.info("Buscando cliente por ID: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customerMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteCustomerById(String id) {
        log.info("Iniciando eliminación de cliente con ID: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")))
                .flatMap(customer -> customerRepository.deleteById(id));
    }
}
