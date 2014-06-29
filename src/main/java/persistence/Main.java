package persistence;

import java.io.File;

import com.googlecode.lanterna.terminal.Terminal.Color;

import playing.GamePlayer;
import data.Conversation;
import data.ConversationLayer;
import data.ConversationOption;
import data.Game;
import data.InventoryItem;
import data.Item;
import data.Location;
import data.Person;
import data.Player;
import data.Way;
import data.action.AbstractAction.Enabling;
import data.action.AddInventoryItemsAction;
import data.action.ChangeActionAction;
import data.action.ChangeConversationAction;
import data.action.ChangeConversationOptionAction;
import data.action.ChangeInspectableObjectAction;
import data.action.ChangeNamedObjectAction;
import data.action.ChangePersonAction;
import data.action.ChangeWayAction;
import data.action.MultiAction;
import data.action.RemoveInventoryItemAction;
import data.action.ChangeItemAction;

/**
 * Test class.
 * 
 * @author Satia
 */
public class Main {

	/**
	 * Test-main.
	 * 
	 * @param args
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		// FIXME this must be put into a proper initialize method
		Class.forName(logging.LogManager.class.getName());

		// Create everything
		Location flat = new Location("Flat", "Your little home.");
		Location balcony = new Location("Balcony",
				"Your balcony. Sitting on the chair you can look at the sunset.");

		ChangeNamedObjectAction changeBalconyDescriptionAction = new ChangeNamedObjectAction(
				balcony);
		changeBalconyDescriptionAction
				.setNewDescription("Your balcony. Standing around stupidly you can look at the sunset.");

		ChangeNamedObjectAction changeFlatDescriptionAction = new ChangeNamedObjectAction(
				flat);
		changeFlatDescriptionAction
				.setNewDescription("Your little home. But now, that you know the answer to everything, you take a completely different look at things.");

		Way wayToBalcony = new Way("Balcony door",
				"There is a door that leads outside.", flat, balcony);
		wayToBalcony.removeIdentifier(wayToBalcony.getName());
		wayToBalcony.addIdentifier("out(side)?");
		wayToBalcony.addIdentifier("(on |to )?(the )?balcony");
		wayToBalcony.addIdentifier("through (the )?balcony door");
		wayToBalcony.setMoveSuccessfulText("You go outside on the balcony.");
		Way wayToFlat = new Way("Balcony door",
				"There is a door that leads inside.", balcony, flat);
		wayToFlat.removeIdentifier(wayToFlat.getName());
		wayToFlat.addIdentifier("in(side)?");
		wayToFlat.addIdentifier("into (the )?flat");
		wayToFlat.addIdentifier("through (the )?balcony door");
		wayToFlat.setMoveSuccessfulText("You go back inside");
		wayToFlat
				.setMoveForbiddenText("I feel like you need something from here before going back in.");
		wayToFlat.setMovingEnabled(false);

		/*
		 * Inspecting satia will give you 5 bucks. You can "give them back" by
		 * using them with him. This is repeatable.
		 */
		Person satia = new Person(flat, "Satia",
				"Satia is hanging around there.");
		satia.setInspectionText("He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");
		InventoryItem money = new InventoryItem("Money", "5 bucks");
		money.setInspectionText("You stole them from poor Satia.");

		// Create conversations first, add other stuff later
		Conversation satiaConversation = new Conversation(
				"I'm busy, keep it short.");
		Conversation satiaShortConversation = new Conversation(
				"Hey. Gimme back my money! Douche!",
				"Satia takes his money back semi-violently.");

		// Combining these 2 actions
		MultiAction stealMoneyAction;
		{
			AddInventoryItemsAction addMoneyAction = new AddInventoryItemsAction();
			addMoneyAction.addPickUpItem(money);
			// The order here is critical!
			stealMoneyAction = new MultiAction(addMoneyAction);
			ChangeActionAction disableStealMoneyAction = new ChangeActionAction(
					stealMoneyAction, Enabling.DISABLE);
			stealMoneyAction.addAction(disableStealMoneyAction);

		}

		// Combining these 3 actions
		MultiAction giveMoneyBackAction;
		{
			RemoveInventoryItemAction removeMoneyAction = new RemoveInventoryItemAction(
					money);
			ChangePersonAction changeSatiaAction2 = new ChangePersonAction(
					satia);
			changeSatiaAction2
					.setNewInspectionText("He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");
			changeSatiaAction2.setNewConversation(satiaConversation);
			ChangeActionAction enableAddMoneyAction = new ChangeActionAction(
					stealMoneyAction, Enabling.ENABLE);
			giveMoneyBackAction = new MultiAction(removeMoneyAction,
					changeSatiaAction2, enableAddMoneyAction);
		}

		// The order here is critical!
		satia.addAdditionalActionToInspect(stealMoneyAction);

		ChangePersonAction changeSatiaAction1 = new ChangePersonAction(satia);
		changeSatiaAction1
				.setNewInspectionText("He looks pretty busy programming nonsense stuff. You stole the poor guy his last 5 bucks.");
		changeSatiaAction1.setNewConversation(satiaShortConversation);

