package playing;

import persistence.GameManager;
import persistence.PlayerManager;
import data.Game;
import data.Player;

public class GamePlayer {

	public static void start() {
		Game game = GameManager.getGame();
		Player player = PlayerManager.getPlayer();
		
		InputOutput.println(game.getStartText());
		// FIXME
		InputOutput.println(game.getStartLocation().getDescription());
		
		InputOutput.startListeningForInput();
	}
}