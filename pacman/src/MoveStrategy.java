package src;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.PacActor;

public interface MoveStrategy {
    public Location move(PacActor pacman, Game game);
}
