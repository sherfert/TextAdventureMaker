package playing;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import configuration.PropertiesReader;
import lanterna.LanternaScreenOptionChooser;
import lanterna.LanternaScreenOptionChooser.OptionHandler;
import lanterna.LanternaScreenTextArea;
import lanterna.LanternaScreenTextArea.TextHandler;
import playing.menu.MenuShower;
import utility.WindowUtil;

/**
 * Providing means to print and read input while playing.
 * 
 * @author Satia
 */
public class InputOutput implements TextHandler, OptionHandler {

	/**
	 * The class using this must provide these methods.
	 * 
	 * @author Satia
	 */
	public interface GeneralIOManager {

		/**
		 * Handles the players input.
		 * 
		 * @param text
		 *            the typed text
		 * @return {@code false} if exit (or similar) was typed, {@code true}
		 *         otherwise.
		 */
		boolean handleText(String text);

		/**
		 * Stops the io manager.
		 */
		void stop();

		/**
		 * Notifies the general io manager, that the state of the game might
		 * have changed.
		 */
		void updateState();
	}

	/**
	 * The class using this in option mode must provide these methods.
	 * 
	 * @author Satia
	 */
	public interface OptionIOManager {
		/**
		 * Choose the option with the given index.
		 * 
		 * @param index
		 *            the index of the option to choose
		 */
		void chooseOption(int index);

		/**
		 * @return the number of options to display in option mode.
		 */
		int getNumberOfOptionLines();
	}

	/**
	 * The io manager for this session.
	 */
	private final GeneralIOManager ioManager;

	/**
	 * The screen that is being used to display to game.
	 */
	private Screen screen;

	/**
	 * The default text area, covering the whole screen.
	 */
	private LanternaScreenTextArea defaultTextArea;

	/**
	 * The option io manager if currently in option mode, {@code null}
	 * otherwise.
	 */
	private OptionIOManager optionIOManager;

	/**
	 * The menu shower.
	 */
	private MenuShower ms;

	/**
	 * The option choser if currently in option mode, {@code null} otherwise.
	 */
	private LanternaScreenOptionChooser optionChoser;

	/**
	 * The interrupted status of the input reader Thread is reset somehow on
	 * Linux after calling interrupt on it. To overcome this problem, this
	 * variable was introduced.
	 */
	private volatile boolean inputReaderRunning;

	/**
	 * @param ioManager
	 *            the ioManager for this session
	 * @param ms
	 *            menu shower
	 */
	public InputOutput(GeneralIOManager ioManager, MenuShower ms) {
		this.ioManager = ioManager;
		this.ms = ms;

		Terminal t;
		if (GraphicsEnvironment.isHeadless()) {
			t = TerminalFacade.createTextTerminal();
		} else {
			// XXX If this size is bigger than the actual screen, lanterna
			// behaves undeterministic and probably it won't work. In that case,
			// change the size value in the properties file for your system.
			int cols = Integer.parseInt(PropertiesReader.getProperty(PropertiesReader.SWING_TERMINAL_COLS_PROPERTY));
			int rows = Integer.parseInt(PropertiesReader.getProperty(PropertiesReader.SWING_TERMINAL_ROWS_PROPERTY));
			t = TerminalFacade.createSwingTerminal(cols, rows);
		}

		this.screen = TerminalFacade.createScreen(t);
		this.screen.startScreen();

		// Then create the default text area
		defaultTextArea = new LanternaScreenTextArea(screen, true, true, Terminal.Color.DEFAULT, Terminal.Color.DEFAULT,
				Terminal.Color.DEFAULT, Terminal.Color.CYAN, 0, screen.getTerminalSize().getColumns(), 0,
				screen.getTerminalSize().getRows(), InputOutput.this);


		// Modify  the JFrame
		if (t instanceof SwingTerminal) {
			modifyJFrame(((SwingTerminal) t).getJFrame());
		} 

		// Start the thread reading input
		startInputReadingThread();

	}

