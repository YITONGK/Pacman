package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;

public class CheckA extends LevelCheckComponent{

    @Override
    public String checkLevel(File file, Grid model) {
        int countPacMan = 0;
        ArrayList<Location> pacmans = new ArrayList<>();
        char tileChar;
        Location location;
        String output = "";
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                tileChar = model.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'f') {
                    countPacMan++;
                    pacmans.add(location);
                }
            }
        }
        // level check 4a
        if (countPacMan == 0){
            output =  "Level " + file.getName() + ".xml - no start for PacMan";
            return output;
        }
        if (countPacMan > 1){
            output = "Level " + file.getName() + ".xml - more than one start for Pacman: " + locationListToString(pacmans);
            return output;
        }
        return output;
    }

}
