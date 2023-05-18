// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;
import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;
import src.utility.PropertiesLoader;

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
    protected PacManGameGrid grid;
    protected PacActor pacActor;
    private Monster troll;
    private Monster tx5;

    private ArrayList<Monster> monsters;
    private ArrayList<Item> pills;
    private ArrayList<Item> goldPieces;
    private ArrayList<Item> iceCubes;
    private Properties properties;
    private final int SPEED_DOWN = 3;

    public GameEngine(String propertiesPath) {
        // Setup game engine
        super(nbHorzCells, nbVertCells, cellSize, isNavigation);
        this.properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        seed = Integer.parseInt(properties.getProperty("seed"));
        mode = properties.getProperty("version");
        grid = new PacManGameGrid(nbHorzCells, nbVertCells);
        setSimulationPeriod(SIMULATION_PERIOD);
        setTitle(TITLE);

        // Set up game actors
        game = new Game(mode, nbHorzCells, nbVertCells);
        pills = new ArrayList<>();
        goldPieces = new ArrayList<>();
        iceCubes = new ArrayList<>();
        setupPillAndItemsLocations();
        game.addItems(pills, goldPieces, iceCubes);
        pacActor = new PacActor(game);
        setupPacActorAttributes();
        monsters = new ArrayList<>();
        troll = new Troll(game);
        tx5 = new TX5(game);
        // Set up monster
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
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
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

    private void setupActorLocations() {
        String[] pacManLocations = this.properties.getProperty("PacMan.location").split(",");
        int pacManX = Integer.parseInt(pacManLocations[0]);
        int pacManY = Integer.parseInt(pacManLocations[1]);
        // loop the monsters ArrayList to add each monster
        for (Monster monster: monsters){
            String[] monsterLocations = this.properties.getProperty(monster.getType().name() + ".location").split(",");
            int monsterX = Integer.parseInt(monsterLocations[0]);
            int monsterY = Integer.parseInt(monsterLocations[1]);
            addActor(monster, new Location(monsterX, monsterY), Location.NORTH);
        }
        addActor(pacActor, new Location(pacManX, pacManY));
    }


    private void setupPillAndItemsLocations() {
        boolean noPropertyPills = false;
        boolean noPropertyGold = false;
        Item item;
        Location location;

        String pillsLocationString = properties.getProperty("Pills.location");
        if (pillsLocationString != null) {
            String[] singlePillLocationStrings = pillsLocationString.split(";");
            for (String singlePillLocationString: singlePillLocationStrings) {
                String[] locationStrings = singlePillLocationString.split(",");
                location = new Location(Integer.parseInt(locationStrings[0]),
                        Integer.parseInt(locationStrings[1]));
                item = new Item(location);
                pills.add(item);
            }
        }
        String goldLocationString = properties.getProperty("Gold.location");
        if (goldLocationString != null) {
            String[] singleGoldLocationStrings = goldLocationString.split(";");
            for (String singleGoldLocationString: singleGoldLocationStrings) {
                String[] locationStrings = singleGoldLocationString.split(",");
                location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
                item = new Item(ItemType.GOLD_PIECE.getImage(), location);
                goldPieces.add(item);
                addActor(item, location);
            }
        }
        // If no pills/gold pieces were read from property file, read presets from Grid instead
        if (pills.size() == 0){
            noPropertyPills = true;
        }
        if (goldPieces.size() == 0){
            noPropertyGold = true;
        }
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                location = new Location(x, y);
                int a = grid.getCell(location);
                if (a == 1 && noPropertyPills) {
                    pills.add(new Item(location));
                }
                if (a == 3 && noPropertyGold) {
                    item = new Item(ItemType.GOLD_PIECE.getImage(), location);
                    goldPieces.add(item);
                    addActor(item, location);
                }
                // Ice is only contained from preset Grid
                if (a == 4) {
                    item = new Item(ItemType.ICE_CUBE.getImage(), location);
                    iceCubes.add(item);
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
                cellValue = grid.getCell(location);
                if (cellValue > 0) {
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
