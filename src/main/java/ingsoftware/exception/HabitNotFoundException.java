package ingsoftware.exception;

public class HabitNotFoundException extends BusinessException {
    public HabitNotFoundException(Long habitId) {
        super(String.format("Habit with id %d not found", habitId));
    }
}
