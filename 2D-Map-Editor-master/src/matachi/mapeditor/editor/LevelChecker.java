package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;
import src.Item;
import src.ItemType;
import src.PortalPair;

import java.io.File;
import java.util.ArrayList;

public class LevelChecker {
    // TODO: Added Level Checking logic

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
        if (countPacMan == 0){
            // TODO: Add log statement for no pacman
            //  e.g., [Level 2mapname.xml – no start for PacMan]
            System.out.println("[Level " + file.getName() + " - no start for PacMan]");
        }
        if (countPacMan > 1){
            // TODO: Add log statement for more than one Pacman
            //  e.g., [Level 5_levelname.xml – more than one start for Pacman: (3,7); (8, 1); (5, 2)]
            System.out.println("[Level " + file.getName() + " - more than one start for Pacman: " +
                    locationListToString(pacmans) + "]");
        }
        if (!white.checkPortalTypeIsValid()){
            // TODO: Add log statement for invalid number of white portals
            //  e.g.,[Level 1_mapname.xml – portal White count is not 2: (2,3); (6,7); (1,8)
            System.out.println("[Level " + file.getName() + " – portal White count is not 2: " +
                    locationListToString(whites) + "]");
        }
        if (!yellow.checkPortalTypeIsValid()){
            // TODO: Add log statement for invalid number of yellow portals
            System.out.println("[Level " + file.getName() + " – portal Yellow count is not 2: " +
                    locationListToString(yellows) + "]");
        }
        if (!darkGold.checkPortalTypeIsValid()){
            // TODO: Add log statement for invalid number of dark gold portals
            System.out.println("[Level " + file.getName() + " – portal DarkGold count is not 2: " +
                    locationListToString(darkGolds) + "]");
        }
        if (!darkGray.checkPortalTypeIsValid()){
            // TODO: Add log statement for invalid number of dark gray portals
            System.out.println("[Level " + file.getName() + " – portal DarkGrey count is not 2: " +
                    locationListToString(darkGreys) + "]");
        }
        if (countPill + countGold < 2){
            // TODO: Add log statement for invalid number of gold + pill
            System.out.println("[Level " + file.getName() + " – less than 2 Gold and Pill]");
        }

        // TODO MISSING: level checking (4d) logic (each gold is accessible accounting for portals)
        else {
            // TODO: BFS
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

    private String locationListToString(ArrayList<Location> locations) {
        String output = "";
        for(Location l: locations) {
            output = output.concat(l.toString()).concat("; ");
        }
        return output;
    }
}
