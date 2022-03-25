package cycling;

/**
 * Represents a categorised climb.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class ClimbSegment extends Segment {

  private double averageGradient;
  private double length;

  /**
   * Constructor of ClimbSegment class.
   *
   * @param stageId ID of the stage the segment belongs to.
   * @param type the type of segment i.e. Sprint, HC.
   * @param location where within the stage the climb is (in kilometers).
   * @param averageGradient Average gradient of the climb.
   * @param length How long the climb is (in kilometers).
   */
  public ClimbSegment(int stageId, SegmentType type, double location,
      double averageGradient, double length) {
    super(stageId, type, location);
    this.averageGradient = averageGradient;
    this.length = length;
  }
}