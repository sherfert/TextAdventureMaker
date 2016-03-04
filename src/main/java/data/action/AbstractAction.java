package data.action;

import data.Conversation;
import data.ConversationOption;
import data.Game;
import data.InspectableObject;
import data.Item;
import data.UsableObject;
import data.Way;
import data.interfaces.HasId;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * Any action that changes something in the game (if enabled).
 * 
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractAction implements HasId {

	/**
	 * When an action changes something that can be disabled or enabled, a
	 * three-value-type is needed to model that.
	 * 
	 * @author Satia
	 */
	public enum Enabling {
		/** Disable the object */
		DISABLE("Disabling"),
		/** Do not change if the object is enabled */
		DO_NOT_CHANGE("Neither enabling or disabling"),
		/** Enable the object */
		ENABLE("Enabling");

		/**
		 * A text describing what this Enabling value does.
		 */
		public final String description;

		/**
		 * @param description
		 *            a text describing what this Enabling value does
		 */
		private Enabling(String description) {
			this.description = description;
		}
	}

	/**
	 * The Id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * If the action is enabled and therefore triggerable.
	 */
	@Column(nullable = false)
	protected boolean enabled;

	// Inverse mappings just for cascading.
	@OneToMany(mappedBy = "action", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ChangeActionAction> changeActions;
	@ManyToMany(mappedBy = "getAdditionalTakeActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Item> takeItems;
	@ManyToMany(mappedBy = "getAdditionalActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Conversation> conversations;
	@ManyToMany(mappedBy = "getAdditionalActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ConversationOption> conversationOptions;
	@ManyToMany(mappedBy = "getAdditionalInspectActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<InspectableObject> inspectObjects;
	@ManyToMany(mappedBy = "getAdditionalUseActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<UsableObject> useObjects;
	@ManyToMany(mappedBy = "getAdditionalMoveActions", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Way> ways;

	/**
	 * An enabled action.
	 */
	protected AbstractAction() {
		enabled = true;
	}

	/**
	 * An action.
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 */
	protected AbstractAction(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * Enables or disables an action.
	 * 
	 * @param enabled
	 *            whether to enable or disable the action
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the class name, id and enabled status.
	 */
	@Override
	public String toString() {
		return "AbstractAction{" + "id=" + id + ", enabled=" + enabled + '}';
	}

	/**
	 * Triggers the associated action. This logs, calls doAction if enabled, and
	 * updates changes.
	 * 
	 * @param game
	 *            a reference to the game, needed for some actions.
	 */
	public final void triggerAction(Game game) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Triggering action {0}", this);

		if (enabled) {
			doAction(game);
		}
	}

	/**
	 * Actually perform the action.
	 * 
	 * @param game
	 *            a reference to the game, needed for some actions.
	 */
	protected abstract void doAction(Game game);

	/**
	 * Constructs a string explaining the action. This is for the GUI, whereas
	 * {@link #toString()} should be preferred for debugging.
	 * 
	 * @return a description of the action.
	 */
	public abstract String getActionDescription();
}