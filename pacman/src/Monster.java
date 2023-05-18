// Monster.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.util.*;

public abstract class Monster extends MovableActor {
  protected MonsterType type;
  protected boolean stopMoving = false;
  protected boolean isFurious = false;
  protected boolean isFrozen = false;

  public Monster(Game game, MonsterType type) {
    super(game, type);
    this.type = type;
  }

  public void act() {
    // either stopMoving or isFrozen is true, monsters stand still, this act() method will not reach the walk() method
    if (stopMoving || isFrozen) {
      return;
    }
    walk();
    if (getDirection() > 150 && getDirection() < 210) {
      setHorzMirror(false);
    }
    else {
      setHorzMirror(true);
    }
  }

  // Return the next move of the monster
  public abstract Location walkApproach();

  // The monsters walk according to different states
  public void walk() {
    Location next = walkApproach();
    // isFurious is false, monsters move normally
    if (!isFurious) {
      setLocation(next);
      addVisitedList(next);
    } else {
      // jump is the next location in the direction of current next possible move action
      Location jump = next.getNeighbourLocation(getLocation().getDirectionTo(next));
      if (canMove(jump)) {
        setLocation(jump);
        addVisitedList(jump);
      }
      // if the monster can't move 2 cells this time, move 1 cell as normal
      else {
        setLocation(next);
        addVisitedList(next);
      }
    }
    game.getGameCallback().monsterLocationChanged(this);
  }

  // Randomly turn left or right, and turn back to the original direction and go forward or turn the other side if
  // hits maze wall
  public Location randomWalk() {
    double oldDirection = getDirection();
    Location next = null;
    int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
    setDirection(oldDirection);
    turn(sign * 90);  // Try to turn left/right
    next = getNextMoveLocation();
    if (canMove(next)) {
      return next;
    } else {
      setDirection(oldDirection);
      next = getNextMoveLocation();
      if (canMove(next)) { // Try to move forward{
        addVisitedList(next);
        return next;
      } else {
        setDirection(oldDirection);
        turn(-sign * 90);  // Try to turn right/left
        next = getNextMoveLocation();
        if (canMove(next)) {
          addVisitedList(next);
          return next;
        } else {
          setDirection(oldDirection);
          turn(180);  // Turn backward
          next = getNextMoveLocation();
          addVisitedList(next);
          return next;
        }
      }
    }
  }

  public void becomeFrozen(int seconds) {
    // as said in spec, monsters become frozen even if they are in furious state
    this.isFrozen = true;
    this.isFurious = false;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.isFrozen = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  public void becomeFurious(int seconds) {
    // if pac eats a gold, but the monsters are frozen at the moment, we won't make monsters furious, just return
    if (isFrozen) {
      return;
    } else {
      this.isFurious = true;
      Timer timer = new Timer(); // Instantiate Timer Object
      int SECOND_TO_MILLISECONDS = 1000;
      final Monster monster = this;
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          monster.isFurious = false;
        }
      }, seconds * SECOND_TO_MILLISECONDS);
    }
  }

  public void stopMoving(int seconds) {
    this.stopMoving = true;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.stopMoving = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  public void setStopMoving(boolean stopMoving) {
    this.stopMoving = stopMoving;
  }

  public MonsterType getType() {
    return type;
  }
}