package nnt_data.yanki_service.infrastructure.persistence.mapper;

import nnt_data.yanki_service.entity.TransactionYanki;
import nnt_data.yanki_service.entity.UserYanki;
import nnt_data.yanki_service.infrastructure.persistence.entity.TransactionYankiEntity;
import nnt_data.yanki_service.infrastructure.persistence.entity.UserYankiEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionYankiMapper implements MapperInterface<TransactionYanki, TransactionYankiEntity> {

    /**
     * Convierte una entidad de dominio a una entidad de persistencia.
     *
     * @param domain Objeto de dominio a convertir
     * @return Mono con la entidad de persistencia resultante
     */
    @Override
    public Mono<TransactionYankiEntity> toEntity(TransactionYanki domain) {
        TransactionYankiEntity entity = new TransactionYankiEntity();
        BeanUtils.copyProperties(domain, entity);
        return Mono.just(entity);
    }

    /**
     * Convierte una entidad de persistencia a una entidad de dominio.
     *
     * @param entity Entidad de persistencia a convertir
     * @return Mono con el objeto de dominio resultante
     */
    @Override
    public Mono<TransactionYanki> toDomain(TransactionYankiEntity entity) {
        TransactionYanki domain = new TransactionYanki();
        BeanUtils.copyProperties(entity, domain);
        return Mono.just(domain);
    }
}
