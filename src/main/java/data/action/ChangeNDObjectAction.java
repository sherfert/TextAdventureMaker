package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import data.Game;
import data.InspectableObject;
import data.NamedDescribedObject;

/**
 * An action changing properties of a {@link NamedDescribedObject} .
 * 
 * FIXME not getting deleted, if the changed object is deleted (case:
 * ChangeWayAction)
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public class ChangeNDObjectAction extends AbstractAction {

	/**
	 * The new description. If {@code null}, the old will not be changed.
	 */
	private String newDescription;

	/**
	 * The new name. If {@code null}, the old will not be changed.
	 */
	private String newName;

	/**
	 * The object to be changed.
	 */
	private NamedDescribedObject object;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeNDObjectAction(String, InspectableObject)}
	 *             instead.
	 */
	@Deprecated
	public ChangeNDObjectAction() {
	}

	/**
	 * @param name
	 *            the name
	 * @param object
	 *            the object to be changed
	 */
	public ChangeNDObjectAction(String name, NamedDescribedObject object) {
		super(name);
		this.object = object;
	}

	/**
	 * @return the newDescription
	 */
	@Column(nullable = true)
	public String getNewDescription() {
		return newDescription;
	}

	/**
	 * @return the newName
	 */
	@Column(nullable = true)
	public String getNewName() {
		return newName;
	}

	/**
	 * XXX This should be nullable = false. This is impossible due to circular
	 * dependencies.
	 * 
	 * @return the object
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_CHANGENAMEDOBJECTACTION_OBJECT", //
	foreignKeyDefinition = "FOREIGN KEY (OBJECT_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") )
	public NamedDescribedObject getObject() {
		return object;
	}
	
	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setObject(NamedDescribedObject object) {
		this.object = object;
	}

	/**
	 * @param newDescription
	 *            the newDescription to set
	 */
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	/**
	 * @param newName
	 *            the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	@Override
	protected void doAction(Game game) {
		// Change fields
		if (newName != null) {
			object.setName(newName);
		}
		if (newDescription != null) {
			object.setDescription(newDescription);
		}
	}

	@Override
	public String actionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing ").append(object.getName()).append(".");
		if (newName != null) {
			builder.append(" Setting name to '").append(newName).append("'.");
		}
		if (newDescription != null) {
			builder.append(" Setting description to '").append(newDescription).append("'.");
		}
		return builder.toString();
	}
}
