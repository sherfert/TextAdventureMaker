package lanterna;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
		 */
		void handleText(String text);
	}

	/**
	 * A triplet of text bgColor and fgColor.
	 * 
	 * @author Satia Herfert
	 */
	private class ColoredText {
		private String text;
		private Color bgColor;
		private Color fgColor;

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
	 * The linesToPrint, which are printed.
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
	 * The background color for the text
	 */
	private final Color textBgColor;

	/**
	 * The foreground color for the text
	 */
	private final Color textFgColor;

	/**
	 * The background color for the input
	 */
	private final Color inputBgColor;

	/**
	 * The foreground color for the input
	 */
	private final Color inputFgColor;

	/**
	 * The x starting coordinate, included.
	 */
	private int fromX;

	/**
	 * The x starting ending coordinate, excluded.
	 */
	private int toX;

	/**
	 * The y starting coordinate, included.
	 */
	private int fromY;

	/**
	 * The y starting ending coordinate, excluded.
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
	 * Points to the line to next print to in linesToPrint.
	 */
	private int printPointer;

	/**
	 * Construct a new TextArea, that uses the full screen, starts printing on
	 * top, and includes no input line. Default colors.
	 * 
	 * @param screen
	 *            the screen to use.
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
	 * @param printTop
	 * @param includeInputLine
	 * @param textBgColor
	 * @param textFgColor
	 * @param inputBgColor
	 * @param inputFgColor
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
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
		this.lines = new LinkedList<ColoredText>();
		this.currentText = new StringBuilder();

		// Set printPointer
		if (this.printTop) {
			printPointer = 0;
		} else {
			printPointer = toY - fromY - 1;
		}

		// Clear
		clear();
	}

	/**
	 * This method applies new dimensions. Behavior not 100 %-ly defined.
	 * 
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
	 */
	public void setNewDimensions(int fromX, int toX, int fromY, int toY) {
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;

		// Set printPointer
		if (!this.printTop) {
			printPointer = toY - fromY - 1;
		}

		// Stack new empty elements into list...
		for (int i = fromY + lines.size(); i < toY; i++) {
			lines.add(new ColoredText("", textBgColor, textFgColor));
		}
		// .. or remove.
		for (int i = toY - fromY; i < lines.size(); i++) {
			lines.remove(0);
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
		return (includeInputLine && printPointer == toY - fromY - 1)
				|| (!includeInputLine && printPointer == toY - fromY);
	}

	/**
	 * Clears the text area. The screen has to be refreshed afterwards.
	 */
	public void clear() {
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
		List<String> linesToPrint = new ArrayList<String>(Arrays.asList(text
				.split("\n")));
		// Split each after length is reached
		for (int i = 0; i < linesToPrint.size(); i++) {
			if (linesToPrint.get(i).length() > toX - fromX) {
				// Split after n chars
				String before = linesToPrint.get(i);
				linesToPrint.set(i, before.substring(0, toX - fromX));
				linesToPrint.add(i + 1, before.substring(toX - fromX));
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
			char[] charArray = new char[toX - fromX
					- lines.get(linesPointer).text.length()];
			Arrays.fill(charArray, ' ');
			String blanks = new String(charArray);

			screen.putString(fromX, screenPointer, lines.get(linesPointer).text
					+ blanks, lines.get(linesPointer).fgColor,
					lines.get(linesPointer).bgColor);
		}

		// When there's an input line, print it
		if (includeInputLine) {
			String toPrint;
			// print currentText (overrides)
			if (currentText.length() <= toX - fromX) {
				// fits into the line
				// Create blank string to fill the rest
				char[] charArray = new char[toX - fromX - currentText.length()];
				Arrays.fill(charArray, ' ');
				toPrint = currentText.toString() + new String(charArray);
			} else {
				// does not fit, cut beginning
				int diff = currentText.length() - (toX - fromX);
				toPrint = "..." + currentText.substring(3 + diff);
			}
			screen.putString(fromX, fromY + printPointer, toPrint,
					inputFgColor, inputBgColor);
			screen.setCursorPosition(fromX + currentText.length(),
					Math.min(fromY + printPointer, toY - 1));
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
	 */
	public void readInput(Key key) {
		if (!includeInputLine) {
			// we must ignore all input
			return;
		}

		if (key.getKind() == Kind.Enter) {
			handleEnter();
		} else if (key.getKind() == Kind.Backspace) {
			handleBackSpace();
		} else if (key.getKind() == Kind.NormalKey) {
			handleChar(key.getCharacter());
		}
		// Print
		print();
	}

	/**
	 * Appends the character the the currentText.
	 * 
	 * @param c
	 *            the character
	 */
	private void handleChar(char c) {
		currentText.append(c);
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
		// Reset current text
		currentText.setLength(0);
	}

	/**
	 * Handles if backspace has been pressed.
	 */
	private void handleBackSpace() {
		if (currentText.length() == 0) {
			return;
		}
		currentText.deleteCharAt(currentText.length() - 1);
	}
}
