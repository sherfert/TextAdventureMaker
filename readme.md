# TextAdventureMaker

With TextAdventureMaker you can build your own text-based adventures. And all this without coding. An intuitive graphical interface will help you to create persons and items, put them into rooms and connect everything in your game.

## Setup

First you should clone the repository. TextAdventureMaker is built with maven. So all you need to do after downloading is running `mvn package`. Afterwards you will find a `TextAdventureMaker-version.jar` in the `target` folder. Double-click that to launch the program, or run `java -jar TextAdventureMaker-version.jar` (and adjust the actual version number, of course).

## Building your own game

Click `File->New Game` and start off from there. Currently there is no documentation on how to use TextAdventureMaker. This will follow. Important to know is that everything you change is saved automatically. You can always try your latest changes by clicking `Game->Play game`.

## Share your amazing game with your friends

Click `File->Export runnable game` to obtain a standalone executable JAR file, that you can send to your friends, or publish wherever you like.

## Contribute

TextAdventureMaker is under irregular development and any ideas and contributions are more than welcome. Clone the repository and fire up your favorite IDE. Eclipse project files are included. The following list tells you all the main entry points you need to know and their roles.

The folder where savegames, configuration files and logs end up is `$HOME/.textAdventureMaker`.

 - `persistence.Main`
 Creates a game that contains every single data class and can be used to play around without having to create a new game from scratch. In Unix it will be placed in : `~/.textAdventureMaker/Test-Adventure.mv.db`
 - `mvn package`
Creates a file called `Game_missing_db.jar`, which still needs to be augmented with a game database to be executable. This file must be present before exporting to a runnable JAR works.	Also creates a `TextAdventureMaker-<version>.jar` executable JAR file with `gui.MainWindow` as main class.
 - `playing.menu.LoadSaveManager`
	Actually starts playing a game.
	 - If run in eclipse or from the command line: Needs as argument the name of the game you want to play, e.g. "Test-Adventure", and a corresponding file must exist in `~/.textAdventureMaker`
	 - If run from a JAR file: Takes the game.db that is present in the JAR as the game.
 - `gui.MainWindow`
 Shows the main window of the application.