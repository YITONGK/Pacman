package src;

// TODO: Added PortalPair class for adding portals to map,
//  and also additional functions for level checking purposes

public class PortalPair {
    // Each portal is an Item
    private Item portal1;
    private Item portal2;

    private int countPortals;
    public final static int VALID_PAIR = 2;

    public void addPortal(Item portal){
        // Only add portals if we have space
        if (portal1 == null){
            portal1 = portal;
        }
        else if (portal2 == null){
            portal2 = portal;
        }
        countPortals++;
    }

    /**
     * Function used in Level Checking to detect validity of current portal pair
     * @return
     */
    public boolean checkPortalTypeIsValid(){
        return checkPortalCount() && checkTwoDistinctTiles();
    }

    /**
     * Function to check that we have the correct number of portals (i.e., 2) for each "type" of portal
     */
    private boolean checkPortalCount(){
        return countPortals == VALID_PAIR;
    }

    /**
     * Function to check that the two portals lie on two distinct tiles
     */
    private boolean checkTwoDistinctTiles(){
        return !(portal1.getLocation().equals(portal2.getLocation()));
    }
}
