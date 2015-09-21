package data;

import com.googlecode.lanterna.terminal.Terminal.Color;
import data.interfaces.HasId;
import java.util.ArrayList;
import java.util.List;
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

/**
 * A game that can be played. Contains all configuration and (default) texts.
 * There should be only one game per database.
 * 
 * @author Satia
 */
@Entity
public class Game implements HasId {
	/*
	 * Thoughts on sets instead of lists:
	 * Pro: Use Sets instead of Lists, where useful. Therefore, if former
	 * Lists of own classes, let these (or better all) classes override equals,
	 * hashCode, and toString.
	 * Then, the "if(contains)" check can be removed.
	 * Con: No ordering. Therefore, lists are kept.
	 */
	
	/**
	 * The help text being displayed for the exit command
	 */
	@Column(nullable = false)
	private String exitCommandHelpText;
	
	/**
	 * All commands that make the game exit. Must be lowercase.
	 */
	@ElementCollection
	private final List<String> exitCommands;

	/**
	 * The background color that is used for text printed after a failed action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color failedBgColor;

	/**
	 * The color that is used for text printed after a failed action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color failedFgColor;

	/**
	 * The name of the game. Should be unique, as savegames are
	 * ordered by this name, if there are multiple games used.
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
	private final List<String> helpCommands;

	/**
	 * The help text being displayed for the help command
	 */
	@Column(nullable = false)
	private String helpHelpText;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue
	private int id;

	/**
	 * All commands that let the player inspect something. Must be lowercase.
	 * Must contain at least one word and exactly one parameter for the object:
	 * {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> inspectCommands;

	/**
	 * The help text being displayed for the inspect command
	 */
	@Column(nullable = false)
	private String inspectHelpText;

	/**
	 * The text being displayed when an object is inspected that does not have
	 * an individual inspection text. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String inspectionDefaultText;
	
	/**
	 * All commands that let the player look into his inventory. Must be
	 * lowercase.
	 */
	@ElementCollection
	private final List<String> inventoryCommands;

	/**
	 * The text being displayed, when the player looks into his empty inventory.
	 * Valid placeholders: {@literal <input>}
	 */
	@Column(nullable = false)
	private String inventoryEmptyText;

	/**
	 * The help text being displayed for the inventory command
	 */
	@Column(nullable = false)
	private String inventoryHelpText;

	/**
	 * The text introducing a look into the inventory. Valid placeholders:
	 * {@literal <input>}
	 */
	@Column(nullable = false)
	private String inventoryText;

	/**
	 * All commands that let the player look around. Must be lowercase.
	 */
	@ElementCollection
	private final List<String> lookAroundCommands;

	/**
	 * The help text being displayed for the look around command
	 */
	@Column(nullable = false)
	private String lookAroundHelpText;

	/**
	 * All move commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the target: {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> moveCommands;
	/**
	 * The help text being displayed for the move command
	 */
	@Column(nullable = false)
	private String moveHelpText;

	/**
	 * The background color that is used for text printed after a neutral
	 * action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color neutralBgColor;

	/**
	 * The color that is used for text printed after a neutral action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color neutralFgColor;

	/**
	 * The text being displayed, when any entered text is not recognized as a
	 * valid command. Valid placeholders: {@literal <input>}
	 */
	@Column(nullable = false)
	private String noCommandText;

	/**
	 * The text being displayed, when the player tries to use, etc. a
	 * non-existing inventory item. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchInventoryItemText;

	/**
	 * The text being displayed, when the player tries to use, take, etc. a
	 * non-existing item. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchItemText;

	/**
	 * The text being displayed, when the player tries to talk to a non-existing
	 * perosn. Valid placeholders: {@literal <input>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchPersonText;

	/**
	 * The text being displayed, when the player tries to travel by a
	 * non-existing way. Valid placeholders: {@literal <input>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String noSuchWayText;

	/**
	 * The default text, when the player tries to take a non-takeable item. May
	 * be overwritten for each individual item. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notTakeableText;

	/**
	 * The default text, when the player tries to talk to a person he cannot
	 * talk to. way. May be overwritten for each individual person. Valid
	 * placeholders: {@literal <input>}, {@literal <name>},
	 * {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notTalkingToEnabledText;

	/**
	 * The default text, when the player tries to travel by a non-travelable
	 * way. May be overwritten for each individual way. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notTravelableText;

	/**
	 * The default text, when the player tries to use a non-usable object. May
	 * be overwritten for each individual object. Valid placeholders:
	 * {@literal <input>}, {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String notUsableText;

	/**
	 * The default text, when the player tries to use two incompatible object
	 * with one another. May be overwritten for each individual combination.
	 * Valid placeholders: {@literal <input>}, {@literal <name>},
	 * {@literal <identifier>}, {@literal <name2>}, {@literal <identifier2>}
	 */
	@Column(nullable = false)
	private String notUsableWithText;

