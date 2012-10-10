package data.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import persistence.Main;
import data.NamedObject;

/**
 * An action changing properties of a {@link NamedObject} .
 * 
 * @author Satia
 */
@Entity
public class ChangeNamedObjectAction extends AbstractAction {
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
	 * The new description. If {@code null}, the old will not be changed.
	 */
	private String newDescription;

	/**
	 * The new inspection text. If {@code null}, the old will not be changed.
	 */
	private String newInspectionText;

	/**
	 * The new name. If {@code null}, the old will not be changed.
	 */
	private String newName;

	/**
	 * The object to be changed.
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private NamedObject object;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link ChangeNamedObjectAction#SetPropertiesAction(NamedObject)}
	 *             instead.
	 */
	@Deprecated
	public ChangeNamedObjectAction() {
		init();
	}

	/**
	 * @param object
	 *            the object to be changed
	 */
	public ChangeNamedObjectAction(NamedObject object) {
		init();
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
		init();
		this.object = object;
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param enabled
	 *            if the action should be enabled
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public ChangeNamedObjectAction(NamedObject object, boolean enabled,
			String forbiddenText, String successfulText) {
		super(enabled, forbiddenText, successfulText);
		init();
		this.object = object;
	}

	/**
	 * @param object
	 *            the object to be changed
	 * @param forbiddenText
	 *            the forbiddenText
	 * @param successfulText
	 *            the successfulText
	 */
	public ChangeNamedObjectAction(NamedObject object, String forbiddenText,
			String successfulText) {
		super(forbiddenText, successfulText);
		init();
		this.object = object;
	}

	/**
	 * Adds an identifier to be added to the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void addIdentifieToAdd(String name) {
		identifiersToAdd.add(name.toLowerCase());
	}

	/**
	 * Adds an identifier to be removed from the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void addIdentifieToRemove(String name) {
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
	 * @return the newDescription
	 */
	public String getNewDescription() {
		return newDescription;
	}

	/**
	 * @return the newInspectionText
	 */
	public String getNewInspectionText() {
		return newInspectionText;
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
	 * Removes an identifier to be added to the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void removeIdentifieToAdd(String name) {
		identifiersToAdd.remove(name.toLowerCase());
	}

	/**
	 * Removes an identifier to be removed from the identifiers.
	 * 
	 * @param name
	 *            the identifier
	 */
	public void removeIdentifieToRemove(String name) {
		identifiersToRemove.remove(name.toLowerCase());
	}

	/**
	 * @param newDescription
	 *            the newDescription to set
	 */
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	/**
	 * @param newInspectionText
	 *            the newInspectionText to set
	 */
	public void setNewInspectionText(String newInspectionText) {
		this.newInspectionText = newInspectionText;
	}

	/**
	 * @param newName
	 *            the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(NamedObject object) {
		this.object = object;
	}

	@Override
	public void triggerAction() {
		if (enabled) {
			// Change fields
			if (newName != null) {
				object.setName(newName);
			}
			if (newDescription != null) {
				object.setDescription(newDescription);
			}
			if (newInspectionText != null) {
				object.setInspectionText(newInspectionText);
			}
			// Add and remove identifiers
			for (String id : identifiersToAdd) {
				object.addIdentifier(id);
			}
			for (String id : identifiersToRemove) {
				object.removeIdentifier(id);
			}
		}
		Main.updateChanges();
	}

	/**
	 * Initializes the fields.
	 */
	private void init() {
		identifiersToAdd = new ArrayList<String>();
		identifiersToRemove = new ArrayList<String>();
	}
}