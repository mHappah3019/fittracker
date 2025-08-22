package ingsoftware.exception;

public class EquipmentNotFoundException extends RuntimeException {
    public EquipmentNotFoundException(Long id) {
        super(String.format("Equipment with id %d not found", id));
    }
}
