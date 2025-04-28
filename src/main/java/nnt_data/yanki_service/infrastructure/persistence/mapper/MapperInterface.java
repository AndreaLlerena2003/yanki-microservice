package nnt_data.yanki_service.infrastructure.persistence.mapper;

import reactor.core.publisher.Mono;

public interface MapperInterface<D, E> {
    /**
     * Convierte una entidad de dominio a una entidad de persistencia.
     *
     * @param domain Objeto de dominio a convertir
     * @return Mono con la entidad de persistencia resultante
     */
    Mono<E> toEntity(D domain);

    /**
     * Convierte una entidad de persistencia a una entidad de dominio.
     *
     * @param entity Entidad de persistencia a convertir
     * @return Mono con el objeto de dominio resultante
     */
    Mono<D> toDomain(E entity);
}