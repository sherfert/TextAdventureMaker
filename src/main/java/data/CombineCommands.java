package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Entity for grouping additional combine commands, as they are asymmetric and
 * not symmetric like the basic combine commands.
 * 
 * @author Satia
 */
@Entity
class CombineCommands {

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * The commands.
	 */
	@ElementCollection
	List<String> commands = new ArrayList<>();

	@Override
	public String toString() {
		return "CombineCommands{id=" + id + ", commands=" + commands + "}";
	}
	
	

}