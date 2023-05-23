package matachi.mapeditor.editor;

import matachi.mapeditor.grid.GridModel;
import src.Game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameChecker {
    private static GameChecker instance;
    FileWriter fileWriter = null;
    private GameChecker() {

    }

    public static GameChecker getInstance(){
        if (instance == null) {
            instance = new GameChecker();
        }
        return instance;
    }

    /**
     * NEWLY ADDED: Function to check if folder is valid (section 2.2.4). Checks following:
     * 1. at least one correctly named map file in the folder
     * 2. the sequence of map files well-defined, where only one map file named with a particular number.
     */
    public ArrayList<File> checkGame(File folder){
        ArrayList<File> mapFiles = new ArrayList<File>();
        File[] files = folder.listFiles();
        HashMap<Integer, ArrayList<String>> nameHashMap = new HashMap<>();
        char firstChar;
        int nameNum, countValidFiles = 0;
        boolean startsWithUniqueNumbers = true;

        // Folder must contain contents to be valid
        if (files != null){
            for (int i = 0; i < files.length; i++){
                // We only use folder contents which are files
                if (files[i].isFile()){
                    firstChar = files[i].getName().charAt(0);
                    if (Character.isDigit(firstChar)){
                        nameNum = Character.getNumericValue(firstChar);
                        // If a particular number has not been used for a map file name
                        if (!nameHashMap.containsKey(nameNum)) {
                            ArrayList<String> names = new ArrayList<>();
                            names.add(files[i].getName());
                            nameHashMap.put(nameNum, names);
                            // Add any valid map files to our arraylist
                            mapFiles.add(files[i]);
                        }
                        else {
                            nameHashMap.get(nameNum).add(files[i].getName());
                        }
                        countValidFiles++;
                    }
                }
            }
        }
        try {
            fileWriter = new FileWriter(new File("log.txt"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (countValidFiles == 0){
            // TODO: print error to log (e.g., [Game foldername – no maps found])
            System.out.println("Game foldername – no maps found");
            writeString("Game foldername – no maps found");
        }
        for (Integer key: nameHashMap.keySet()){
            if (nameHashMap.get(key).size() > 1){
                startsWithUniqueNumbers = false;
                // TODO: print error to log (e.g., [Game foldername – multiple maps at same level: 6level.xml; 6_map.xml; 6also.xml])
                System.out.println("Game foldername – multiple maps at same level: ");
                writeString("Game foldername – multiple maps at same level: ");
            }
        }
        boolean status = startsWithUniqueNumbers && countValidFiles >= 1;
        if (!status) {
            return null;
        }
        return mapFiles;
    }

    public void writeString(String str) {
        try {
            fileWriter.write(str);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
