package src;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test5.properties";

    /**
     * Starting point
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String gameMapPath = "";
        if (args.length > 0) {
            gameMapPath = args[0];
        }
        Controller controller = new Controller(gameMapPath);
        new GameEngine(DEFAULT_PROPERTIES_PATH, controller);
    }
}
