package ingsoftware.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long message) {
        super(String.format("User with id %d not found", message));
    }
}
