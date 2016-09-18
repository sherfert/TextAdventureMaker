package data.interfaces;

import javax.persistence.Column;

/**
 * Anything with a name
 *
 *
 * @author Satia
 */
public interface HasName extends HasId {

	/**
	 * @return the name
	 */
	@Column(nullable = false)
	public String getName();
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name);
}
