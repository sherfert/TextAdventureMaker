package data.action;

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
import data.NamedObject;

/**
 * An action changing properties of a {@link NamedObject} .
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ChangeNamedObjectAction extends AbstractAction {

	/**
	 * The new description. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newDescription;

	/**
	 * The new name. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newName;

	/**
	 * The object to be changed.
	 * 
	 * XXX This should be (nullable = false). This is impossible due to circular
	 * dependencies.
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_CHANGENAMEDOBJECTACTION_OBJECT", foreignKeyDefinition = "FOREIGN KEY (OBJECT_ID) REFERENCES NAMEDOBJECT (ID) ON DELETE CASCADE") )
	private NamedObject object;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeNamedObjectAction(InspectableObject)}
	 *             instead.
	 */
	@Deprecated
	public ChangeNamedObjectAction() {
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeNamedObjectAction(NamedObject object) {
		this.object = object;
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeNamedObjectAction(NamedObject object, boolean enabled) {
		super(enabled);
		this.object = object;
	}

	/**
	 * @return the newDescription
	 */
	public String getNewDescription() {
		return newDescription;
	}

	/**
	 * @return the newName
	 */
	public String getNewName() {
		return newName;
	}

	/**
	 * @return the object
	 */
	public NamedObject getObject() {
		return object;
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
	public String toString() {
		return "ChangeNamedObjectAction{" + "newDescription=" + newDescription
				+ ", newName=" + newName + ", objectID=" + object.getId() + " "
				+ super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Changing ").append(object.getName()).append(".");
		if (newName != null) {
			builder.append(" Setting name to '").append(newName).append("'.");
		}
		if (newDescription != null) {
			builder.append(" Setting description to '").append(newDescription)
					.append("'.");
		}
		return builder.toString();
	}
}
