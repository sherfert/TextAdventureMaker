package playing.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import data.interfaces.Inspectable;
import exception.DBClosedException;
import exception.DBIncompatibleException;
import persistence.InspectableObjectManager;
import playing.parser.GeneralParser;
import playing.parser.Parameter;

/**
 * A particular execution of a command. This class has internal states and its
 * methods must be called in a particular sequence.
 * 
 * {@link #matches()} has to be called first. If that returns {@code true},
 * {@link #hasObjects()} may be called next. Following {@link #execute()} may be
 * called last.
 * 
 * @author Satia
 */
public abstract class CommandExecution {

	// Set in Constructor:
	protected final Command command;
	protected final String input;

	// Set during matches():
	protected boolean isMatch;
	protected boolean originalCommand;
	protected Matcher usedMatcher;
	protected Parameter[] parameters;

	// Set during hasObjects():
	protected Inspectable object1;
	protected Inspectable object2;

	/**
	 * Create a new command execution with the given command and input.
	 * 
	 * @param command
	 *            the command
	 * @param input
	 *            the input
	 */
	protected CommandExecution(Command command, String input) {
		this.command = command;
		this.input = input;
	}

	/**
	 * Tests whether this command matches the provided input. Must only be
	 * called once and as the first method after construction. If this returns
	 * {@code false}, no further methods must be called on this object.
	 * 
	 * @return if the command matches the input.
	 */
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
			parameters = getParameters(usedMatcher, command.numberOfParameters);
			return true;
		}
		// This commandType did not match
		return false;
	}

	/**
	 * Must only be called after {@link #matches()} was called and returned
	 * {@code true}. Must only be called once.
	 * 
	 * Tests if the command finds all objects that it needs to execute. If
	 * {@code false}, the command may still execute, but an error message will
	 * be shown to the user.
	 * 
	 * @return if the command finds all objects to execute.
	 */
	public abstract boolean hasObjects();

	/**
	 * Must only be called after {@link #hasObjects()} was called. Must only be
	 * called once.
	 * 
	 * Executes the command.
	 * 
	 * Subclasses may call setters on the currentReplacer in this method, but
	 * not in other methods.
	 */
	public abstract void execute();

	/**
	 * As part of {@link #hasObjects()} this method may be called by subclasses
	 * to find up to two inspectable objects with the identifiers that were
	 * extracted from the user input.
	 */
	protected void findInspectableObjects() {
		// Collect all inspectable objects.
		InspectableObjectManager iom = command.persistenceManager.getInspectableObjectManager();
		try {
			if (command.numberOfParameters >= 1) {
				String identifier = parameters[0].getIdentifier();
				object1 = iom.getInspectable(identifier);
			}
			if (command.numberOfParameters >= 2) {
				String identifier = parameters[1].getIdentifier();
				object2 = iom.getInspectable(identifier);
			}
		} catch (DBClosedException | DBIncompatibleException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Operating on a closed/incompatible DB", e);
			return;
		}
	}

	/**
	 * As part of {@link #execute()} this must be called to configure the
	 * replacer correctly for subsequent output.
	 */
	protected void configureReplacer() {
		command.gamePlayer.getCurrentReplacer().setPattern(usedMatcher.pattern().toString());
		if (command.numberOfParameters >= 1) {
			String identifier = parameters[0].getIdentifier();
			command.currentReplacer.setIdentifier(identifier);
			if (object1 != null) {
				command.currentReplacer.setName(object1.getName());
			}
			if (command.numberOfParameters >= 2) {
				String identifier2 = parameters[1].getIdentifier();
				command.currentReplacer.setIdentifier2(identifier2);
				if (object2 != null) {
					command.currentReplacer.setName2(object2.getName());
				}
			}
		}

	}

	/**
	 * Extracts the used parameters from a given matcher.
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
