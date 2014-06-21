package persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

import com.googlecode.lanterna.terminal.Terminal.Color;

import playing.GamePlayer;
import data.Game;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.Person;
import data.Player;
import data.Way;
import data.action.AddInventoryItemsAction;
import data.action.ChangeNamedObjectAction;
import data.action.RemoveInventoryItemAction;
import data.action.SetItemLocationAction;

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
		// FIXME at the moment close the vm
		System.exit(0);
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
		// FIXME this must be put into a proper initialize method
		Class.forName(logging.LogManager.class.getName());

		// Create everything
		Location flat = new Location("Flat", "Your little home.");
		Location balcony = new Location("Balcony", "Your balcony.");
		balcony.setInspectionText("Nice wooden floor and some chairs.");

		Way wayToBalcony = new Way("Balcony door",
				"This door leads to your balcony.", flat, balcony);
		wayToBalcony.removeIdentifier(wayToBalcony.getName());
		wayToBalcony.addIdentifier("out(side)?");
		wayToBalcony.addIdentifier("(on |to )?(the )?balcony");
		wayToBalcony.addIdentifier("through (the )?balcony door");
		wayToBalcony.setMoveSuccessfulText("You go outside on the balcony.");
		Way wayToFlat = new Way("Balcony door",
				"This door leads inside your flat.", balcony, flat);
		wayToFlat.removeIdentifier(wayToFlat.getName());
		wayToFlat.addIdentifier("in(side)?");
		wayToFlat.addIdentifier("into (the )?flat");
		wayToFlat.addIdentifier("through (the )?balcony door");
		wayToFlat.setMoveSuccessfulText("You go back inside");
		wayToFlat.setMoveForbiddenText("I feel like you need something from here before going back in.");
		// TODO only let him in if he has the chair

		/*
		 * Inspecting satia will give you 5 bucks. You can "give them back" by
		 * using them with him. This is repeatable.
		 * 
		 * TODO getting money should only be possible once or after giving it
		 * back!
		 */
		InventoryItem money = new InventoryItem("Money", "5 bucks");
		money.setInspectionText("You stole them from poor Satia.");
		AddInventoryItemsAction addMoneyAction = new AddInventoryItemsAction();
		addMoneyAction.addPickUpItem(money);
		RemoveInventoryItemAction removeMoneyAction = new RemoveInventoryItemAction(
				money);

		Person satia = new Person(flat, "Satia",
				"Satia is hanging around there.");
		satia.setInspectionText("He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");
		satia.addAdditionalActionToInspect(addMoneyAction);

		ChangeNamedObjectAction changeSatiaAction1 = new ChangeNamedObjectAction(
				satia);
		changeSatiaAction1
				.setNewInspectionText("He looks pretty busy programming nonsense stuff. You stole the poor guy his last 5 bucks.");
		ChangeNamedObjectAction changeSatiaAction2 = new ChangeNamedObjectAction(
				satia);
		changeSatiaAction2
				.setNewInspectionText("He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");

		satia.addAdditionalActionToInspect(changeSatiaAction1);

		money.setUsingEnabledWith(satia, true);
		money.setUseWithSuccessfulText(satia,
				"You feel guilty and put the money back.");
		money.addAdditionalActionToUseWith(satia, removeMoneyAction);
		money.addAdditionalActionToUseWith(satia, changeSatiaAction2);
		
		// TODO EnableActionAction / DisableActionAction

		Item tv = new Item(flat, "Television", "A television.");
		tv.setInspectionText("A 32\" television.");
		tv.addIdentifier("tv");
		tv.setTakeForbiddenText("This is a little heavy.");
		tv.setUseForbiddenText("I am not in the mood.");
		/*
		 * Inspecting the tv will change its inspection text.
		 */
		ChangeNamedObjectAction changeTVAction = new ChangeNamedObjectAction(tv);
		changeTVAction
				.setNewInspectionText("A 32\" television. You should not waste your time admiring it.");
		tv.addAdditionalActionToInspect(changeTVAction);

		/*
		 * A takeable banana. If the banana (as an item OR as an inventory item)
		 * is being used the item/inventory item disappears and the peel is
		 * being added to the inventory.
		 */
		Item banana = new Item(flat, "Banana", "A banana.");
		banana.setInspectionText("Rich in cholesterol.");
		banana.setTakingEnabled(true);
		banana.setUsingEnabled(true);
		banana.setUseSuccessfulText("You ate the banana. The peel looks useful, so you kept it.");
		InventoryItem peel = new InventoryItem("Banana peel",
				"The peel of the banana you ate.");
		peel.setInspectionText("You ate the banana inside it.");
		peel.addIdentifier("peel");
		peel.setUseForbiddenText("Do you want to eat the peel, too?");
		AddInventoryItemsAction addPeelAction = new AddInventoryItemsAction();
		addPeelAction.addPickUpItem(peel);
		banana.addAdditionalActionToUse(addPeelAction);
		banana.addAdditionalActionToUse(new SetItemLocationAction(banana, null));
		banana.getAddInventoryItemsAction().addPickUpItem(
				new InventoryItem(banana));

		Item chair = new Item(balcony, "Chair", "A wooden chair.");
		chair.setInspectionText("Made of solid oak.");
		chair.setTakingEnabled(true);
		InventoryItem invChair = new InventoryItem(chair);
		chair.getAddInventoryItemsAction().addPickUpItem(invChair);

		/*
		 * The tv can be hit with the chair. It is then replaced with a
		 * destroyed tv.
		 */
		Item destroyedTv = new Item(null, "Destroyed television",
				"A destroyed television.");
		destroyedTv
				.setInspectionText("You hit it several times with the chair.");
		destroyedTv.addIdentifier("television");
		destroyedTv.addIdentifier("tv");
		destroyedTv
				.setTakeForbiddenText("What the hell do you want with this mess?");
		destroyedTv.setUseForbiddenText("It does not work anymore.");

		invChair.addAdditionalActionToUseWith(tv, new SetItemLocationAction(
				destroyedTv, flat));
		invChair.addAdditionalActionToUseWith(tv, new SetItemLocationAction(tv,
				null));
		invChair.setUseWithSuccessfulText(tv,
				"You smash the chair into the television.");
		invChair.setUsingEnabledWith(tv, true);

		/*
		 * A pen that can be used to paint the banana peel. The pen can be used
		 * for that when in the flat and when already taken.
		 */
		Item pen = new Item(flat, "Pen", "A pen.");
		pen.setInspectionText("An advertising gift.");
		pen.setUseForbiddenText("You must use it with something else.");
		pen.setTakingEnabled(true);
		InventoryItem invPen = new InventoryItem(pen);
		pen.getAddInventoryItemsAction().addPickUpItem(invPen);

		InventoryItem paintedPeel = new InventoryItem("Painted banana peel",
				"The peel of the banana you ate.");
		paintedPeel
				.setInspectionText("You ate the banana and painted the peel.");
		paintedPeel.addIdentifier("peel");
		paintedPeel.addIdentifier("banana peel");

		peel.setUsingEnabledWith(pen, true);
		peel.setCombiningEnabledWith(invPen, true);
		peel.setUseWithSuccessfulText(pen, "You painted the banana peel.");
		peel.setCombineWithSuccessfulText(invPen,
				"You painted the banana peel.");
		peel.addNewInventoryItemWhenCombinedWith(invPen, paintedPeel);

		AddInventoryItemsAction addPaintedPeelAction = new AddInventoryItemsAction();
		RemoveInventoryItemAction removePeelAction = new RemoveInventoryItemAction(
				peel);
		addPaintedPeelAction.addPickUpItem(paintedPeel);
		peel.addAdditionalActionToUseWith(pen, addPaintedPeelAction);
		peel.addAdditionalActionToUseWith(pen, removePeelAction);
		peel.addAdditionalActionToCombineWith(invPen, removePeelAction);

		Player player = new Player(flat);
		// Game options
		Game game = new Game();
		game.setStartLocation(flat);
		game.setStartText("This is a little text adventure.");

		game.setInspectionDefaultText("Nothing interesting.");
		game.setInventoryEmptyText("Your inventory is empty.");
		game.setInventoryText("You are carrying the following things:");
		game.setNoCommandText("I do not understand you.");
		game.setNoSuchInventoryItemText("You do not have a <identifier>.");
		game.setNoSuchItemText("There is no <identifier> here.");
		game.setNoSuchWayText("You cannot <input>.");
		game.setNotTakeableText("You cannot take the <name>.");
		game.setNotTravelableText("You cannot <input>.");
		game.setNotUsableText("You cannot use the <name>.");
		game.setNotUsableWithText("You cannot use the <name> with the <name2>.");
		game.setTakenText("You picked up the <name>.");
		game.setUsedText("So you used the <name>. Nothing interesting happened.");
		game.setUsedWithText("So you used the <name> with the <name2>. Nothing interesting happened.");
		game.setSuccessfullFgColor(Color.GREEN);
		game.setNeutralFgColor(Color.YELLOW);
		game.setFailedFgColor(Color.RED);
		game.setSuccessfullBgColor(Color.DEFAULT);
		game.setNeutralBgColor(Color.DEFAULT);
		game.setFailedBgColor(Color.DEFAULT);

		game.addExitCommand("exit");
		game.addExitCommand("quit");
		game.addInspectCommand("look(?: at)? (.+)");
		game.addInspectCommand("inspect (.+)");
		game.addInventoryCommand("inventory");
		game.addMoveCommand("go(?: to)? (.+)");
		game.addMoveCommand("move(?: to)? (.+)");
		game.addTakeCommand("take (.+)");
		game.addTakeCommand("pick up (.+)");
		game.addTakeCommand("pick (.+) up");
		game.addUseCommand("use (.+)");
		game.addUseWithCombineCommand("use (.+) with (.+)");
		game.addUseWithCombineCommand("combine (.+) and (.+)");

		// Connect to database
		connect();

		// Persist everything (Cascade.PERSIST persists the rest)
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
	}

	/**
	 * Updates any changes. Should be called after each change of persisted
	 * data.
	 * 
	 * TODO call this in triggerAction or not?
	 */
	public static void updateChanges() {
		entityManager.getTransaction().commit();
		entityManager.getTransaction().begin();
	}
}