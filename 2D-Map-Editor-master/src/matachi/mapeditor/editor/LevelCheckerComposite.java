package matachi.mapeditor.editor;

import matachi.mapeditor.grid.Grid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
//    LevelCheckerComposite levelChecker = new LevelCheckerComposite();
//            levelChecker.addLevelChecker(new CheckA());
//            levelChecker.addLevelChecker(new CheckB());
//            levelChecker.addLevelChecker(new CheckC());
//            levelChecker.addLevelChecker(new CheckD());
//    String log = levelChecker.checkLevel(map, this.grid);
//            if (log.length() != 0) {
//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter(new File("log.txt"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        try {
//            fileWriter.write(log);
//            fileWriter.write("\n");
//            fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return;
//    }

}


