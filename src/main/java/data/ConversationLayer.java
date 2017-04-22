package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

/**
 * A layer has a list of options to choose from.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ConversationLayer extends NamedObject {

	/**
	 * The conversation this layer belongs to. This is fixed and cannot be
	 * changed.
	 */
	private Conversation conversation;

	/**
	 * The options of this layer. Options can only belong to one layer.
	 */
	private List<ConversationOption> options;

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #ConversationLayer(String)} instead.
	 */
	@Deprecated
	public ConversationLayer() {
		this.options = new ArrayList<>();
	}

	/**
	 * Create a new conversation layer.
	 * 
	 * @param name
	 *            the name
	 */
	public ConversationLayer(String name) {
		super(name);
		this.options = new ArrayList<>();
	}

	/**
	 * @return the conversation
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_LAYER_CONVERSATION", //
	foreignKeyDefinition = "FOREIGN KEY (LAYERS_ID) REFERENCES CONVERSATION (ID) ON DELETE CASCADE") )
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * @param conversation
	 *            the conversation to set
	 */
	void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	/**
	 * @return the options
	 */
	@OneToMany(mappedBy = "getLayer", cascade = { CascadeType.PERSIST })
	@OrderBy("layerOrder ASC, getId ASC")
	public List<ConversationOption> getOptions() {
		return options;
	}

	/**
	 * Updates the list of options of this layer. This method must only be
	 * called if the elements of the passed list and the current list are the
	 * same, except for the order.
	 * 
	 * @param newOptions
	 *            the permutated list of options
	 */
	public void updateOptions(List<ConversationOption> newOptions) {
		// Set items list, so that the order of the passed list is preserved
		options.clear();
		options.addAll(newOptions);
		// Update the layer order on all options
		for (int i = 0; i < options.size(); i++) {
			options.get(i).setLayerOrder(i);
		}
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setOptions(List<ConversationOption> options) {
		this.options = options;
	}

	/**
	 * Adds an option.
	 * 
	 * @param option
	 *            the option to add
	 */
	public void addOption(ConversationOption option) {
		options.add(option);
		// Set the layer in the option
		option.setLayer(this);
		// Give the option the highest layerOrder, so it appears last in
		// the list
		int newOrder = this.options.stream().map(ConversationOption::getLayerOrder).max(Comparator.<Integer> naturalOrder())
				.orElse(-1) + 1;
		option.setLayerOrder(newOrder);
	}

	/**
	 * Removes an option. Can only be called by the layer when the layer is
	 * deleted.
	 * 
	 * @param option
	 *            the option to remove
	 */
	void removeOption(ConversationOption option) {
		options.remove(option);
	}

	/**
	 * @return a list of the texts of all enabled options.
	 */
	@Transient
	public List<String> getOptionTexts() {
		List<String> result = new ArrayList<>(options.size());
		for (ConversationOption option : options) {
			if (option.getEnabled()) {
				result.add(option.getText());
			}
		}

		return result;
	}

	/**
	 * @return a list of all enabled options.
	 */
	@Transient
	public List<ConversationOption> getEnabledOptions() {
		List<ConversationOption> result = new ArrayList<>(options.size());
		for (ConversationOption option : options) {
			if (option.getEnabled()) {
				result.add(option);
			}
		}

		return result;
	}

	/**
	 * A layer is playable if there is at least one enabled option.
	 * 
	 * @return if the layer is playable.
	 */
	@Transient
	public boolean isPlayable() {
		for (ConversationOption option : options) {
			if (option.getEnabled()) {
				return true;
			}
		}
		return false;
	}
	
	@PreRemove
	private void preRemove() {
		if(conversation.getStartLayer() == this) {
			// Otherwise the CASCADE persist on the *OneToOne* cancels the delete
			conversation.setStartLayer(null);
		}
	}

}
