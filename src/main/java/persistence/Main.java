package persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import data.Item;
import data.Location;
import data.Player;
import data.Way;

public class Main {

	public static final String PERSISTENCE_UNIT_NAME = "textAdventureMaker";

	public static void main(String[] args) throws Exception {
		// Create everything
		Location flat = new Location();
		flat.setName("Flat");
		flat.setDescription("Your little home.");
		Location balcony = new Location();
		balcony.setName("Balcony");
		balcony.setDescription("Nice wooden floor and some chairs.");

		Way way = new Way();
		way.setName("Balcony door");
		way.setDescription("This door connects your flat with your balcony.");
		flat.addWayOut(way);
		balcony.addWayIn(way);

		Item tv = new Item();
		tv.setName("Television");
		tv.setDescription("32\" television.");
		flat.addItem(tv);
		Item banana = new Item();
		banana.setName("Banana");
		banana.setDescription("Rich in cholesterol.");
		flat.addItem(banana);
		Item chair = new Item();
		chair.setName("Chair");
		chair.setDescription("A wooden chair.");
		balcony.addItem(chair);

		Player player = new Player();
		player.setLocation(flat);

		// Create objects for database access
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// Persist everything
		entityManager.getTransaction().begin();
		entityManager.persist(flat);
		entityManager.persist(balcony);
		entityManager.persist(way);
		entityManager.persist(tv);
		entityManager.persist(banana);
		entityManager.persist(chair);
		entityManager.persist(player);
		entityManager.getTransaction().commit();

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
		Root<Item> itemRoot = criteriaQueryWay.from(Item.class);
		criteriaQueryItem.select(itemRoot);
		List<Item> resultListItem = entityManager
				.createQuery(criteriaQueryItem).getResultList();
		for (Item i : resultListItem) {
			System.out.println(i);
		}
		// Find all players (hopefully only one)
		CriteriaQuery<Player> criteriaQueryPlayer = criteriaBuilder
				.createQuery(Player.class);
		Root<Player> playerRoot = criteriaQueryWay.from(Player.class);
		criteriaQueryPlayer.select(playerRoot);
		List<Player> resultListPlayer = entityManager.createQuery(
				criteriaQueryPlayer).getResultList();
		for (Player p : resultListPlayer) {
			System.out.println(p);
		}

		// Close everything
		entityManager.close();
		entityManagerFactory.close();
	}
}