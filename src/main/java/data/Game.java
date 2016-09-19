package data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.googlecode.lanterna.terminal.Terminal.Color;

import data.action.AddInventoryItemsAction;
import data.interfaces.Combinable;
import data.interfaces.HasId;
import data.interfaces.PassivelyUsable;
import data.interfaces.UsableWithSomething;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A game that can be played. Contains all configuration and (default) texts.
 * There should be only one game per database. <br>
 * <br>
 * Parameters in commands must be exactly the following: {@literal "(?<o0>.+?)"}
 * for the first parameter and {@literal "(?<o1>.+?)"} for the second parameter,
 * if there are two. In additional, commands may contain optional parts, written
 * as {@literal "(optional text)?"}. <br>
 * <br>
 * The default texts, etc. can have placeholders. Which ones are allowed is
 * stated in the corresponding field. The following placeholders exist: <br>
 * {@literal <input>}: The exact input the user typed. <br>
 * {@literal <identifier>} (and {@literal <identifier2>}): The identifiers used
 * for the first (and second) object. <br>
 * {@literal <name>} (and {@literal <name2>}): The name of the first (and
 * second) object. <br>
 * {@literal <pattern|a|b|>}: The command pattern that matched the input.
 * {@literal a} and {@literal b} are the substitutions for the first and second
 * parameter in the pattern, may be empty and may also contain more
 * placeholders, except the pattern placeholder itself. <br>
 * <br>
 * Except for the pattern placeholder, all placeholders come in three flavors:
 * lowercase, e.g. {@literal <name>}, standard case (first word starts
 * uppercase, rest lowercase), e.g. {@literal <Name>}, and uppercase, e.g.
 * {@literal <NAME>}.
 * 
 * @author Satia
 */
@Entity
@Access(AccessType.FIELD)
public class Game implements HasId {
	/*
	 * Thoughts on sets instead of lists: Pro: Use Sets instead of Lists, where
	 * useful. Therefore, if former Lists of own classes, let these (or better
	 * all) classes override equals, hashCode, and toString. Then, the
	 * "if(contains)" check can be removed. Con: No ordering. Therefore, lists
	 * are kept.
	 */

	/**
	 * The {@link AddInventoryItemsAction} that adds item to your inventory when
	 * starting the game.
	 * 
	 * No ON CASCADE definitions, since this field is not accessible.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(nullable = false)
	private AddInventoryItemsAction addInventoryItemsAction;
	
	/**
	 * The help text being displayed for the exit command
	 */
	@Transient
	private final StringProperty exitCommandHelpText;

	/**
	 * All commands that make the game exit. Must be lowercase.
	 */
	@ElementCollection
	private List<String> exitCommands;

	/**
	 * The background color that is used for text printed after a failed action.
	 */
	@Transient
	private final Property<Color> failedBgColor;

	/**
	 * The color that is used for text printed after a failed action.
	 */
	@Transient
	private final Property<Color> failedFgColor;

	/**
	 * The name of the game. Should be unique, as savegames are ordered by this
	 * name, if there are multiple games used.
	 */
	@Column(nullable = false)
	private String gameTitle;

	/**
	 * If the game has ended.
	 */
	@Column(nullable = false)
	private boolean hasEnded = false;

	/**
	 * All commands that let the player see the help. Must be lowercase.
	 */
	@ElementCollection
	private List<String> helpCommands;

	/**
	 * The help text being displayed for the help command
	 */
	@Transient
	private final StringProperty helpHelpText;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * All commands that let the player inspect something. Must be lowercase.
	 * Must contain at least one word and exactly one parameter for the object.
	 */
	@ElementCollection
	private List<String> inspectCommands;

	/**
	 * The help text being displayed for the inspect command
	 */
	@Transient
	private final StringProperty inspectHelpText;

	/**
	 * The text being displayed when an object is inspected that does not have
	 * an individual inspection text. Valid placeholders: input, name,
	 * identifier, pattern
	 */
	@Transient
	private final StringProperty inspectionDefaultText;

	/**
	 * The text displayed when an invalid action is being performed (e.g.
	 * talking to objects, taking persons, etc.). Valid placeholders: input,
	 * pattern
	 */
	@Transient
	private final StringProperty invalidCommandText;

	/**
	 * All commands that let the player look into his inventory. Must be
	 * lowercase.
	 */
	@ElementCollection
	private List<String> inventoryCommands;

	/**
	 * The text being displayed, when the player looks into his empty inventory.
	 * Valid placeholders: input, pattern
	 */
	@Transient
	private final StringProperty inventoryEmptyText;

