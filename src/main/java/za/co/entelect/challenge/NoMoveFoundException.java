package za.co.entelect.challenge;

public class NoMoveFoundException extends Exception {

    public NoMoveFoundException(String message) {
        super(message);
    }

    public NoMoveFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
