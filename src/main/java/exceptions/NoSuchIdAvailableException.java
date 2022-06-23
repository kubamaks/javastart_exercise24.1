package exceptions;

public class NoSuchIdAvailableException extends RuntimeException {
    public NoSuchIdAvailableException(String message) {
        super(message);
    }
}