	/**
	 * The help text being displayed for the inventory command.
	 */
	@Transient
	private final StringProperty inventoryHelpText;

	/**
	 * The text introducing a look into the inventory. Valid placeholders:
	 * input, pattern
	 */
	@Transient
	private final StringProperty inventoryText;

	/**
	 * All commands that let the player look around. Must be lowercase.
	 */
	@ElementCollection
	private List<String> lookAroundCommands;

	/**
	 * The help text being displayed for the look around command
	 */
	@Transient
	private final StringProperty lookAroundHelpText;

	/**
	 * All move commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the target.
	 */
	@ElementCollection
	private List<String> moveCommands;

	/**
	 * The help text being displayed for the move command
	 */
	@Transient
	private final StringProperty moveHelpText;

	/**
	 * The background color that is used for text printed after a neutral
	 * action.
	 */
	@Transient
	private final Property<Color> neutralBgColor;

	/**
	 * The color that is used for text printed after a neutral action.
	 */
	@Transient
	private final Property<Color> neutralFgColor;

	/**
	 * The text being displayed, when any entered text is not recognized as a
	 * valid command. Valid placeholders: input
	 */
	@Transient
	private final StringProperty noCommandText;

	/**
	 * The text being displayed, when the player tries to use, etc. a
	 * non-existing inventory item. Valid placeholders: input, pattern,
	 * identifier
	 */
	@Transient
	private final StringProperty noSuchInventoryItemText;

	/**
	 * The text being displayed, when the player tries to use, take, etc. a
	 * non-existing item. Valid placeholders: input, pattern, identifier
	 */
	@Transient
	private final StringProperty noSuchItemText;

	/**
	 * The text being displayed, when the player tries to talk to a non-existing
	 * person. Valid placeholders: input, pattern, identifier
	 */
	@Transient
	private final StringProperty noSuchPersonText;

	/**
	 * The text being displayed, when the player tries to travel by a
	 * non-existing way. Valid placeholders: input, pattern, identifier
	 */
	@Transient
	private final StringProperty noSuchWayText;

	/**
	 * The default text, when the player tries to take a non-takeable item. May
	 * be overwritten for each individual item. Valid placeholders: input,
	 * pattern, name, identifier
	 */
	@Transient
	private final StringProperty notTakeableText;

	/**
	 * The default text, when the player tries to talk to a person he cannot
	 * talk to. way. May be overwritten for each individual person. Valid
	 * placeholders: input, pattern, name, identifier
	 */
	@Transient
	private final StringProperty notTalkingToEnabledText;

	/**
	 * The default text, when the player tries to travel by a non-travelable
	 * way. May be overwritten for each individual way. Valid placeholders:
	 * input, pattern, name, identifier
	 */
	@Transient
	private final StringProperty notTravelableText;

	/**
	 * The default text, when the player tries to use a non-usable object. May
	 * be overwritten for each individual object. Valid placeholders: input,
	 * pattern, name, identifier
	 */
	@Transient
	private final StringProperty notUsableText;

	/**
	 * The default text, when the player tries to use two incompatible object
	 * with one another. May be overwritten for each individual combination.
	 * Valid placeholders: input, pattern, name, identifier, name2, identifier2
	 */
	@Transient
	private final StringProperty notUsableWithText;

	/**
	 * The number of lines used to display options in dialogues. Recommendation
	 * is ~10.
	 */
	@Transient
	private final IntegerProperty numberOfOptionLines;

	/**
	 * The player of this game.
	 * 
	 * No ON CASCADE definitions, the player must not be deleted.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(nullable = false)
	private Player player;

	/**
	 * The starting location of the game.
	 * 
	 * No ON CASCADE definitions, since it is not allowed deleting the start
	 * location.
	 */
	@OneToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(nullable = false)
	private Location startLocation;

	/**
	 * The text being displayed, when the game starts.
	 */
	@Transient
	private final StringProperty startText;

	/**
	 * The background color that is used for text printed after a successful
	 * action.
	 */
	@Transient
	private final Property<Color> successfullBgColor;

	/**
	 * The color that is used for text printed after a successful action.
	 */
	@Transient
	private final Property<Color> successfullFgColor;

	/**
	 * All take commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object.
	 */
	@ElementCollection
	private List<String> takeCommands;

	/**
	 * The help text being displayed for the take command
	 */
	@Transient
	private final StringProperty takeHelpText;

