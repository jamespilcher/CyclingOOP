package cycling;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents a race.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class Race extends IdHaver implements Serializable {
  private int id;
  private String name;
  private String description;

  LinkedList<Stage> stages = new LinkedList<Stage>(); // A Linked List of stages within this race
  private static int numberOfRaces = 0; // The number of the races in existence.

  /**
   * Constructor for the Race class.
   *
   * @param name the name of the race
   * @param description a description of the race
   */
  public Race(String name, String description) {
    this.name = name;
    this.description = description;
    id = ++numberOfRaces;
    super.setId(id);
  }

  /**
   * Adds a stage to this race.
   *
   * @param stage The stage object that has been added to this race
   */
  public void addStage(Stage stage) {
    stages.add(stage);
  }
    

  /**
   * Removes a stage from this race.
   *
   * @param stage The stage object that has been removed from this race
   */
  public void removeStage(Stage stage) {
    stages.remove(stage);
  }

  /**
   * Calculates and returns the total length of the race.
   *
   * @return The total length of this race, calculated by summing the 
   *         length of each stage in the race.
   */
  public double totalLength() { 
    double totLength = 0D;
    for (Stage stage : stages) {
      totLength += stage.getLength();
    }
    return totLength;
  }

  /**
   * Sorts and returns all stages ordered by their location in the race.
   *
   * @return An LinkedList of stages sorted by their location in the race.
   */
  public LinkedList<Stage> getStages() {
    //sort them here
    stages.sort((o1, o2)
        -> o1.getStartTime().compareTo(
        o2.getStartTime()));
    return stages; 
  }
  
  /**
   * Gets the ID of the race.
   *
   * @return the ID of the race
   */
  public int getId() { 
    return id;
  }
  
  /**
   * Gets the name of the race.
   *
   * @return the name of the race
   */
  public String getName() { 
    return name; 
  }

  /**
   * Gets the description of the race.
   *
   * @return the description of the race
   */
  public String getDescription() { 
    return description; 
  }

  /**
   * Resets the number of races, used when erasing the cycling portal to reset to an empty state.
   */
  public static void resetId() {
    numberOfRaces = 0;
  }
}
