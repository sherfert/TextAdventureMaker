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

public class Main {

	public static final String PERSISTENCE_UNIT_NAME = "textAdventureMaker";

	private static EntityManagerFactory entityManagerFactory;

	private static EntityManager entityManager;

	private static CriteriaBuilder criteriaBuilder;

	public static void connect() {
		// Create objects for database access
		entityManagerFactory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		entityManager = entityManagerFactory.createEntityManager();
		criteriaBuilder = entityManager.getCriteriaBuilder();

		// Begin a transaction
		entityManager.getTransaction().begin();
	}

	public static void updateChanges() {
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
	}

	public static void disconnect() {
		entityManager.getTransaction().commit();
		// Close everything
		entityManager.close();
		entityManagerFactory.close();
	}

	public static void main(String[] args) throws Exception {
		// Create everything
		Location flat = new Location();
		flat.setName("Flat");
		flat.setDescription("Your little home.");
		Location balcony = new Location();
		balcony.setName("Balcony");
		balcony.setDescription("Nice wooden floor and some chairs.");

		Way wayToBalcony = new Way();
		wayToBalcony.setName("Balcony door");
		wayToBalcony.setDescription("This door leads to your balcony.");
		wayToBalcony.setDestination(balcony);
		flat.addWayOut(wayToBalcony);
		Way wayToFlat = new Way();
		wayToFlat.setName("Balcony door");
		wayToFlat.setDescription("This door leads inside your flat.");
		wayToFlat.setDestination(flat);
		balcony.addWayOut(wayToFlat);

		InventoryItem bananaInv = new InventoryItem();
		bananaInv.setName("Banana");
		bananaInv.setDescription("Rich in cholesterol.");

		Item tv = new Item();
		tv.setName("Television");
		tv.setDescription("32\" television.");
		flat.addItem(tv);
		Item banana = new Item();
		banana.setName("Banana");
		banana.setDescription("Rich in cholesterol.");
		banana.addTakeAction();
		banana.getPrimaryAction().addPickUpItem(bananaInv);

		flat.addItem(banana);
		Item chair = new Item();
		chair.setName("Chair");
		chair.setDescription("A wooden chair.");
		balcony.addItem(chair);

		Player player = new Player();
		player.setLocation(flat);
		
		// Game options
		Game game = new Game();
		game.setStartLocation(flat);
		game.setStartText("This is a little text adventure.");
		game.addExitCommand("exit");
		game.addExitCommand("quit");
		game.addInventoryCommand("inventory");
		// FIXME
		game.addMoveCommand("go (.+)");
		game.addMoveCommand("move to (.+)");
		game.addMoveCommand("ship (.+) ya");
		
		game.addTakeCommand("take");

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

		// Find location by id
		Location lo = entityManager.find(Location.class, 1);
		System.out.println(lo);

		// Find all locations
		CriteriaQuery<Location> criteriaQueryLoc = criteriaBuilder
				.createQuery(Location.class);
		Root<Location> locationRoot = criteriaQueryLoc.from(Location.class);
		criteriaQueryLoc.select(locationRoot);
		List<Location> resultListLoc = entityManager.createQuery(
				criteriaQueryLoc).getResultList();
		for (Location l : resultListLoc) {
			System.out.println(l);
		}
		// Find all ways
		CriteriaQuery<Way> criteriaQueryWay = criteriaBuilder
				.createQuery(Way.class);
		Root<Way> wayRoot = criteriaQueryWay.from(Way.class);
		criteriaQueryWay.select(wayRoot);
		List<Way> resultListWay = entityManager.createQuery(criteriaQueryWay)
				.getResultList();
		for (Way w : resultListWay) {
			System.out.println(w);
		}
		// Find all items
		CriteriaQuery<Item> criteriaQueryItem = criteriaBuilder
				.createQuery(Item.class);
		Root<Item> itemRoot = criteriaQueryItem.from(Item.class);
		criteriaQueryItem.select(itemRoot);
		List<Item> resultListItem = entityManager
				.createQuery(criteriaQueryItem).getResultList();
		for (Item i : resultListItem) {
			System.out.println(i);
		}
		// Find player
		Player p = PlayerManager.getPlayer();
		System.out.println(p);

//		// Test: take banana and go to balcony
//		banana.getPrimaryAction().triggerAction();
//		wayToBalcony.getPrimaryAction().triggerAction();
//
//		// Find player again
//		p = PlayerManager.getPlayer();
//		System.out.println(p);
		
		// Start a game
		GamePlayer.start();

		disconnect();
	}

	/**
	 * @return the entityManagerFactory
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * @return the entityManager
	 */
	public static EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @return the criteriaBuilder
	 */
	public static CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}
}