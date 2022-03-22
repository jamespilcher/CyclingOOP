package cycling;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a race.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class Race extends IdHaver implements Serializable {
  private int id; // ID associated with this race
  private String name; // Name associated with this race
  private String description; // Description associated with this race

  ArrayList<Stage> stages = new ArrayList<Stage>(); // An arraylist of stages within this race
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
   * @return An ArrayList of stages sorted by their location in the race.
   */
  public ArrayList<Stage> getStages() {
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
  public static void resetIdCounter() {
    numberOfRaces = 0;
  }
}
