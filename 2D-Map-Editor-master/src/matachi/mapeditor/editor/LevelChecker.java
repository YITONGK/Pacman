package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;
import src.BFS;
import src.Item;
import src.ItemType;
import src.PortalPair;

import java.io.File;
import java.util.ArrayList;

public class LevelChecker {

    private static LevelChecker instance;

    private LevelChecker(){
    }

    public static LevelChecker getInstance(){
        if (instance == null) {
            instance = new LevelChecker();
        }
        return instance;
    }

    public boolean checkLevel(File file, Grid model){
        int countPacMan = 0;
        int countGold = 0, countPill = 0;
        char tileChar;
        Location location;
        PortalPair white = new PortalPair();
        PortalPair yellow = new PortalPair();
        PortalPair darkGold = new PortalPair();
        PortalPair darkGray = new PortalPair();
        ArrayList<Location> pacmans = new ArrayList<>();
        ArrayList<Location> whites = new ArrayList<>();
        ArrayList<Location> yellows = new ArrayList<>();
        ArrayList<Location> darkGolds = new ArrayList<>();
        ArrayList<Location> darkGreys = new ArrayList<>();
        ArrayList<Location> pills = new ArrayList<>();
        ArrayList<Location> golds = new ArrayList<>();
        for (int y = 0; y < model.getHeight(); y++){
            for (int x = 0; x < model.getWidth(); x++){
                tileChar = model.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'f') {
                    countPacMan++;
                    pacmans.add(location);
                }
                else if (tileChar == 'c') {
                    countPill++;
                    pills.add(location);
                }
                else if (tileChar == 'd'){
                    countGold++;
                    golds.add(location);
                }
                else if (tileChar == 'i'){
                    white.addPortal(new Item(ItemType.WHITE_PORTAL.getImage(), location));
                    whites.add(location);
                }
                else if (tileChar == 'j'){
                    yellow.addPortal(new Item(ItemType.YELLOW_PORTAL.getImage(), location));
                    yellows.add(location);
                }
                else if (tileChar == 'k'){
                    darkGold.addPortal(new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location));
                    darkGolds.add(location);
                }
                else if (tileChar == 'l'){
                    darkGray.addPortal(new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location));
                    darkGreys.add(location);
                }
            }
        }
        // level check 4a
        if (countPacMan == 0){
            System.out.println("Level " + file.getName() + ".xml - no start for PacMan");
        }
        if (countPacMan > 1){
            System.out.println("Level " + file.getName() + ".xml - more than one start for Pacman: " +
                    locationListToString(pacmans));
        }
        // level check 4b
        if (!white.checkPortalTypeIsValid()){
            System.out.println("Level " + file.getName() + ".xml – portal White count is not 2: " +
                    locationListToString(whites));
        }
        if (!yellow.checkPortalTypeIsValid()){
            System.out.println("Level " + file.getName() + ".xml – portal Yellow count is not 2: " +
                    locationListToString(yellows));
        }
        if (!darkGold.checkPortalTypeIsValid()){
            System.out.println("Level " + file.getName() + ".xml – portal DarkGold count is not 2: " +
                    locationListToString(darkGolds));
        }
        if (!darkGray.checkPortalTypeIsValid()){
            System.out.println("Level " + file.getName() + ".xml – portal DarkGrey count is not 2: " +
                    locationListToString(darkGreys));
        }
        // level check 4c
        if (countPill + countGold < 2){
            System.out.println("Level " + file.getName() + ".xml – less than 2 Gold and Pill");
        }
        // level check 4d
        else {
            // if pacman start point goes wrong, no need to check 4d
            if (countPacMan != 1) {
                return false;
            }
            else {
                boolean allPillsAccessible = true;
                boolean allGoldsAccessible = true;
                ArrayList<Location> inaccessiblePills = new ArrayList<>();
                ArrayList<Location> inaccessibleGolds = new ArrayList<>();
                boolean[][] accessibleLocations = BFS.bfs(model, pacmans.get(0));
                // loop the item list to see whether every item is at a reachable location
                for (Location l: pills) {
                    if (!accessibleLocations[l.x][l.y]) {
                        allPillsAccessible = false;
                        inaccessiblePills.add(l);
                    }
                }
                for (Location l: golds) {
                    if (!accessibleLocations[l.x][l.y]) {
                        allGoldsAccessible = false;
                        inaccessibleGolds.add(l);
                    }
                }
                // print log and return false
                if (!allPillsAccessible || !allGoldsAccessible) {
                    if (!allPillsAccessible) {
                        System.out.println("Level " + file.getName() + ".xml - Pill not accessible: " +
                                locationListToString(inaccessiblePills));
                    }
                    if (!allGoldsAccessible) {
                        System.out.println("Level " + file.getName() + ".xml - Gold not accessible: " +
                                locationListToString(inaccessibleGolds));
                    }
                    return false;
                }
            }
        }

        return (countPacMan == 1) && (countGold + countPill >= 2) &&
                (checkPortalTypeIsValid(white, yellow, darkGold, darkGray));
    }

    /**
     * Function used in Level Checking to detect validity of current portal pair
     * @return
     */
    private boolean checkPortalTypeIsValid(PortalPair white, PortalPair yellow, PortalPair darkGold, PortalPair darkGray) {
        return white.checkPortalTypeIsValid() && yellow.checkPortalTypeIsValid() && darkGray.checkPortalTypeIsValid()
                && darkGold.checkPortalTypeIsValid();
    }

    // used for printing log as locations should be displayed as "(x1, y1); (x2, y2); (x3, y3)"
    private String locationListToString(ArrayList<Location> locations) {
        String output = "";
        for(Location l: locations) {
            // in order to match log
            l.x += 1;
            l.y += 1;
            if (locations.indexOf(l) == locations.size() - 1) {
                output = output.concat(l.toString());
            }
            else {
                output = output.concat(l.toString()).concat("; ");
            }
        }
        return output;
    }

    private void print2D(boolean[][] array) {
        for (int i = 0; i < 11; i ++) {
            for (int j = 0; j < 20; j ++) {
                System.out.print(array[j][i] ? "T " : "F ");
            }
            System.out.println();
        }
    }
}