		satia.addAdditionalActionToInspect(changeSatiaAction1);

		/*
		 * The normal conversation with Satia.
		 */
		ConversationLayer startLayer = new ConversationLayer();
		ConversationLayer csLayer = new ConversationLayer();

		startLayer.addOption(new ConversationOption("Why so hostile?",
				"Just TextAdventureMaker is harder to code than I though!",
				startLayer));
		startLayer.addOption(new ConversationOption(
				"Let's talk about computer science.", "Ask me anything.",
				csLayer));
		startLayer.addOption(new ConversationOption("I'm gonne leave you now.",
				"Finally.", null));

		ConversationOption option42 = new ConversationOption(
				"What is the answer to everything?", "42", csLayer);
		option42.addAdditionalAction(changeFlatDescriptionAction);
		csLayer.addOption(option42);
		csLayer.addOption(new ConversationOption("Is Java also an island?",
				"That's just a rumor.", csLayer));
		csLayer.addOption(new ConversationOption(
				"Actually I don't like computer science so much.", "Well",
				startLayer));

		satiaConversation.addLayer(startLayer);
		satiaConversation.addLayer(csLayer);
		satiaConversation.setStartLayer(startLayer);
		satia.setConversation(satiaConversation);

		ChangeConversationAction changeSatiaConversation = new ChangeConversationAction(
				satiaConversation);
		changeSatiaConversation.setNewGreeting("What is it, stinky thief?");

		ChangeConversationOptionAction changeSatiaOption42 = new ChangeConversationOptionAction(
				option42);
		changeSatiaOption42.setNewAnswer("I won't tell you that now, thief!");

		ChangeActionAction disableChangeFlatDescriptionAction = new ChangeActionAction(
				changeFlatDescriptionAction, Enabling.DISABLE);

		/*
		 * The short conversation with Satia
		 */
		satiaShortConversation.addAdditionalAction(giveMoneyBackAction);
		satiaShortConversation.addAdditionalAction(changeSatiaConversation);
		satiaShortConversation.addAdditionalAction(changeSatiaOption42);
		satiaShortConversation
				.addAdditionalAction(disableChangeFlatDescriptionAction);

		money.setUsingEnabledWith(satia, true);
		money.setUseWithSuccessfulText(satia,
				"You feel guilty and put the money back.");
		money.addAdditionalActionToUseWith(satia, giveMoneyBackAction);

		Item tv = new Item(flat, "Television",
				"There is a television in the corner.");
		tv.setInspectionText("A 32\" television.");
		tv.addIdentifier("tv");
		tv.setTakeForbiddenText("This is a little heavy.");
		tv.setUseForbiddenText("I am not in the mood.");
		/*
		 * Inspecting the tv will change its inspection text.
		 */
		ChangeInspectableObjectAction changeTVAction = new ChangeInspectableObjectAction(
				tv);
		changeTVAction
				.setNewInspectionText("A 32\" television. You should not waste your time admiring it.");
		tv.addAdditionalActionToInspect(changeTVAction);

		/*
		 * A banana. If the banana is being used the item disappears and the
		 * peel is being added to the inventory.
		 */
		Item banana = new Item(flat, "Banana",
				"In the fruit bowl there's a single, lonely banana.");
		banana.setInspectionText("Rich in cholesterol. Now that I look at it, I might also call it banana phone.");
		banana.setUsingEnabled(true);
		banana.setTakeForbiddenText("It looks delicious, but I don't wanna carry that around.");
		banana.setUseSuccessfulText("You ate the banana. The peel looks useful, so you kept it.");
		/*
		 * Inspecting the banana will "convert" it into a bananaphone.
		 */
		ChangeInspectableObjectAction changeBananaAction = new ChangeInspectableObjectAction(
				banana);
		changeBananaAction.setNewName("Banana phone");
		changeBananaAction
				.setNewInspectionText("Ring ring ring ring ring ring ring - banana phone.");
		changeBananaAction.addIdentifierToAdd("banana phone");
		banana.addAdditionalActionToInspect(changeBananaAction);

		InventoryItem peel = new InventoryItem("Banana peel",
				"The peel of the banana you ate.");
		peel.setInspectionText("You ate the banana inside it.");
		peel.addIdentifier("peel");
		peel.setUseForbiddenText("Do you want to eat the peel, too?");
		AddInventoryItemsAction addPeelAction = new AddInventoryItemsAction();
		addPeelAction.addPickUpItem(peel);
		banana.addAdditionalActionToUse(addPeelAction);
		banana.addAdditionalActionToUse(new ChangeItemAction(banana, null));
		InventoryItem invBanana = new InventoryItem(banana);
		invBanana.setDescription("A banana");

