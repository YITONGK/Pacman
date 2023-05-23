package matachi.mapeditor.editor;

import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelCheckerComposite extends LevelCheckComponent{
    private List<LevelCheckComponent> levelCheckers;

    public LevelCheckerComposite () {
        levelCheckers = new ArrayList<>();
    }

    public void addLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.add(levelChecker);
    }
    public void removeLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.remove(levelChecker);
    }

    public String checkLevel(File file, Grid model) {
        String output = "";
        for (LevelCheckComponent checker: levelCheckers) {
            output = output + checker.checkLevel(file, model);
        }
        return output;
    }

}
