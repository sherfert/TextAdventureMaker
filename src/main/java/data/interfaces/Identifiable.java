package data.interfaces;

import java.util.List;

/**
 * Anything with identifiers and a name in the game.
 * 
 * @author Satia
 */
public interface Identifiable extends HasId {
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
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Removes an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeIdentifier(String name);
}