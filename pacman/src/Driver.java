package src;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test4.properties";
    public static final String DEFAULT_GAME_PATH = "pacman/GameFolder";


    /**
     * Starting point
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String gameMapPath = DEFAULT_GAME_PATH;
        if (args.length > 0) {
            gameMapPath = args[0];
        }
        new GameEngine(DEFAULT_PROPERTIES_PATH, gameMapPath);
    }
}
