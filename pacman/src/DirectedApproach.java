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
    private Location closestPillLocation(PacActor pacman, Game game) {
        int currentDistance = 1000;
        Location currentLocation = null;
        int distanceToItem;
        Item closestItem = null;
        System.out.println("===================");
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

    public Location move(PacActor pacman, Game game, Grid grid) {
        System.out.println("directed approach");
        if (target == null || path.size() == 0) {
            target = closestPillLocation(pacman, game);
            System.out.println(target);
            path = BFS.bfs(pacman.getLocation(), target, grid);
            System.out.println(path.size());
//            for (Location l: path) {
//                System.out.println(l);
//            }
        }
        Location next = path.remove(0);
        if (next.equals(target)) {
            target = null;
            path.clear();
        }
        Location.CompassDirection compassDir = pacman.getLocation().get4CompassDirectionTo(next);
        pacman.setDirection(compassDir);
        return next;
    }



//        pacman.setDirection(oldDirection);
//        Location next = pacman.getNextMoveLocation();
//        ArrayList<Location> nextLocations = new ArrayList<>();
//        ArrayList<Integer> nextDistances = new ArrayList<>();
//        if (pacman.canMove(next)) {
//            nextLocations.add(next);
//            nextDistances.add(next.getDistanceTo(closestPill));
//        }
//        // get all moveable next moves
//        for (int i = 0; i < 3; i++) {
//            pacman.turn(90);
//            next = pacman.getNextMoveLocation();
//            if (pacman.canMove(next)) {
//                nextLocations.add(next);
//                nextDistances.add(next.getDistanceTo(closestPill));
//            }
//        }
//        // select the optimal next move
//        Collections.sort(nextDistances);
//        for (Integer d : nextDistances) {
//            for (Location l : nextLocations) {
//                if (l.getDistanceTo(closestPill) == d && !pacman.isVisited(l)) {
//                    return l;
//                }
//            }
//        }
//        if (nextLocations.size() > 0) {
//            next = nextLocations.get(0);
//        }
//        return next;
//        Location.CompassDirection compassDir =
//                pacman.getLocation().get4CompassDirectionTo(closestPill);
//        Location next = pacman.getLocation().getNeighbourLocation(compassDir);
//        pacman.setDirection(compassDir);
//        if (!pacman.isVisited(next) && pacman.canMove(next)) {
//            return next;
//        } else {
//            // normal movement
//            int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
//            pacman.setDirection(oldDirection);
//            pacman.turn(sign * 90);  // Try to turn left/right
//            next = pacman.getNextMoveLocation();
//            if (pacman.canMove(next)) {
//                return next;
//            } else {
//                pacman.setDirection(oldDirection);
//                next = pacman.getNextMoveLocation();
//                if (pacman.canMove(next)) // Try to move forward
//                {
//                    return next;
//                } else {
//                    pacman.setDirection(oldDirection);
//                    pacman.turn(-sign * 90);  // Try to turn right/left
//                    next = pacman.getNextMoveLocation();
//                    if (pacman.canMove(next)) {
//                        return next;
//                    } else {
//                        pacman.setDirection(oldDirection);
//                        pacman.turn(180);  // Turn backward
//                        next = pacman.getNextMoveLocation();
//                        return next;
//                    }
//                }
//            }
//        }
//    }

}