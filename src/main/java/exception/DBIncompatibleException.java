package exception;

/**
 * This exception indicates that a DB was used that is incompatible with the expected DB model.
 * 
 * @author Satia
 */
@SuppressWarnings("serial")
public class DBIncompatibleException extends Exception {

	public DBIncompatibleException() {
	}

	public DBIncompatibleException(String message) {
		super(message);
	}

	public DBIncompatibleException(Throwable cause) {
		super(cause);
	}

	public DBIncompatibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBIncompatibleException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
