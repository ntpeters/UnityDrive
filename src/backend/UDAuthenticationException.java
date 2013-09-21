package backend;

/**
 * Exception to be thrown upon failure to authenticate
 */
public class UDAuthenticationException extends UDException {

    private static final long serialVersionUID = -7825819251545125340L;

    public UDAuthenticationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public UDAuthenticationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public UDAuthenticationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}