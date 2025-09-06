package ingsoftware.exception;

/**
 * Eccezione per errori di database (connessione, query, transazioni)
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}