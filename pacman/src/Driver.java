package src;

import matachi.mapeditor.editor.Controller;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;
import java.util.Properties;

public class Driver {
    public static final String GMAP_PATH = "pacman/TestFolder";
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test4.properties";


    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        // TODO: added game path to load maps, added controller

        String gmapPath = GMAP_PATH;
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        Controller controller = new Controller();
        if (args.length > 0) {
            gmapPath = args[0];
        }
        new GameEngine(propertiesPath, gmapPath, controller);
    }
}
