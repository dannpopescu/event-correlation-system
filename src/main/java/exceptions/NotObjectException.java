package exceptions;

public class NotObjectException extends RuntimeException {

    public NotObjectException() {
    }

    public NotObjectException(String message) {
        super(message);
    }
}
