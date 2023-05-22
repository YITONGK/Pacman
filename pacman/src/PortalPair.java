package src;

// TODO: Added PortalPair class for adding portals to map,
//  and also additional functions for level checking purposes

import ch.aplu.jgamegrid.*;

import java.util.ArrayList;

public class PortalPair {
    // Each portal is an Item
    private ArrayList<Item> portals;

    private int countPortals;
    // Flag to check the nature of PacMan-Portal overlap
    // (i.e., did PacMan move onto Portal, or was PacMan transported onto Portal?)
    private boolean movedOntoPortal = true;
    public final static int VALID_PAIR = 2;
    public final static int EMPTY_PAIR = 0;

    public PortalPair(){
        countPortals = 0;
        portals = new ArrayList<>();
    }

    /**
     * Function to add portals during Pacman game
     */
    public void addPortal(Item portal){
        portals.add(portal);
        countPortals++;
    }

    /**
     * Function used in Level Checking to detect validity of current portal pair
     * @return
     */
    public boolean checkPortalTypeIsValid(){
        return checkPortalCount();
//        return checkPortalCount() && checkTwoDistinctTiles();
    }

    /**
     * Function to check that we have either 0 or 2 portals, for each "type" of portal
     */
    private boolean checkPortalCount(){
        return (countPortals == VALID_PAIR) || (countPortals == EMPTY_PAIR);
    }

    /**
     * Function to check that the two portals lie on two distinct tiles
     */
//    private boolean checkTwoDistinctTiles(){
//        Item portal1 = portals.get(0);
//        Item portal2 = portals.get(1);
//        if (portal1.getLocation().equals(portal2.getLocation())){
//            // Decrement number of portals since they are in the same location (i.e., does nothing)
//            countPortals = countPortals - 1;
//            portals.remove(portal2);
//            return false;
//        }
//        return true;
//    }

    /**
     * Function to transport PacMan to the other portal
     */
    public Location moveActor(Actor actor){
        if (countPortals == 0){
            return null;
        }
        // If PacMan overlapped with portal #1, move PacMan to portal #2
        Item portal1 = portals.get(0);
        Item portal2 = portals.get(1);
        if (portal1.getLocation().equals(actor.getLocation()) && movedOntoPortal){
            movedOntoPortal = false;
            return portal2.getLocation();
        }
        // If PacMan overlapped with portal #2, move PacMan to portal #1
        else if (portal2.getLocation().equals(actor.getLocation()) && movedOntoPortal){
            movedOntoPortal = false;
            return portal1.getLocation();
        }
        return null;
    }

    /**
     * Function to reset movedOntoPortal flag, so that PacMan can repeatedly travel via portals
     */
    public void setMovedOntoPortal(Actor actor){
        if (countPortals == 0){
            return;
        }
        Item portal1 = portals.get(0);
        Item portal2 = portals.get(1);
        if (!(portal1.getLocation().equals(actor.getLocation()) &&
                    portal2.getLocation().equals(actor.getLocation()))){
            movedOntoPortal = true;
        }
    }

    public String locationsToString(){
        String locations = "";
        for (int i = 0; i < portals.size(); i++){
            locations += portals.get(i).getLocation().toString();
            if (i < portals.size() - 1){
                locations += "; ";
            }
        }
        return locations;
    }

//    public ArrayList<Item> getPortals() {
//        return portals;
//    }
//
//    public int getCountPortals() {
//        return countPortals;
//    }

}
