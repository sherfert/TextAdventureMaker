package data;

import data.action.ChangeNamedObjectAction;
import data.interfaces.HasId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

/**
 * Anything having a name and description.
 *
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.PROPERTY)
public abstract class NamedObject implements HasId {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The name.
	 */
	private final StringProperty name;

	/**
	 * The description. It is being displayed when the named object is e.g.
	 * in the same location.
	 */
	private final StringProperty description;
	
	// Inverse mappings just for cascading.
	@OneToMany(mappedBy = "object", cascade = CascadeType.ALL)
	@Access(AccessType.FIELD)
	private List<ChangeNamedObjectAction> changeActions;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String, String)} instead.
	 */
	@Deprecated
	protected NamedObject() {
		name = new SimpleStringProperty();
		description = new SimpleStringProperty();
	}

	/**
	 * @param name the name
	 * @param description the description
	 */
	protected NamedObject(String name, String description) {
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
	}

	@Override
	@Id
	@GeneratedValue
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
	 * @return the name
	 */
	@Column(nullable = false)
	public String getName() {
		return name.get();
	}

	/**
	 * @return the description
	 */
	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	/**
	 * @return the name property
	 */
	public StringProperty nameProperty() {
        return name;
    }

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	/**
	 * @return the description property
	 */
	public StringProperty descriptionProperty() {
        return description;
    }

	/**
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {
		return "NamedObject{" + "id=" + id + ", name=" + name + ", description=" + description + '}';
	}

	/**
	 * Helper function for toString methods. Instead of listing all members
	 * of a list, only their IDs are Listed.
	 *
	 * @param originalList the original list
	 * @return the ID list
	 */
	public static List<Integer> getIDList(List<? extends HasId> originalList) {
		List<Integer> result = new ArrayList<>(originalList.size());
		for (HasId hasId : originalList) {
			result.add(hasId.getId());
		}

		return result;
	}

	/**
	 * Helper function for toString methods. Instead of listing the keys
	 * directly, only their IDs are listed.
	 *
	 * @param <E> the  map value type
	 * @param originalMap the original map
	 * @return the ID map
	 */
	public static <E> Map<Integer, E> getIDMap(Map<? extends HasId, E> originalMap) {
		Map<Integer, E> result = new HashMap<>();
		for (Map.Entry<? extends HasId, E> entry : originalMap.entrySet()) {
			result.put(entry.getKey().getId(), entry.getValue());
		}

		return result;
	}
}
