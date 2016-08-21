package exception;

/**
 * This exception indicates that the DB was closed while trying to operate on it.
 * 
 * @author Satia
 */
@SuppressWarnings("serial")
public class DBClosedException extends Exception {

	public DBClosedException() {
	}

	public DBClosedException(String message) {
		super(message);
	}

	public DBClosedException(Throwable cause) {
		super(cause);
	}

	public DBClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBClosedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
