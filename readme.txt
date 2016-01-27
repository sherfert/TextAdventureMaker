persistence.Main:
	Creates a test game database: ~/.textAdventureMaker/Test-Adventure.h2.db

mvn package:
	Creates a file called "Game_missing_db.jar", which still needs to be augmented with a game database to be executable.
	Also creates a TextAdventureMaker-<version>.jar executable JAR file with MainWindow as main class.
	
playing.menu.LoadSaveManager:
	Actually starts playing a game.
	If run in eclipse:
		Needs as argument the name of the game you want to play, e.g. "Test-Adventure", and a corresponding file must
		exist in ~/.textAdventureMaker/
	If run from a JAR file:
		Takes the game.db that is present in the JAR as the game.

gui.MainWindow:
	Copies the game database (hardcoded) into the file "Game_missing_db.jar" created by maven and creates a "Game.jar"
	that can be executed with the LoadSaveManager as main class.