package nnt_data.customer_service.domain.exception;

public class CustomerMappingException extends RuntimeException {
    public CustomerMappingException(String message) {
        super(message);
    }

    public CustomerMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
/**
 * Excepción personalizada para errores de mapeo de clientes.
 * Extiende RuntimeException y proporciona constructores para
 * mensajes de error y causas subyacentes.
 */