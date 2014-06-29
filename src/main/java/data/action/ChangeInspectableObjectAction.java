package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import data.InspectableObject;

/**
 * An action changing properties of a {@link InspectableObject}.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ChangeInspectableObjectAction extends ChangeNamedObjectAction {

	/**
	 * All identifiers to be added.
	 */
	@ElementCollection
	private List<String> identifiersToAdd;

	/**
	 * All identifiers to be removed.
	 */
	@ElementCollection
	private List<String> identifiersToRemove;

	/**
	 * The new inspection text. If {@code null}, the old will not be changed.
	 */
	@Column(nullable = true)
	private String newInspectionText;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ChangeInspectableObjectAction(InspectableObject)}
	 *             instead.
	 */
	@Deprecated
	public ChangeInspectableObjectAction() {
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeInspectableObjectAction(InspectableObject object) {
		super(object);
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 */
	public ChangeInspectableObjectAction(InspectableObject object,
			boolean enabled) {
		super(object, enabled);
		init();
	}

	/**
	 * Adds an identifier to be added to the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void addIdentifierToAdd(String name) {
		identifiersToAdd.add(name.toLowerCase());
	}

	/**
	 * Adds an identifier to be removed from the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void addIdentifierToRemove(String name) {
		identifiersToRemove.add(name.toLowerCase());
	}

	/**
	 * @return the identifiersToAdd
	 */
	public List<String> getIdentifiersToAdd() {
		return identifiersToAdd;
	}

	/**
	 * @return the identifiersToRemove
	 */
	public List<String> getIdentifiersToRemove() {
		return identifiersToRemove;
	}

	/**
	 * @return the newInspectionText
	 */
	public String getNewInspectionText() {
		return newInspectionText;
	}

	/**
	 * @param identifiersToAdd
	 *            the identifiersToAdd to set
	 */
	public void setIdentifiersToAdd(List<String> identifiersToAdd) {
		this.identifiersToAdd = identifiersToAdd;
	}

	/**
	 * @param identifiersToRemove
	 *            the identifiersToRemove to set
	 */
	public void setIdentifiersToRemove(List<String> identifiersToRemove) {
		this.identifiersToRemove = identifiersToRemove;
	}

	/**
	 * @return the object
	 */
	@Override
	public InspectableObject getObject() {
		return (InspectableObject) super.getObject();
	}

	/**
	 * Removes an identifier to be added to the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void removeIdentifierToAdd(String name) {
		identifiersToAdd.remove(name.toLowerCase());
	}

	/**
	 * Removes an identifier to be removed from the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void removeIdentifierToRemove(String name) {
		identifiersToRemove.remove(name.toLowerCase());
	}

	/**
	 * @param newInspectionText
	 *            the newInspectionText to set
	 */
	public void setNewInspectionText(String newInspectionText) {
		this.newInspectionText = newInspectionText;
	}

	@Override
	protected void doAction() {
		// Call the super method
		super.doAction();

		// Change fields
		if (newInspectionText != null) {
			getObject().setInspectionText(newInspectionText);
		}
		// Add and remove identifiers
		for (String id : identifiersToAdd) {
			getObject().addIdentifier(id);
		}
		for (String id : identifiersToRemove) {
			getObject().removeIdentifier(id);
		}
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		identifiersToAdd = new ArrayList<>();
		identifiersToRemove = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "ChangeInspectableObjectAction{" + "identifiersToAdd="
				+ identifiersToAdd + ", identifiersToRemove="
				+ identifiersToRemove + ", newInspectionText="
				+ newInspectionText + " " + super.toString() + '}';
	}

	@Override
	public String getActionDescription() {
		StringBuilder builder = new StringBuilder(super.getActionDescription());
		if (newInspectionText != null) {
			builder.append(" Setting inspection text to '")
					.append(newInspectionText).append("'.");
		}
		if (!identifiersToAdd.isEmpty()) {
			builder.append(" Adding identifiers: ");
			for (String identifier : identifiersToAdd) {
				builder.append("'").append(identifier).append("', ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		if (!identifiersToAdd.isEmpty()) {
			builder.append(" Removing identifiers: ");
			for (String identifier : identifiersToRemove) {
				builder.append("'").append(identifier).append("', ");
			}
			builder.setLength(builder.length() - 2);
			builder.append(".");
		}
		return builder.toString();
	}
}
