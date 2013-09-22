package base;

/**
 * General UnityDrive exception
 *
 * @author ntpeters
 */
public class UDException extends Exception {

    private static final long serialVersionUID = -1625163500602002773L;

    public UDException(String message) {
        super(message);
    }

    public UDException(Throwable cause) {
        super(cause);
    }

    public UDException(String message, Throwable cause) {
        super(message, cause);
    }
}