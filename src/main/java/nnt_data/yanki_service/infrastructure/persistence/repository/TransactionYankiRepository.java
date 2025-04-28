package nnt_data.yanki_service.infrastructure.persistence.repository;

import nnt_data.yanki_service.infrastructure.persistence.entity.TransactionYankiEntity;
import nnt_data.yanki_service.infrastructure.persistence.entity.UserYankiEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionYankiRepository extends ReactiveMongoRepository<TransactionYankiEntity, String> {
}
