package exceptions;

public class NoResultsInResultSetException extends RuntimeException {
    public NoResultsInResultSetException(String message) {
        super(message);
    }
}
