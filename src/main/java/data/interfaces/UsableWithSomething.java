package data.interfaces;

import java.util.List;

import data.Game;
import data.action.AbstractAction;

/**
 * Anything usable with an {@link PassivelyUsable} in the game.
 * 
 * @author Satia
 */
public interface UsableWithSomething extends Identifiable {
	/**
	 * Adds an additional action for that item
	 * 
	 * @param object
	 *            the object
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToUseWith(PassivelyUsable object, AbstractAction action);

	/**
	 * Adds an additional command that can be used to use this with something
	 * else.
	 * 
	 * @param object
	 *            the object
	 * @param command
	 *            the command
	 */
	public void addAdditionalUseWithCommand(PassivelyUsable object, String command);

	/**
	 * @param object
	 *            the object
	 * 
	 * @return the additional actions for that object.
	 */
	public List<AbstractAction> getAdditionalActionsFromUseWith(PassivelyUsable object);

	/**
	 * @param object
	 *            the object
	 * 
	 * @return the additional use with commands.
	 */
	public List<String> getAdditionalUseWithCommands(PassivelyUsable object);

	/**
	 * @param object
	 *            the object
	 * 
	 * @return the forbiddenText for that object or {@code null}.
	 */
	public String getUseWithForbiddenText(PassivelyUsable object);

	/**
	 * @param object
	 *            the object
	 * 
	 * @return the successfulText for that object or {@code null}.
	 */
	public String getUseWithSuccessfulText(PassivelyUsable object);

	/**
	 * @param object
	 *            the object
	 * 
	 * @return if using is enabled with the given object.
	 */
	public boolean isUsingEnabledWith(PassivelyUsable object);

	/**
	 * Removes an additional action for that object.
	 * 
	 * @param object
	 *            the object
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromUseWith(PassivelyUsable object, AbstractAction action);

	/**
	 * Removes an additional use with command.
	 * 
	 * @param object
	 *            the object
	 * @param command
	 *            the command
	 */
	public void removeAdditionalUseWithCommand(PassivelyUsable object, String command);

	/**
	 * Sets the forbidden text for that object. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param object
	 *            the object
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setUseWithForbiddenText(PassivelyUsable object, String forbiddenText);

	/**
	 * Sets the successful text for that object. If {@code null} passed, the
	 * default text will be used when the action is triggered.
	 * 
	 * @param object
	 *            the object
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setUseWithSuccessfulText(PassivelyUsable object, String successfulText);

	/**
	 * @param object
	 *            the object
	 * @param enabled
	 *            if using should be enabled with that item
	 */
	public void setUsingEnabledWith(PassivelyUsable object, boolean enabled);

	/**
	 * Triggers all additional actions for that item, if any.
	 * 
	 * @param object
	 *            the object
	 * @param game
	 *            the game
	 */
	public void useWith(PassivelyUsable object, Game game);

	/**
	 * Sets the additional commands that can be used to use this with something
	 * else.
	 * 
	 * @param object
	 *            the object
	 * @param commands
	 *            the commands
	 */
	public void setAdditionalUseWithCommands(PassivelyUsable object, List<String> commands);
}