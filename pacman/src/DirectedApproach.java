package src;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;

import java.util.*;

public class DirectedApproach implements MoveStrategy {

    private List<Item> items = new ArrayList<>();
    private Location target = null;
    private List<Location> path;

    public DirectedApproach(Game game) {
        items.addAll(game.getPills());
        items.addAll(game.getGoldPieces());
    }


    // this method is used to choose the location which is closest to a remaining item
    // will be used in auto move mode, to help pacman automatically choose next step
    private Location closestPillLocation(PacActor pacman) {
        int currentDistance = 1000;
        Location currentLocation = null;
        int distanceToItem;
        Item closestItem = null;
        for (Item item : items) {
            distanceToItem = item.getLocation().getDistanceTo(pacman.getLocation());
            if (distanceToItem < currentDistance) {
                currentLocation = item.getLocation();
                currentDistance = distanceToItem;
                closestItem = item;
            }
        }
        items.remove(closestItem);
        return currentLocation;
    }

    public Location move(PacActor pacman, Grid grid) {
        if (target == null || path.size() == 0) {
            target = closestPillLocation(pacman);
            path = BFS.bfs(pacman.getLocation(), target, grid);

        }
        Location next = path.remove(1);
        if (next.equals(target)) {
            target = null;
            path.clear();
        }
        Location.CompassDirection compassDir = pacman.getLocation().get4CompassDirectionTo(next);
        pacman.setDirection(compassDir);
        return next;
    }

}