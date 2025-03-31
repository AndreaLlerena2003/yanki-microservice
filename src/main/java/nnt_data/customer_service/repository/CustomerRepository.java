package nnt_data.customer_service.repository;

import nnt_data.customer_service.document.CustomerEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDni(String dni);
    Mono<Boolean> existsByRuc(String ruc);
    Mono<CustomerEntity> findById(String id);
}