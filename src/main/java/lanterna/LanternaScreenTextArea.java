package lanterna;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * This class provides a wrapper, if you want to simply write text to a certain
 * area of the screen. This class will only update the screen, if it handles the
 * input itself.
 * 
 * @author Satia Herfert
 */
public class LanternaScreenTextArea {
	/**
	 * Used to callback if a new line has been typed, when the area includes an
	 * input line.
	 * 
	 * @author Satia Herfert
	 */
	public interface TextHandler {
		/**
		 * Handles one line of text.
		 * 
		 * @param text
		 *            the text to handle
		 */
		void handleText(String text);
	}

	/**
	 * A triplet of text bgColor and fgColor.
	 * 
	 * @author Satia Herfert
	 */
	public static class ColoredText {
		private final String text;
		private final Color bgColor;
		private final Color fgColor;

		/**
		 * @param text
		 * @param bgColor
		 * @param fgColor
		 */
		public ColoredText(String text, Color bgColor, Color fgColor) {
			this.text = text;
			this.bgColor = bgColor;
			this.fgColor = fgColor;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return the bgColor
		 */
		public Color getBgColor() {
			return bgColor;
		}

		/**
		 * @return the fgColor
		 */
		public Color getFgColor() {
			return fgColor;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * The screen to use.
	 */
	private final Screen screen;

	/**
	 * The lines, which are printed.
	 */
	private final List<ColoredText> lines;

	/**
	 * If {@code true}, prints from top to bottom, if {@code false}, prints from
	 * bottom to top.
	 */
	private final boolean printTop;

	/**
	 * If {@code true}, the last line will be used as input. The readInput
	 * method has to be called then.
	 */
	private final boolean includeInputLine;

	/**
	 * The background color for the text.
	 */
	private final Color textBgColor;

	/**
	 * The foreground color for the text.
	 */
	private final Color textFgColor;

	/**
	 * The background color for the input.
	 */
	private final Color inputBgColor;

	/**
	 * The foreground color for the input.
	 */
	private final Color inputFgColor;

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
	 * A callback implementation to handle input. Can be {@code null}.
	 */
	private final TextHandler textHandler;

	/**
	 * The text currently being entered, not sent yet.
	 */
	private final StringBuilder currentText;

	/**
	 * Save the last used commands to enable terminal-like up/down-arrow
	 * functionality.
	 */
	private final Stack<String> lastCommands;

	/**
	 * The stackPointer for the above stack. Points at an invalid location at
	 * first.
	 */
	private int stackPointer = -1;

	/**
	 * Points to the line to print next in linesToPrint.
	 */
	private int printPointer;

	/**
	 * The current-text based index of the cursor. This is not identically to
	 * the on-screen cursor position, as the text can be longer than the screen.
	 */
	private int currentTextCursorIndex;

	/**
	 * Construct a new TextArea, that uses the full screen, starts printing on
	 * top, and includes no input line. Default colors.
	 * 
	 * @param screen
	 *            the screen to use
	 */
	public LanternaScreenTextArea(Screen screen) {
		this(screen, true, false, Color.DEFAULT, Color.DEFAULT, Color.DEFAULT,
				Color.DEFAULT, 0, screen.getTerminalSize().getColumns(), 0,
				screen.getTerminalSize().getRows(), null);
	}

	/**
	 * Constructs a full configurable TextArea.
	 * 
	 * @param screen
	 *            the screen to use
	 * @param printTop
	 *            if {@code true}, prints from top to bottom, if {@code false},
	 *            prints from bottom to top
	 * @param includeInputLine
	 *            if {@code true}, the last line will be used as input. The
	 *            readInput method has to be called then
	 * @param textBgColor
	 *            The background color for the text
	 * @param textFgColor
	 *            The foreground color for the text
	 * @param inputBgColor
	 *            The background color for the input
	 * @param inputFgColor
	 *            The foreground color for the input
	 * @param fromX
	 *            the x starting coordinate, included
	 * @param toX
	 *            the x ending coordinate, excluded
	 * @param fromY
	 *            the y starting coordinate, included
	 * @param toY
	 *            the y ending coordinate, excluded
	 * @param textHandler
	 *            the text handler or {@code null}, if the text can be ignored
	 */
	public LanternaScreenTextArea(Screen screen, boolean printTop,
			boolean includeInputLine, Color textBgColor, Color textFgColor,
			Color inputBgColor, Color inputFgColor, int fromX, int toX,
			int fromY, int toY, TextHandler textHandler) {
		this.screen = screen;
		this.printTop = printTop;
		this.includeInputLine = includeInputLine;
		this.textBgColor = textBgColor;
		this.textFgColor = textFgColor;
		this.inputBgColor = inputBgColor;
		this.inputFgColor = inputFgColor;
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;
		this.textHandler = textHandler;
		this.lines = new LinkedList<>();
		this.currentText = new StringBuilder();
		this.lastCommands = new Stack<>();

		// Clear
		clear();
	}

	/**
	 * This method applies new dimensions. Behavior not 100 %-ly defined.
	 * 
	 * @param fromX
	 *            the x starting coordinate, included
	 * @param toX
	 *            the x ending coordinate, excluded
	 * @param fromY
	 *            the y starting coordinate, included
	 * @param toY
	 *            the y ending coordinate, excluded
	 */
	public void setNewDimensions(int fromX, int toX, int fromY, int toY) {
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;

		// Set printPointer
		if (!this.printTop || areaFull()) {
			printPointer = rows() - 1;
		}

		// Stack new empty elements into list...
		for (int i = fromY + lines.size(); i < toY; i++) {
			lines.add(new ColoredText("", textBgColor, textFgColor));
		}
		// .. or remove.
		for (int i = rows(); i < lines.size();) {
			if (printTop) {
				// If the last lines are empty, remove these, otherwise remove
				// the first line, but always keep the last line (input line) empty
				if (lines.get(lines.size() - 2).text.isEmpty()) {
					lines.remove(lines.size() - 2);
				} else {
					lines.remove(0);
				}
			} else {
				// When printing from bottom to top, just remove the first lines
				lines.remove(0);
			}
		}

		// Print
		print();
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
	 * @return whether the printPointer points to a line that cannot be printed
	 *         to, that means, the area is full.
	 */
	public boolean areaFull() {
		return (includeInputLine && printPointer >= rows() - 1)
				|| (!includeInputLine && printPointer >= rows());
	}

	/**
	 * Clears the text area. The screen has to be refreshed afterwards. This
	 * method is final, as called in the constructor.
	 */
	public final void clear() {
		// Set printPointer
		if (this.printTop) {
			printPointer = 0;
		} else {
			printPointer = rows() - 1;
		}

		// clear lines
		lines.clear();
		// Fill lines with empty strings
		for (int i = fromY; i < toY; i++) {
			lines.add(new ColoredText("", textBgColor, textFgColor));
		}
		// clear currentText
		currentText.setLength(0);
		// print
		print();
	}

	/**
	 * Print text to the TextArea. The screen has to be refreshed afterwards.
	 * Text is automatically wrapped into lines. Given colors are used.
	 * 
	 * @param text
	 *            the text to print
	 * @param bgColor
	 * @param fgColor
	 */
	public void println(String text, Color bgColor, Color fgColor) {
		List<String> linesToPrint = new ArrayList<>(Arrays.asList(text
				.split("\n")));
		// Split each after length is reached
		for (int i = 0; i < linesToPrint.size(); i++) {
			if (linesToPrint.get(i).length() > columns()) {
				// Split after n chars or the blank before that
				String before = linesToPrint.get(i);
				int splitIndex = before.lastIndexOf(' ', columns() - 1) + 1;
				// Set to the end, if there is no space
				if (splitIndex == -1) {
					splitIndex = columns();
				}

				linesToPrint.set(i, before.substring(0, splitIndex));
				// if the next line starts with space, trim that
				linesToPrint.add(i + 1, before.substring(splitIndex).trim());
			}
		}
		// Include each Text line
		for (String line : linesToPrint) {
			includeTextLine(line, bgColor, fgColor);
		}
	}

	/**
	 * Print text to the TextArea. The screen has to be refreshed afterwards.
	 * Text is automatically wrapped into lines. Default colors are used.
	 * 
	 * @param text
	 *            the text to print
	 */
	public void println(String text) {
		println(text, textBgColor, textFgColor);
	}

	/**
	 * This method includes a line of text, which has no line breaks and is
	 * guaranteed to fit in one line.
	 * 
	 * @param line
	 *            the line
	 */
	private void includeTextLine(String line, Color bgColor, Color fgColor) {
		// If the area is full, we have to shift input up
		if (areaFull()) {
			shiftUp();
		}

		// Append the line at the correct position in lines
		lines.set(printPointer, new ColoredText(line, bgColor, fgColor));
		// Increment the printpointer
		printPointer++;

		// Now we have to print the content to the screen
		print();
	}

	/**
	 * Shifts all lines up, decrements the printPointer.
	 */
	private void shiftUp() {
		lines.remove(0);
		lines.add(new ColoredText("", textBgColor, textFgColor));
		printPointer--;
	}

	/**
	 * Prints the current lines content to the screen. When called from outside,
	 * this can be interpreted as a refresh.
	 */
	public void print() {
		for (int linesPointer = 0, screenPointer = fromY; linesPointer < lines
				.size(); linesPointer++, screenPointer++) {
			// Create blank string to fill the rest
			String blanks = blankString(columns()
					- lines.get(linesPointer).text.length());

			screen.putString(fromX, screenPointer, lines.get(linesPointer).text
					+ blanks, lines.get(linesPointer).fgColor,
					lines.get(linesPointer).bgColor);
		}

		// When there's an input line, print it
		if (includeInputLine) {
			String toPrint;
			// print currentText (overrides)
			if (currentText.length() < columns()) {
				// text plus cursor fit into the line
				// Create blank string to fill the rest
				toPrint = currentText.toString()
						+ blankString(columns() - currentText.length());
				screen.setCursorPosition(fromX + currentTextCursorIndex,
						Math.min(fromY + printPointer, toY - 1));
			} else {
				// does not fit
				int startIndex = Math.max(0, currentTextCursorIndex
						- (columns()) + 1);
				int endindex = Math.min(
						currentText.length(),
						startIndex
								+ toX
								- fromX
								+ (currentTextCursorIndex == currentText
										.length() ? 0 : 1));
				toPrint = currentText.substring(startIndex, endindex);

				screen.setCursorPosition(fromX + currentTextCursorIndex
						- startIndex, Math.min(fromY + printPointer, toY - 1));

			}
			screen.putString(fromX, fromY + printPointer, toPrint,
					inputFgColor, inputBgColor);
		}
	}

	/**
	 * Starts a thread that reads the input in an infinite loop. This thread
	 * will ALSO refresh the screen, so if you don't want that, start your own
	 * thread and use {@linkplain #readInput(Key)} directly.
	 */
	public void startInputReadingThread() {
		if (!includeInputLine) {
			// we must ignore that if we don't have an input line
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Key key = screen.readInput();
					if (key == null) {
						continue;
					}
					readInput(key);
					screen.refresh();
				}
			}
		}).start();
	}

