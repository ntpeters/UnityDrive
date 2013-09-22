package base;

/**
 * Exception to be thrown upon failure to authenticate
 *
 * @author ntpeters
 */
public class UDAuthenticationException extends UDException {

    private static final long serialVersionUID = -7825819251545125340L;

    public UDAuthenticationException(String message) {
        super(message);
    }

    public UDAuthenticationException(Throwable cause) {
        super(cause);
    }

    public UDAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}