package exceptions;

public class NotArrayException extends RuntimeException {

    public NotArrayException() {
    }

    public NotArrayException(String message) {
        super(message);
    }
}
