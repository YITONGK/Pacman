package src;

import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Grid;

import java.util.*;

public class BFS {
    private final static int Horz = 20;
    private final static int Vert = 11;
    private final static int[] delta_x = {-1, 0, 1, 0};
    private final static int[] delta_y = {0, 1, 0, -1};
    private final static char[] portals = {'i', 'j', 'k', 'l'};

    // return a 2D boolean array
    public static boolean[][] bfs(Grid grid, Location start) {
        boolean[][] visited = new boolean[Horz][Vert];
        List<Location> reachableLocations = new ArrayList<>();
        Queue<Location> queue = new LinkedList<>();
        // mark the starting location as visited and add it to the queue
        visited[start.x][start.y] = true;
        queue.add(start);
        while (!queue.isEmpty()) {
            Location current = queue.poll();
            reachableLocations.add(current);
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
                queue.add(new Location(newX, newY));
                // if the new location is a portal, add its corresponding portal to the queue
                for (char c: portals) {
                    if (cell == c) {
                        for (int m = 0; m < Horz; m ++) {
                            for (int n = 0; n < Vert; n ++) {
                                if ((grid.getTile(m, n) == c) && !visited[m][n]) {
                                    queue.add(new Location(m, n));
                                }
                            }
                        }
                    }
                }
            }
        }
        return visited;
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
                return constructPath(parentMap, target);
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
                queue.add(new Location(newX, newY));
                boolean isPortal = false;
                for (char c: portals) {
                    if (cell == c) {
                        isPortal = true;
                        for (int m = 0; m < Horz; m ++) {
                            for (int n = 0; n < Vert; n ++) {
                                if ((grid.getTile(m, n) == c) && !visited[m][n]) {
                                    queue.add(new Location(m, n));
                                    parentMap.put(new Location(m, n), current);
                                }
                            }
                        }
                    }
                }
                if (!isPortal) {
                    parentMap.put(new Location(newX, newY), current);
                }
            }
        }
        return null;
    }

    public static List<Location> constructPath(Map<Location, Location> parentMap, Location target) {
        List<Location> path = new ArrayList<>();
        Location current = target;
//        for (Map.Entry<Location, Location> entry : parentMap.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);
        }
        return path;
    }



}