	/**
	 * The default text, when the player takes an item. May be overwritten for
	 * each individual item. Valid placeholders: input, pattern, name,
	 * identifier
	 */
	@Transient
	private final StringProperty takenText;

	/**
	 * All commands that let the player talk to someone. Must be lowercase. Must
	 * contain at least one word and exactly one parameter for the person.
	 */
	@ElementCollection
	private List<String> talkToCommands;

	/**
	 * The help text being displayed for the talk to command
	 */
	@Transient
	private final StringProperty talkToHelpText;

	/**
	 * All use commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object.
	 */
	@ElementCollection
	private List<String> useCommands;

	/**
	 * The default text, when the player uses an object. May be overwritten for
	 * each individual object. Valid placeholders: input, pattern, name,
	 * identifier
	 */
	@Transient
	private final StringProperty usedText;

	/**
	 * The default text, when the player uses two compatible object with one
	 * another. May be overwritten for each individual combination. Valid
	 * placeholders: input, pattern, name, identifier, name2, identifier2
	 */
	@Transient
	private final StringProperty usedWithText;

	/**
	 * The help text being displayed for the use command.
	 */
	@Transient
	private final StringProperty useHelpText;

	/**
	 * All useWith/combine commands. Must be lowercase. Must contain at least
	 * one word and exactly two parameters for the objects.
	 * 
	 * UseWith commands are asymmetric, combine commands are symmetric.
	 * 
	 * {@literal o0} has to denote a {@link Combinable} or a
	 * {@link UsableWithSomething}. {@literal o1} can be that or a
	 * {@link PassivelyUsable}.
	 */
	@ElementCollection
	private List<String> useWithCombineCommands;

	/**
	 * The help text being displayed for the use with/combine command.
	 */
	@Transient
	private final StringProperty useWithCombineHelpText;

	/**
	 * Constructs a new game object.
	 */
	public Game() {
		addInventoryItemsAction = new AddInventoryItemsAction("");
		addInventoryItemsAction.setHidden(true);

		exitCommands = new ArrayList<>();
		lookAroundCommands = new ArrayList<>();
		helpCommands = new ArrayList<>();
		inspectCommands = new ArrayList<>();
		inventoryCommands = new ArrayList<>();
		moveCommands = new ArrayList<>();
		takeCommands = new ArrayList<>();
		useCommands = new ArrayList<>();
		useWithCombineCommands = new ArrayList<>();
		talkToCommands = new ArrayList<>();

		useWithCombineHelpText = new SimpleStringProperty();
		useHelpText = new SimpleStringProperty();
		talkToHelpText = new SimpleStringProperty();
		exitCommandHelpText = new SimpleStringProperty();
		helpHelpText = new SimpleStringProperty();
		takeHelpText = new SimpleStringProperty();
		inspectHelpText = new SimpleStringProperty();
		inventoryHelpText = new SimpleStringProperty();
		lookAroundHelpText = new SimpleStringProperty();
		moveHelpText = new SimpleStringProperty();
		startText = new SimpleStringProperty();
		inspectionDefaultText = new SimpleStringProperty();
		invalidCommandText = new SimpleStringProperty();
		inventoryEmptyText = new SimpleStringProperty();
		inventoryText = new SimpleStringProperty();
		noCommandText = new SimpleStringProperty();
		noSuchInventoryItemText = new SimpleStringProperty();
		noSuchItemText = new SimpleStringProperty();
		noSuchPersonText = new SimpleStringProperty();
		noSuchWayText = new SimpleStringProperty();
		notTakeableText = new SimpleStringProperty();
		notTalkingToEnabledText = new SimpleStringProperty();
		notTravelableText = new SimpleStringProperty();
		notUsableText = new SimpleStringProperty();
		notUsableWithText = new SimpleStringProperty();
		takenText = new SimpleStringProperty();
		usedText = new SimpleStringProperty();
		usedWithText = new SimpleStringProperty();
		numberOfOptionLines = new SimpleIntegerProperty();
		failedBgColor = new SimpleObjectProperty<>();
		failedFgColor = new SimpleObjectProperty<>();
		neutralBgColor = new SimpleObjectProperty<>();
		neutralFgColor = new SimpleObjectProperty<>();
		successfullBgColor = new SimpleObjectProperty<>();
		successfullFgColor = new SimpleObjectProperty<>();
	}

