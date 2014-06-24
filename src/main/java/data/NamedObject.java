package data;

import data.interfaces.HasId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Anything having a name and description.
 *
 * @author Satia
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class NamedObject implements HasId {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The name.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * The description. It is being displayed when the named object is e.g.
	 * in the same location.
	 */
	@Column(nullable = false)
	private String description;

	/**
	 * No-arg constructor for the database. Use
	 * {@link NamedObject#NamedObject(String, String)} instead.
	 */
	@Deprecated
	protected NamedObject() {
	}

	/**
	 * @param name the name
	 * @param description the description
	 */
	protected NamedObject(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

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
