package cycling;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Represents a stage within a race.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 */
public class Stage extends IdHaver implements Serializable {
  private int id;
  private int raceId; // ID of race the stage belongs to
  private StageType type;
  private String stageName;
  private String description;
  private String stageState; // Stage state i.e., waiting for results
  private Double length;
  private LocalDateTime startTime;

  private LinkedList<Segment> segments = new LinkedList<Segment>(); /* Linked list of segments 
  within this stage*/

  private LinkedList<RiderStageResults> riderResultsList = 
      new LinkedList<RiderStageResults>(); // Linked list of rider results in stage

  private static int numberOfStages = 0; // Number of stages in portal.

  /**
   * Constructor for the Stage class.
   *
   * @param raceId ID of the race the stage belongs to.
   * @param stageName Name of the stage
   * @param description Description of the stage
   * @param length Length of the stage in kilometers
   * @param startTime Date and time of the start of the stage
   * @param type The type of the stage (determines how many points winners get)
   */
  public Stage(int raceId, String stageName, String description, double length, 
      LocalDateTime startTime, StageType type) { 
    this.raceId = raceId;
    this.stageName = stageName;
    this.description = description;
    this.length = length;
    this.startTime = startTime;
    this.type = type;
    id = ++numberOfStages;
    stageState = "in preparation";
    super.setId(id);
  }

  /**
   * Adds rider's results in stage to riderResultsList.
   *
   * @param rider Rider object of the corresponding rider 
   */
  public void addRiderResultToStage(RiderStageResults rider) { 
    riderResultsList.add(rider);
  }

  /**
   * Adds a segment to the stage (adds segment object to stage's list of segments).
   *
   * @param segment Segment object to be added.
   */
  public void addSegment(Segment segment) { 
    segments.add(segment);
  }


  /**
   * Removes specified segment object from stage's list of segments.
   *
   * @param segment Segment object to be removed.
   */
  public void removeSegment(Segment segment) { 
    segments.remove(segment);
  }

  /**
   * Removes all segments from the stage's list of segments.
   */
  public void deleteSegments() { 
    segments.clear();
  }

  /**
   * Sorts the list of segments by their position within the stage and returns the sorted list.
   *
   * @return List of segments ordered by their location/poistion in the stage.
   */
  public LinkedList<Segment> getSegments() { 
    segments.sort((o1, o2)
        -> o1.getLocation().compareTo(
        o2.getLocation()));
    return segments;
  }

  public int getRaceId() { 
    return raceId; 
  }

  public StageType getType() { 
    return type;
  }

  public String getStageName() { 
    return stageName;
  }

  public String getDescription() { 
    return description;
  }

  public double getLength() { 
    return length;
  }

  public LocalDateTime getStartTime() { 
    return startTime; 
  }

  public String getStageState() { 
    return stageState; 
  }

  public LinkedList<RiderStageResults> getRiderResultsList() { 
    return riderResultsList; 
  }

  public void concludeStageState() {
    this.stageState = "waiting for results";
  }

  /**
   * Reset the number of stages, used when erasing the cycling portal.
   */
  public static void resetIdCounter() {
    numberOfStages = 0;
  }
}
