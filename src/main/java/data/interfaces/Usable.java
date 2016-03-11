package data.interfaces;

import java.util.List;

import data.Game;
import data.action.AbstractAction;

/**
 * Anything usable for itself (without other objects) in the game.
 * 
 * @author Satia
 */
public interface Usable extends Identifiable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToUse(AbstractAction action);

	/**
	 * Adds an additional command that can be used to use the object.
	 * 
	 * @param command
	 *            the command
	 */
	public void addAdditionalUseCommand(String command);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalUseActions();

	/**
	 * @return the additional use commands.
	 */
	public List<String> getAdditionalUseCommands();

	/**
	 * @return the forbiddenText or {@code null}.
	 */
	public String getUseForbiddenText();

	/**
	 * @return the successfulText or {@code null}.
	 */
	public String getUseSuccessfulText();

	/**
	 * @return if using is enabled.
	 */
	public boolean getUsingEnabled();

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromUse(AbstractAction action);

	/**
	 * Removes an additional use command
	 * 
	 * @param command
	 *            the command
	 */
	public void removeAdditionalUseCommand(String command);

	/**
	 * Sets the forbidden text. If {@code null} passed, the default text will be
	 * used when the action is triggered.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setUseForbiddenText(String forbiddenText);

	/**
	 * Sets the successful text. If {@code null} passed, the default text will
	 * be used when the action is triggered.
	 * 
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setUseSuccessfulText(String successfulText);

	/**
	 * @param enabled
	 *            if using should be enabled
	 */
	public void setUsingEnabled(boolean enabled);

	/**
	 * Triggers all additional actions.
	 * 
	 * @param game
	 *            the game
	 */
	public void use(Game game);

	/**
	 * Sets additional use commands.
	 * 
	 * @param additionalUseCommands
	 *            the additionalUseCommands to set
	 */
	public void setAdditionalUseCommands(List<String> additionalUseCommands);

	/**
	 * Sets additional use actions.
	 * 
	 * @param additionalUseActions
	 *            the additionalUseActions to set
	 */
	public void setAdditionalUseActions(List<AbstractAction> additionalUseActions);
}