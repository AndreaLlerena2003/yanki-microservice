package nnt_data.customer_service.domain.exception;

public class CustomerUniqueFieldException extends RuntimeException {
    public CustomerUniqueFieldException(String message) {
        super(message);
    }
}
/**
 * Excepción personalizada para indicar que un campo único de un cliente ya existe.
 * Extiende RuntimeException y proporciona un constructor para mensajes de error.
 */