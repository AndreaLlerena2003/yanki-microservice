package nnt_data.yanki_service.infrastructure.persistence.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageWrapper<T> {
    private T payload;
    private String correlationId;

    @JsonCreator
    public MessageWrapper(@JsonProperty("payload") T payload,
                          @JsonProperty("correlationId") String correlationId) {
        this.payload = payload;
        this.correlationId = correlationId;
    }
}