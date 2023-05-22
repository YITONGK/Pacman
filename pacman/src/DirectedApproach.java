package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DirectedApproach implements MoveStrategy {

    protected Random randomiser = new Random();
    public DirectedApproach(int seed){
        randomiser.setSeed(seed);
    }


    // this method is used to choose the location which is closest to a remaining item
    // will be used in auto move mode, to help pacman automatically choose next step
    private Location closestPillLocation(PacActor pacman, Game game) {
        int currentDistance = 1000;
        Location currentLocation = null;
        int distanceToItem;
        List<Item> items = new ArrayList<>();
        items.addAll(game.getPills());
        items.addAll(game.getGoldPieces());
        for (Item item: items) {
            distanceToItem = item.getLocation().getDistanceTo(pacman.getLocation());
            if (distanceToItem < currentDistance) {
                currentLocation = item.getLocation();
                currentDistance = distanceToItem;
            }
        }
        return currentLocation;
    }

    public Location move(PacActor pacman, Game game) {
        Location closestPill = closestPillLocation(pacman, game);
        double oldDirection = pacman.getDirection();
        Location.CompassDirection compassDir =
                pacman.getLocation().get4CompassDirectionTo(closestPill);
        Location next = pacman.getLocation().getNeighbourLocation(compassDir);
        pacman.setDirection(compassDir);
        if (pacman.isVisited(next) && pacman.canMove(next)) {
            return next;
        } else {
            // normal movement
            int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
            pacman.setDirection(oldDirection);
            pacman.turn(sign * 90);  // Try to turn left/right
            next = pacman.getNextMoveLocation();
            if (pacman.canMove(next)) {
                return next;
            } else {
                pacman.setDirection(oldDirection);
                next = pacman.getNextMoveLocation();
                if (pacman.canMove(next)) // Try to move forward
                {
                    return next;
                } else {
                    pacman.setDirection(oldDirection);
                    pacman.turn(-sign * 90);  // Try to turn right/left
                    next = pacman.getNextMoveLocation();
                    if (pacman.canMove(next)) {
                        return next;
                    } else {
                        pacman.setDirection(oldDirection);
                        pacman.turn(180);  // Turn backward
                        next = pacman.getNextMoveLocation();
                        return next;
                    }
                }
            }
        }
    }
}
