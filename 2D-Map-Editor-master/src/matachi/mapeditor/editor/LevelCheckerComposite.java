package matachi.mapeditor.editor;

import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelCheckerComposite extends LevelCheckComponent{
    private static LevelCheckerComposite instance;
    private List<LevelCheckComponent> levelCheckers;

    private LevelCheckerComposite () {
        levelCheckers = new ArrayList<>();
        addLevelChecker(new CheckA());
        addLevelChecker(new CheckB());
        addLevelChecker(new CheckC());
        addLevelChecker(new CheckD());
    }

    public static LevelCheckerComposite getInstance(){
        if (instance == null) {
            instance = new LevelCheckerComposite();
        }
        return instance;
    }

    public void addLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.add(levelChecker);
    }
    public void removeLevelChecker(LevelCheckComponent levelChecker) {
        levelCheckers.remove(levelChecker);
    }

    public String checkLevel(File file, Grid grid) {
        String log = "";
        for (LevelCheckComponent checker: levelCheckers) {
            log = log + checker.checkLevel(file, grid);
        }
        return log;
    }
}


