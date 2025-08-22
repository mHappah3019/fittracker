package ingsoftware.exception;

/**
 * Eccezione per errori durante il completamento di abitudini
 */
public class HabitCompletionException extends BusinessException {
    public HabitCompletionException(String message) {
        super(message);
    }
}