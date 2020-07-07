package exceptions;

public class KeyNotFoundException extends RuntimeException {

    public KeyNotFoundException() {
    }

    public KeyNotFoundException(String message) {
        super(message);
    }
}
