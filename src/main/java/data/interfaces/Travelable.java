package data.interfaces;

import java.util.List;

import data.Game;
import data.Location;
import data.action.AbstractAction;
import data.action.MoveAction;

/**
 * Anything by which one can travel.
 * 
 * @author Satia
 */
public interface Travelable extends Identifiable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToMove(AbstractAction action);

	/**
	 * Adds an additional command that can be used to travel here.
	 * 
	 * @param command
	 *            the command
	 */
	public void addAdditionalTravelCommand(String command);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalMoveActions();

	/**
	 * @return the additional travel commands.
	 */
	public List<String> getAdditionalTravelCommands();

	/**
	 * @return the destination
	 */
	public Location getDestination();

	/**
	 * @return the forbiddenText or {@code null}.
	 */
	public String getMoveForbiddenText();

	/**
	 * @return the successfulText or {@code null}.
	 */
	public String getMoveSuccessfulText();

	/**
	 * @return if moving is enabled.
	 */
	public boolean isMovingEnabled();

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalActionFromMove(AbstractAction action);

	/**
	 * Removes an additional travel command
	 * 
	 * @param command
	 *            the command
	 */
	public void removeAdditionalTravelCommand(String command);

	/**
	 * Sets the forbidden text. If {@code null} passed, the default text will be
	 * used when the action is triggered.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setMoveForbiddenText(String forbiddenText);

	/**
	 * Sets the successful text. If {@code null} passed, no text will be
	 * displayed.
	 * 
	 * @param successfulText
	 *            the successfulText to set
	 */
	public void setMoveSuccessfulText(String successfulText);

	/**
	 * @param enabled
	 *            if taking should be enabled
	 */
	public void setMovingEnabled(boolean enabled);

	/**
	 * Triggers the {@link MoveAction} and all additional actions.
	 * 
	 * @param game
	 *            the game
	 */
	public void travel(Game game);

	/**
	 * Set the destination.
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Location destination);

	/**
	 * Sets additional travel commands.
	 * 
	 * @param additionalTravelCommands
	 *            the additionalTravelCommands to set
	 */
	public void setAdditionalTravelCommands(List<String> additionalTravelCommands);

	/**
	 * Sets additional move actions
	 * 
	 * @param additionalMoveActions
	 *            the additionalMoveActions to set
	 */
	public void setAdditionalMoveActions(List<AbstractAction> additionalMoveActions);
}