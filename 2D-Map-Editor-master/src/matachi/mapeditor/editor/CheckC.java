package matachi.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import com.sun.source.tree.BreakTree;
import matachi.mapeditor.grid.Grid;
import src.Item;
import src.ItemType;
import src.PortalPair;

import java.io.File;
import java.util.ArrayList;

public class CheckC extends LevelCheckComponent{
    public String checkLevel(File file, Grid model) {
        int countGold = 0;
        int countPill = 0;
        char tileChar;
        Location location;
        String output = "";
        ArrayList<Location> pills = new ArrayList<>();
        ArrayList<Location> golds = new ArrayList<>();
        for (int y = 0; y < model.getHeight(); y++){
            for (int x = 0; x < model.getWidth(); x++){
                tileChar = model.getTile(x, y);
                location = new Location(x, y);
                if (tileChar == 'c') {
                    countPill++;
                    pills.add(location);
                }
                else if (tileChar == 'd'){
                    countGold++;
                    golds.add(location);
                }
            }
        }
        if (countPill + countGold < 2){
            output = "Level " + file.getName() + ".xml – less than 2 Gold and Pill\n";
        }
        return output;
    }
}