	/**
	 * Adds an exit command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addExitCommand(String cmd) {
		this.exitCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a help command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addHelpCommand(String cmd) {
		this.helpCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addInspectCommand(String cmd) {
		this.inspectCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addInventoryCommand(String cmd) {
		this.inventoryCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a look-around command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addLookAroundCommand(String cmd) {
		this.lookAroundCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addMoveCommand(String cmd) {
		this.moveCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a start item.
	 * 
	 * @param item
	 */
	public void addStartItem(InventoryItem item) {
		addInventoryItemsAction.addPickUpItem(item);

	}

	/**
	 * Adds a take command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addTakeCommand(String cmd) {
		this.takeCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a talk to command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addTalkToCommand(String cmd) {
		this.talkToCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds a use command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addUseCommand(String cmd) {
		this.useCommands.add(cmd.toLowerCase());
	}

	/**
	 * Adds an useWith/combine command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void addUseWithCombineCommand(String cmd) {
		this.useWithCombineCommands.add(cmd.toLowerCase());
	}

	/**
	 * @return the exitCommandHelpText property
	 */
	public StringProperty exitCommandHelpTextProperty() {
		return exitCommandHelpText;
	}

	/**
	 * @return the failedBgColor property
	 */
	public Property<Color> failedBgColorProperty() {
		return failedBgColor;
	}

	/**
	 * @return the failedFgColor property
	 */
	public Property<Color> failedFgColorProperty() {
		return failedFgColor;
	}

	/**
	 * @return the exitCommandHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getExitCommandHelpText() {
		return exitCommandHelpText.get();
	}

	/**
	 * @return the exitCommands
	 */
	public List<String> getExitCommands() {
		return exitCommands;
	}

	/**
	 * @return the failedBgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getFailedBgColor() {
		return failedBgColor.getValue();
	}

	/**
	 * @return the failedFgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getFailedFgColor() {
		return failedFgColor.getValue();
	}

	/**
	 * @return the gameTitle
	 */
	public String getGameTitle() {
		return gameTitle;
	}

	/**
	 * @return the hasEnded
	 */
	public boolean getHasEnded() {
		return hasEnded;
	}

	/**
	 * @return the helpCommands
	 */
	public List<String> getHelpCommands() {
		return helpCommands;
	}

	/**
	 * @return the helpHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getHelpHelpText() {
		return helpHelpText.get();
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the inspectCommands
	 */
	public List<String> getInspectCommands() {
		return inspectCommands;
	}

	/**
	 * @return the inspectHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInspectHelpText() {
		return inspectHelpText.get();
	}

	/**
	 * @return the inspectionDefaultText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInspectionDefaultText() {
		return inspectionDefaultText.get();
	}

	/**
	 * @return the invalidCommandText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInvalidCommandText() {
		return invalidCommandText.get();
	}

	/**
	 * @return the inventoryCommands
	 */
	public List<String> getInventoryCommands() {
		return inventoryCommands;
	}

	/**
	 * @return the inventoryEmptyText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInventoryEmptyText() {
		return inventoryEmptyText.get();
	}

	/**
	 * @return the inventoryHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInventoryHelpText() {
		return inventoryHelpText.get();
	}

	/**
	 * @return the inventoryText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getInventoryText() {
		return inventoryText.get();
	}

	/**
	 * @return the lookAroundCommands
	 */
	public List<String> getLookAroundCommands() {
		return lookAroundCommands;
	}

	/**
	 * @return the lookAroundHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getLookAroundHelpText() {
		return lookAroundHelpText.get();
	}

	/**
	 * @return the moveCommands
	 */
	public List<String> getMoveCommands() {
		return moveCommands;
	}

	/**
	 * @return the moveHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getMoveHelpText() {
		return moveHelpText.get();
	}

	/**
	 * @return the neutralBgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getNeutralBgColor() {
		return neutralBgColor.getValue();
	}

	/**
	 * @return the neutralFgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getNeutralFgColor() {
		return neutralFgColor.getValue();
	}

	/**
	 * @return the noCommandText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNoCommandText() {
		return noCommandText.get();
	}

	/**
	 * @return the noSuchInventoryItemText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNoSuchInventoryItemText() {
		return noSuchInventoryItemText.get();
	}

	/**
	 * @return the noSuchItemText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNoSuchItemText() {
		return noSuchItemText.get();
	}

	/**
	 * @return the noSuchPersonText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNoSuchPersonText() {
		return noSuchPersonText.get();
	}

	/**
	 * @return the noSuchWayText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNoSuchWayText() {
		return noSuchWayText.get();
	}

	/**
	 * @return the notTakeableText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNotTakeableText() {
		return notTakeableText.get();
	}

	/**
	 * @return the notTalkingToEnabledText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNotTalkingToEnabledText() {
		return notTalkingToEnabledText.get();
	}

	/**
	 * @return the notTravelableText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNotTravelableText() {
		return notTravelableText.get();
	}

	/**
	 * @return the notUsableText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNotUsableText() {
		return notUsableText.get();
	}

	/**
	 * @return the notUsableWithText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getNotUsableWithText() {
		return notUsableWithText.get();
	}

	/**
	 * @return the numberOfOptionLines
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public int getNumberOfOptionLines() {
		return numberOfOptionLines.get();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the start items
	 */
	@Transient
	public List<InventoryItem> getStartItems() {
		return addInventoryItemsAction.getPickUpItems();
	}