	/**
	 * Reads the input and handles it. The screen has to be refreshed
	 * afterwards.
	 * 
	 * @param key
	 *            the key to read
	 */
	public void readInput(Key key) {
		if (!includeInputLine) {
			// we must ignore all input
			return;
		}

		if (key.getKind() == Kind.NormalKey) {
			handleChar(key.getCharacter());
		} else if (key.getKind() == Kind.Enter) {
			handleEnter();
		} else if (key.getKind() == Kind.Backspace) {
			handleBackSpace();
		} else if (key.getKind() == Kind.Delete) {
			handleDel();
		} else if (key.getKind() == Kind.ArrowUp) {
			handleUp();
		} else if (key.getKind() == Kind.ArrowDown) {
			handleDown();
		} else if (key.getKind() == Kind.ArrowLeft) {
			handleLeft();
		} else if (key.getKind() == Kind.ArrowRight) {
			handleRight();
		} else if (key.getKind() == Kind.Home) {
			handleHome();
		} else if (key.getKind() == Kind.End) {
			handleEnd();
		} else {
			return;
		}

		// Print
		print();
	}

	/**
	 * Checks if a character is printable
	 * 
	 * @param c
	 *            the char
	 * @return if it is printable.
	 */
	private boolean isPrintableChar(char c) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
		return (!Character.isISOControl(c)) && c != KeyEvent.CHAR_UNDEFINED
				&& block != null && block != Character.UnicodeBlock.SPECIALS;
	}

