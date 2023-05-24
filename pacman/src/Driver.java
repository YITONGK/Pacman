package src;

public class Driver {
    public static final String DEFAULT_GAME_MAP_PATH = "pacman/TestFolder";
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test4.properties";


    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        // TODO: added game path to load maps, added controller

        String gameMapPath = DEFAULT_GAME_MAP_PATH;
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            gameMapPath = args[0];
        }
        Controller controller = new Controller(gameMapPath);
        new GameEngine(propertiesPath, gameMapPath, controller);
    }
}
