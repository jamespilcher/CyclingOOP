package cycling;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents a rider.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class Rider extends IdHaver implements Serializable {

  private int id;
  private String riderName;
  private int riderYearOfBirth;
  private int teamId;

  private Long totalAdjustedTime = 0L; /* Total time elapsed, adjusted for if a rider finishes
                                        within a second of the rider ahead */
  private Long totalElapsedTime = 0L; // Total time elapsed


  private int totalPoints = 0; // Total points rider has accumulated
  private int totalMountainPoints = 0; // Total Mountain points rider has accumulated

  private static int numberofRiders = 0; // Number of riders in the portal.

  private LinkedList<RiderStageResults> riderResultsList = 
      new LinkedList<RiderStageResults>();   /* Linked list of rider's results in all stages */


  /**
   * Constructor for the rider class.
   *
   * @param riderYearOfBirth Year of birth of the rider
   * @param riderName Name of the rider
   * @param teamId ID of the team the rider belongs to
   */
  public Rider(int riderYearOfBirth, String riderName, int teamId) {
    this.riderYearOfBirth = riderYearOfBirth;
    this.riderName = riderName;
    this.teamId = teamId;
    id = ++numberofRiders;
    super.setId(id);
  }

  /**
   * Adds the rider's StageResult object for the corresponding stage to a LinkedList of all 
   * the rider's stage results.
   *
   * @param rider Instace of the RiderStageResults object to be added to the LinkedList.
   */
  public void addStageResults(RiderStageResults rider) {
    riderResultsList.add(rider);
  }

  /**
   * Adds earnt points to the rider's total points.
   *
   * @param points The number of points to be added to the total points
   */
  public void addTotalPoints(int points) {
    totalPoints += points;
  }

  /**
   * Adds earnt mountain points to the rider's total mountain points.
   *
   * @param points The number of mountain points to be added to the total mountain points.
   */
  public void addTotalMountainPoints(int points) {
    totalMountainPoints += points;
  }

  /**
   * Adds adjusted time taken in specific stage for rider.
   *
   * @param time The time taken in specific stage for rider.
   */
  public void addTotalAdjustedTime(Long time) {
    totalAdjustedTime += time;
  }

  /**
   * Adds time taken in specific stage for rider.
   *
   * @param time The time taken in specific stage for rider.
   */
  public void addTotalElapsedTime(Long time) {
    totalElapsedTime += time;
  }


  /**
   * Gets ID of the team the rider belongs to.
   *
   * @return The ID of the team the rider belongs to.
   */
  public int getTeamId() {
    return teamId;
  }

  /**
   * Gets total time taken by rider.
   *
   * @return total time taken by rider
   */
  public Long getTotalElapsedTime() {
    return totalElapsedTime;
  }

  /**
   * Gets total adjusted time taken by rider.
   *
   * @return total adjusted time taken by rider
   */
  public Long getTotalAdjustedTime() {
    return totalAdjustedTime;
  }

  /**
   * Gets all the RiderStageResults objects belonging to the rider.
   *
   * @return LinkedList of all RiderStageResults objects belonging to the rider
   */
  public LinkedList<RiderStageResults> getRiderResultsList() {
    return riderResultsList;
  }

  /**
   * Resets the total adjusted time for the rider to 0.
   */
  public void resetTotalAdjustedTime() {
    totalAdjustedTime = 0L;
  }

  /**
   * Resets the total elapsed time for the rider to 0.
   */
  public void resetTotalElapsedTime() {
    totalElapsedTime = 0L;
  }

  /**
   * Reset the total points for the rider to 0.
   */
  public void resetTotalPoints() {
    totalPoints = 0;
  }

  /**
   * Reset the total mountain points for the rider to 0.
   */
  public void resetTotalMountainPoints() {
    totalMountainPoints = 0;
  }

  /**
   * Gets total points for rider.
   *
   * @return Total points for the rider
   */
  public int getTotalPoints() {
    return totalPoints;
  }

  /**
   * Gets total mountain points for rider.
   *
   * @return total mountain points for rider
   */
  public int getTotalMountainPoints() {
    return totalMountainPoints;
  }

  /**
   * Reset the number of riders, used when erasing the cycling portal to reset to an empty state.
   */
  public static void resetIdCounter() {
    numberofRiders = 0;
  }
}
