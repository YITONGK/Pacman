// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;
import java.util.ArrayList;

/**
 * Game class defines the rules of the Pacman game and controls the win and lose logic,
 * and updates the monsters' behaviors during the game.
 */
public class Game {
  private final String mode;
  private final int nbHorzCells;
  private final int nbVertCells;
  private boolean hasPacmanBeenHit;
  private boolean hasPacmanEatAllPills;
  private PacActor pacActor;
  private ArrayList<Monster> monsters;
  private ArrayList<Item> pills;
  private ArrayList<Item> goldPieces;
  private ArrayList<Item> iceCubes;
  private PortalPair whitePortals;
  private PortalPair yellowPortals;
  private PortalPair darkGoldPortals;
  private PortalPair darkGrayPortals;
  private GameCallback gameCallback;

  public Game(String mode, int nbHorzCells, int nbVertCells) {
    this.mode = mode;
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;
    this.gameCallback = new GameCallback();
    this.hasPacmanBeenHit = false;
    this.hasPacmanEatAllPills = false;
  }


  public void removeItem(ItemType type,Location location){
    if(type == ItemType.GOLD_PIECE){
      for (Item item : goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
    else if(type == ItemType.ICE_CUBE){
      for (Item item : iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }

  public void addPacMan(PacActor pacActor) {
    this.pacActor = pacActor;
  }

  public void addMonsters(ArrayList<Monster> monsters) {
    this.monsters = monsters;
  }

  // TODO: Added portals to game
  public void addItems(ArrayList<Item> pills, ArrayList<Item> goldPieces, ArrayList<Item> iceCubes,
    PortalPair whitePortals, PortalPair yellowPortals, PortalPair darkGrayPortals, PortalPair darkGoldPortals) {
    this.pills = new ArrayList<>();
    this.pills.addAll(pills);
    this.goldPieces = new ArrayList<>();
    this.goldPieces.addAll(goldPieces);
    this.iceCubes = new ArrayList<>();
    this.iceCubes.addAll(iceCubes);
    this.whitePortals = whitePortals;
    this.yellowPortals = yellowPortals;
    this.darkGoldPortals = darkGoldPortals;
    this.darkGrayPortals = darkGrayPortals;
  }

  // TODO: Added portal-overlap-with-pacman logic
  public Location movePacmanThroughPortal(){
    Location moveTo;
    if ((moveTo = whitePortals.movePacMan(pacActor)) != null){
      return moveTo;
    }
    else if ((moveTo = yellowPortals.movePacMan(pacActor)) != null){
      return moveTo;
    }
    else if ((moveTo = darkGrayPortals.movePacMan(pacActor)) != null){
      return moveTo;
    }
    else if ((moveTo = darkGoldPortals.movePacMan(pacActor)) != null){
      return moveTo;
    }
    return null;
  }

  public void resetPortal(){
    yellowPortals.setMovedOntoPortal(pacActor);
    whitePortals.setMovedOntoPortal(pacActor);
    darkGrayPortals.setMovedOntoPortal(pacActor);
    darkGoldPortals.setMovedOntoPortal(pacActor);
  }

  // Check if Pacman is hit by a monster
  public boolean checkLost() {
    if (!hasPacmanBeenHit) {
      for(Monster monster: monsters) {
        if(monster.getLocation().equals(pacActor.getLocation())){
          hasPacmanBeenHit = true;
        }
      }
    }
    return hasPacmanBeenHit;
  }

  // Check if Pacman has eaten all pills
  public boolean checkWin() {
    int maxPillsAndItems = pills.size() + goldPieces.size();
    if (!hasPacmanEatAllPills) {
      hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
    }
    return hasPacmanEatAllPills;
  }

  public GameCallback getGameCallback() {
    return this.gameCallback;
  }

  public ArrayList<Item> getPills() {
    return this.pills;
  }
  public ArrayList<Item> getGoldPieces() {
    return this.goldPieces;
  }
  public ArrayList<Item> getIceCubes() {
    return this.iceCubes;
  }

  public String getGameVersion() {
    return this.mode;
  }

  public int getNumHorzCells() {
    return this.nbHorzCells;
  }

  public int getNumVertCells() {
    return this.nbVertCells;
  }

  public PacActor getPacActor() {
    return this.pacActor;
  }

  public boolean isLost() {
    return this.hasPacmanBeenHit;
  }

  public boolean isWin() {
    return this.hasPacmanEatAllPills;
  }

}
