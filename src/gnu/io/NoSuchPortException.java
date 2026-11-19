package gnu.io;

/**
 * Compatibility exception used during the migration away from RXTX.
 */
public class NoSuchPortException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuchPortException() {
        super();
    }

    public NoSuchPortException(String message) {
        super(message);
    }
}
