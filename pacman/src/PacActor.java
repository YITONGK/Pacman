// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacActor extends MovableActor implements GGKeyRepeatListener {
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private List<String> propertyMoves = new ArrayList<>();
  private int propertyMoveIndex = 0;
  private boolean isAuto = false;

  public PacActor(Game game) {
    super(game);
  }

  public void act() {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;
    if (isAuto) {
      moveInAutoMode();
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  // this method is used for user keyboard input to control pacman when pacman is not in auto mode
  public void keyRepeated(int keyCode) {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode) {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      setLocation(next);
      eatPill(next);
    }
  }

  private void moveInAutoMode() {
    // pacman will initially move following the commands
    if (propertyMoves.size() > propertyMoveIndex) {
      followPropertyMoves();
      return;
    }
    // after finishing all the move commands, pacman will automatically choose next location due to the closest pill
    Location closestPill = closestPillLocation();
    double oldDirection = getDirection();
    Location.CompassDirection compassDir =
            getLocation().get4CompassDirectionTo(closestPill);
    Location next = getLocation().getNeighbourLocation(compassDir);
    setDirection(compassDir);
    if (!isVisited(next) && canMove(next)) {
      setLocation(next);
    } else {
      // normal movement
      int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
      setDirection(oldDirection);
      turn(sign * 90);  // Try to turn left/right
      next = getNextMoveLocation();
      if (canMove(next)) {
        setLocation(next);
      } else {
        setDirection(oldDirection);
        next = getNextMoveLocation();
        if (canMove(next)) // Try to move forward
        {
          setLocation(next);
        } else {
          setDirection(oldDirection);
          turn(-sign * 90);  // Try to turn right/left
          next = getNextMoveLocation();
          if (canMove(next)) {
            setLocation(next);
          } else {
            setDirection(oldDirection);
            turn(180);  // Turn backward
            next = getNextMoveLocation();
            setLocation(next);
          }
        }
      }
    }
    eatPill(next);
    addVisitedList(next);
  }

  // this method is used to choose the location which is closest to a remaining item
  // will be used in auto move mode, to help pacman automatically choose next step
  private Location closestPillLocation() {
    int currentDistance = 1000;
    Location currentLocation = null;
    int distanceToItem;
    List<Item> items = new ArrayList<>();
    items.addAll(game.getPills());
    items.addAll(game.getGoldPieces());
    items.addAll(game.getIceCubes());
    for (Item item: items) {
      distanceToItem = item.getLocation().getDistanceTo(getLocation());
      if (distanceToItem < currentDistance) {
        currentLocation = item.getLocation();
        currentDistance = distanceToItem;
      }
    }
    return currentLocation;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public void setPropertyMoves(String propertyMoveString) {
    if (propertyMoveString != null) {
      this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }
  }

  private void followPropertyMoves() {
    String currentMove = propertyMoves.get(propertyMoveIndex);
    // "R" and "L" command for pacman to change facing direction, "M" command for pacman to move
    switch (currentMove) {
      case "R":
        turn(90);
        break;
      case "L":
        turn(-90);
        break;
      case "M":
        Location next = getNextMoveLocation();
        if (canMove(next)) {
          setLocation(next);
          eatPill(next);
        }
        break;
    }
    propertyMoveIndex++;
  }

  // check whether the current location pacman stands in has an item, if so, different items have different effects
  private void eatPill(Location location) {
    Color c = getBackground().getColor(location);
    // If pill was eaten
    if (c.equals(Color.white)) {
      nbPills++;
      score += ItemType.PILL.getScore();
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "pills");
    }
    // If gold piece was eaten
    else if (c.equals(Color.yellow)) {
      nbPills++;
      score += ItemType.GOLD_PIECE.getScore();
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem(ItemType.GOLD_PIECE,location);
    }
    // If ice cube was eaten
    else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem(ItemType.ICE_CUBE,location);
    }
    String title = "[PacMan in the Torusverse] Current score: " + score;
    gameGrid.setTitle(title);
  }

  public int getNbPills() {
    return nbPills;
  }
}
