package data.interfaces;

import java.util.List;

/**
 * Anything with identifiers in the game.
 * 
 * @author Satia
 */
public interface Identifiable {
	/**
	 * Adds an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void addIdentifier(String name);

	/**
	 * @return the identifiers.
	 */
	public List<String> getIdentifiers();

	/**
	 * Removes an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeIdentifier(String name);
}