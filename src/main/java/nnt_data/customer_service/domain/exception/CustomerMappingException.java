package nnt_data.customer_service.domain.exception;

public class CustomerMappingException extends RuntimeException {
    public CustomerMappingException(String message) {
        super(message);
    }

    public CustomerMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
