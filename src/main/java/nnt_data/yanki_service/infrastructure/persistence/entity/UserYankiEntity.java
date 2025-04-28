package nnt_data.yanki_service.infrastructure.persistence.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import nnt_data.yanki_service.entity.UserYanki;

@Data
@Document("user_yanki")
public class UserYankiEntity {
    @Id
    private String id;
    private UserYanki.TipoDocumentoEnum tipoDocumento;
    private String pin;
    @Indexed(unique = true)
    private String numeroDocumento;
    private String telefono;
    private String imei;
    private String correo;
    private String tarjetaAsociada;
}
