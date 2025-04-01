package nnt_data.customer_service.infraestructure.persistence.mapper;

import nnt_data.customer_service.infraestructure.persistence.entity.CustomerEntity;
import nnt_data.customer_service.domain.exception.CustomerMappingException;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.infraestructure.persistence.mapper.strategy.CustomerMappingStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class CustomerMapperImpl implements CustomerMapper {

    private final Map<Customer.TypeEnum, CustomerMappingStrategy> strategyMap;

    public CustomerMapperImpl(List<CustomerMappingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.supports(Customer.TypeEnum.PERSONAL)
                                ? Customer.TypeEnum.PERSONAL
                                : Customer.TypeEnum.BUSINESS,
                        Function.identity()
                ));
    }

    @Override
    public Mono<CustomerEntity> toEntity(Customer customer) {
        return Mono.justOrEmpty(customer)
                .switchIfEmpty(Mono.error(new CustomerMappingException("Cannot map null customer")))
                .map(c -> strategyMap.get(c.getType()).toEntity(c));
    }

    @Override
    public Mono<Customer> toDomain(CustomerEntity customerEntity) {
        return Mono.justOrEmpty(customerEntity)
                .switchIfEmpty(Mono.error(new CustomerMappingException("Cannot map null customer")))
                .map(entity -> strategyMap.get(entity.getType()).toDomain(entity));
    }

}
