package nnt_data.yanki_service.infrastructure.persistence.repository;

import nnt_data.yanki_service.infrastructure.persistence.entity.UserYankiEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserYankiRepository extends ReactiveMongoRepository<UserYankiEntity, String> {
    Mono<UserYankiEntity> findByTelefono(String telefono);
}
