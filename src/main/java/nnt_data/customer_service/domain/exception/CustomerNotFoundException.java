package nnt_data.customer_service.domain.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
/**
 * Excepción personalizada para indicar que un cliente no fue encontrado.
 * Extiende RuntimeException y proporciona un constructor para mensajes de error.
 */