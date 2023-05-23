package src;

import matachi.mapeditor.editor.Controller;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.Properties;

public class Driver {
    public static final String GMAP_PATH = "pacman/GameFolder";
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test4.properties";


    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        // TODO: added game path to load maps, added controller
        Controller controller = new Controller();
        String gmapPath = GMAP_PATH;
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            gmapPath = args[0];
        }
        new GameEngine(propertiesPath, gmapPath, controller);
    }
}
