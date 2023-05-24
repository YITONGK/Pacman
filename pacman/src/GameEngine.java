// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import matachi.mapeditor.editor.Controller;
import matachi.mapeditor.grid.Grid;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import src.utility.PropertiesLoader;

/**
 * GameEngine reads the property file, initialises the game, and runs the game.
 */
public class GameEngine extends GameGrid {

    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;
    private final static int cellSize = 32;
    private final static int SIMULATION_PERIOD = 100;
    private final static int KEY_PERIOD = 150;
    private final static String TITLE = "[PacMan in the Torusverse]";
    private final static boolean isNavigation = false;
    private String mapDir;
    private String currFile;
    private int seed = 30006;
    private boolean isAuto;
    private Game game;
    private Controller controller;
    private GGBackground background;
    private ArrayList<File> sortedFile;
    protected PacActor pacActor;
    private Grid grid;
    private final String[] monsterCharacters = {"g", "h"};
    private final String[] itemCharacters = {"c", "d", "e", "i", "j", "k", "l"};
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

    public GameEngine(String propertiesPath, String mapArg, Controller controller) {
        // Setup game engine
        super(nbHorzCells, nbVertCells, cellSize, isNavigation);
        this.properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        this.controller = controller;
        this.mapDir = mapArg;
        if (mapArg == null) {
            grid = controller.getModel();
        } else {
            grid = controller.loadFile(mapArg);
        }
        if (grid != null) {
            seed = Integer.parseInt(properties.getProperty("seed"));
            isAuto = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
            setSimulationPeriod(SIMULATION_PERIOD);
            setTitle(TITLE);
            // Set up game actors
            game = new Game(nbHorzCells, nbVertCells);
            setUpAll();
            // Run game
            GGBackground bg = getBg();
            this.background = bg;
            drawGrid();
            runGame();
        }
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
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char type = grid.getTile(x, y);
                if (type == 'f') {
                    addActor(pacActor, location);
                }
                if (Arrays.asList(monsterCharacters).contains(Character.toString(type))) {
                    Monster newMonster = MonsterFactory.getInstance().createMonster(type, game);
                    addActor(newMonster, location, Location.NORTH);
                    monsters.add(newMonster);
                }
            }
        }
    }


    // TODO: use map to set up pill and items locations instead of property file
    private void setupPillAndItemsLocations() {
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                Location location = new Location(x, y);
                char type = grid.getTile(x, y);
                if (Arrays.asList(itemCharacters).contains(Character.toString(type))) {
                    Item item = ItemFactory.getInstance().createItem(type, location);
                    addActor(item, location);
                    switch (type) {
                        case 'c': pills.add(item); break;
                        case 'd': goldPieces.add(item); break;
                        case 'e': iceCubes.add(item); break;
                        case 'i': whitePortals.addPortal(item); break;
                        case 'j': yellowPortals.addPortal(item); break;
                        case 'k': darkGoldPortals.addPortal(item); break;
                        case 'l': darkGrayPortals.addPortal(item); break;
                    }
                }
            }
        }
    }

    // Check the status of the game and render relevant texts
    private void runGame() {

        GGBackground bg = this.background;
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

            grid = controller.loadNextFile();
            if (grid != null) {
                removeAllActors();
                this.game = new Game(nbHorzCells, nbVertCells);
                setUpAll();
                this.background = getBg();
                drawGrid();
                runGame();
            } else {
                bg.setPaintColor(Color.yellow);
                title = "YOU WIN";
            }
        }
        setTitle(title);
        game.getGameCallback().endOfGame(title);
        doPause();
    }

    public void drawGrid() {
        GGBackground bg = this.background;
        Location location;
        int cellValue;
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);
        for (int y = 0; y < nbVertCells; y++) {
            for (int x = 0; x < nbHorzCells; x++) {
                bg.setPaintColor(Color.white);
                location = new Location(x, y);
                cellValue = grid.getTile(x, y);
                bg.fillCell(location, Color.gray);
                // TODO: changed value
                if (cellValue != 'b') {
                    bg.fillCell(location, Color.lightGray);
                }
            }
        }
        for (Item item: pills) {
            bg.fillCircle(toPoint(item.getLocation()), 10);
        }
        for (Item item : goldPieces) {
            bg.setPaintColor(Color.yellow);
            bg.fillCircle(toPoint(item.getLocation()), 10);
        }
        for (Item item: iceCubes) {
            bg.setPaintColor(Color.blue);
            bg.fillCircle(toPoint(item.getLocation()), 5);
        }
    }

    public static String findFile(String currFile, String mapDir) {
        File folder = new File(mapDir);
        File[] files = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.getName().matches("\\d.*")) {
                fileNames.add(file.getName());
            }
        }

        fileNames.sort((fileName1, fileName2) -> {
            int num1 = Integer.parseInt(fileName1.replaceAll("\\D+", ""));
            int num2 = Integer.parseInt(fileName2.replaceAll("\\D+", ""));
            return Integer.compare(num1, num2);
        });

        int i = fileNames.indexOf(currFile);
        if (i >= 0 && i < fileNames.size() - 1) {
            return fileNames.get(i + 1);
        } else {
            return null;
        }
    }

    public void setUpAll() {
        pills = new ArrayList<>();
        goldPieces = new ArrayList<>();
        iceCubes = new ArrayList<>();
        whitePortals = new PortalPair();
        yellowPortals = new PortalPair();
        darkGoldPortals = new PortalPair();
        darkGrayPortals = new PortalPair();
        setupPillAndItemsLocations();
        game.addItems(pills, goldPieces, iceCubes, whitePortals, yellowPortals, darkGrayPortals, darkGoldPortals);
        pacActor = new PacActor(game, grid, isAuto);
        setupPacActorAttributes();
        monsters = new ArrayList<>();
        setupActorLocations();
        setupMonsterAttributes();
        game.addPacMan(pacActor);
        game.addMonsters(monsters);
    }
}
