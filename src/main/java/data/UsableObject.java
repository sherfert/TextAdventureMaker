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
import data.interfaces.Usable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Anything useable for itself (without other objects) in the game.
 * Implementation of the {@link Usable} interface.
 *
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public abstract class UsableObject extends InspectableObject implements Usable {

	/**
	 * All additional use actions.
	 */
	private List<AbstractAction> additionalUseActions;

	/**
	 * All additional use commands.
	 */
	private List<String> additionalUseCommands;

	/**
	 * The text being displayed when not successfully used. The default message
	 * is used if this is {@code null}.
	 */
	private final StringProperty useForbiddenText;

	/**
	 * The text being displayed when successfully used. The default message is
	 * used if this is {@code null}.
	 */
	private final StringProperty useSuccessfulText;

	/**
	 * If using is enabled. {@code false} by default.
	 */
	private final BooleanProperty usingEnabled;

	/**
	 * No-arg constructor for the database. Use
	 * {@link UsableObject#UsableObject(String, String)} instead.
	 */
	@Deprecated
	protected UsableObject() {
		useForbiddenText = new SimpleStringProperty();
		useSuccessfulText = new SimpleStringProperty();
		usingEnabled = new SimpleBooleanProperty();
		init();
	}

	/**
	 * By default non-usable.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 */
	protected UsableObject(String name, String description) {
		super(name, description);
		useForbiddenText = new SimpleStringProperty();
		useSuccessfulText = new SimpleStringProperty();
		usingEnabled = new SimpleBooleanProperty();
		init();
	}

	@Override
	public void addAdditionalActionToUse(AbstractAction action) {
		additionalUseActions.add(action);
	}

	@Override
	public void addAdditionalUseCommand(String command) {
		additionalUseCommands.add(command);
	}

	@Override
	@ManyToMany(cascade = { CascadeType.PERSIST })
	@JoinTable(name = "USABLEOBJ_AUA",foreignKey = @ForeignKey(name = "FK_USABLEOBJECT_ADDITIONALUSEACTIONS_S", //
	foreignKeyDefinition = "FOREIGN KEY (UsableObject_ID) REFERENCES NAMEDDESCRIBEDOBJECT (ID) ON DELETE CASCADE") , //
	inverseForeignKey = @ForeignKey(name = "FK_USABLEOBJECT_ADDITIONALUSEACTIONS_D", //
	foreignKeyDefinition = "FOREIGN KEY (additionalUseActions_ID) REFERENCES ABSTRACTACTION (ID) ON DELETE CASCADE") )
	public List<AbstractAction> getAdditionalUseActions() {
		return additionalUseActions;
	}

	@Override
	public void setAdditionalUseActions(List<AbstractAction> additionalUseActions) {
		this.additionalUseActions = additionalUseActions;
	}

	@Override
	@ElementCollection
	public List<String> getAdditionalUseCommands() {
		return additionalUseCommands;
	}

	@Override
	public void setAdditionalUseCommands(List<String> additionalUseCommands) {
		this.additionalUseCommands = additionalUseCommands;
	}

	@Override
	@Column(nullable = true)
	public String getUseForbiddenText() {
		return useForbiddenText.get();
	}
	
	/**
	 * @return the use forbidden text property
	 */
	public StringProperty useForbiddenTextProperty() {
		return useForbiddenText;
	}

	@Override
	@Column(nullable = true)
	public String getUseSuccessfulText() {
		return useSuccessfulText.get();
	}
	
	/**
	 * @return the use successful text property
	 */
	public StringProperty useSuccessfulTextProperty() {
		return useSuccessfulText;
	}

	@Override
	@Column(nullable = false)
	public boolean getUsingEnabled() {
		return usingEnabled.get();
	}
	
	/**
	 * @return using enabled property.
	 */
	public BooleanProperty usingEnabledProperty() {
		return usingEnabled;
	}

	@Override
	public void removeAdditionalActionFromUse(AbstractAction action) {
		additionalUseActions.remove(action);
	}

	@Override
	public void removeAdditionalUseCommand(String command) {
		additionalUseCommands.remove(command);
	}

	@Override
	public void setUseForbiddenText(String forbiddenText) {
		useForbiddenText.set(forbiddenText);
	}

	@Override
	public void setUseSuccessfulText(String successfulText) {
		useSuccessfulText.set(successfulText);
	}

	@Override
	public void setUsingEnabled(boolean enabled) {
		usingEnabled.set(enabled);
	}

	@Override
	public void use(Game game) {
		// There is no "primary" action, so no "isEnabled" check
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Using {0}", this);

		for (AbstractAction abstractAction : additionalUseActions) {
			abstractAction.triggerAction(game);
		}
	}

	/**
	 * Initializes the fields
	 */
	private final void init() {
		additionalUseActions = new ArrayList<>();
		additionalUseCommands = new ArrayList<>();
		usingEnabled.set(false);
	}
}
