package data;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Anything having a name and description.
 *
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public abstract class NamedDescribedObject extends NamedObject {
	
	/**
	 * The description. It is being displayed when the named object is e.g.
	 * in the same location.
	 */
	private final StringProperty description;
	
	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedDescribedObject#NamedDescribedObject(String, String)} instead.
	 */
	@Deprecated
	protected NamedDescribedObject() {
		description = new SimpleStringProperty();
	}

	/**
	 * @param name the name
	 * @param description the description
	 */
	protected NamedDescribedObject(String name, String description) {
		super(name);
		this.description = new SimpleStringProperty(description);
	}
	
	/**
	 * @return the description
	 */
	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	/**
	 * @return the description property
	 */
	public StringProperty descriptionProperty() {
        return description;
    }
	
	/**
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {
		return "NamedDescribedObject{" + "description=" + description + " " + super.toString() + '}';
	}

}
