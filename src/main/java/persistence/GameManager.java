package persistence;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.googlecode.lanterna.terminal.Terminal.Color;

import data.Game;
import data.Location;
import data.Player;
import exception.DBClosedException;
import exception.DBIncompatibleException;

/**
 * Managing access to the game in a database.
 *
 * @author Satia
 */
public class GameManager {

	/**
	 * A reference to the overall manager of the persistence.
	 */
	private PersistenceManager persistenceManager;

	/**
	 * The game. Is retrieved from the DB once and kept in memory until reset is
	 * called.
	 */
	private Game game;

	/**
	 * @param persistenceManager
	 */
	public GameManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * Resets the state of the GameManager by deleting its reference to the
	 * Game.
	 */
	public void reset() {
		game = null;
	}

	/**
	 * Retrieves the game. Uses the in-memory reference if not {@code null} or
	 * retrieves the game from the DB otherwise.
	 * 
	 * @return the game
	 * 
	 * @throws DBIncompatibleException
	 *             if the database is not compatible with the model.
	 * @throws DBClosedException
	 *             if the DB is closed
	 */
	public Game getGame() throws DBIncompatibleException, DBClosedException {
		if (game != null) {
			return game;
		}

		// Find all games (hopefully only one)
		CriteriaQuery<Game> criteriaQueryGame = persistenceManager.getCriteriaBuilder().createQuery(Game.class);
		Root<Game> gameRoot = criteriaQueryGame.from(Game.class);
		criteriaQueryGame.select(gameRoot);
		List<Game> resultListGame = persistenceManager.getEntityManager().createQuery(criteriaQueryGame)
				.getResultList();

		// There should be exactly 1 game
		if (resultListGame.size() != 1) {
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There are {0} games.",
					resultListGame.size());
			throw new DBIncompatibleException("There is no game in the database.");
		}

		return game = resultListGame.get(0);
	}

	/**
	 * Retrieves the title of the game via a SQL select. Does not cache the
	 * game.
	 * 
	 * @return the game title.
	 * @throws DBClosedException 
	 */
	public String getGameTitle() throws DBIncompatibleException, DBClosedException {
		if (game != null) {
			return game.getGameTitle();
		}

		// Find all games (hopefully only one)
		CriteriaQuery<String> criteriaQueryGame = persistenceManager.getCriteriaBuilder().createQuery(String.class);
		Root<Game> gameRoot = criteriaQueryGame.from(Game.class);
		criteriaQueryGame.select(gameRoot.get("gameTitle"));
		List<String> resultList = persistenceManager.getEntityManager().createQuery(criteriaQueryGame).getResultList();

		// There should be exactly 1 game
		if (resultList.size() != 1) {
			Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, "There are {0} games.", resultList.size());
			throw new DBIncompatibleException("There is no game in the database.");
		}

		return resultList.get(0);
	}

	/**
	 * This method places a minimal default game in the Database. It is only
	 * supposed to be called on an empty database.
	 * @throws DBClosedException 
	 */
	public void loadDefaultGame() throws DBClosedException {
		Player player = new Player();
		Game game = new Game();
		Location startLocation = new Location("Default location", "This is where your adventure starts.");
		game.setPlayer(player);
		game.setStartLocation(startLocation);

		game.setStartText("This text is being displayed at the beginning of the game.");

		// Default commands and messages
		// TODO configurable
		game.setNoCommandText("I don't understand you.");
		game.setInvalidCommandText("This doesn't make sense.");
		game.setNoSuchInventoryItemText("You do not have a <identifier>.");
		game.setNoSuchItemText("There is no <identifier> here.");
		game.setNoSuchPersonText("There is no <Identifier> here.");
		game.setNoSuchWayText("You cannot <input>.");
		game.setNotTakeableText("You cannot <pattern|the <name>||>.");
		game.setNotTravelableText("You cannot <pattern|the <name>||>.");
		game.setNotUsableText("You cannot <pattern|the <name>||>.");
		game.setNotTalkingToEnabledText("You cannot <pattern|<Name>||>.");
		game.setNotUsableWithText("You cannot do that.");

		game.setInspectionDefaultText("Nothing interesting.");
		game.setInventoryEmptyText("Your inventory is empty.");
		game.setInventoryText("You are carrying the following things:");
		game.setTakenText("You picked up the <name>.");
		game.setUsedText("You <pattern|the <name>||>. Nothing interesting happens.");
		game.setUsedWithText("Nothing interesting happens.");

		game.setSuccessfullFgColor(Color.GREEN);
		game.setNeutralFgColor(Color.YELLOW);
		game.setFailedFgColor(Color.RED);
		game.setSuccessfullBgColor(Color.DEFAULT);
		game.setNeutralBgColor(Color.DEFAULT);
		game.setFailedBgColor(Color.DEFAULT);
		game.setNumberOfOptionLines(10);

		game.setExitCommandHelpText("Exit the game");
		game.setHelpHelpText("Display this help");
		game.setInspectHelpText("Inspect an item of your curiosity");
		game.setInventoryHelpText("Look into your inventory");
		game.setLookAroundHelpText("Look around");
		game.setMoveHelpText("Move somewhere else");
		game.setTakeHelpText("Pick something up and put it in your inventory");
		game.setUseHelpText("Use an item in any imagineable way");
		game.setUseWithCombineHelpText("Use one item with another or combine two items in your inventory");
		game.setTalkToHelpText("Talk to someone");

		game.addExitCommand("exit");
		game.addExitCommand("quit");
		game.addInspectCommand("look( at)? (?<o0>.+?)");
		game.addInspectCommand("inspect (?<o0>.+?)");
		game.addInventoryCommand("inventory");
		game.addHelpCommand("help");
		game.addLookAroundCommand("look around");
		game.addMoveCommand("go( to)? (?<o0>.+?)");
		game.addMoveCommand("move( to)? (?<o0>.+?)");
		game.addTakeCommand("take (?<o0>.+?)");
		game.addTakeCommand("pick up (?<o0>.+?)");
		game.addTakeCommand("pick (?<o0>.+?) up");
		game.addUseCommand("use (?<o0>.+?)");
		game.addUseWithCombineCommand("use (?<o0>.+?) with (?<o1>.+?)");
		game.addUseWithCombineCommand("combine (?<o0>.+?) and (?<o1>.+?)");
		game.addUseWithCombineCommand("combine (?<o0>.+?) with (?<o1>.+?)");
		game.addTalkToCommand("talk to (?<o0>.+?)");
		game.addTalkToCommand("speak with (?<o0>.+?)");

		// Random game title from 0 to 999999
		game.setGameTitle("Adventure-" + (int) (Math.random() * 1000000));

		// Save the game
		persistenceManager.getEntityManager().persist(game);
		persistenceManager.updateChanges();
	}
}
