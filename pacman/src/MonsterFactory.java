package src;

import matachi.mapeditor.editor.GameChecker;

public class MonsterFactory {
    private static MonsterFactory instance;

    private MonsterFactory(){

    }

    public static MonsterFactory getInstance(){
        if (instance == null) {
            instance = new MonsterFactory();
        }
        return instance;
    }

    public Monster createMonster(char type, Game game) {
        Monster newMonster = null;
        if (type == 'g') {
            newMonster = new Troll(game);
        }
        if (type == 'h') {
            newMonster = new TX5(game);
        }
        return newMonster;
    }
}