	/**
	 * Appends the character the the currentText.
	 * 
	 * @param c
	 *            the character
	 */
	private void handleChar(char c) {
		// Exclude non printable chars
		if (!isPrintableChar(c)) {
			return;
		}

		if (currentTextCursorIndex == currentText.length()) {
			currentText.append(c);
		} else {
			currentText.insert(currentTextCursorIndex, c);
		}

		currentTextCursorIndex++;
	}

	/**
	 * Handles if Enter has been pressed.
	 */
	private void handleEnter() {
		if (currentText.length() == 0) {
			return;
		}
		// Include current text in area
		println(currentText.toString());
		// Call the callback
		if (textHandler != null) {
			textHandler.handleText(currentText.toString());
		}

		// Save last used command
		lastCommands.push(currentText.toString());
		// Reset stackPointer
		stackPointer = -1;

		// Reset current text
		currentText.setLength(0);
		// Place cursor at the beginning
		currentTextCursorIndex = 0;

	}

	/**
	 * Handles if backspace has been pressed.
	 */
	private void handleBackSpace() {
		if (currentTextCursorIndex == 0) {
			return;
		}
		currentText.deleteCharAt(currentTextCursorIndex - 1);
		currentTextCursorIndex--;
	}

	/**
	 * Handles if delete has been pressed.
	 */
	private void handleDel() {
		if (currentTextCursorIndex == currentText.length()) {
			return;
		}
		currentText.deleteCharAt(currentTextCursorIndex);
	}

