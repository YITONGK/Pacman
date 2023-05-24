package src.Items;

public enum ItemType {
    PILL,
    ICE_CUBE,
    GOLD_PIECE,
    WHITE_PORTAL,
    DARK_GOLD_PORTAL,
    DARK_GRAY_PORTAL,
    YELLOW_PORTAL;

    public int getScore(){
        return switch (this) {
            case PILL -> 1;
            case GOLD_PIECE -> 5;
            default -> 0;
        };
    }

    // TODO: Added image paths to each portal type
    //  (this may be modified if we change the location of the editor module)

    public String getImage(){
        return switch (this) {
            case GOLD_PIECE -> "sprites/gold.png";
            case ICE_CUBE -> "sprites/ice.png";
            case WHITE_PORTAL -> "2D-Map-Editor-master/data/i_portalWhiteTile.png";
            case YELLOW_PORTAL -> "2D-Map-Editor-master/data/j_portalYellowTile.png";
            case DARK_GOLD_PORTAL -> "2D-Map-Editor-master/data/k_portalDarkGoldTile.png";
            case DARK_GRAY_PORTAL -> "2D-Map-Editor-master/data/l_portalDarkGrayTile.png";
            default -> "";
        };
    }
}
