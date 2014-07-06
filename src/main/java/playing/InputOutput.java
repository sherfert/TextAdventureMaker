package playing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import playing.menu.LoadSaveManager;
import lanterna.LanternaScreenOptionChooser;
import lanterna.LanternaScreenOptionChooser.OptionHandler;
import lanterna.LanternaScreenTextArea;
import lanterna.LanternaScreenTextArea.TextHandler;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.Terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Providing means to print and read input while playing.
 * 
 * @author Satia
 */
public class InputOutput implements TextHandler, OptionHandler, ResizeListener {

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
		 */
		void handleText(String text);

		/**
		 * Stops the io manager.
		 */
		void stop();
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
	 * The io manager for this session
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
	 * The option choser if currently in option mode, {@code null} otherwise.
	 */
	private LanternaScreenOptionChooser optionChoser;

	/**
	 * Signalizing whether the initialization is finished.
	 */
	private volatile boolean isFinished;

	/**
	 * @param ioManager
	 *            the ioManager for this session
	 */
	public InputOutput(GeneralIOManager ioManager) {
		this.ioManager = ioManager;
		this.screen = TerminalFacade.createScreen();
		this.screen.startScreen();

		// Add us as resize listener
		screen.getTerminal().addResizeListener(this);

		Terminal t = this.screen.getTerminal();
		// Modify (which includes resize) the JFrame
		if (t instanceof SwingTerminal) {
			modifyJFrame(((SwingTerminal) t).getJFrame());
		} else {
			// If we're not waiting for a resize, call onResized
			// manually to finish initialization.
			onResized(screen.getTerminalSize());
		}

		// Wait for the resize to be finished
		while (!isFinished) {
		}

		// Start the thread reading input
		startInputReadingThread();
	}

	/**
	 * Called when the Terminal is resized. {@code synchronized} modifier added.
	 * This will finish the initialization.
	 */
	@Override
	public synchronized void onResized(TerminalSize newSize) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINE,
				"Resized, new size: {0} vs old size {1}",
				new Object[] { newSize, screen.getTerminalSize() });
		// Refresh to adapt to new size
		screen.refresh();

		if (isFinished) {
			// Resize rather than create the defaultTextArea
			defaultTextArea.setNewDimensions(0, screen.getTerminalSize()
					.getColumns(), 0, screen.getTerminalSize().getRows());
		} else {
			if (newSize.getColumns() == 101 && newSize.getRows() == 30) {
				// XXX This is the default size, and we ignore this resize.
				return;
			}

			// Then create the default text area
			defaultTextArea = new LanternaScreenTextArea(screen, true, true,
					Terminal.Color.DEFAULT, Terminal.Color.DEFAULT,
					Terminal.Color.DEFAULT, Terminal.Color.CYAN, 0, screen
							.getTerminalSize().getColumns(), 0, screen
							.getTerminalSize().getRows(), InputOutput.this);

			// Tell the constructor we are finished.
			isFinished = true;
		}
	}

	/**
	 * If the screen is a JFrame, modifications to it will be executed here
	 */
	private void modifyJFrame(final JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				ioManager.stop();
			}
		});
		Dimension fullScreen = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		// Let the screen cover 80 % of the real screen in each dimension
		frame.setPreferredSize(new Dimension((int) (fullScreen.width * 0.8),
				(int) (fullScreen.height * 0.8)));
		// Not resizable
		frame.setResizable(false);
	}

	/**
	 * Starts a thread that reads the input in an infinite loop.
	 */
	private void startInputReadingThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Key key = screen.readInput();
					if (key == null) {
						continue;
					}
					readInput(key);
				}
			}
		}).start();
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
			if(key.getKind() == Kind.Escape) {
				// If this is escape key: show menu
				LoadSaveManager.showMenu();
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
		screen.stopScreen();
	}

	/**
	 * Prints a line of text for the player. Default colors.
	 * 
	 * @param output
	 *            the text to be printed
	 */
	public void println(String output) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
				"Printing \"{0}\"", output);

		defaultTextArea.println(output);
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
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
				"Printing \"{0}\"", output);

		defaultTextArea.println(output, bgColor, fgColor);
		this.screen.refresh();
	}

	/**
	 * Enters option mode. Now {@link #setOptions(List)} may be called.
	 * 
	 * @param optionIOManager
	 *            the optionIOManager
	 * @param options
	 *            the first options to display.
	 */
	public void enterOptionMode(OptionIOManager optionIOManager,
			List<String> options) {
		this.optionIOManager = optionIOManager;

		int numLines = optionIOManager.getNumberOfOptionLines();

		// Resize text area by cutting the last lines
		this.defaultTextArea
				.setNewDimensions(0, screen.getTerminalSize().getColumns(), 0,
						screen.getTerminalSize().getRows() - numLines);
		// Use the last lines to display options
		this.optionChoser = new LanternaScreenOptionChooser(screen, options,
				this, 0, screen.getTerminalSize().getColumns(), screen
						.getTerminalSize().getRows() - numLines, screen
						.getTerminalSize().getRows());

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
		this.defaultTextArea.setNewDimensions(0, screen.getTerminalSize()
				.getColumns(), 0, screen.getTerminalSize().getRows());
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
	}

	@Override
	public void handleText(String text) {
		ioManager.handleText(text);
	}
}
