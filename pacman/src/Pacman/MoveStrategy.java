package src.Pacman;

import ch.aplu.jgamegrid.*;
import matachi.mapeditor.grid.Grid;

public interface MoveStrategy {
    public Location move(PacActor pacman, Grid grid);
}