	/**
	 * @return the startLocation
	 */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * @return the startText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getStartText() {
		return startText.get();
	}

	/**
	 * @return the successfullBgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getSuccessfullBgColor() {
		return successfullBgColor.getValue();
	}

	/**
	 * @return the successfullFgColor
	 */
	@Access(AccessType.PROPERTY)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Color getSuccessfullFgColor() {
		return successfullFgColor.getValue();
	}

	/**
	 * @return the takeCommands
	 */
	public List<String> getTakeCommands() {
		return takeCommands;
	}

	/**
	 * @return the takeHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getTakeHelpText() {
		return takeHelpText.get();
	}

	/**
	 * @return the takenText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getTakenText() {
		return takenText.get();
	}

	/**
	 * @return the talkToCommands
	 */
	public List<String> getTalkToCommands() {
		return talkToCommands;
	}

	/**
	 * @return the talkToHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getTalkToHelpText() {
		return talkToHelpText.get();
	}

	/**
	 * @return the useCommands
	 */
	public List<String> getUseCommands() {
		return useCommands;
	}

	/**
	 * @return the usedText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getUsedText() {
		return usedText.get();
	}

	/**
	 * @return the usedWithText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getUsedWithText() {
		return usedWithText.get();
	}

	/**
	 * @return the useHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getUseHelpText() {
		return useHelpText.get();
	}

	/**
	 * @return the useWithCombineCommands
	 */
	public List<String> getUseWithCombineCommands() {
		return useWithCombineCommands;
	}

	/**
	 * @return the useWithCombineHelpText
	 */
	@Access(AccessType.PROPERTY)
	@Column(nullable = false)
	public String getUseWithCombineHelpText() {
		return useWithCombineHelpText.get();
	}

	/**
	 * @return the helphelptext property
	 */
	public StringProperty helphelpTextProperty() {
		return helpHelpText;
	}

	/**
	 * @return inspectHelpText property
	 */
	public StringProperty inspectHelpTextProperty() {
		return inspectHelpText;
	}

	/**
	 * @return inspectionDefaultText property
	 */
	public StringProperty inspectionDefaultTextProperty() {
		return inspectionDefaultText;
	}

	/**
	 * @return invalidCommandText property
	 */
	public StringProperty invalidCommandTextProperty() {
		return invalidCommandText;
	}

	/**
	 * @return inventoryEmptyText property
	 */
	public StringProperty inventoryEmptyTextProperty() {
		return inventoryEmptyText;
	}

	/**
	 * @return inventoryHelpText property
	 */
	public StringProperty inventoryHelpTextProperty() {
		return inventoryHelpText;
	}

	/**
	 * @return inventoryText property
	 */
	public StringProperty inventoryTextProperty() {
		return inventoryText;
	}

	/**
	 * @return lookAroundHelpText property
	 */
	public StringProperty lookAroundHelpTextProperty() {
		return lookAroundHelpText;
	}

	/**
	 * @return moveHelpText property
	 */
	public StringProperty moveHelpTextProperty() {
		return moveHelpText;
	}

	/**
	 * @return the neutralBgColor property
	 */
	public Property<Color> neutralBgColorProperty() {
		return neutralBgColor;
	}

	/**
	 * @return the neutralFgColor property
	 */
	public Property<Color> neutralFgColorProperty() {
		return neutralFgColor;
	}

	/**
	 * @return noCommandText property
	 */
	public StringProperty noCommandTextProperty() {
		return noCommandText;
	}

	/**
	 * @return noSuchInventoryItemText property
	 */
	public StringProperty noSuchInventoryItemTextProperty() {
		return noSuchInventoryItemText;
	}

	/**
	 * @return noSuchItemText property
	 */
	public StringProperty noSuchItemTextProperty() {
		return noSuchItemText;
	}

