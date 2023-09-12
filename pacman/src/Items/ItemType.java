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

    public String getImage(){
        return switch (this) {
            case GOLD_PIECE -> "sprites/data/d_goldTile.png";
            case ICE_CUBE -> "sprites/data/e_iceTile.png";
            case WHITE_PORTAL -> "sprites/data/i_portalWhiteTile.png";
            case YELLOW_PORTAL -> "sprites/data/j_portalYellowTile.png";
            case DARK_GOLD_PORTAL -> "sprites/data/k_portalDarkGoldTile.png";
            case DARK_GRAY_PORTAL -> "sprites/data/l_portalDarkGrayTile.png";
            default -> "";
        };
    }
}
