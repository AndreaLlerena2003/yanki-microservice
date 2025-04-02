package nnt_data.customer_service.infraestructure.persistence.entity;


import lombok.Data;
import nnt_data.customer_service.entity.Customer;
import nnt_data.customer_service.entity.CustomerSubtype;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * Entidad de persistencia para `Customer`, mapeada a la colección "costumer" en MongoDB.
 * Incluye campos como id, nombre, email, teléfono, dirección, tipo, dni y ruc.
 */
@Data
@Document(collection = "costumer")
public class CustomerEntity {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private String address;
    private Customer.TypeEnum type;
    private CustomerSubtype subtype;
    private String dni;
    private String ruc;

}