	/**
	 * @return noSuchPersonText property
	 */
	public StringProperty noSuchPersonTextProperty() {
		return noSuchPersonText;
	}

	/**
	 * @return noSuchWayText property
	 */
	public StringProperty noSuchWayTextProperty() {
		return noSuchWayText;
	}

	/**
	 * @return notTakeableText property
	 */
	public StringProperty notTakeableTextProperty() {
		return notTakeableText;
	}

	/**
	 * @return notTalkingToEnabledText property
	 */
	public StringProperty notTalkingToEnabledTextProperty() {
		return notTalkingToEnabledText;
	}

	/**
	 * @return notTravelableText property
	 */
	public StringProperty notTravelableTextProperty() {
		return notTravelableText;
	}

	/**
	 * @return notUsableText property
	 */
	public StringProperty notUsableTextProperty() {
		return notUsableText;
	}

	/**
	 * @return notUsableWithText property
	 */
	public StringProperty notUsableWithTextProperty() {
		return notUsableWithText;
	}

	/**
	 * @return numberOfOptionLines property
	 */
	public IntegerProperty numberOfOptionLinesProperty() {
		return numberOfOptionLines;
	}

	/**
	 * Puts the configured start items in the players inventory. Should be
	 * called once upon starting a new game.
	 */
	public void putStartItemsIntoInventory() {
		addInventoryItemsAction.triggerAction(this);
	}

	/**
	 * Removes an exit command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeExitCommand(String cmd) {
		this.exitCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a help command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeHelpCommand(String cmd) {
		this.helpCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes an inventory command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeInventoryCommand(String cmd) {
		this.inventoryCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a look-around command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeLookAroundCommand(String cmd) {
		this.lookAroundCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a move command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeMoveCommand(String cmd) {
		this.moveCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a start item.
	 * 
	 * @param item
	 */
	public void removeStartItem(InventoryItem item) {
		addInventoryItemsAction.removePickUpItem(item);
	}

	/**
	 * Removes a take command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeTakeCommand(String cmd) {
		this.takeCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a talk to command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeTalkToCommand(String cmd) {
		this.talkToCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes a use command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeUseCommand(String cmd) {
		this.useCommands.remove(cmd.toLowerCase());
	}

	/**
	 * Removes an useWith/combine command.
	 * 
	 * @param cmd
	 *            the command
	 */
	public void removeUseWithCombineCommand(String cmd) {
		this.useWithCombineCommands.remove(cmd.toLowerCase());
	}

	/**
	 * @param exitCommandHelpText
	 *            the exitCommandHelpText to set
	 */
	public void setExitCommandHelpText(String exitCommandHelpText) {
		this.exitCommandHelpText.set(exitCommandHelpText);
	}

	/**
	 * @param exitCommands
	 *            the exitCommands to set
	 */
	public void setExitCommands(List<String> exitCommands) {
		this.exitCommands = exitCommands;
	}

	/**
	 * @param failedBgColor
	 *            the failedBgColor to set
	 */
	public void setFailedBgColor(Color failedBgColor) {
		this.failedBgColor.setValue(failedBgColor);
	}

	/**
	 * @param failedFgColor
	 *            the failedFgColor to set
	 */
	public void setFailedFgColor(Color failedFgColor) {
		this.failedFgColor.setValue(failedFgColor);
	}

