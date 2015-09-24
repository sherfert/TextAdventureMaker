package playing.parser;

/**
 * 
 * A parameter consists of the identifier and the name of its capturing group.
 * 
 * @author Satia
 */
public class Parameter {

	/**
	 * The identifier.
	 */
	private String identifier;

	/**
	 * Capturing group name.
	 */
	private String capturingGroupName;

	/**
	 * @param identifier
	 *            the identifier
	 * @param capturingGroupName
	 *            the capturing group name
	 */
	public Parameter(String identifier, String capturingGroupName) {
		this.identifier = identifier;
		this.capturingGroupName = capturingGroupName;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the capturingGroupName
	 */
	public String getCapturingGroupName() {
		return capturingGroupName;
	}

}
