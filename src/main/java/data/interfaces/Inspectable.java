package data.interfaces;

import java.util.List;

import data.Game;
import data.action.AbstractAction;

/**
 * Anything inspectable in the game.
 * 
 * @author Satia
 */
public interface Inspectable extends Identifiable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalInspectAction(AbstractAction action);

	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalInspectActions();

	/**
	 * @return the text being displayed when inspected or {@code null} if the
	 *         default text is to be used.
	 */
	public String getInspectionText();

	/**
	 * Triggers all additional actions.
	 * 
	 * @param game
	 *            the game
	 */
	public void inspect(Game game);

	/**
	 * Removes an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void removeAdditionalInspectAction(AbstractAction action);

	/**
	 * Sets the inspection text. If {@code null} passed, the default text will
	 * be used.
	 * 
	 * @param inspectionText
	 *            the inspectionText to set
	 */
	public void setInspectionText(String inspectionText);

	/**
	 * Sets the identifiers.
	 * 
	 * @param identifiers
	 *            the identifiers to set
	 */
	public void setIdentifiers(List<String> identifiers);

	/**
	 * Sets the additional inspect actions.
	 * 
	 * @param additionalInspectActions
	 *            the additionalInspectActions to set
	 */
	public void setAdditionalInspectActions(List<AbstractAction> additionalInspectActions);
}