	/**
	 * If the screen is a JFrame, modifications to it will be executed here.
	 */
	private void modifyJFrame(final JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				ioManager.stop();
			}
		});

		// Not resizable
		frame.setResizable(false);

		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(WindowUtil.getWindowIconURL()));
		frame.setTitle("Text-Adventure");
	}

	/**
	 * Starts a thread that reads the input in an infinite loop.
	 * 
	 * XXX is this possible without busy waiting?
	 */
	private void startInputReadingThread() {
		this.inputReaderRunning = true;

		Thread t = new Thread(() -> {
			while (true) {
				// Check for interruptions
				if (!this.inputReaderRunning) {
					break;
				}

				Key key = screen.readInput();
				if (key == null) {
					continue;
				}
				readInput(key);
			}
		});
		t.start();
	}

	/**
	 * Reads a key and forwards them to the readInput methods of the lanterna
	 * classes. This will also refresh the screen.
	 * 
	 * If ESC is typed (outside option mode) the menu will be shown.
	 * 
	 * @param key
	 *            the typed key.
	 */
	private void readInput(Key key) {
		if (optionIOManager == null) {
			if (key.getKind() == Kind.Escape) {
				// If this is escape key: show menu
				ms.showMenu(true);
			} else {
				// If not in option mode, the default text area handles the
				// key,
				defaultTextArea.readInput(key);
			}
		} else {
			// otherwise the optionChoser
			optionChoser.readInput(key);
		}

		screen.refresh();
	}

	/**
	 * Exits the IO.
	 */
	public void exitIO() {
		this.screen.stopScreen();
		this.inputReaderRunning = false;
	}

	/**
	 * Prints a line of text for the player. Default colors.
	 * 
	 * @param output
	 *            the text to be printed
	 */
	public void println(String output) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Printing \"{0}\"", output);

		this.defaultTextArea.println(output);
		this.screen.refresh();
	}

	/**
	 * Prints a line of text for the player. Given colors.
	 * 
	 * @param output
	 *            the text to be printed
	 * @param bgColor
	 *            the background color
	 * @param fgColor
	 *            the foreground color
	 */
	public void println(String output, Color bgColor, Color fgColor) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST, "Printing \"{0}\"", output);

		defaultTextArea.println(output, bgColor, fgColor);
		this.screen.refresh();
	}

	/**
	 * Clears the text area. (Not the options!)
	 */
	public void clear() {
		defaultTextArea.clear();
	}

	/**
	 * Enters option mode. Now {@link #setOptions(List)} may be called.
	 * 
	 * @param optionIOManager
	 *            the optionIOManager
	 * @param options
	 *            the first options to display.
	 */
	public void enterOptionMode(OptionIOManager optionIOManager, List<String> options) {
		this.optionIOManager = optionIOManager;

		int cols = screen.getTerminalSize().getColumns();
		int rows = screen.getTerminalSize().getRows();

		// The options should never cover more than half the screen!
		int numLines = Math.min(optionIOManager.getNumberOfOptionLines(), rows / 2);

		// Resize text area by cutting the last lines
		this.defaultTextArea.setNewDimensions(0, screen.getTerminalSize().getColumns(), 0,
				screen.getTerminalSize().getRows() - numLines);
		// Use the last lines to display options
		this.optionChoser = new LanternaScreenOptionChooser(screen, options, this, 0, cols, rows - numLines, rows);

		// Refresh
		this.screen.refresh();
	}

	/**
	 * Exits option mode. Now {@link #setOptions(List)} must not be called any
	 * more.
	 */
	public void exitOptionMode() {
		this.optionIOManager = null;
		this.optionChoser = null;
		// Resize text area by giving back the last lines
		this.defaultTextArea.setNewDimensions(0, screen.getTerminalSize().getColumns(), 0,
				screen.getTerminalSize().getRows());
		// Refresh
		this.screen.refresh();
	}

	/**
	 * Sets the options to display. Must only be called in option mode.
	 * 
	 * @param options
	 *            the options to display
	 */
	public void setOptions(List<String> options) {
		optionChoser.setOptions(options);
	}

	@Override
	public void chooseOption(int index) {
		optionIOManager.chooseOption(index);
		ioManager.updateState();
	}

	@Override
	public void handleText(String text) {
		if (ioManager.handleText(text)) {
			ioManager.updateState();
		}
	}
}
