package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;
import src.BFS;
import src.Item;
import src.ItemType;
import src.PortalPair;

import java.io.File;
import java.util.ArrayList;

public class CheckD extends LevelCheckComponent{
    public String checkLevel(File file, Grid model) {
        int countPacMan = 0;
        char tileChar;
        Location location;
        String output = "";
        ArrayList<Location> pacmans = new ArrayList<>();
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
                    pills.add(location);
                }
                else if (tileChar == 'd'){
                    golds.add(location);
                }
            }
        }
        if (countPacMan != 1) {
            return output;
        }
        else {
            boolean allPillsAccessible = true;
            boolean allGoldsAccessible = true;
            ArrayList<Location> inaccessiblePills = new ArrayList<>();
            ArrayList<Location> inaccessibleGolds = new ArrayList<>();
            boolean[][] accessibleLocations = BFS.bfs(model, pacmans.get(0));
            // loop the item list to see whether every item is at a reachable location
            for (Location l : pills) {
                if (!accessibleLocations[l.x][l.y]) {
                    allPillsAccessible = false;
                    inaccessiblePills.add(l);
                }
            }
            for (Location l : golds) {
                if (!accessibleLocations[l.x][l.y]) {
                    allGoldsAccessible = false;
                    inaccessibleGolds.add(l);
                }
            }
            // print log and return false
            if (!allPillsAccessible || !allGoldsAccessible) {
                if (!allPillsAccessible) {
                    output = "Level " + file.getName() + ".xml - Pill not accessible: " +
                            locationListToString(inaccessiblePills) + "\n";
                }
                if (!allGoldsAccessible) {
                    output = output + "Level " + file.getName() + ".xml - Gold not accessible: " +
                            locationListToString(inaccessibleGolds) + "\n";
                }
            }
        }
        return output;
    }
}
