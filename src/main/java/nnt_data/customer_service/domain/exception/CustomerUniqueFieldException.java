package nnt_data.customer_service.domain.exception;

public class CustomerUniqueFieldException extends RuntimeException {
    public CustomerUniqueFieldException(String message) {
        super(message);
    }
}
