package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import data.interfaces.HasId;

/**
 * A layer has a list of options to choose from.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class ConversationLayer implements HasId {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The options of this layer. Options can only belong to one layer.
	 */
	private List<ConversationOption> options;

	/**
	 * Create a new conversation layer.
	 */
	public ConversationLayer() {
		this.options = new ArrayList<>();
	}

	@Id
	@GeneratedValue
	@Override
	public int getId() {
		return id;
	}

	/**
	 * Just for the database.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the options
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	public List<ConversationOption> getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(List<ConversationOption> options) {
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
	}

	/**
	 * Removes an option.
	 * 
	 * @param option
	 *            the option to remove
	 */
	public void removeOption(ConversationOption option) {
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

	@Override
	public String toString() {
		return "ConversationLayer{id=" + id + ", optionsIDs="
				+ NamedObject.getIDList(options) + "}";
	}

}