	/**
	 * Handles if up arrow has been pressed.
	 */
	private void handleUp() {
		if (stackPointer != lastCommands.size() - 1) {
			stackPointer++;
		}
		if (stackPointer != -1) {
			currentText.setLength(0);
			currentText.append(lastCommands.get(lastCommands.size()
					- stackPointer - 1));
			// Place cursor at the end
			currentTextCursorIndex = currentText.length();
		}
	}

	/**
	 * Handles if down arrow has been pressed.
	 */
	private void handleDown() {
		if (stackPointer == -1) {
			return;
		} else if (stackPointer == 0) {
			stackPointer--;
			currentText.setLength(0);
			// Place cursor at the beginning
			currentTextCursorIndex = 0;
			return;
		}
		stackPointer--;
		currentText.setLength(0);
		currentText.append(lastCommands.get(lastCommands.size() - stackPointer
				- 1));
		// Place cursor at the end
		currentTextCursorIndex = currentText.length();
	}

	/**
	 * Handles if right arrow has been pressed.
	 */
	private void handleRight() {
		if (currentTextCursorIndex != currentText.length()) {
			currentTextCursorIndex++;
		}
	}

	/**
	 * Handles if left arrow has been pressed.
	 */
	private void handleLeft() {
		if (currentTextCursorIndex != 0) {
			currentTextCursorIndex--;
		}
	}

	/**
	 * Handles if home/pos1 has been pressed.
	 */
	private void handleHome() {
		// Place cursor at the beginning
		currentTextCursorIndex = 0;
	}

	/**
	 * Handles if end has been pressed.
	 */
	private void handleEnd() {
		// Place cursor at the end
		currentTextCursorIndex = currentText.length();
	}

	/**
	 * Generates a blank string of the given size.
	 * 
	 * @param size
	 * @return
	 */
	public static String blankString(int size) {
		// Create blank string to fill the rest
		char[] charArray = new char[size];
		Arrays.fill(charArray, ' ');
		return new String(charArray);
	}
}
