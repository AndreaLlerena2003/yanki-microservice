package nnt_data.customer_service.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    @Indexed(unique = true)
    private String dni;
    @Indexed(unique = true)
    private String ruc;

}