	/**
	 * The number of lines used to display options in dialogues.
	 * Recommendation is ~10.
	 */
	@Column(nullable = false)
	private int numberOfOptionLines;

	/**
	 * The player of this game.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private Player player;

	/**
	 * The starting location of the game.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private Location startLocation;

	/**
	 * The text being displayed, when the game starts.
	 */
	@Column(nullable = false)
	private String startText;

	/**
	 * The background color that is used for text printed after a successful
	 * action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color successfullBgColor;

	/**
	 * The color that is used for text printed after a successful action.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color successfullFgColor;

	/**
	 * All take commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> takeCommands;

	/**
	 * The help text being displayed for the take command
	 */
	@Column(nullable = false)
	private String takeHelpText;

	/**
	 * The default text, when the player takes an item. May be overwritten for
	 * each individual item. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String takenText;

	/**
	 * All commands that let the player talk to someone. Must be lowercase. Must
	 * contain at least one word and exactly one parameter for the person:
	 * {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> talkToCommands;

	/**
	 * The help text being displayed for the talk to command
	 */
	@Column(nullable = false)
	private String talkToHelpText;

	/**
	 * All use commands. Must be lowercase. Must contain at least one word and
	 * exactly one parameter for the object: {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> useCommands;

	/**
	 * The default text, when the player uses an object. May be overwritten for
	 * each individual object. Valid placeholders: {@literal <input>},
	 * {@literal <name>}, {@literal <identifier>}
	 */
	@Column(nullable = false)
	private String usedText;

	/**
	 * The default text, when the player uses two compatible object with one
	 * another. May be overwritten for each individual combination. Valid
	 * placeholders: {@literal <input>}, {@literal <name>},
	 * {@literal <identifier>}, {@literal <name2>}, {@literal <identifier2>}
	 */
	@Column(nullable = false)
	private String usedWithText;

	/**
	 * The help text being displayed for the use command
	 */
	@Column(nullable = false)
	private String useHelpText;

	/**
	 * All useWith/combine commands. Must be lowercase. Must contain at least
	 * one word and exactly two parameters for the objects: {@literal (.+)}
	 */
	@ElementCollection
	private final List<String> useWithCombineCommands;

	/**
	 * The help text being displayed for the use with/combine command
	 */
	@Column(nullable = false)
	private String useWithCombineHelpText;

