package ingsoftware.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaSystemException;

import java.sql.SQLException;

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