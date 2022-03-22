package cycling;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents a team.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class Team extends IdHaver implements Serializable {

  private int id;
  private String teamName;
  private String teamDescription;

  private LinkedList<Rider> riders = 
      new LinkedList<Rider>(); // Linked list of all riders belonging to the team

  private static int numberOfTeams = 0; // number of teams in the portal

  /**
   * Constructor for team class.
   *
   * @param teamName Name of the new team
   * @param teamDescription Description of the new team
   */
  public Team(String teamName, String teamDescription) {
    this.teamName = teamName;
    this.teamDescription = teamDescription;
    id = ++numberOfTeams;
    super.setId(id);
  }

  /**
   * Adds rider object to team's list of riders.
   *
   * @param rider The rider object to be removed.
   */
  public void addRider(Rider rider) {
    riders.add(rider);
  }

  /**
   * Removes a rider object from the team's list of riders.
   *
   * @param rider Rider object to be removed.
   */
  public void removeRider(Rider rider) {
    riders.remove(rider);
  }

  
  public LinkedList<Rider> getRiders() {
    return riders;
  }

  public int getId() {
    return id;
  }

  public String getTeamName() {
    return teamName;
  }

  public String getTeamDescription() {
    return teamDescription;
  }

  /**
   * Reset the number of teams, used when erasing the portal.
   */
  public static void resetIdCounter() {
    numberOfTeams = 0;
  }
}
