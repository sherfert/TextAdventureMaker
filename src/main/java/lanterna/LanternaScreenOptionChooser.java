package lanterna;

import java.util.ArrayList;
import java.util.List;

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
	public LanternaScreenOptionChooser(Screen screen, List<String> options,
			OptionHandler optionHandler) {
		this(screen, options, optionHandler, 0, screen.getTerminalSize()
				.getColumns(), 0, screen.getTerminalSize().getRows());
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
	public LanternaScreenOptionChooser(Screen screen, List<String> options,
			OptionHandler optionHandler, int fromX, int toX, int fromY, int toY) {
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

			while (remainingText.length() > 0) {
				if (remainingText.length() > columns()) {
					// Split after n chars or the blank before that
					int splitIndex = remainingText.toString().lastIndexOf(' ',
							columns()) + 1;
					// Set to the end, if there is no space
					if (splitIndex == -1) {
						splitIndex = columns();
					}

					splitLine.add(remainingText.substring(0, splitIndex));
					// Remove this part from remaining text
					remainingText = remainingText.substring(splitIndex).trim();
					// Indentation
					remainingText = " " + remainingText;
				} else {
					splitLine.add(remainingText.toString());
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
					String blanks = LanternaScreenTextArea
							.blankString(columns() - text.length());

					if (i == index) {
						// Use black on white to mark the selected option
						screen.putString(fromX, fromY + lineIndex, text
								+ blanks, Color.BLACK, Color.WHITE);
					} else {
						// Use default colors for unselected option
						screen.putString(fromX, fromY + lineIndex, text
								+ blanks, Color.DEFAULT, Color.DEFAULT);
					}
				}
			}
			// Fill the remaining lines with whitespace
			for (int i = lineIndex; i < rows(); i++) {
				// Create blank string to fill the rest
				String blanks = LanternaScreenTextArea.blankString(columns());

				screen.putString(fromX, fromY + i, blanks, Color.DEFAULT,
						Color.DEFAULT);
			}

		} else {
			// TODO
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
