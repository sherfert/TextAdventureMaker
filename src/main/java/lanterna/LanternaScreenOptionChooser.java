package lanterna;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lanterna.LanternaScreenTextArea.ColoredText;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * This class provides a wrapper, if you want to have options displayed in a
 * certain area of the screen, and want to be able to scroll them with the
 * up/down arrow keys and choose with the enter key.
 * 
 * @author Satia
 */
public class LanternaScreenOptionChooser {
	/**
	 * Used to callback if an option has been chosen.
	 * 
	 * @author Satia Herfert
	 */
	public interface OptionHandler {
		/**
		 * An option has been chosen.
		 * 
		 * @param index
		 *            the index of the option
		 */
		void chooseOption(int index);
	}

	/**
	 * The screen to use.
	 */
	private final Screen screen;

	/**
	 * The options, which are printed.
	 */
	private List<String> options;

	/**
	 * The options split into lines.
	 */
	private List<List<String>> optionsSplit;

	/**
	 * The x starting coordinate, included.
	 */
	private int fromX;

	/**
	 * The x ending coordinate, excluded.
	 */
	private int toX;

	/**
	 * The y starting coordinate, included.
	 */
	private int fromY;

	/**
	 * The y ending coordinate, excluded.
	 */
	private int toY;

	/**
	 * A callback implementation to handle chosen options.
	 */
	private final OptionHandler optionHandler;

	/**
	 * The index of the currently chosen option.
	 */
	private int index;

	/**
	 * Constructs a new OptionChoser, that uses the full screen.
	 * 
	 * @param screen
	 *            the screen to use
	 * @param options
	 *            the options to display
	 * @param optionHandler
	 *            the option handler
	 */
	public LanternaScreenOptionChooser(Screen screen, List<String> options, OptionHandler optionHandler) {
		this(screen, options, optionHandler, 0, screen.getTerminalSize().getColumns(), 0,
				screen.getTerminalSize().getRows());
	}

	/**
	 * Constructs a fully configurable OptionChoser.
	 * 
	 * @param screen
	 *            the screen to use
	 * @param options
	 *            the options to display
	 * @param optionHandler
	 *            the option handler
	 * @param fromX
	 *            the x starting coordinate, included
	 * @param toX
	 *            the x ending coordinate, excluded
	 * @param fromY
	 *            the y starting coordinate, included
	 * @param toY
	 *            the y ending coordinate, excluded
	 */
	public LanternaScreenOptionChooser(Screen screen, List<String> options, OptionHandler optionHandler, int fromX,
			int toX, int fromY, int toY) {
		this.screen = screen;
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;
		this.optionHandler = optionHandler;

		// This also sets index, optionsSplit and updates
		this.setOptions(options);
	}

	/**
	 * @return the number of rows
	 */
	public int rows() {
		return toY - fromY;
	}

	/**
	 * @return the number of columns
	 */
	public int columns() {
		return toX - fromX;
	}

	/**
	 * The list MUST NOT be empty!
	 * 
	 * Also resets the index to 0.
	 * 
	 * @param options
	 *            the options to set
	 */
	public final void setOptions(List<String> options) {
		this.options = options;
		this.optionsSplit = splitLines(options);
		this.index = 0;
		update();
	}

	/**
	 * Splits a list of lines into a list of lists of Strings, where each String
	 * fits into one row.
	 * 
	 * @param lines
	 *            the original lines
	 * @return the split lines.
	 */
	private List<List<String>> splitLines(List<String> lines) {
		List<List<String>> result = new ArrayList<>();
		// Split each after length is reached
		for (String line : lines) {
			List<String> splitLine = new ArrayList<>();
			String remainingText = line;

			int columns = columns();
			while (remainingText.length() > 0) {
				if (remainingText.length() > columns) {
					// Split after n chars or the blank before that
					int splitIndex = remainingText.lastIndexOf(' ', columns);
					// Set to the end, if there is no space
					if (splitIndex == -1) {
						splitIndex = columns;
					}

					splitLine.add(remainingText.substring(0, splitIndex));
					// Remove this part from remaining text
					remainingText = remainingText.substring(splitIndex).trim();
					// Indentation
					remainingText = " " + remainingText;
				} else {
					splitLine.add(remainingText);
					remainingText = "";
				}
			}
			result.add(splitLine);
		}
		return result;
	}

