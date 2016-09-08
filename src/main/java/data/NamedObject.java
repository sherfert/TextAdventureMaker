package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import data.interfaces.HasId;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Anything having a name.
 * 
 * XXX overthink inheritance strategy!
 *
 * @author Satia
 */
@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class NamedObject implements HasId {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The name. For classes that do not inherit from NamedDescribedObject, this
	 * name is only relevant in the GUI, in will not be visible in the game.
	 */
	private final StringProperty name;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String)} instead.
	 */
	@Deprecated
	protected NamedObject() {
		name = new SimpleStringProperty();
	}

	/**
	 * @param name
	 *            the name
	 */
	protected NamedObject(String name) {
		this.name = new SimpleStringProperty(name);
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
	 * @param name
	 *            the name to set
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
	 * @return a string representation of this object
	 */
	@Override
	public String toString() {
		return "NamedObject{" + "id=" + id + ", name=" + name + '}';
	}

	/**
	 * Helper function for toString methods. Instead of listing all members of a
	 * list, only their IDs are Listed.
	 *
	 * @param originalList
	 *            the original list
	 * @return the ID list
	 */
	@Transient
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
	 * @param <E>
	 *            the map value type
	 * @param originalMap
	 *            the original map
	 * @return the ID map
	 */
	@Transient
	public static <E> Map<Integer, E> getIDMap(Map<? extends HasId, E> originalMap) {
		Map<Integer, E> result = new HashMap<>();
		for (Map.Entry<? extends HasId, E> entry : originalMap.entrySet()) {
			result.put(entry.getKey().getId(), entry.getValue());
		}

		return result;
	}
}
