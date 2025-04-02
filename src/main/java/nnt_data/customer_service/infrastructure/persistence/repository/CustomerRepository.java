package nnt_data.customer_service.infrastructure.persistence.repository;

import nnt_data.customer_service.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
/**
 * Repositorio reactivo para la entidad `CustomerEntity` en MongoDB.
 * Define métodos para verificar la existencia de campos únicos y buscar por ID.
 */
public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDni(String dni);
    Mono<Boolean> existsByRuc(String ruc);
    Mono<CustomerEntity> findById(String id);
}