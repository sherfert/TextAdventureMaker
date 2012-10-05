package persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

import playing.GamePlayer;
import data.Game;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.Player;
import data.Way;

/**
 * TODO Test class.
 * 
 * @author Satia
 */
public class Main {

	/**
	 * The name of the persistence unit. Specified in
	 * src/main/resources/persistence.xml
	 */
	public static final String PERSISTENCE_UNIT_NAME = "textAdventureMaker";

	/**
	 * The criteria builder.
	 */
	private static CriteriaBuilder criteriaBuilder;

	/**
	 * The entity manager.
	 */
	private static EntityManager entityManager;

	/**
	 * The entity manager factory.
	 */
	private static EntityManagerFactory entityManagerFactory;

	/**
	 * Connects to the database.
	 */
	public static void connect() {
		// Create objects for database access
		entityManagerFactory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		entityManager = entityManagerFactory.createEntityManager();
		criteriaBuilder = entityManager.getCriteriaBuilder();

		// Begin a transaction
		entityManager.getTransaction().begin();
	}

	/**
	 * Disconnects from the database.
	 */
	public static void disconnect() {
		entityManager.getTransaction().commit();
		// Close everything
		entityManager.close();
		entityManagerFactory.close();
	}

	/**
	 * @return the criteriaBuilder
	 */
	public static CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}

	/**
	 * @return the entityManager
	 */
	public static EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @return the entityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * Test-main.
	 */
	public static void main(String[] args) throws Exception {
		// Create everything
		Location flat = new Location("Flat", "Your little home.",
				"Nothing interesting.");
		Location balcony = new Location("Balcony", "Your balcony.",
				"Nice wooden floor and some chairs.");

		Way wayToBalcony = new Way("Balcony door",
				"This door leads to your balcony.", "Nothing special", flat,
				balcony);
		wayToBalcony.removeIdentifier(wayToBalcony.getName());
		wayToBalcony.addIdentifier("out(side)?");
		wayToBalcony.addIdentifier("on (the )?balcony");
		wayToBalcony.addIdentifier("through (the )?balcony door");
		Way wayToFlat = new Way("Balcony door",
				"This door leads inside your flat.", "Nothing special",
				balcony, flat);
		wayToFlat.removeIdentifier(wayToFlat.getName());
		wayToFlat.addIdentifier("in(side)?");
		wayToFlat.addIdentifier("into (the )?flat");
		wayToFlat.addIdentifier("through (the )?balcony door");

		Item tv = new Item(flat, "Television", "A television.",
				"A 32\" television.");
		tv.addIdentifier("tv");
		tv.setTakingEnabled(false);
		tv.setTakeForbiddenText("This is a little heavy.");
		Item banana = new Item(flat, "Banana", "A banana.",
				"Rich in cholesterol.");
		InventoryItem bananaInv = new InventoryItem(banana);
		banana.setTakeSuccessfulText("Ya got bananaaa-a-a.");
		banana.getTakeAction().addPickUpItem(bananaInv);
		Item chair = new Item(balcony, "Chair", "A wooden chair.",
				"Nothing special.");
		chair.setTakingEnabled(false);

		Player player = new Player(flat);

		// Game options
		Game game = new Game();
		game.setStartLocation(flat);

		game.setInventoryEmptyText("Your inventory is empty.");
		game.setInventoryText("You are carrying the following things:");
		game.setNoCommandText("I do not understand you.");
		game.setNoSuchItemText("There is no <identifier> here.");
		game.setNoSuchWayText("You cannot go <identifier>.");
		game.setNotTakeableText("You cannot take the <identifier>.");
		game.setNotTravelableText("You cannot go <identifier>.");
		game.setStartText("This is a little text adventure.");
		game.setTakenText("You picked up the <identifier>.");

		game.addExitCommand("exit");
		game.addExitCommand("quit");

		game.addInspectCommand("look at (.+)");
		game.addInspectCommand("inspect (.+)");
		game.addInventoryCommand("inventory");
		game.addMoveCommand("go (.+)");
		game.addMoveCommand("move to (.+)");
		game.addTakeCommand("take (.+)");
		game.addTakeCommand("pick up (.+)");
		game.addTakeCommand("pick (.+) up");

		// Connect to database
		connect();

		// Persist everything
		entityManager.persist(flat);
		entityManager.persist(balcony);
		entityManager.persist(tv);
		entityManager.persist(banana);
		entityManager.persist(chair);
		entityManager.persist(player);
		entityManager.persist(game);

		// Updates changes
		updateChanges();

		// // Find location by id
		// Location lo = entityManager.find(Location.class, 1);
		// System.out.println(lo);
		//
		// // Find all locations
		// CriteriaQuery<Location> criteriaQueryLoc = criteriaBuilder
		// .createQuery(Location.class);
		// Root<Location> locationRoot = criteriaQueryLoc.from(Location.class);
		// criteriaQueryLoc.select(locationRoot);
		// List<Location> resultListLoc = entityManager.createQuery(
		// criteriaQueryLoc).getResultList();
		// for (Location l : resultListLoc) {
		// System.out.println(l);
		// }
		// // Find all ways
		// CriteriaQuery<Way> criteriaQueryWay = criteriaBuilder
		// .createQuery(Way.class);
		// Root<Way> wayRoot = criteriaQueryWay.from(Way.class);
		// criteriaQueryWay.select(wayRoot);
		// List<Way> resultListWay = entityManager.createQuery(criteriaQueryWay)
		// .getResultList();
		// for (Way w : resultListWay) {
		// System.out.println(w);
		// }
		// // Find all items
		// CriteriaQuery<Item> criteriaQueryItem = criteriaBuilder
		// .createQuery(Item.class);
		// Root<Item> itemRoot = criteriaQueryItem.from(Item.class);
		// criteriaQueryItem.select(itemRoot);
		// List<Item> resultListItem = entityManager
		// .createQuery(criteriaQueryItem).getResultList();
		// for (Item i : resultListItem) {
		// System.out.println(i);
		// }
		// // Find player
		// Player p = PlayerManager.getPlayer();
		// System.out.println(p);

		// Start a game
		new GamePlayer(GameManager.getGame(), PlayerManager.getPlayer())
				.start();
		// Disconnect
		disconnect();
	}

	/**
	 * Updates any changes.
	 * 
	 * TODO is this necessary?
	 */
	public static void updateChanges() {
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
	}
}