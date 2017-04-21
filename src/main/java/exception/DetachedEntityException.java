package exception;

/**
 * This exception indicates that an item to be edited is detached from the database.
 * 
 * @author Satia
 */
@SuppressWarnings("serial")
public class DetachedEntityException extends Exception {

	public DetachedEntityException() {
	}

	public DetachedEntityException(String message) {
		super(message);
	}

	public DetachedEntityException(Throwable cause) {
		super(cause);
	}

	public DetachedEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public DetachedEntityException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
