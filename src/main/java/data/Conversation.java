package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import data.interfaces.HasId;

/**
 * A conversation, consisting of different layers and a greeting.
 * Can be enabled and disabled.
 * 
 * @author Satia
 */
@Entity
public class Conversation implements HasId {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;
	
	/**
	 * The greeting from the Person.
	 */
	private String greeting;
	
	/**
	 * If this conversation is enabled.
	 */
	private boolean enabled;
	
	/**
	 * All layers belonging to this conversation. A layer can only belong to
	 * one conversation.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	private List<ConversationLayer> layers;
	
	/**
	 * The starting layer. The options of this layer will be presented
	 * after the greeting. If this is {@code null}, the conversation will
	 * end after the greeting.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private ConversationLayer startLayer;
	
	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use
	 *             {@link #Conversation(String)}
	 *             instead.
	 */
	@Deprecated
	public Conversation() {
		layers = new ArrayList<>();
	}
	
	

	/**
	 * An enabled conversation with no layers yet.
	 * 
	 * @param greeting the greeting
	 */
	public Conversation(String greeting) {
		this();
		this.greeting = greeting;
	}

	@Override
	public int getId() {
		return id;
	}



	/**
	 * @return the greeting
	 */
	public String getGreeting() {
		return greeting;
	}



	/**
	 * @param greeting the greeting to set
	 */
	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}



	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}



	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}



	/**
	 * @return the layers
	 */
	public List<ConversationLayer> getLayers() {
		return layers;
	}



	/**
	 * @param layers the layers to set
	 */
	public void setLayers(List<ConversationLayer> layers) {
		this.layers = layers;
	}



	/**
	 * @return the startLayer
	 */
	public ConversationLayer getStartLayer() {
		return startLayer;
	}



	/**
	 * @param startLayer the startLayer to set
	 */
	public void setStartLayer(ConversationLayer startLayer) {
		this.startLayer = startLayer;
	}
	
	/**
	 * Adds a layer.
	 * 
	 * @param layer
	 *            the layer to add
	 */
	public void addLayer(ConversationLayer layer) {
		layers.add(layer);
	}

	/**
	 * Removes a layer.
	 * 
	 * @param layer
	 *            the layer to remove
	 */
	public void removeLayer(ConversationLayer layer) {
		layers.remove(layer);
	}

}
