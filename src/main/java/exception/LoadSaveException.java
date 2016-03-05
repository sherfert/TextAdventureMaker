package exception;

/**
 * Indicating that loading or saving failed.
 * 
 * @author Satia
 *
 */
@SuppressWarnings("serial")
public class LoadSaveException extends Exception {

	public LoadSaveException() {
	}

	public LoadSaveException(String message) {
		super(message);
	}

	public LoadSaveException(Throwable cause) {
		super(cause);
	}

	public LoadSaveException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