	/**
	 * @return the number of total lines after splitting.
	 */
	private int totalLines() {
		int num = 0;
		for (List<String> splitLine : optionsSplit) {
			num += splitLine.size();
		}
		return num;
	}

	private void update() {
		if (rows() >= totalLines()) {
			// All options fit on the screen.
			int lineIndex = 0;
			for (int i = 0; i < optionsSplit.size(); i++) {
				for (int j = 0; j < optionsSplit.get(i).size(); j++, lineIndex++) {
					String text = optionsSplit.get(i).get(j);

					// Create blank string to fill the rest
					String blanks = LanternaScreenTextArea.blankString(columns() - text.length());

					if (i == index) {
						// Use black on white to mark the selected option
						screen.putString(fromX, fromY + lineIndex, text + blanks, Color.BLACK, Color.WHITE);
					} else {
						// Use default colors for unselected option
						screen.putString(fromX, fromY + lineIndex, text + blanks, Color.DEFAULT, Color.DEFAULT);
					}
				}
			}
			// Fill the remaining lines with whitespace
			for (int i = lineIndex; i < rows(); i++) {
				// Create blank string to fill the rest
				String blanks = LanternaScreenTextArea.blankString(columns());

				screen.putString(fromX, fromY + i, blanks, Color.DEFAULT, Color.DEFAULT);
			}

		} else {
			// List to represent text before actually printing
			List<ColoredText> linesToPrint = new LinkedList<>();
			// Fill with empty lines
			for (int i = 0; i < rows(); i++) {
				linesToPrint.add(new ColoredText("", Color.DEFAULT, Color.DEFAULT));
			}

			// Print chosen option in the middle
			int lineIndex = rows() / 2 - optionsSplit.get(index).size() / 2;
			// The index used later to print upwards
			int upLineIndex = lineIndex - 1;
			for (int i = 0; i < optionsSplit.get(index).size(); i++, lineIndex++) {
				// In case it does not fit
				if (lineIndex < 0) {
					continue;
				}
				if (lineIndex >= linesToPrint.size()) {
					break;
				}

				// Black on white
				linesToPrint.set(lineIndex, new ColoredText(optionsSplit.get(index).get(i), Color.WHITE, Color.BLACK));
			}

			int optionIndex = index;
			// As long as options left below
			while (++optionIndex < optionsSplit.size()) {
				// If this is not the last option, we need space to at least
				// print "..."
				int additionLines = optionIndex == optionsSplit.size() - 1 ? 0 : 1;
				if (lineIndex + optionsSplit.get(optionIndex).size() + additionLines <= rows()) {
					// fits
					for (int i = 0; i < optionsSplit.get(optionIndex).size(); i++, lineIndex++) {
						linesToPrint.set(lineIndex,
								new ColoredText(optionsSplit.get(optionIndex).get(i), Color.DEFAULT, Color.DEFAULT));
					}
				} else {
					// Does not fit, break
					break;
				}
			}

			{
				// If there were no options left, but blank lines
				// remain, we can shift everything down completely,
				// otherwise we must leave one line free.
				int additionLines = optionIndex == optionsSplit.size() ? 0 : 1;
				for (; lineIndex + additionLines < rows(); lineIndex++) {
					// shift down and increment to upLineIndex
					linesToPrint.add(0, new ColoredText("", Color.DEFAULT, Color.DEFAULT));
					linesToPrint.remove(linesToPrint.size() - 1);
					upLineIndex++;
				}
			}

			// The upper part...
			int upOptionIndex = index;
			// Start with optionIndex before the selected option
			// As long as options left before
			while (--upOptionIndex >= 0) {
				// If this is not the first option, we need space to at least
				// print "..."
				int additionLines = upOptionIndex == 0 ? 0 : 1;
				if (upLineIndex + 1 - optionsSplit.get(upOptionIndex).size() - additionLines >= 0) {
					// fits
					for (int i = optionsSplit.get(upOptionIndex).size() - 1; i >= 0; i--, upLineIndex--) {
						linesToPrint.set(upLineIndex,
								new ColoredText(optionsSplit.get(upOptionIndex).get(i), Color.DEFAULT, Color.DEFAULT));
					}
				} else {
					// Does not fit, break
					break;
				}
			}

			{
				// If there were no options left, but blank lines
				// remain, we can shift everything up completely,
				// otherwise we must leave one line free.
				int additionLines = upOptionIndex == -1 ? 0 : 1;
				for (; upLineIndex - additionLines >= 0; upLineIndex--) {
					// shift up and decrement to lineIndex
					linesToPrint.add(new ColoredText("", Color.DEFAULT, Color.DEFAULT));
					linesToPrint.remove(0);
					lineIndex--;
				}
			}

			// Now it can happen that below fit new options
			optionIndex--;
			// As long as options left below
			while (++optionIndex < optionsSplit.size()) {
				// If this is not the last option, we need space to at least
				// print "..."
				int additionLines = optionIndex == optionsSplit.size() - 1 ? 0 : 1;
				if (lineIndex + optionsSplit.get(optionIndex).size() + additionLines <= rows()) {
					// fits
					for (int i = 0; i < optionsSplit.get(optionIndex).size(); i++, lineIndex++) {
						linesToPrint.set(lineIndex,
								new ColoredText(optionsSplit.get(optionIndex).get(i), Color.DEFAULT, Color.DEFAULT));
					}
				} else {
					// Does not fit, break
					break;
				}
			}

			// Print "..."s in first and/or last line, if there were options
			// left
			if (upOptionIndex >= 0) {
				linesToPrint.set(0, new ColoredText("...", Color.DEFAULT, Color.DEFAULT));
			}
			if (optionIndex < optionsSplit.size()) {
				linesToPrint.set(linesToPrint.size() - 1, new ColoredText("...", Color.DEFAULT, Color.DEFAULT));
			}

			// Actually print lines
			int columns = columns();
			for (int i = 0; i < linesToPrint.size(); i++) {
				int lineLength = linesToPrint.get(i).getText().length();

				String blanks = LanternaScreenTextArea.blankString(columns - lineLength);
				screen.putString(fromX, fromY + i, linesToPrint.get(i).getText() + blanks,
						linesToPrint.get(i).getFgColor(), linesToPrint.get(i).getBgColor());
			}
		}
	}

	/**
	 * Reads the input and handles it. The screen has to be refreshed
	 * afterwards.
	 * 
	 * @param key
	 *            the key to read
	 */
	public void readInput(Key key) {
		if (key.getKind() == Kind.Enter) {
			handleEnter();
		} else if (key.getKind() == Kind.ArrowUp) {
			handleUp();
			// Update
			update();
		} else if (key.getKind() == Kind.ArrowDown) {
			handleDown();
			// Update
			update();
		}
	}

	/**
	 * Handles if down arrow has been pressed.
	 */
	private void handleDown() {
		// Increase index, unless the last option is already chosen
		if (index != options.size() - 1) {
			index++;
			// Update
			update();
		}
	}

	/**
	 * Handles if up arrow has been pressed.
	 */
	private void handleUp() {
		// Decrease index, unless the first option is already chosen
		if (index != 0) {
			index--;
			// Update
			update();
		}
	}

	/**
	 * Handles if Enter has been pressed.
	 */
	private void handleEnter() {
		// Call the option handler
		optionHandler.chooseOption(index);
	}
}
