package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import data.action.AbstractAction;
import data.interfaces.Usable;

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
public abstract class UsableObject extends InspectableObject implements Usable {

	/**
	 * All additional use actions.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable
	private List<AbstractAction> additionalUseActions;
	
	/**
	 * All additional use commands.
	 */
	@ElementCollection
	private List<String> additionalUseCommands;

	/**
	 * The text being displayed when not successfully used. The default
	 * message is used if this is {@code null}.
	 */
	@Column(nullable = true)
	private String useForbiddenText;

	/**
	 * The text being displayed when successfully used. The default message
	 * is used if this is {@code null}.
	 */
	@Column(nullable = true)
	private String useSuccessfulText;

	/**
	 * If using is enabled. {@code false} by default.
	 */
	@Column(nullable = false)
	private boolean usingEnabled;

	/**
	 * No-arg constructor for the database. Use
	 * {@link UsableObject#UsableObject(String, String)} instead.
	 */
	@Deprecated
	protected UsableObject() {
		init();
	}

	/**
	 * By default non-usable.
	 *
	 * @param name the name
	 * @param description the description
	 */
	protected UsableObject(String name, String description) {
		super(name, description);
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
	public List<AbstractAction> getAdditionalActionsFromUse() {
		return additionalUseActions;
	}
	
	@Override
	public List<String> getAdditionalUseCommands() {
		return additionalUseCommands;
	}

	@Override
	public String getUseForbiddenText() {
		return useForbiddenText;
	}

	@Override
	public String getUseSuccessfulText() {
		return useSuccessfulText;
	}

	@Override
	public boolean isUsingEnabled() {
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
		useForbiddenText = forbiddenText;
	}

	@Override
	public void setUseSuccessfulText(String successfulText) {
		useSuccessfulText = successfulText;
	}

	@Override
	public void setUsingEnabled(boolean enabled) {
		usingEnabled = enabled;
	}

	@Override
	public void use() {
		// There is no "primary" action, so no "isEnabled" check
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Using {0}", this);
		
		for (AbstractAction abstractAction : additionalUseActions) {
			abstractAction.triggerAction();
		}
	}

	/**
	 * Initializes the fields
	 */
	private void init() {
		additionalUseActions = new ArrayList<>();
		additionalUseCommands = new ArrayList<>();
		usingEnabled = false;
	}

	@Override
	public String toString() {
		return "UsableObject{" + "additionalUseActionsIDs="
			+ NamedObject.getIDList(additionalUseActions)
			+ ", useForbiddenText=" + useForbiddenText
			+ ", useSuccessfulText=" + useSuccessfulText
			+ ", usingEnabled=" + usingEnabled + " " + super.toString() + '}';
	}
}
