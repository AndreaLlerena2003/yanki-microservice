package nnt_data.yanki_service.infrastructure.persistence.entity;

import lombok.Data;
import nnt_data.yanki_service.entity.TransactionYanki;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document("transaction_yanki")
public class TransactionYankiEntity {
    @Id
    private String id;
    private String telefonoOrigen;
    private String telefonoDestino;
    private BigDecimal monto;
    private TransactionYanki.TypeEnum type;
}
