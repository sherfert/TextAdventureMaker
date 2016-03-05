package persistence;

import com.googlecode.lanterna.terminal.Terminal.Color;

import configuration.PropertiesReader;
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
import data.action.ChangeInvItemCombinationAction;
import data.action.ChangeInvItemUsageAction;
import data.action.ChangeItemAction;
import data.action.ChangeNamedObjectAction;
import data.action.ChangePersonAction;
import data.action.ChangeWayAction;
import data.action.EndGameAction;
import data.action.MultiAction;
import data.action.RemoveInventoryItemAction;

/**
 * Test class.
 * 
 * General TODOs:
 * 
 * TODO GUI 
 * 
 * TODO Tooltips everywhere!
 * 
 * TODO Test suite for deleting items
 * 
 * TODO Default new game
 * 
 * TODO no logging to console
 * 
 * TODO sound support (optional)
 * 
 * @author Satia
 */
public class Main {

	/**
	 * Test-main. Creates a test game db.
	 * 
	 * @param args
	 * @throws java.lang.Exception
	 */
	public static void main(String[] args) throws Exception {
		Player player = new Player();
		Game game = new Game();

		// Create everything
		Location flat = new Location("Flat", "Your little home.");
		Location balcony = new Location("Balcony", "Your balcony. Sitting on the chair you can look at the sunset.");
		Location voidLoc = new Location("Void", "You jump into the black hole and die. Well done!");

		ChangeNamedObjectAction changeBalconyDescriptionAction = new ChangeNamedObjectAction(balcony);
		changeBalconyDescriptionAction
				.setNewDescription("Your balcony. Standing around stupidly you can look at the sunset.");

		ChangeNamedObjectAction changeFlatDescriptionAction = new ChangeNamedObjectAction(flat);
		changeFlatDescriptionAction.setNewDescription(
				"Your little home. But now, that you know the answer to everything, you take a completely different look at things.");

		Way wayToBalcony = new Way("Balcony door", "There is a door that leads outside.", flat, balcony);
		wayToBalcony.removeIdentifier(wayToBalcony.getName());
		wayToBalcony.addIdentifier("out(side)?");
		wayToBalcony.addIdentifier("(on |to )?(the )?balcony");
		wayToBalcony.addIdentifier("through (the )?balcony door");
		wayToBalcony.setMoveSuccessfulText("You go outside on the balcony.");
		Way wayToFlat = new Way("Balcony door", "There is a door that leads inside.", balcony, flat);
		wayToFlat.removeIdentifier(wayToFlat.getName());
		wayToFlat.addIdentifier("in(side)?");
		wayToFlat.addIdentifier("into (the )?flat");
		wayToFlat.addIdentifier("through (the )?balcony door");
		wayToFlat.setMoveSuccessfulText("You go back inside");
		wayToFlat.setMoveForbiddenText("I feel like you need something from here before going back in.");
		wayToFlat.setMovingEnabled(false);

		// Going into the black hole ends the game
		Way wayToVoid = new Way("Black hole", "There is a big black hole where the front door " + "used to be.", flat,
				voidLoc);
		wayToVoid.addAdditionalTravelCommand("climb into (?<o0>.+?)");
		wayToVoid.addAdditionalTravelCommand("jump into (?<o0>.+?)");
		wayToVoid.addAdditionalActionToMove(new EndGameAction());

		/*
		 * Inspecting satia will give you 5 bucks. You can "give them back" by
		 * using them with him. This is repeatable.
		 */
		Person satia = new Person(flat, "Satia", "Satia is hanging around there.");
		satia.setInspectionText(
				"He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");
		InventoryItem money = new InventoryItem("Money", "5 bucks");
		money.setInspectionText("You stole them from poor Satia.");

		// Create conversations first, add other stuff later
		Conversation satiaConversation = new Conversation("I'm busy, keep it short.");
		Conversation satiaShortConversation = new Conversation("Hey. Gimme back my money! Douche!",
				"Satia takes his money back semi-violently.");

		// Combining these 2 actions
		MultiAction stealMoneyAction;
		{
			AddInventoryItemsAction addMoneyAction = new AddInventoryItemsAction();
			addMoneyAction.addPickUpItem(money);
			// The order here is critical!
			stealMoneyAction = new MultiAction(addMoneyAction);
			ChangeActionAction disableStealMoneyAction = new ChangeActionAction(stealMoneyAction, Enabling.DISABLE);
			stealMoneyAction.addAction(disableStealMoneyAction);

		}

		// Combining these 3 actions
		MultiAction giveMoneyBackAction;
		{
			RemoveInventoryItemAction removeMoneyAction = new RemoveInventoryItemAction(money);
			ChangePersonAction changeSatiaAction2 = new ChangePersonAction(satia);
			changeSatiaAction2.setNewInspectionText(
					"He looks pretty busy programming nonsense stuff. You steal some money out of his pocket.");
			changeSatiaAction2.setNewConversation(satiaConversation);
			ChangeActionAction enableAddMoneyAction = new ChangeActionAction(stealMoneyAction, Enabling.ENABLE);
			giveMoneyBackAction = new MultiAction(removeMoneyAction, changeSatiaAction2, enableAddMoneyAction);
		}

		// The order here is critical!
		satia.addAdditionalInspectAction(stealMoneyAction);

		ChangePersonAction changeSatiaAction1 = new ChangePersonAction(satia);
		changeSatiaAction1.setNewInspectionText(
				"He looks pretty busy programming nonsense stuff. You stole the poor guy his last 5 bucks.");
		changeSatiaAction1.setNewConversation(satiaShortConversation);

		satia.addAdditionalInspectAction(changeSatiaAction1);

		/*
		 * The normal conversation with Satia.
		 */
		ConversationLayer startLayer = new ConversationLayer();
		ConversationLayer csLayer = new ConversationLayer();

		startLayer.addOption(new ConversationOption("Why so hostile?",
				"Just TextAdventureMaker is harder to code than I though!", "He looks really annoyed!", startLayer));
		startLayer.addOption(new ConversationOption("Let's talk about computer science.", "Ask me anything.", csLayer));
		startLayer.addOption(new ConversationOption("I'm gonne leave you now.", "Finally.", null));
		// An option that finishes the game
		ConversationOption endGameOption = new ConversationOption("Fuck this. Ima quit!",
				"Quit what? Don't tell me I ended up in a game! Please not!", null);
		endGameOption.addAdditionalAction(new EndGameAction());
		startLayer.addOption(endGameOption);

		ConversationOption option42 = new ConversationOption(
				"What is the answer to the Ultimate Question of Life, the Universe, and Everything? "
						+ "Additionally, here is some text to make it cover two lines.",
				"42", csLayer);
		option42.addAdditionalAction(changeFlatDescriptionAction);
		csLayer.addOption(option42);
		csLayer.addOption(new ConversationOption("Is Java also an island?", "That's just a rumor.", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Java?", "Plenty", csLayer));
		csLayer.addOption(new ConversationOption("Do you like C#?", "It's OK", csLayer));
		csLayer.addOption(new ConversationOption("Do you like C++?", "I can accomodate", csLayer));
		csLayer.addOption(new ConversationOption("Do you like C?", "Not really", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Assembler?", "It's cool, but I'm not crazy.", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Python?", "I recommend you a gif a made", csLayer));
		csLayer.addOption(new ConversationOption(
				"Do you like this threeliner that had to come somewhere "
						+ "in order to test the conversation option chosing mechasnism for a hell of a lot of options properly "
						+ "and also with options that are way too long and nobody will ever read them? Assholes! "
						+ "Still not long enough - need to add some more bullshit. That should do it!",
				"What!?!", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Whitespace?", "I never find the code", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Brainfuck?", "I prefer to fuck other things", csLayer));
		csLayer.addOption(new ConversationOption("Do you like Perl?", "Hell no", csLayer));
		ConversationOption disappearingOption = new ConversationOption("I am never gonna say this again.", "What!?",
				csLayer);
		disappearingOption.setDisablingOptionAfterChosen(true);
		csLayer.addOption(disappearingOption);
		csLayer.addOption(
				new ConversationOption("Actually I don't like computer science so much.", "Well", startLayer));

		satiaConversation.addLayer(startLayer);
		satiaConversation.addLayer(csLayer);
		satiaConversation.setStartLayer(startLayer);
		satia.setConversation(satiaConversation);

		ChangeConversationAction changeSatiaConversation = new ChangeConversationAction(satiaConversation);
		changeSatiaConversation.setNewGreeting("What is it, stinky thief?");

		ChangeConversationOptionAction changeSatiaOption42 = new ChangeConversationOptionAction(option42);
		changeSatiaOption42.setNewAnswer("I won't tell you that now, thief!");

		ChangeActionAction disableChangeFlatDescriptionAction = new ChangeActionAction(changeFlatDescriptionAction,
				Enabling.DISABLE);

		/*
		 * The short conversation with Satia
		 */
		satiaShortConversation.addAdditionalAction(giveMoneyBackAction);
		satiaShortConversation.addAdditionalAction(changeSatiaConversation);
		satiaShortConversation.addAdditionalAction(changeSatiaOption42);
		satiaShortConversation.addAdditionalAction(disableChangeFlatDescriptionAction);

		// A hot chick
		Person hotChick = new Person(flat, "Hot chick", "A hot chick is standing in the corner.");
		hotChick.addIdentifier("chick");
		hotChick.setInspectionText("Stunning.");
		Conversation hotChickConversation = new Conversation("Sorry, you're not my type.");
		hotChick.setConversation(hotChickConversation);
		hotChick.addAdditionalTalkToCommand("flirt with (?<o0>.+?)");

		// A gremlin
		Person gremlin = new Person(flat, "Gremlin", "A gremlin is sitting around and staring at the black tv screen.");
		gremlin.setInspectionText("Do not feed after midnight!");

		money.setUsingEnabledWith(satia, true);
		money.setUseWithSuccessfulText(satia, "You feel guilty and put the money back.");
		money.addAdditionalActionToUseWith(satia, giveMoneyBackAction);

		Item tv = new Item(flat, "Television", "There is a television in the corner.");
		tv.setInspectionText("A 32\" television.");
		tv.addIdentifier("tv");
		tv.setTakeForbiddenText("This is a little heavy.");
		tv.setUseForbiddenText("I am not in the mood.");
		tv.addAdditionalTakeCommand("lift (?<o0>.+?)");
		/*
		 * Inspecting the tv will change its inspection text.
		 */
		ChangeInspectableObjectAction changeTVAction = new ChangeInspectableObjectAction(tv);
		changeTVAction.setNewInspectionText("A 32\" television. You should not waste your time admiring it.");
		tv.addAdditionalInspectAction(changeTVAction);

		// A useless button
		Item button = new Item(flat, "Button", "There is a button the wall.");
		button.setInspectionText("\"DANGER. SELF-DESTRUCTION\"");
		button.setUsingEnabled(true);
		button.addAdditionalUseCommand("push (?<o0>.+?)");

		/*
		 * A banana. If the banana is being used the item disappears and the
		 * peel is being added to the inventory.
		 */
		Item banana = new Item(flat, "Banana", "In the fruit bowl there's a single, lonely banana.");
		banana.setInspectionText("Rich in cholesterol. Now that I look at it, I might also call it banana phone.");
		banana.setUsingEnabled(true);
		banana.setTakeForbiddenText("It looks delicious, but I don't wanna carry that around.");
		banana.setUseSuccessfulText("You ate the banana. The peel looks useful, so you kept it.");
		banana.addAdditionalUseCommand("eat (?<o0>.+?)");

		/*
		 * Inspecting the banana will "convert" it into a bananaphone.
		 */
		ChangeInspectableObjectAction changeBananaAction = new ChangeInspectableObjectAction(banana);
		changeBananaAction.setNewName("Banana phone");
		changeBananaAction.setNewInspectionText("Ring ring ring ring ring ring ring - banana phone.");
		changeBananaAction.addIdentifierToAdd("banana phone");
		banana.addAdditionalInspectAction(changeBananaAction);

		InventoryItem peel = new InventoryItem("Banana peel", "The peel of the banana you ate.");
		peel.setInspectionText("You ate the banana inside it.");
		peel.addIdentifier("peel");
		peel.setUseForbiddenText("Do you want to eat the peel, too?");
		AddInventoryItemsAction addPeelAction = new AddInventoryItemsAction();
		addPeelAction.addPickUpItem(peel);
		banana.addAdditionalActionToUse(addPeelAction);
		banana.addAdditionalActionToUse(new ChangeItemAction(banana, null));
		InventoryItem invBanana = new InventoryItem(banana);
		invBanana.setDescription("A banana");

		Item chair = new Item(balcony, "Chair", "A wooden chair stands beside you.");
		chair.setInspectionText("Made of solid oak.");
		chair.setTakingEnabled(true);
		InventoryItem invChair = new InventoryItem(chair);
		invChair.setDescription("A wooden chair");
		chair.addPickUpItem(invChair);

		// Only let him in if he has the chair
		ChangeWayAction allowGettingInsideAction = new ChangeWayAction(wayToFlat);
		allowGettingInsideAction.setEnabling(Enabling.ENABLE);
		chair.addAdditionalTakeAction(allowGettingInsideAction);
		chair.addAdditionalTakeAction(changeBalconyDescriptionAction);

		/*
		 * The tv can be hit with the chair. It is then replaced with a
		 * destroyed tv.
		 */
		Item destroyedTv = new Item(null, "Destroyed television",
				"The former so glorious television looks severely broken by brutal manforce.");
		destroyedTv.setInspectionText("You hit it several times with the chair.");
		destroyedTv.addIdentifier("television");
		destroyedTv.addIdentifier("tv");
		destroyedTv.setTakeForbiddenText("What the hell do you want with this mess?");
		destroyedTv.setUseForbiddenText("It does not work anymore.");

		invChair.addAdditionalActionToUseWith(tv, new ChangeItemAction(destroyedTv, flat));
		invChair.addAdditionalActionToUseWith(tv, new ChangeItemAction(tv, null));
		invChair.setUseWithSuccessfulText(tv, "You smash the chair into the television.");
		invChair.setUsingEnabledWith(tv, true);

		/*
		 * A pen that can be used to paint the banana peel. The pen can be used
		 * for that when in the flat and when already taken.
		 */
		Item pen = new Item(flat, "Pen", "A pen lies on a table.");
		pen.setInspectionText("An advertising gift. Cheap.");
		pen.setUseForbiddenText("You must use it with something else.");
		pen.setTakingEnabled(true);
		InventoryItem invPen = new InventoryItem(pen);
		invPen.setDescription("A pen");
		pen.addPickUpItem(invPen);

		// Make it possible to paint Satia and the chick
		invPen.setUsingEnabledWith(satia, true);
		invPen.setUseWithSuccessfulText(satia, "You drew a penis on Satia's arm. He didn't even notice.");
		invPen.setUsingEnabledWith(hotChick, true);
		invPen.setUseWithSuccessfulText(hotChick,
				"You drew a heart on the hot chick's arm. She looks at you as if you were a 5 year old that is very proud of achieving something competely useless.");

		InventoryItem paintedPeel = new InventoryItem("Painted banana peel", "The peel of the banana you ate.");
		paintedPeel.setInspectionText("You ate the banana and painted the peel.");
		paintedPeel.addIdentifier("peel");
		paintedPeel.addIdentifier("banana peel");

		peel.setUsingEnabledWith(pen, true);
		peel.setCombiningEnabledWith(invPen, true);
		// Unidirectional additional combine command
		peel.addAdditionalCombineCommand(invPen, "paint (?<o0>.+?) with (?<o1>.+?)");
		// and additional use with command
		peel.addAdditionalUseWithCommand(pen, "paint (?<o0>.+?) with (?<o1>.+?)");
		// and another to test different ordering
		peel.addAdditionalCombineCommand(invPen, "use (?<o1>.+?) to paint (?<o0>.+?)");
		peel.addAdditionalUseWithCommand(pen, "use (?<o1>.+?) to paint (?<o0>.+?)");

		peel.setUseWithSuccessfulText(pen, "You painted the banana peel.");
		peel.setCombineWithSuccessfulText(invPen, "You painted the banana peel.");
		peel.addNewCombinableWhenCombinedWith(invPen, paintedPeel);

		AddInventoryItemsAction addPaintedPeelAction = new AddInventoryItemsAction();
		RemoveInventoryItemAction removePeelAction = new RemoveInventoryItemAction(peel);
		addPaintedPeelAction.addPickUpItem(paintedPeel);
		peel.addAdditionalActionToUseWith(pen, addPaintedPeelAction);
		peel.addAdditionalActionToUseWith(pen, removePeelAction);
		peel.addAdditionalActionToCombineWith(invPen, removePeelAction);

		ChangeInvItemUsageAction changeChairTVaction = new ChangeInvItemUsageAction(invChair, tv);
		changeChairTVaction.setNewUseWithSuccessfulText("32\" wasted!");

		ChangeInvItemUsageAction changeSatiaMoneyAction = new ChangeInvItemUsageAction(money, satia);
		changeSatiaMoneyAction
				.setNewUseWithSuccessfulText("You feel guilty and put the money back. Although he has a big tv.");

		ChangeInvItemCombinationAction changePeelPenCombinationAction = new ChangeInvItemCombinationAction(peel,
				invPen);
		changePeelPenCombinationAction.setNewCombineWithSuccessfulText(
				"Absent-mindedly you paint the peel while staring at the huge tv screen - which is black.");

		tv.addAdditionalInspectAction(changeChairTVaction);
		tv.addAdditionalInspectAction(changeSatiaMoneyAction);
		tv.addAdditionalInspectAction(changePeelPenCombinationAction);
		
		// Start item in the inventory
		InventoryItem dildo = new InventoryItem("Dildo", "Made out of Kruppstahl.");
		dildo.setInspectionText("Why are you carrying that around with you!?");
		game.addStartItem(dildo);

		// Game options
		game.setPlayer(player);
		game.setStartLocation(balcony);
		game.setStartText("This is a little text adventure.");

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
		// game.setNotUsableWithText("You cannot <pattern|the <name>|the
		// <name2>|>.");
		// The above pattern is more verbose, but leads to strange output if
		// used with persons. E.g. "You cannot use the peel with the satia."
		game.setNotUsableWithText("You cannot do that.");

		game.setInspectionDefaultText("Nothing interesting.");
		game.setInventoryEmptyText("Your inventory is empty.");
		game.setInventoryText("You are carrying the following things:");
		game.setTakenText("You picked up the <name>.");
		game.setUsedText("You <pattern|the <name>||>. Nothing interesting happens.");
		// Same as above. // You use the <name> with the <name2>.
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

		game.setGameTitle("Test-Adventure");

		PersistenceManager pm = new PersistenceManager();

		// Connect to database
		pm.connect(PropertiesReader.DIRECTORY + "Test-Adventure", true);

		// Persist everything (Cascade.PERSIST persists the rest)
		pm.getEntityManager().persist(player);
		pm.getEntityManager().persist(game);

		///////////////////////
		// Additional test data
		Location locToDel = new Location("Del me", "pls");
		ChangeNamedObjectAction cltd = new ChangeNamedObjectAction(locToDel);
		cltd.setNewName("Did ja del me?");
		Item thing = new Item(locToDel, "Thing", "useless");
		
		pm.getEntityManager().persist(locToDel);
		pm.getEntityManager().persist(cltd);
		///////////////////////

		// Updates changes and disconnect
		pm.updateChanges();
		pm.disconnect();
	}
}