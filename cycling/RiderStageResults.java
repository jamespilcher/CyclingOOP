package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;


/**
 * Stores a single rider's results in a single stage.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class RiderStageResults implements Serializable {
  private Rider rider;
  private Stage stage;
  private int raceId;
  private Long startTime;

  Long elapsedTimeForStage; // time taken for the stage 
  Long adjustedTimeForStage; /* time taken for the stage, adjusted for if a rider finishes
                            within a second of the rider ahead */
  LinkedList<Long> segmentTimes = new LinkedList<Long>(); // List of rider's times in each segment

  int riderPoints = 0;
  int riderMountainPoints = 0;

  /**
   * Constructor for the RiderStageResults class.
   *
   * @param rider the rider the results relate to
   * @param stage the stage the results relate to
   * @param raceId the ID of the race the stage belongs to
   * @param checkpoints time at each checkpoint in the stage for the rider
   */
  public RiderStageResults(Rider rider, Stage stage, int raceId, LocalTime... checkpoints) {
    this.rider = rider;
    this.stage = stage;
    this.raceId = raceId;
    startTime = checkpoints[0].toNanoOfDay();
    elapsedTimeForStage 
        = checkpoints[0].until(checkpoints[checkpoints.length - 1], ChronoUnit.NANOS);
    for (int i = 1; i < checkpoints.length - 1; i++) {
      segmentTimes.add(checkpoints[0].until(checkpoints[i], ChronoUnit.NANOS));
    }
  }

  /**
   *  Set rider's mountain points to specified value.
   *
   * @param points value to set mountain points to
   */
  public void setMountainPoints(int points) {
    riderPoints = points;
  }

  /**
   * Set rider's points to specified value.
   *
   * @param points value to set points to
   */
  public void setPoints(int points) {
    riderPoints = points;
  }

  /**
   * Increase rider's points by specified value.
   *
   * @param points value to increase rider's points by
   */
  public void addPoints(int points) {
    riderPoints += points;
  }

  /**
   * Increase rider's mountain points by specified value.
   *
   * @param points value to increase rider's mountain points by'
   */
  public void addMountainPoints(int points) {
    riderMountainPoints += points;
  }

  /**
   * Gets an arraylist of all the rider's segment times.
   *
   * @return arraylist of all the rider's segment times'
   */
  public LinkedList<Long> getSegmentTimes() { 
    return segmentTimes;
  }

  /**
   * Gets the time of a rider in a certain segment.
   *
   * @param index index in the array of segment times
   * @return rider's time in specified segment
   */
  public Long getSegmentTime(int index) { 
    return segmentTimes.get(index);
  }

  /**
   * Gets the total time of the rider in the stage.
   *
   * @return total time of the rider in the stage
   */
  public Long getElapsedTimeForStage() { 
    return elapsedTimeForStage;
  }

  /**
   * Gets ID of the race the stage is a part of.
   *
   * @return race ID
   */
  public int getRaceId() { 
    return raceId;
  }

  /**
   * Gets rider's total points.
   *
   * @return rider's points
   */
  public int getRiderPoints() { 
    return riderPoints; 
  }

  /**
   * Gets rider's total mountain points.
  *
   * @return rider's mountain points
   */
  public int getRiderMountainPoints() { 
    return riderMountainPoints;
  }
  

  /**
   * Gets the object of the stage.
   *
   * @return the stage object
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Gets the object of the rider.
   *
   * @return the rider object
   */
  public Rider getRider() {
    return rider; 
  }

  /**
   * Gets the start time of the stage.
   *
   * @return stage's start time
   */
  public Long getStartTime() {
    return startTime; 
  }

  /**
   * Gets rider's adjusted time for stage, adjusted for if 
   * a rider finishes within a second of the rider ahead.
   *
   * @return Rider's adjusted time
   */
  public Long getAdjustedTimeForStage() { 
    return adjustedTimeForStage; 
  }

  /**
   * Sets rider's adjusted time for this stage, adjusted for
   * if a rider finishes within a second of the rider ahead.
   *
   * @param adjustedTimeForStage the new adjusted time for the stage
   */
  public void setAdjustedTimeForStage(Long adjustedTimeForStage) { 
    this.adjustedTimeForStage = adjustedTimeForStage;
  }
}
