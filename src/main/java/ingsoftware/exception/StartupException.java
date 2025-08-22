package ingsoftware.exception;

/**
 * Eccezione per errori durante l'avvio dell'applicazione
 */
public class StartupException extends RuntimeException {
    public StartupException(String message) {
        super(message);
    }
    
    public StartupException(String message, Throwable cause) {
        super(message, cause);
    }
}