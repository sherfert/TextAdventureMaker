package playing.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import playing.parser.GeneralParser;
import playing.parser.Parameter;

// TODO
public class CommandExecution {

	// Set in Constructor:
	private final Command command;
	private final String input;
	
	// Set during matches():
	private boolean isMatch;
	private Matcher usedMatcher;
	private boolean originalCommand = false;
	private Parameter[] params = null;

	/**
	 * @param command
	 *            the command
	 * @param input
	 *            the input
	 */
	public CommandExecution(Command command, String input) {
		this.command = command;
		this.input = input;
	}
	
	public boolean matches() {
		Matcher matcher = command.pattern.matcher(input);
		Matcher additionalCommandsMatcher = command.additionalCommandsPattern.matcher(input);
		
		if (matcher.matches()) {
			isMatch = true;
			originalCommand = true;
			usedMatcher = matcher;
		} else if (additionalCommandsMatcher.matches()) {
			isMatch = true;
			originalCommand = false;
			usedMatcher = additionalCommandsMatcher;
		}

		// Either a normal or an additional commandType matched
		if (isMatch) {
			params = getParameters(usedMatcher, command.numberOfParameters);
			return true;
		}
		// This commandType did not match
		return false;
	}
	
	public void execute() {
		// Save the pattern in the replacer TODO later
		command.gamePlayer.getCurrentReplacer().setPattern(usedMatcher.pattern().toString());
		command.execute(originalCommand, params);
	}
	
	/**
	 * Extracts the used parameters from a given matcher.
	 * TODO move
	 *
	 * @param matcher
	 *            the matcher that must have matched an input
	 * @param numberOfParameters
	 *            the expected number of parameters
	 * @return an array with the typed parameters
	 */
	public static Parameter[] getParameters(Matcher matcher, int numberOfParameters) {
		/*
		 * By convention, all capturing groups that denote parameters must be
		 * named with o0, o1, o2, and so on, without leaving gaps. This can be
		 * used to pass the parameter to the command function together with its
		 * capturing group title for further processing.
		 */

		List<Parameter> parameters = new ArrayList<>(2);
		try {
			for (int i = 0; i < numberOfParameters; i++) {
				String groupName = "o" + i;
				String identifier;
				if ((identifier = matcher.group(groupName)) != null) {
					parameters.add(new Parameter(identifier, groupName));
				} else {
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			Logger.getLogger(GeneralParser.class.getName()).log(Level.SEVERE,
					"Capturing group in the range of expected parameters not found.", e);
		}

		return parameters.toArray(new Parameter[0]);
	}

}
