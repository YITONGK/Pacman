// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import matachi.mapeditor.editor.Controller;
import matachi.mapeditor.grid.Grid;
import matachi.mapeditor.grid.GridView;
import src.utility.GameCallback;
import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;
import src.utility.PropertiesLoader;

import javax.sound.sampled.Port;

/**
 * GameEngine reads the property file, initialises the game, and runs the game.
 */
public class GameEngine extends GameGrid {

    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;
    private final static int cellSize = 20;
    private final static int SIMULATION_PERIOD = 100;
    private final static int KEY_PERIOD = 150;
    private final static String TITLE = "[PacMan in the Torusverse]";
    private final static boolean isNavigation = false;
    private int seed = 30006;
    private String mode;

    private Game game;
    protected PacActor pacActor;
    private Monster troll;
    private Monster tx5;
    private Grid grid;
    private ArrayList<Monster> monsters;
    private ArrayList<Item> pills;
    private ArrayList<Item> goldPieces;
    private ArrayList<Item> iceCubes;
    // TODO: Added portal instances (can also instantiate in own class - I may change this code in the future)
    private PortalPair whitePortals;
    private PortalPair yellowPortals;
    private PortalPair darkGoldPortals;
    private PortalPair darkGrayPortals;

    private Properties properties;
    private final int SPEED_DOWN = 3;

    public GameEngine(String propertiesPath, String mapPath, Controller controller) {
        // Setup game engine
        super(nbHorzCells, nbVertCells, cellSize, isNavigation);
        this.properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        // TODO: changed grid from PacManGameGrid to Grid
        grid = controller.loadFile();
        seed = Integer.parseInt(properties.getProperty("seed"));
        mode = properties.getProperty("version");
        setSimulationPeriod(SIMULATION_PERIOD);
        setTitle(TITLE);

        // Set up game actors
        game = new Game(mode, nbHorzCells, nbVertCells);
        pills = new ArrayList<>();
        goldPieces = new ArrayList<>();
        iceCubes = new ArrayList<>();
        whitePortals = new PortalPair();
        yellowPortals = new PortalPair();
        darkGoldPortals = new PortalPair();
        darkGrayPortals = new PortalPair();
        setupPillAndItemsLocations();
        game.addItems(pills, goldPieces, iceCubes);
        pacActor = new PacActor(game);
        setupPacActorAttributes();
        monsters = new ArrayList<>();
        troll = new Troll(game);
        tx5 = new TX5(game);
        monsters.add(troll);
        monsters.add(tx5);
        setupMonsterAttributes();
        setupActorLocations();
        game.addPacMan(pacActor);
        game.addMonsters(monsters);

        // Run game
        GGBackground bg = getBg();
        drawGrid(bg);
        runGame(bg);
    }

    private void setupPacActorAttributes() {
        //Setup for auto test
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        //Setup Random seeds
        pacActor.setSeed(seed);
        addKeyRepeatListener(pacActor);
        setKeyRepeatPeriod(KEY_PERIOD);
        pacActor.setSlowDown(SPEED_DOWN);
    }

    private void setupMonsterAttributes() {
        // loop the monsters ArrayList to set seed and slowdown
        for(Monster monster: monsters){
            monster.setSeed(seed);
            monster.setSlowDown(SPEED_DOWN);
        }
    }

    // TODO: using map to set up actor locations instead of property file
    private void setupActorLocations() {
        Location location;
        Location pacLocation = null;
        Location trollLocation = null;
        Location tx5Location = null;

        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                location = new Location(x, y);
                char a = grid.getTile(x, y);
                if (a == 'f') {
                    pacLocation = location;
                }
                if (a == 'g') {
                    trollLocation = location;
                }
                if (a == 'h') {
                    tx5Location = location;
                }
            }
        }
        addActor(troll, trollLocation, Location.NORTH);
        addActor(tx5, tx5Location, Location.NORTH);
        addActor(pacActor, pacLocation);
    }


    // TODO: use map to set up pill and items locations instead of property file
    private void setupPillAndItemsLocations() {
        Item item;
        Location location;
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                location = new Location(x, y);
                char a = grid.getTile(x, y);
                if (a == 'c') {
                    pills.add(new Item(location));
                }
                if (a == 'd') {
                    item = new Item(ItemType.GOLD_PIECE.getImage(), location);
                    goldPieces.add(item);
                    addActor(item, location);
                }
                if (a == 'e') {
                    item = new Item(ItemType.ICE_CUBE.getImage(), location);
                    iceCubes.add(item);
                    addActor(item, location);
                }
                if (a == 'i'){
                    item = new Item(ItemType.WHITE_PORTAL.getImage(), location);
                    whitePortals.addPortal(item);
                    addActor(item, location);
                }
                if (a == 'j'){
                    item = new Item(ItemType.YELLOW_PORTAL.getImage(), location);
                    yellowPortals.addPortal(item);
                    addActor(item, location);
                }
                if (a == 'k'){
                    item = new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location);
                    darkGoldPortals.addPortal(item);
                    addActor(item, location);
                }
                if (a == 'l'){
                    item = new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location);
                    darkGrayPortals.addPortal(item);
                    addActor(item, location);
                }
            }
        }
    }

    // Check the status of the game and render relevant texts
    private void runGame(GGBackground bg) {
        doRun();
        show();
        do {
            delay(10);
        } while(!game.checkWin() && !game.checkLost());
        delay(120);

        Location loc = pacActor.getLocation();
        for(Monster monster: monsters){
            monster.setStopMoving(true);
        }
        pacActor.removeSelf();

        String title = "";
        if (game.isLost()) {
            bg.setPaintColor(Color.red);
            title = "GAME OVER";
            addActor(new Actor("sprites/explosion3.gif"), loc);
        } else if (game.isWin()) {
            bg.setPaintColor(Color.yellow);
            title = "YOU WIN";
        }
        setTitle(title);
        game.getGameCallback().endOfGame(title);
        doPause();
    }

    public void drawGrid(GGBackground bg) {
        Location location;
        int cellValue;
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                bg.setPaintColor(Color.white);
                location = new Location(x, y);
                cellValue = grid.getTile(x, y);
                // TODO: changed value
                if (cellValue != 'b') {
                    bg.fillCell(location, Color.lightGray);
                }
            }
        }
        for (Item item: pills) {
            bg.fillCircle(toPoint(item.getLocation()), 5);
        }
        for (Item item : goldPieces) {
            bg.setPaintColor(Color.yellow);
            bg.fillCircle(toPoint(item.getLocation()), 5);
        }
        for (Item item: iceCubes) {
            bg.setPaintColor(Color.blue);
            bg.fillCircle(toPoint(item.getLocation()), 5);
        }
    }
}
