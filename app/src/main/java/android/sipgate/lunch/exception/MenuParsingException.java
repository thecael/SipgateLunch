package android.sipgate.lunch.exception;

/**
 * @author schafm
 */
public class MenuParsingException extends Exception {

    public MenuParsingException(Throwable cause) {
        super(cause);
    }

    public MenuParsingException(String message) {
        super(message);
    }
}
