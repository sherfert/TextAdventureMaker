package playing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import lanterna.LanternaScreenTextArea;
import lanterna.LanternaScreenTextArea.TextHandler;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.Terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Providing means to print and read input while playing.
 *
 * @author Satia
 */
public class InputOutput implements TextHandler {

	/**
	 * The GamePlayer for this session
	 */
	private final GamePlayer gamePlayer;

	/**
	 * The screen that is being used to display to game.
	 */
	private Screen screen;

	/**
	 * The default text area, covering the whole screen.
	 */
	private LanternaScreenTextArea defaultTextArea;

	/**
	 * @param gamePlayer the GamePlayer for this session
	 */
	public InputOutput(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
		this.screen = TerminalFacade.createScreen();
		this.screen.startScreen();
		Terminal t = this.screen.getTerminal();
		if (t instanceof SwingTerminal) {
			modifyJFrame(((SwingTerminal) t).getJFrame());
		}

		// Rest of the initialization
		defaultTextArea = new LanternaScreenTextArea(screen, true, true,
			Terminal.Color.DEFAULT, Terminal.Color.DEFAULT,
			Terminal.Color.DEFAULT, Terminal.Color.CYAN, 0, screen
			.getTerminalSize().getColumns(), 0, screen
			.getTerminalSize().getRows(), InputOutput.this);

		// TODO the key listener loop must be implemented here as soon
		// as conversations are supported
		defaultTextArea.startInputReadingThread();

		screen.getTerminal().addResizeListener(new ResizeListener() {
			@Override
			public void onResized(TerminalSize newSize) {
				defaultTextArea.setNewDimensions(0, newSize.getColumns(), 0,
					newSize.getRows());
				screen.refresh();
			}
		});

	}

	/**
	 * If the screen is a JFrame, modifications to it will be executed here
	 */
	private void modifyJFrame(final JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				gamePlayer.stop();
			}
		});
		Dimension fullScreen = java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize();
		// Let the screen cover 80 % of the real screen in each dimension
		frame.setPreferredSize(new Dimension((int) (fullScreen.width * 0.8),
			(int) (fullScreen.height * 0.8)));
		// Not resizable
		frame.setResizable(false);

		frame.revalidate();
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
	 * @param output the text to be printed
	 */
	public void println(String output) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
			"Printing '{0}'", output);

		defaultTextArea.println(output);
		this.screen.refresh();
	}

	/**
	 * Prints a line of text for the player. Given colors.
	 *
	 * @param output the text to be printed
	 * @param bgColor the background color
	 * @param fgColor the foreground color
	 */
	public void println(String output, Color bgColor, Color fgColor) {
		Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
			"Printing '{0}'", output);

		defaultTextArea.println(output, bgColor, fgColor);
		this.screen.refresh();
	}

	@Override
	public void handleText(String text) {
		if (!gamePlayer.getParser().parse(text)) {
			gamePlayer.stop();
		}
	}
}
