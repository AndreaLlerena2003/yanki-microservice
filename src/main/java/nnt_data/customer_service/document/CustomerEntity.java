package nnt_data.customer_service.document;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nnt_data.customer_service.model.Customer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collection = "costumer")
public class CustomerEntity {
    @Id
    private String id;
    private String name;
    private String lastname;
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
