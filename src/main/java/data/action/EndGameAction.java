package data.action;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import data.Game;

/**
 * Actions that ends a game by setting the {@link Game#hasEnded} attribute to
 * {@code true}.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.PROPERTY)
public class EndGameAction extends AbstractAction {

	/**
	 * No-arg constructor for the database.
	 * 
	 * @deprecated Use {@link #EndGameAction(String)} instead.
	 */
	@Deprecated
	public EndGameAction() {
	}
	
	/**
	 * An enabled EndGameAction.
	 * 
	 * @param name
	 *            the name
	 */
	public EndGameAction(String name) {
		super(name);
	}

	@Override
	protected void doAction(Game game) {
		game.setHasEnded(true);
	}

	@Override
	public String actionDescription() {
		return "Ends the game.";
	}

}