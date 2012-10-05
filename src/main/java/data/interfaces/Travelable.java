package data.interfaces;

import java.util.List;

import data.action.AbstractAction;
import data.action.MoveAction;

/**
 * Anything by which one can travel.
 * 
 * @author Satia
 */
public interface Travelable {
	/**
	 * Adds an additional action.
	 * 
	 * @param action
	 *            the action
	 */
	public void addAdditionalActionToMove(AbstractAction action);
	
	/**
	 * Adds an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void addIdentifier(String name);
	
	/**
	 * @return the additional actions.
	 */
	public List<AbstractAction> getAdditionalActionsFromMove();
	
	/**
	 * @return the identifiers.
	 */
	public List<String> getIdentifiers();
	
	/**
	 * @return the takeAction
	 */
	public MoveAction getMoveAction();
	
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
	 * Removes an identifier.
	 * 
	 * @param name
	 *            the name
	 */
	public void removeIdentifier(String name);

	/**
	 * @param identifiers
	 *            the identifiers to set
	 */
	public void setIdentifiers(List<String> identifiers);

	/**
	 * Sets the forbidden text. If {@code null} passed, the default text will be
	 * used when the action is triggered.
	 * 
	 * @param forbiddenText
	 *            the forbiddenText to set
	 */
	public void setMoveForbiddenText(String forbiddenText);

	/**
	 * Sets the successful text. If {@code null} passed, the default text will
	 * be used when the action is triggered.
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
	 */
	public void travel();
}