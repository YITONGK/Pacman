package src;

import ch.aplu.jgamegrid.Location;

public class Troll extends Monster{
    public Troll(Game game) {
        super(game, MonsterType.Troll);
    }

    // Troll implements random walk
    public Location walkApproach() {
        return randomWalk();
    }
}
