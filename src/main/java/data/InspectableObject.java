package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import data.action.AbstractAction;
import data.interfaces.Inspectable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Anything having a name, identifiers, a description and being
 * {@link Inspectable}.
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public abstract class InspectableObject extends NamedDescribedObject implements Inspectable {
	
	/**
	 * All additional inspect actions.
	 */
	private List<AbstractAction> additionalInspectActions;

	/**
	 * All identifiers. Must be lowercase.
	 */
	private List<String> identifiers;

	/**
	 * It is being displayed when the named object is inspected or suggests the
	 * default text to be displayed if {@code null}.
	 */
	private final StringProperty inspectionText;

	/**
	 * No-arg constructor for the database. Use
	 * {@link InspectableObject#NamedObject(String, String)} instead.
	 */
	@Deprecated
	protected InspectableObject() {
		inspectionText = new SimpleStringProperty();
		init();
	}

	/**
	 * Saves the name as an identifier.
	 * 
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	protected InspectableObject(String name, String description) {
		super(name, description);
		inspectionText = new SimpleStringProperty();
		init();
		addIdentifier(name);
	}

	@Override
	public void addAdditionalInspectAction(AbstractAction action) {
		additionalInspectActions.add(action);
	}

	/**
	 * Note: The identifier is added as a lower case String.
	 */
	@Override
	public final void addIdentifier(String name) {
		identifiers.add(name.toLowerCase());
	}

	@Override
	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name = "INSPOBJ_AIA", foreignKey = @ForeignKey(name = "FK_INSPECTABLEOBJECT_ADDITIONAINSPECTACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (InspectableObject_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_INSPECTABLEOBJECT_ADDITIONAINSPECTACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalInspectActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	public List<AbstractAction> getAdditionalInspectActions() {
		return additionalInspectActions;
	}

	@Override
	public void setAdditionalInspectActions(List<AbstractAction> additionalInspectActions) {
		this.additionalInspectActions = additionalInspectActions;
	}

	@Override
	@ElementCollection
	public List<String> getIdentifiers() {
		return identifiers;
	}

	@Override
	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	@Override
	@Column(nullable = true)
	public String getInspectionText() {
		return inspectionText.get();
	}
	
	/**
	 * @return the inspection text property
	 */
	public StringProperty inspectionTextProperty() {
        return inspectionText;
    }

	@Override
	public void inspect(Game game) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Inspecting {0}", this);

		for (AbstractAction abstractAction : additionalInspectActions) {
			abstractAction.triggerAction(game);
		}
	}

	@Override
	public void removeAdditionalInspectAction(AbstractAction action) {
		additionalInspectActions.remove(action);
	}

	@Override
	public void removeIdentifier(String name) {
		identifiers.remove(name);
	}

	@Override
	public void setInspectionText(String inspectionText) {
		this.inspectionText.set(inspectionText);
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		additionalInspectActions = new ArrayList<>();
		identifiers = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "InspectableObject{" + "additionalInspectActionsIDs=" + NamedObject.getIDList(additionalInspectActions)
				+ ", identifiers=" + identifiers + ", inspectionText=" + inspectionText + " " + super.toString() + '}';
	}
}