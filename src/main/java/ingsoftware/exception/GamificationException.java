package ingsoftware.exception;

/**
 * Eccezione per errori nel sistema di gamification (calcolo XP, gestione livelli, punti vita)
 */
public class GamificationException extends BusinessException {
    public GamificationException(String message) {
        super(message);
    }
}