package logic;

import java.io.IOException;

/**
 * Encapsulates an action that takes no parameters, produces no results, but can
 * throw an {@link IOException}.
 * 
 * @author Satia
 *
 */
@FunctionalInterface
public interface Do {
	/**
	 * Carries out the action.
	 * 
	 * @throws IOException
	 *             if some IO related error occurred.
	 */
	public void doSth() throws IOException;
}
