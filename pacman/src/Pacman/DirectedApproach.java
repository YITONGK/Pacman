package src.Pacman;

import ch.aplu.jgamegrid.Location;
import src.grid.Grid;
import src.Game;
import src.Items.Item;
import java.util.*;

public class DirectedApproach implements MoveStrategy {
    private final static int Horz = 20;
    private final static int Vert = 11;
    private final static int[] delta_x = {-1, 0, 1, 0};
    private final static int[] delta_y = {0, 1, 0, -1};
    private final static char[] portals = {'i', 'j', 'k', 'l'};
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
            path = bfs(pacman.getLocation(), target, grid);
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

    // return a path
    public static List<Location> bfs(Location start, Location target, Grid grid) {
        boolean[][] visited = new boolean[Horz][Vert];
        visited[start.x][start.y] = true;
        Queue<Location> queue = new LinkedList<>();
        queue.add(start);
        Map<Location, Location> parentMap = new HashMap<>();
        parentMap.put(start, null);
        while (!queue.isEmpty()) {
            Location current = queue.poll();
            if (current.equals(target)) {
                return constructPath(parentMap, current);
            }
            // check four adjacent locations
            for (int i = 0; i < 4; i ++) {
                int newX = current.x + delta_x[i];
                int newY = current.y + delta_y[i];
                // skip if the new location is out of bounds or visited or is a wall
                if ((newX < 0 || newX >= Horz) || (newY < 0 || newY >= Vert)) {
                    continue;
                }
                char cell = grid.getTile(newX, newY);
                if (visited[newX][newY] || cell == 'b') {
                    continue;
                }
                visited[newX][newY] = true;
                Location newLocation = new Location(newX, newY);
                queue.add(newLocation);
                boolean isPortal = false;
                for (char c: portals) {
                    if (cell == c) {
                        isPortal = true;
                        for (int m = 0; m < Horz; m ++) {
                            for (int n = 0; n < Vert; n ++) {
                                if ((grid.getTile(m, n) == c) && !visited[m][n]) {
                                    newLocation =new Location(m, n);
                                    queue.add(newLocation);
                                    parentMap.put(newLocation, current);
                                }
                            }
                        }
                    }
                }
                if (!isPortal) {
                    parentMap.put(newLocation, current);
                }
            }
        }
        return null;
    }

    public static List<Location> constructPath(Map<Location, Location> parentMap, Location target) {
        ArrayList<Location> path = new ArrayList<>();
        Location current = target;
        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }
        return path;
    }
}