	/**
	 * @param gameTitle
	 *            the gameTitle to set
	 */
	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}

	/**
	 * @param hasEnded
	 *            the hasEnded to set
	 */
	public void setHasEnded(boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	/**
	 * @param helpCommands
	 *            the helpCommands to set
	 */
	public void setHelpCommands(List<String> helpCommands) {
		this.helpCommands = helpCommands;
	}

	/**
	 * @param helpHelpText
	 *            the helpHelpText to set
	 */
	public void setHelpHelpText(String helpHelpText) {
		this.helpHelpText.set(helpHelpText);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param inspectCommands
	 *            the inspectCommands to set
	 */
	public void setInspectCommands(List<String> inspectCommands) {
		this.inspectCommands = inspectCommands;
	}

	/**
	 * @param inspectHelpText
	 *            the inspectHelpText to set
	 */
	public void setInspectHelpText(String inspectHelpText) {
		this.inspectHelpText.set(inspectHelpText);
	}

	/**
	 * @param inspectionDefaultText
	 *            the inspectionDefaultText to set
	 */
	public void setInspectionDefaultText(String inspectionDefaultText) {
		this.inspectionDefaultText.set(inspectionDefaultText);
	}

	/**
	 * 
	 * @param invalidCommandText
	 *            the invalidCommandText to set
	 */
	public void setInvalidCommandText(String invalidCommandText) {
		this.invalidCommandText.set(invalidCommandText);
	}

	/**
	 * @param inventoryCommands
	 *            the inventoryCommands to set
	 */
	public void setInventoryCommands(List<String> inventoryCommands) {
		this.inventoryCommands = inventoryCommands;
	}

	/**
	 * @param inventoryEmptyText
	 *            the inventoryEmptyText to set
	 */
	public void setInventoryEmptyText(String inventoryEmptyText) {
		this.inventoryEmptyText.set(inventoryEmptyText);
	}

	/**
	 * @param inventoryHelpText
	 *            the inventoryHelpText to set
	 */
	public void setInventoryHelpText(String inventoryHelpText) {
		this.inventoryHelpText.set(inventoryHelpText);
	}

	/**
	 * @param inventoryText
	 *            the inventoryText to set
	 */
	public void setInventoryText(String inventoryText) {
		this.inventoryText.set(inventoryText);
	}

	/**
	 * @param lookAroundCommands
	 *            the lookAroundCommands to set
	 */
	public void setLookAroundCommands(List<String> lookAroundCommands) {
		this.lookAroundCommands = lookAroundCommands;
	}

	/**
	 * @param lookAroundHelpText
	 *            the lookAroundHelpText to set
	 */
	public void setLookAroundHelpText(String lookAroundHelpText) {
		this.lookAroundHelpText.set(lookAroundHelpText);
	}

	/**
	 * @param moveCommands
	 *            the moveCommands to set
	 */
	public void setMoveCommands(List<String> moveCommands) {
		this.moveCommands = moveCommands;
	}

	/**
	 * @param moveHelpText
	 *            the moveHelpText to set
	 */
	public void setMoveHelpText(String moveHelpText) {
		this.moveHelpText.set(moveHelpText);
	}

	/**
	 * @param neutralBgColor
	 *            the neutralBgColor to set
	 */
	public void setNeutralBgColor(Color neutralBgColor) {
		this.neutralBgColor.setValue(neutralBgColor);
	}

	/**
	 * @param neutralFgColor
	 *            the neutralFgColor to set
	 */
	public void setNeutralFgColor(Color neutralFgColor) {
		this.neutralFgColor.setValue(neutralFgColor);
	}

	/**
	 * @param noCommandText
	 *            the noCommandText to set
	 */
	public void setNoCommandText(String noCommandText) {
		this.noCommandText.set(noCommandText);
	}

	/**
	 * @param noSuchInventoryItemText
	 *            the noSuchInventoryItemText to set
	 */
	public void setNoSuchInventoryItemText(String noSuchInventoryItemText) {
		this.noSuchInventoryItemText.set(noSuchInventoryItemText);
	}

	/**
	 * @param noSuchItemText
	 *            the noSuchItemText to set
	 */
	public void setNoSuchItemText(String noSuchItemText) {
		this.noSuchItemText.set(noSuchItemText);
	}

	/**
	 * @param noSuchPersonText
	 *            the noSuchPersonText to set
	 */
	public void setNoSuchPersonText(String noSuchPersonText) {
		this.noSuchPersonText.set(noSuchPersonText);
	}

	/**
	 * @param noSuchWayText
	 *            the noSuchWayText to set
	 */
	public void setNoSuchWayText(String noSuchWayText) {
		this.noSuchWayText.set(noSuchWayText);
	}

	/**
	 * @param notTakeableText
	 *            the notTakeableText to set
	 */
	public void setNotTakeableText(String notTakeableText) {
		this.notTakeableText.set(notTakeableText);
	}

	/**
	 * @param notTalkingToEnabledText
	 *            the notTalkingToEnabledText to set
	 */
	public void setNotTalkingToEnabledText(String notTalkingToEnabledText) {
		this.notTalkingToEnabledText.set(notTalkingToEnabledText);
	}

	/**
	 * @param notTravelableText
	 *            the notTravelableText to set
	 */
	public void setNotTravelableText(String notTravelableText) {
		this.notTravelableText.set(notTravelableText);
	}

	/**
	 * @param notUsableText
	 *            the notUsableText to set
	 */
	public void setNotUsableText(String notUsableText) {
		this.notUsableText.set(notUsableText);
	}

	/**
	 * @param notUsableWithText
	 *            the notUsableWithText to set
	 */
	public void setNotUsableWithText(String notUsableWithText) {
		this.notUsableWithText.set(notUsableWithText);
	}

	/**
	 * @param numberOfOptionLines
	 *            the numberOfOptionLines to set
	 */
	public void setNumberOfOptionLines(int numberOfOptionLines) {
		this.numberOfOptionLines.set(numberOfOptionLines);
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @param startLocation
	 *            the startLocation to set
	 */
	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	/**
	 * @param startText
	 *            the startText to set
	 */
	public void setStartText(String startText) {
		this.startText.set(startText);
	}

	/**
	 * @param successfullBgColor
	 *            the successfullBgColor to set
	 */
	public void setSuccessfullBgColor(Color successfullBgColor) {
		this.successfullBgColor.setValue(successfullBgColor);
	}

	/**
	 * @param successfullFgColor
	 *            the successfullFgColor to set
	 */
	public void setSuccessfullFgColor(Color successfullFgColor) {
		this.successfullFgColor.setValue(successfullFgColor);
	}

	/**
	 * @param takeCommands
	 *            the takeCommands to set
	 */
	public void setTakeCommands(List<String> takeCommands) {
		this.takeCommands = takeCommands;
	}

	/**
	 * @param takeHelpText
	 *            the takeHelpText to set
	 */
	public void setTakeHelpText(String takeHelpText) {
		this.takeHelpText.set(takeHelpText);
	}

	/**
	 * @param takenText
	 *            the takenText to set
	 */
	public void setTakenText(String takenText) {
		this.takenText.set(takenText);
	}

	/**
	 * @param talkToCommands
	 *            the talkToCommands to set
	 */
	public void setTalkToCommands(List<String> talkToCommands) {
		this.talkToCommands = talkToCommands;
	}

	/**
	 * @param talkToHelpText
	 *            the talkToHelpText to set
	 */
	public void setTalkToHelpText(String talkToHelpText) {
		this.talkToHelpText.set(talkToHelpText);
	}

	/**
	 * @param useCommands
	 *            the useCommands to set
	 */
	public void setUseCommands(List<String> useCommands) {
		this.useCommands = useCommands;
	}

	/**
	 * @param usedText
	 *            the usedText to set
	 */
	public void setUsedText(String usedText) {
		this.usedText.set(usedText);
	}

	/**
	 * @param usedWithText
	 *            the usedWithText to set
	 */
	public void setUsedWithText(String usedWithText) {
		this.usedWithText.set(usedWithText);
	}

	/**
	 * @param useHelpText
	 *            the useHelpText to set
	 */
	public void setUseHelpText(String useHelpText) {
		this.useHelpText.set(useHelpText);
	}

	/**
	 * @param useWithCombineCommands
	 *            the useWithCombineCommands to set
	 */
	public void setUseWithCombineCommands(List<String> useWithCombineCommands) {
		this.useWithCombineCommands = useWithCombineCommands;
	}

	/**
	 * @param useWithCombineHelpText
	 *            the useWithCombineHelpText to set
	 */
	public void setUseWithCombineHelpText(String useWithCombineHelpText) {
		this.useWithCombineHelpText.set(useWithCombineHelpText);
	}

	/**
	 * @return startText property
	 */
	public StringProperty startTextProperty() {
		return startText;
	}

	/**
	 * @return the successfullBgColor property
	 */
	public Property<Color> successfullBgColorProperty() {
		return successfullBgColor;
	}

	/**
	 * @return the successfullFgColor property
	 */
	public Property<Color> successfullFgColorProperty() {
		return successfullFgColor;
	}

	/**
	 * @return takeHelpText property
	 */
	public StringProperty takeHelpTextProperty() {
		return takeHelpText;
	}

	/**
	 * @return takenText property
	 */
	public StringProperty takenTextProperty() {
		return takenText;
	}

	/**
	 * @return the talkToHelpText property
	 */
	public StringProperty talkToHelpTextProperty() {
		return talkToHelpText;
	}

	/**
	 * @return usedText property
	 */
	public StringProperty usedTextProperty() {
		return usedText;
	}

	/**
	 * @return usedWithText property
	 */
	public StringProperty usedWithTextProperty() {
		return usedWithText;
	}

	/**
	 * @return the useHelpText property
	 */
	public StringProperty useHelpTextProperty() {
		return useHelpText;
	}

	/**
	 * @return the useWithCombineHelpText property
	 */
	public StringProperty useWithCombineHelpTextProperty() {
		return useWithCombineHelpText;
	}
}