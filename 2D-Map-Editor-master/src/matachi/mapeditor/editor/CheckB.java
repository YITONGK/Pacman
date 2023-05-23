package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;
import src.Item;
import src.ItemType;
import src.PortalPair;

import java.io.File;
import java.util.ArrayList;

public class CheckB extends LevelCheckComponent{

    public String checkLevel(File file, Grid model) {
        char tileChar;
        Location location;
        String output = "";
        PortalPair white = new PortalPair();
        PortalPair yellow = new PortalPair();
        PortalPair darkGold = new PortalPair();
        PortalPair darkGray = new PortalPair();
        ArrayList<Location> whites = new ArrayList<>();
        ArrayList<Location> yellows = new ArrayList<>();
        ArrayList<Location> darkGolds = new ArrayList<>();
        ArrayList<Location> darkGreys = new ArrayList<>();
        for (int y = 0; y < model.getHeight(); y++){
            for (int x = 0; x < model.getWidth(); x++){
                tileChar = model.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'i'){
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
        // level check 4b
        if (!white.checkPortalTypeIsValid()){
            output = output + "Level " + file.getName() + ".xml – portal White count is not 2: " +
                    locationListToString(whites);
        }
        if (!yellow.checkPortalTypeIsValid()){
            output = output + "\nLevel " + file.getName() + ".xml – portal Yellow count is not 2: " +
                    locationListToString(yellows);
        }
        if (!darkGold.checkPortalTypeIsValid()){
            output = output + "\nLevel " + file.getName() + ".xml – portal DarkGold count is not 2: " +
                    locationListToString(darkGolds);
        }
        if (!darkGray.checkPortalTypeIsValid()){
            output = output + "\nLevel " + file.getName() + ".xml – portal DarkGrey count is not 2: " +
                    locationListToString(darkGreys);
        }
        return output;
    }

}
