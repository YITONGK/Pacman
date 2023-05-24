package src.Monsters;

import src.Game;

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
        switch (type) {
            case 'g': newMonster = new Troll(game); break;
            case 'h': newMonster = new TX5(game); break;
        }
        return newMonster;
    }
}
