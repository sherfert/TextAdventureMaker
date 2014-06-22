package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Anything having a name and description.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NamedObject {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The name.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * The description. It is being displayed when the named object is
	 * e.g. in the same location.
	 */
	@Column(nullable = false)
	private String description;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String, String)} instead.
	 */
	@Deprecated
	protected NamedObject() {
	}

	/**
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	protected NamedObject(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}