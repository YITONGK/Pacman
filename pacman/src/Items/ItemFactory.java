package src.Items;

import ch.aplu.jgamegrid.Location;

public class ItemFactory {
    private static ItemFactory instance;

    private ItemFactory(){
    }

    public static ItemFactory getInstance(){
        if (instance == null) {
            instance = new ItemFactory();
        }
        return instance;
    }

    public Item createItem(char type, Location location) {
        Item item = null;
        switch (type) {
            case 'c': item = new Item(location); break;
            case 'd': item = new Item(ItemType.GOLD_PIECE.getImage(), location); break;
            case 'e': item = new Item(ItemType.ICE_CUBE.getImage(), location); break;
            case 'i': item = new Item(ItemType.WHITE_PORTAL.getImage(), location); break;
            case 'j': item = new Item(ItemType.YELLOW_PORTAL.getImage(), location); break;
            case 'k': item = new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location); break;
            case 'l': item = new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location); break;
        }
        return item;
    }
}
