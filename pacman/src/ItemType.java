package src;

public enum ItemType {
    PILL,
    ICE_CUBE,
    GOLD_PIECE;

    public int getScore(){
        return switch (this) {
            case PILL -> 1;
            case GOLD_PIECE -> 5;
            default -> 0;
        };
    }
    public String getImage(){
        return switch (this) {
            case GOLD_PIECE -> "sprites/gold.png";
            case ICE_CUBE -> "sprites/ice.png";
            default -> "";
        };
    }
}
