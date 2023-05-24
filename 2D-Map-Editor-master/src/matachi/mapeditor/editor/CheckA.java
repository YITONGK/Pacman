package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;

public class CheckA extends LevelCheckComponent{

    @Override
    public String checkLevel(File file, Grid grid) {
        int countPacMan = 0;
        ArrayList<Location> pacmans = new ArrayList<>();
        char tileChar;
        Location location;
        String log = "";
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                tileChar = grid.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'f') {
                    countPacMan++;
                    pacmans.add(location);
                }
            }
        }
        // no pacman start point
        if (countPacMan == 0){
            log =  "Level " + file.getName() + ".xml - no start for PacMan\n";
            return log;
        }
        // multiple pacman start points
        if (countPacMan > 1){
            log = "Level " + file.getName() + ".xml - more than one start for Pacman: " + locationListToString(pacmans) + "\n";
            return log;
        }
        return log;
    }

}
