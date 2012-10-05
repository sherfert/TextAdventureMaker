package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import data.action.AbstractAction;
import data.interfaces.Inspectable;

/**
 * Anything having a name, identifiers, a short and a long description.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NamedObject implements Inspectable {

	/**
	 * All additional actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalInspectActions;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * All identifiers. Must be lowercase.
	 */
	@ElementCollection
	private List<String> identifiers;

	/**
	 * The long description. It is being displayed when the named object is
	 * inspected.
	 */
	private String longDescription;

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The short description. It is being displayed when the named object is
	 * e.g. in the same location.
	 */
	private String shortDescription;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String, String, String)} instead.
	 */
	@Deprecated
	protected NamedObject() {
		additionalInspectActions = new ArrayList<AbstractAction>();
		identifiers = new ArrayList<String>();
	}

	/**
	 * Saves the name as an identifier.
	 * 
	 * @param name
	 *            the name
	 * @param shortDescription
	 *            the shortDescription
	 * @param longDescription
	 *            the longDescription
	 */
	protected NamedObject(String name, String shortDescription,
			String longDescription) {
		this();
		this.name = name;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		addIdentifier(name);
	}

	@Override
	public void addAdditionalActionToInspect(AbstractAction action) {
		additionalInspectActions.add(action);
	}

	/**
	 * Note: The identifier is added as a lower case String.
	 */
	@Override
	public void addIdentifier(String name) {
		identifiers.add(name.toLowerCase());
	}

	@Override
	public List<AbstractAction> getAdditionalActionsFromInspect() {
		return additionalInspectActions;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	@Override
	public List<String> getIdentifiers() {
		return identifiers;
	}

	@Override
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	@Override
	public void inspect() {
		for (AbstractAction abstractAction : additionalInspectActions) {
			abstractAction.triggerAction();
		}
	}

	@Override
	public void removeAdditionalActionFromInspect(AbstractAction action) {
		additionalInspectActions.remove(action);
	}

	@Override
	public void removeIdentifier(String name) {
		identifiers.remove(name);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Note: This method does not convert the Strings to lowercase, so you must
	 * do this prior to the call.
	 */
	@Override
	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	@Override
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
}