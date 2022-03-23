package cycling;

import java.io.Serializable;

/**
 * Represents a segment.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 */
public class Segment extends IdHaver implements Serializable {
  private int id;
  private int stageId;
  private Double location;
  private SegmentType type;
  
  private static int numberOfSegments = 0;

  /**
   * Constructor for segment class.
   *
   * @param stageId ID of the stage the segment is a part of
   * @param type The type of segment i.e. Sprint or HC
   * @param location Where in the stage the segment occurs (in kilometers)
   */
  public Segment(int stageId, SegmentType type, double location) {
    this.stageId = stageId;
    this.type = type;
    this.location = location;
    id = ++numberOfSegments;
    super.setId(id);
  }

  public int getId() { 
    return id; 
  }

  public Double getLocation() { 
    return location;
  }

  public SegmentType getSegmentType() { 
    return type; 
  }

  public int getstageId() { 
    return stageId; 
  }

  /**
   * Resets the number of segments to 0, use when erasing cycling portal.
   */
  public static void resetIdCounter() {
    numberOfSegments = 0;
  }
}
