package persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
		Location flat = new Location("Flat",
				"Your little home. A banana and a tv.");
		Location balcony = new Location("Balcony",
				"Nice wooden floor and some chairs.");

		Way wayToBalcony = new Way("Balcony door",
				"This door leads to your balcony.", flat, balcony);
		Way wayToFlat = new Way("Balcony door",
				"This door leads inside your flat.", balcony, flat);

		InventoryItem bananaInv = new InventoryItem("Banana", "Rich in cholesterol.");

		Item tv = new Item(flat, "Television", "32\" television.");
		tv.setPrimaryActionEnabled(false);
		tv.setForbiddenText("This is a little heavy.");
		Item banana = new Item(flat, "Banana", "Rich in cholesterol.");
		banana.setSuccessfulText("Ya got bananaaa-a-a.");
		banana.getPrimaryAction().addPickUpItem(bananaInv);
		Item chair = new Item(balcony, "Chair", "A wooden chair.");
		chair.setPrimaryActionEnabled(false);

		Player player = new Player(flat);

		// Game options
		Game game = new Game();
		game.setStartLocation(flat);

		game.setInventoryEmptyText("Your inventory is empty.");
		game.setNoCommandText("I do not understand you.");
		game.setNoSuchItemText("There is no <item> here.");
		game.setNotTakeableText("You cannot take the <item>.");
		game.setStartText("This is a little text adventure.");
		game.setTakenText("You picked up the <item>.");

		game.addExitCommand("exit");
		game.addExitCommand("quit");

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

//		// Find location by id
//		Location lo = entityManager.find(Location.class, 1);
//		System.out.println(lo);
//
//		// Find all locations
//		CriteriaQuery<Location> criteriaQueryLoc = criteriaBuilder
//				.createQuery(Location.class);
//		Root<Location> locationRoot = criteriaQueryLoc.from(Location.class);
//		criteriaQueryLoc.select(locationRoot);
//		List<Location> resultListLoc = entityManager.createQuery(
//				criteriaQueryLoc).getResultList();
//		for (Location l : resultListLoc) {
//			System.out.println(l);
//		}
//		// Find all ways
//		CriteriaQuery<Way> criteriaQueryWay = criteriaBuilder
//				.createQuery(Way.class);
//		Root<Way> wayRoot = criteriaQueryWay.from(Way.class);
//		criteriaQueryWay.select(wayRoot);
//		List<Way> resultListWay = entityManager.createQuery(criteriaQueryWay)
//				.getResultList();
//		for (Way w : resultListWay) {
//			System.out.println(w);
//		}
//		// Find all items
//		CriteriaQuery<Item> criteriaQueryItem = criteriaBuilder
//				.createQuery(Item.class);
//		Root<Item> itemRoot = criteriaQueryItem.from(Item.class);
//		criteriaQueryItem.select(itemRoot);
//		List<Item> resultListItem = entityManager
//				.createQuery(criteriaQueryItem).getResultList();
//		for (Item i : resultListItem) {
//			System.out.println(i);
//		}
//		// Find player
//		Player p = PlayerManager.getPlayer();
//		System.out.println(p);

		// Start a game
		GamePlayer.start();

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