package dev.petproject.exception;

public class UserCanNotBeDeletedException extends RuntimeException {
    public UserCanNotBeDeletedException(String message) {
        super(message);
    }
}