		Item chair = new Item(balcony, "Chair",
				"A wooden chair stands beside you.");
		chair.setInspectionText("Made of solid oak.");
		chair.setTakingEnabled(true);
		InventoryItem invChair = new InventoryItem(chair);
		invChair.setDescription("A wooden chair");
		chair.addPickUpItem(invChair);

		// Only let him in if he has the chair
		ChangeWayAction allowGettingInsideAction = new ChangeWayAction(
				wayToFlat);
		allowGettingInsideAction.setEnabling(Enabling.ENABLE);
		chair.addAdditionalActionToTake(allowGettingInsideAction);
		chair.addAdditionalActionToTake(changeBalconyDescriptionAction);

		/*
		 * The tv can be hit with the chair. It is then replaced with a
		 * destroyed tv.
		 */
		Item destroyedTv = new Item(null, "Destroyed television",
				"The former so glorious television looks severely broken by brutal manforce.");
		destroyedTv
				.setInspectionText("You hit it several times with the chair.");
		destroyedTv.addIdentifier("television");
		destroyedTv.addIdentifier("tv");
		destroyedTv
				.setTakeForbiddenText("What the hell do you want with this mess?");
		destroyedTv.setUseForbiddenText("It does not work anymore.");

		invChair.addAdditionalActionToUseWith(tv, new ChangeItemAction(
				destroyedTv, flat));
		invChair.addAdditionalActionToUseWith(tv,
				new ChangeItemAction(tv, null));
		invChair.setUseWithSuccessfulText(tv,
				"You smash the chair into the television.");
		invChair.setUsingEnabledWith(tv, true);

		/*
		 * A pen that can be used to paint the banana peel. The pen can be used
		 * for that when in the flat and when already taken.
		 */
		Item pen = new Item(flat, "Pen", "A pen lies on a table.");
		pen.setInspectionText("An advertising gift.");
		pen.setUseForbiddenText("You must use it with something else.");
		pen.setTakingEnabled(true);
		InventoryItem invPen = new InventoryItem(pen);
		invPen.setDescription("A pen");
		pen.addPickUpItem(invPen);

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
		peel.addNewCombinableWhenCombinedWith(invPen, paintedPeel);

		AddInventoryItemsAction addPaintedPeelAction = new AddInventoryItemsAction();
		RemoveInventoryItemAction removePeelAction = new RemoveInventoryItemAction(
				peel);
		addPaintedPeelAction.addPickUpItem(paintedPeel);
		peel.addAdditionalActionToUseWith(pen, addPaintedPeelAction);
		peel.addAdditionalActionToUseWith(pen, removePeelAction);
		peel.addAdditionalActionToCombineWith(invPen, removePeelAction);

		Player player = new Player();
		// Game options
		Game game = new Game();
		game.setPlayer(player);
		game.setStartLocation(flat);
		game.setStartText("This is a little text adventure.");

		game.setInspectionDefaultText("Nothing interesting.");
		game.setInventoryEmptyText("Your inventory is empty.");
		game.setInventoryText("You are carrying the following things:");
		game.setNoCommandText("I do not understand you.");
		game.setNoSuchInventoryItemText("You do not have a <identifier>.");
		game.setNoSuchItemText("There is no <identifier> here.");
		game.setNoSuchPersonText("There is no <Identifier> here.");
		game.setNoSuchWayText("You cannot <input>.");
		game.setNotTakeableText("You cannot take the <name>.");
		game.setNotTravelableText("You cannot <input>.");
		game.setNotUsableText("You cannot use the <name>.");
		game.setNotTalkingToEnabledText("You cannot talk to <name>.");
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

		// TODO special command identifiers like "eat banana" instead of
		// "use banana"?

		game.addExitCommand("exit");
		game.addExitCommand("quit");
		game.addInspectCommand("look(?: at)? (.+)");
		game.addInspectCommand("inspect (.+)");
		game.addInventoryCommand("inventory");
		game.addHelpCommand("help");
		game.addLookAroundCommand("look around");
		game.addMoveCommand("go(?: to)? (.+)");
		game.addMoveCommand("move(?: to)? (.+)");
		game.addTakeCommand("take (.+)");
		game.addTakeCommand("pick up (.+)");
		game.addTakeCommand("pick (.+) up");
		game.addUseCommand("use (.+)");
		game.addUseWithCombineCommand("use (.+) with (.+)");
		game.addUseWithCombineCommand("combine (.+) and (.+)");
		game.addUseWithCombineCommand("combine (.+) with (.+)");
		game.addTalkToCommand("talk to (.+)");
		game.addTalkToCommand("speek with (.+)");

		// Connect to database
		PersistenceManager.connect(System.getProperty("user.home")
				+ File.separator + ".textAdventureMaker" + File.separator
				+ "test.db");

		// Persist everything (Cascade.PERSIST persists the rest)
		PersistenceManager.getEntityManager().persist(player);
		PersistenceManager.getEntityManager().persist(game);

		// Updates changes
		PersistenceManager.updateChanges();

		// Start a game
		new GamePlayer(GameManager.getGame(), PlayerManager.getPlayer())
				.start();
	}
}