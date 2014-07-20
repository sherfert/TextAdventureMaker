package data.action;

import javax.persistence.Entity;

import persistence.GameManager;
import data.Game;

/**
 * Actions that ends a game by setting the {@link Game#hasEnded} attribute to
 * {@code true}.
 * 
 * @author Satia
 */
@Entity
public class EndGameAction extends AbstractAction {

	/**
	 * An enabled EndGameAction.
	 */
	public EndGameAction() {
	}

	/**
	 * An EndGameAction.
	 * 
	 * @param enabled
	 *            if the action should be enabled
	 */
	public EndGameAction(boolean enabled) {
		super(enabled);
	}

	@Override
	protected void doAction() {
		GameManager.getGame().setHasEnded(true);
	}

	@Override
	public String getActionDescription() {
		return "Ends the game.";
	}

	@Override
	public String toString() {
		return "EndGameAction{ " + super.toString() + "}";
	}

}