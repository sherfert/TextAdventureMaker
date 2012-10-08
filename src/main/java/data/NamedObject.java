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
 * Anything having a name, identifiers, a description and being
 * {@link Inspectable}.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NamedObject implements Inspectable {
	/**
	 * All additional inspect actions.
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
	 * It is being displayed when the named object is inspected or suggests the
	 * default text to be displayed if {@code null}.
	 */
	private String inspectionText;

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The description. It is being displayed when the named object is
	 * e.g. in the same location.
	 */
	private String description;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String, String)} instead.
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
	 * @param description
	 *            the description
	 */
	protected NamedObject(String name, String description) {
		this();
		this.name = name;
		this.description = description;
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
	public String getInspectionText() {
		return inspectionText;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
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

	@Override
	public void setInspectionText(String inspectionText) {
		this.inspectionText = inspectionText;
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