	/**
	 * Constructs a new game object.
	 */
	public Game() {
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
	 * @return the exitCommandHelpText
	 */
	public String getExitCommandHelpText() {
		return exitCommandHelpText;
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
	public Color getFailedBgColor() {
		return failedBgColor;
	}

	/**
	 * @return the failedFgColor
	 */
	public Color getFailedFgColor() {
		return failedFgColor;
	}

	/**
	 * @return the gameTitle
	 */
	public String getGameTitle() {
		return gameTitle;
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
	public String getHelpHelpText() {
		return helpHelpText;
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
	public String getInspectHelpText() {
		return inspectHelpText;
	}

	/**
	 * @return the inspectionDefaultText
	 */
	public String getInspectionDefaultText() {
		return inspectionDefaultText;
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
	public String getInventoryEmptyText() {
		return inventoryEmptyText;
	}

	/**
	 * @return the inventoryHelpText
	 */
	public String getInventoryHelpText() {
		return inventoryHelpText;
	}

	/**
	 * @return the inventoryText
	 */
	public String getInventoryText() {
		return inventoryText;
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
	public String getLookAroundHelpText() {
		return lookAroundHelpText;
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
	public String getMoveHelpText() {
		return moveHelpText;
	}

	/**
	 * @return the neutralBgColor
	 */
	public Color getNeutralBgColor() {
		return neutralBgColor;
	}

	/**
	 * @return the neutralFgColor
	 */
	public Color getNeutralFgColor() {
		return neutralFgColor;
	}

	/**
	 * @return the noCommandText
	 */
	public String getNoCommandText() {
		return noCommandText;
	}

	/**
	 * @return the noSuchInventoryItemText
	 */
	public String getNoSuchInventoryItemText() {
		return noSuchInventoryItemText;
	}

	/**
	 * @return the noSuchItemText
	 */
	public String getNoSuchItemText() {
		return noSuchItemText;
	}

	/**
	 * @return the noSuchPersonText
	 */
	public String getNoSuchPersonText() {
		return noSuchPersonText;
	}

	/**
	 * @return the noSuchWayText
	 */
	public String getNoSuchWayText() {
		return noSuchWayText;
	}

	/**
	 * @return the notTakeableText
	 */
	public String getNotTakeableText() {
		return notTakeableText;
	}

	/**
	 * @return the notTalkingToEnabledText
	 */
	public String getNotTalkingToEnabledText() {
		return notTalkingToEnabledText;
	}

	/**
	 * @return the notTravelableText
	 */
	public String getNotTravelableText() {
		return notTravelableText;
	}

	/**
	 * @return the notUsableText
	 */
	public String getNotUsableText() {
		return notUsableText;
	}

	/**
	 * @return the notUsableWithText
	 */
	public String getNotUsableWithText() {
		return notUsableWithText;
	}

	/**
	 * @return the numberOfOptionLines
	 */
	public int getNumberOfOptionLines() {
		return numberOfOptionLines;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
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
	public String getStartText() {
		return startText;
	}

	/**
	 * @return the successfullBgColor
	 */
	public Color getSuccessfullBgColor() {
		return successfullBgColor;
	}

	/**
	 * @return the successfullFgColor
	 */
	public Color getSuccessfullFgColor() {
		return successfullFgColor;
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
	public String getTakeHelpText() {
		return takeHelpText;
	}

	/**
	 * @return the takenText
	 */
	public String getTakenText() {
		return takenText;
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
	public String getTalkToHelpText() {
		return talkToHelpText;
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
	public String getUsedText() {
		return usedText;
	}

	/**
	 * @return the usedWithText
	 */
	public String getUsedWithText() {
		return usedWithText;
	}

	/**
	 * @return the useHelpText
	 */
	public String getUseHelpText() {
		return useHelpText;
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
	public String getUseWithCombineHelpText() {
		return useWithCombineHelpText;
	}

	/**
	 * @return the hasEnded
	 */
	public boolean isHasEnded() {
		return hasEnded;
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
		this.exitCommandHelpText = exitCommandHelpText;
	}

	/**
	 * @param failedBgColor
	 *            the failedBgColor to set
	 */
	public void setFailedBgColor(Color failedBgColor) {
		this.failedBgColor = failedBgColor;
	}

	/**
	 * @param failedFgColor
	 *            the failedFgColor to set
	 */
	public void setFailedFgColor(Color failedFgColor) {
		this.failedFgColor = failedFgColor;
	}

	/**
	 * @param gameTitle the gameTitle to set
	 */
	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}

	/**
	 * @param hasEnded the hasEnded to set
	 */
	public void setHasEnded(boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	/**
	 * @param helpHelpText
	 *            the helpHelpText to set
	 */
	public void setHelpHelpText(String helpHelpText) {
		this.helpHelpText = helpHelpText;
	}

	/**
	 * @param inspectHelpText
	 *            the inspectHelpText to set
	 */
	public void setInspectHelpText(String inspectHelpText) {
		this.inspectHelpText = inspectHelpText;
	}

	/**
	 * @param inspectionDefaultText
	 *            the inspectionDefaultText to set
	 */
	public void setInspectionDefaultText(String inspectionDefaultText) {
		this.inspectionDefaultText = inspectionDefaultText;
	}

	/**
	 * @param inventoryEmptyText
	 *            the inventoryEmptyText to set
	 */
	public void setInventoryEmptyText(String inventoryEmptyText) {
		this.inventoryEmptyText = inventoryEmptyText;
	}

	/**
	 * @param inventoryHelpText
	 *            the inventoryHelpText to set
	 */
	public void setInventoryHelpText(String inventoryHelpText) {
		this.inventoryHelpText = inventoryHelpText;
	}

	/**
	 * @param inventoryText
	 *            the inventoryText to set
	 */
	public void setInventoryText(String inventoryText) {
		this.inventoryText = inventoryText;
	}

	/**
	 * @param lookAroundHelpText
	 *            the lookAroundHelpText to set
	 */
	public void setLookAroundHelpText(String lookAroundHelpText) {
		this.lookAroundHelpText = lookAroundHelpText;
	}

	/**
	 * @param moveHelpText
	 *            the moveHelpText to set
	 */
	public void setMoveHelpText(String moveHelpText) {
		this.moveHelpText = moveHelpText;
	}

	/**
	 * @param neutralBgColor
	 *            the neutralBgColor to set
	 */
	public void setNeutralBgColor(Color neutralBgColor) {
		this.neutralBgColor = neutralBgColor;
	}

	/**
	 * @param neutralFgColor
	 *            the neutralFgColor to set
	 */
	public void setNeutralFgColor(Color neutralFgColor) {
		this.neutralFgColor = neutralFgColor;
	}

	/**
	 * @param noCommandText
	 *            the noCommandText to set
	 */
	public void setNoCommandText(String noCommandText) {
		this.noCommandText = noCommandText;
	}

	/**
	 * @param noSuchInventoryItemText
	 *            the noSuchInventoryItemText to set
	 */
	public void setNoSuchInventoryItemText(String noSuchInventoryItemText) {
		this.noSuchInventoryItemText = noSuchInventoryItemText;
	}

	/**
	 * @param noSuchItemText
	 *            the noSuchItemText to set
	 */
	public void setNoSuchItemText(String noSuchItemText) {
		this.noSuchItemText = noSuchItemText;
	}

	/**
	 * @param noSuchPersonText the noSuchPersonText to set
	 */
	public void setNoSuchPersonText(String noSuchPersonText) {
		this.noSuchPersonText = noSuchPersonText;
	}

	/**
	 * @param noSuchWayText
	 *            the noSuchWayText to set
	 */
	public void setNoSuchWayText(String noSuchWayText) {
		this.noSuchWayText = noSuchWayText;
	}

	/**
	 * @param notTakeableText
	 *            the notTakeableText to set
	 */
	public void setNotTakeableText(String notTakeableText) {
		this.notTakeableText = notTakeableText;
	}

	/**
	 * @param notTalkingToEnabledText
	 *            the notTalkingToEnabledText to set
	 */
	public void setNotTalkingToEnabledText(String notTalkingToEnabledText) {
		this.notTalkingToEnabledText = notTalkingToEnabledText;
	}

	/**
	 * @param notTravelableText
	 *            the notTravelableText to set
	 */
	public void setNotTravelableText(String notTravelableText) {
		this.notTravelableText = notTravelableText;
	}

	/**
	 * @param notUsableText
	 *            the notUsableText to set
	 */
	public void setNotUsableText(String notUsableText) {
		this.notUsableText = notUsableText;
	}

	/**
	 * @param notUsableWithText
	 *            the notUsableWithText to set
	 */
	public void setNotUsableWithText(String notUsableWithText) {
		this.notUsableWithText = notUsableWithText;
	}

	/**
	 * @param numberOfOptionLines the numberOfOptionLines to set
	 */
	public void setNumberOfOptionLines(int numberOfOptionLines) {
		this.numberOfOptionLines = numberOfOptionLines;
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
		this.startText = startText;
	}

	/**
	 * @param successfullBgColor
	 *            the successfullBgColor to set
	 */
	public void setSuccessfullBgColor(Color successfullBgColor) {
		this.successfullBgColor = successfullBgColor;
	}

	/**
	 * @param successfullFgColor
	 *            the successfullFgColor to set
	 */
	public void setSuccessfullFgColor(Color successfullFgColor) {
		this.successfullFgColor = successfullFgColor;
	}

	/**
	 * @param takeHelpText
	 *            the takeHelpText to set
	 */
	public void setTakeHelpText(String takeHelpText) {
		this.takeHelpText = takeHelpText;
	}

	/**
	 * @param takenText
	 *            the takenText to set
	 */
	public void setTakenText(String takenText) {
		this.takenText = takenText;
	}

	/**
	 * @param talkToHelpText
	 *            the talkToHelpText to set
	 */
	public void setTalkToHelpText(String talkToHelpText) {
		this.talkToHelpText = talkToHelpText;
	}

	/**
	 * @param usedText
	 *            the usedText to set
	 */
	public void setUsedText(String usedText) {
		this.usedText = usedText;
	}

	/**
	 * @param usedWithText
	 *            the usedWithText to set
	 */
	public void setUsedWithText(String usedWithText) {
		this.usedWithText = usedWithText;
	}

	/**
	 * @param useHelpText
	 *            the useHelpText to set
	 */
	public void setUseHelpText(String useHelpText) {
		this.useHelpText = useHelpText;
	}

	/**
	 * @param useWithCombineHelpText
	 *            the useWithCombineHelpText to set
	 */
	public void setUseWithCombineHelpText(String useWithCombineHelpText) {
		this.useWithCombineHelpText = useWithCombineHelpText;
	}
}