package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;

public abstract class LevelCheckComponent {
    public abstract String checkLevel(File file, Grid model);

    // used for printing log as locations should be displayed as "(x1, y1); (x2, y2); (x3, y3)"
    public String locationListToString(ArrayList<Location> locations) {
        String output = "";
        for(Location l: locations) {
            // in order to match log
            l.x += 1;
            l.y += 1;
            // if l is the last element in locations, don't need to add "; " to the output string
            if (locations.indexOf(l) == locations.size() - 1) {
                output = output.concat(l.toString());
            }
            else {
                output = output.concat(l.toString()).concat("; ");
            }
        }
        return output;
    }

}
