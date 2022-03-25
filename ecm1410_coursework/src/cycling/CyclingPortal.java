package cycling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * CyclingPortal is an implementor of the CyclingPortalInterface interface.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 *
 */
public class CyclingPortal implements CyclingPortalInterface {

  //Array of segment points to be awarded depending on position (and type)
  private static final Integer[] SPRINT_SEGMENT_POINTS = 
    {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
  private static final Integer[] HC_SEGMENT_POINTS = 
    {20, 15, 12, 10, 8, 6, 4, 2};
  private static final Integer[] C1_SEGMENT_POINTS = 
    {10, 8, 6, 4, 2, 1};
  private static final Integer[] C2_SEGMENT_POINTS = 
    {5, 3, 2, 1};
  private static final Integer[] C3_SEGMENT_POINTS = 
    {2, 1};
  private static final Integer[] C4_SEGMENT_POINTS =
    {1};

  //Array of stage points to be awarded depending on position (and type)

  private static final Integer[] FLAT_STAGE_POINTS = 
    {50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2};
  private static final Integer[] MM_STAGE_POINTS = 
    {30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2};
  private static final Integer[] HM_STAGE_POINTS = 
    {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5,  4, 3, 2, 1};
  private static final Integer[] TT_STAGE_POINTS = 
    {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

  private LinkedList<Rider> riderList = new LinkedList<Rider>(); // List of riders

  private LinkedList<Team> teamList = new LinkedList<Team>(); // List of teams

  private LinkedList<Segment> segmentList = new LinkedList<Segment>();  // List of segments

  private LinkedList<Stage> stageList = new LinkedList<Stage>(); // List of stages

  private LinkedList<Race> raceList = new LinkedList<Race>(); // List of races

  private LinkedList<RiderStageResults> riderStageResultsList = 
      new LinkedList<RiderStageResults>(); // List of rider stage results


  /**
   * Given an ID to search for, and a list of objects (i.e. riders) searches through the list
   * and either returns the object with that ID, or null if no such object exists.
   *
   * @param id ID of object to find
   * @param objectList List of objects to search through
   * @return The object with that ID, or null if no such object exists
   */
  private <T extends IdHaver> T correspondingObjectFinder(int id, LinkedList<T> objectList, 
      String objectType)  throws IDNotRecognisedException {
    T correspondingObject = null;
    for (T object : objectList) {
      if (id == object.getId()) {
        correspondingObject = object;
        break;
      }
    }
    if (correspondingObject == null) {
      throw new IDNotRecognisedException(objectType + " ID " + id
        + " not recognised in the system.");
    }
    return correspondingObject;
  }

  /**
   * Will throw an InvalidNameException, if the object name is null, empty,
   * has more than 30 characters, or has whitespaces.
   *
   * @param name Proposed name of a team/stage/race
   * @param objectType Denotes whether it is a team/stage/race.
   */
  private void validNameChecker(String name, String objectType) throws InvalidNameException {
    if (name == null || name == "" || name.length() > 30 || name.contains(" ")) {
      throw new InvalidNameException(objectType
        +  " name is null, empty, has more than 30 characters, or has whitespaces");
    }
  }

  /**
   * Will throw an InvalidStageStateException if the stage is waiting for results.
   *
   * @param stageState String of the current stage state
   */
  private void validStageStateChecker(String stageState) throws InvalidStageStateException {
    if (stageState == "waiting for results") {
      throw new InvalidStageStateException("Stage preparation has been concluded.");
    }
  }

  /**
   * Goes through each RiderStageResult object associated with the rider and passes this to
   * deleteRiderResult which will delete the results.
   *
   * @param rider Object of the rider who's results are being deleted.
   */
  private void deleteAllRiderResults(Rider rider) {
    assert rider != null;
    LinkedList<RiderStageResults> riderResultsList = 
        new LinkedList<RiderStageResults>(rider.getRiderResultsList());
    for (RiderStageResults riderStageResults : riderResultsList) {
      deleteRiderResult(riderStageResults);
    }
  }

  /**
   * Deletes a riderStageResults object, removes all references to it.
   *
   * @param riderStageResults rider result object to be deleted.
   */
  private void deleteRiderResult(RiderStageResults riderStageResults) {
    riderStageResults.getStage().getRiderResultsList().remove(riderStageResults);
    riderStageResults.getRider().getRiderResultsList().remove(riderStageResults);
    riderStageResultsList.remove(riderStageResults);
  }

  /**
   * Deletes all the results in a given stage.
   *
   * @param stage Stage for results to be deleted within.
   */
  private void deleteAllStageResults(Stage stage) {
    LinkedList<RiderStageResults> riderResultsList 
        = new LinkedList<RiderStageResults>(stage.getRiderResultsList());
    for (RiderStageResults riderStageResults : riderResultsList) {
      deleteRiderResult(riderStageResults);
    }
  }

  /**
   * Deletes a Team, all references to it, and all of its riders.
   *
   * @param team Team to be deleted.
   */
  private void deleteTeam(Team team) {
    assert team != null;
    teamList.remove(team.getId());
    LinkedList<Rider> riders = new LinkedList<Rider>(team.getRiders());
    for (Rider rider : riders) {
      deleteRider(rider, team);
    }
    team = null;
  }

  /**
   * Deletes a Rider, all references to them, and all of their corresponding results.
   *
   * @param rider Rider to be deleted.
   * @param team Team the rider belongs to.
   */
  private void deleteRider(Rider rider, Team team) {
    assert rider != null;
    riderList.remove(rider);
    deleteAllRiderResults(rider);
    team.removeRider(rider);
  }

  /**
   * Deletes a race, all references to it, and all of its corresponding results.
   *
   * @param race Race to be deleted.
   */
  private void deleteRace(Race race) {
    assert race != null;
    raceList.remove(race);
    LinkedList<Stage> raceStages = new LinkedList<Stage>(race.getStages());
    for (Stage stage : raceStages) {
      deleteStage(stage, race);
    }
    race = null;
  }

  /**
   * Deletes a stage, all references to it, and all of its corresponding results.
   *
   * @param stage Stage to be deleted.
   * @param race Race the stage belongs to.
   */
  private void deleteStage(Stage stage, Race race) {
    assert stage != null;
    stageList.remove(stage);
    race.removeStage(stage);
    LinkedList<Segment> stageSegments = new LinkedList<Segment>(stage.getSegments());
    for (Segment segment : stageSegments) {
      deleteSegment(segment, stage);
    }
    deleteAllStageResults(stage);
    stage = null;
  }

  /**
   * Removes a segment from a given stage.
   *
   * @param segment Segment to be deleted.
   * @param stage Stage the segment belongs to.
   */
  private void deleteSegment(Segment segment, Stage stage) {
    segmentList.remove(segment);
    stage.removeSegment(segment);
    segment = null;
  }

  /**
   * Sorts a list of riders results by their elapsed time attribute.
   *
   * @param competingRiders List of ridersResults in a stage.
   */
  private void sortRidersByElapsedTime(LinkedList<RiderStageResults> competingRiders) {
    competingRiders.sort(Comparator.comparing((RiderStageResults rider) 
        -> rider.getElapsedTimeForStage()));
  }

  /**
   * Adjusts all riders times in a stage. If one finishes within one second of another,
   * their time is bumped to the lowest of the two. This cascades all the way down.
   *
   * @param competingRiders List of rider results in the stage.
   */
  private void adjustRiderTimesInStage(LinkedList<RiderStageResults> competingRiders) {
    sortRidersByElapsedTime(competingRiders);
    competingRiders.get(0).setAdjustedTimeForStage(
        competingRiders.get(0).getElapsedTimeForStage());

    for (int i = 0; i < competingRiders.size() - 1; i++) {
      if (competingRiders.get(i + 1).getElapsedTimeForStage() 
          - competingRiders.get(i).getElapsedTimeForStage() < 1000_000_000L) {
        competingRiders.get(i + 1).setAdjustedTimeForStage(
            competingRiders.get(i).getAdjustedTimeForStage());
      } else {
        competingRiders.get(i + 1).setAdjustedTimeForStage(
            competingRiders.get(i + 1).getElapsedTimeForStage());
      }
    }
  }


  /**
   * This function appends the 0's to the pointsToBeAdded array, depending on the number of
   * riders in a given stage/segment.
   *
   * @param numRiders number of riders in the stage/segment.
   * @param rankPoints Array of points that index's match the position in a segment/race,
      and the points match the points awarded to those positions.
   * @return number of points to add.
   */
  private LinkedList<Integer> pointsToBeAddedFormatter(int numRiders, Integer[] rankPoints) {
    int rankPointsSize = rankPoints.length;
    LinkedList<Integer> pointsToBeAdded 
        = new LinkedList<Integer>(Arrays.asList(rankPoints));
    if (numRiders > rankPointsSize) {
      int sizeDifference = numRiders - rankPointsSize;
      for (int i = 0; i < sizeDifference; i++) {
        pointsToBeAdded.add(0);
      }
    }
    return pointsToBeAdded;
  }

  /**
   * Awards each rider in a segment their segment points, given their position and the
   * type of segment.
   * Mountain or sprint segments are decided by the boolean variable isSprintSegment
   *
   * @param competingRiders List of riders who competed in the segment.
   * @param segment The segment we want to award points within.
   * @param stageSegments List of all segments in the stage the segment is in.
   * @param segmentPointsToBeAdded List of segment points to be added,
       the index corresponds to position
   * @param isSprintSegment Do we want to award a sprint segment or a mountain segment?
   */
  private void awardSegmentPoints(LinkedList<RiderStageResults> competingRiders, Segment segment, 
      LinkedList<Segment> stageSegments, LinkedList<Integer> segmentPointsToBeAdded, 
      boolean isSprintSegment) {
    int indexForSegment = stageSegments.indexOf(segment);

    LinkedList<RiderStageResults> ridersInSegment 
        = new LinkedList<RiderStageResults>(competingRiders);
    ridersInSegment.sort(Comparator.comparing((RiderStageResults rider) 
        -> rider.getSegmentTime(indexForSegment)));

    for (RiderStageResults rider : ridersInSegment) {
      int indexForPoints = ridersInSegment.indexOf(rider);
      int points = segmentPointsToBeAdded.get(indexForPoints);
      if (isSprintSegment) {
        rider.addPoints(points);
      } else {
        rider.addMountainPoints(points);
      }
    }
    
  }

  /**
   * Awards all the riders in a given stage their (sprint) points. Points are awarded to their
   * Corresponding riderStageResults object.
   *
   * @param stage The stage to award (sprint) points within.
   */
  private void awardPointsInStage(Stage stage) {
    LinkedList<RiderStageResults> riderResultsList 
        = new LinkedList<RiderStageResults>(stage.getRiderResultsList());

    LinkedList<Integer> pointsToBeAdded = new LinkedList<Integer>();
    sortRidersByElapsedTime(riderResultsList);

    for (RiderStageResults riderStageResults : riderResultsList) {
      riderStageResults.resetPoints();
    }

    int riderResultsListSize = riderResultsList.size();
    StageType stageType = stage.getType();
    switch (stageType) {
      case FLAT: 
        pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, FLAT_STAGE_POINTS);
        break;
      case MEDIUM_MOUNTAIN: 
        pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, MM_STAGE_POINTS);
        break;
      case HIGH_MOUNTAIN: 
        pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, HM_STAGE_POINTS);
        break;
      case TT: 
        pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, TT_STAGE_POINTS);
        break;
      default: assert false;
    }

    for (RiderStageResults riderStageResults : riderResultsList) {
      int indexForPoints = riderResultsList.indexOf(riderStageResults);
      int points = pointsToBeAdded.get(indexForPoints);
      riderStageResults.setPoints(points);
    }
    LinkedList<Segment> segments = new LinkedList<Segment>(stage.getSegments());
    LinkedList<Integer> segmentPointsToBeAdded = new LinkedList<Integer>();
    for (Segment segment : segments) {
      if (segment.getSegmentType() == SegmentType.SPRINT) {
        segmentPointsToBeAdded 
            = pointsToBeAddedFormatter(riderResultsList.size(), SPRINT_SEGMENT_POINTS);
        awardSegmentPoints(riderResultsList, segment, segments, segmentPointsToBeAdded, true);
      }
    }
  }


  /**
   * Awards all the riders in a given stage their mountain points. Points are awarded to their
   * Corresponding riderStageResults object.
   *
   * @param stage The stage to award mountain points within.
   */
  private void awardMountainPointsInStage(Stage stage) {
    LinkedList<RiderStageResults> riderResultsList 
        = new LinkedList<RiderStageResults>(stage.getRiderResultsList());
    LinkedList<Integer> pointsToBeAdded = new LinkedList<Integer>();
    sortRidersByElapsedTime(riderResultsList);

    for (RiderStageResults riderStageResults : riderResultsList) {
      riderStageResults.resetMountainPoints();
    }

    LinkedList<Segment> segments = new LinkedList<Segment>(stage.getSegments());

    for (Segment segment : segments) {
      SegmentType segmentType = segment.getSegmentType();
      int riderResultsListSize = riderResultsList.size();
      if (!(segmentType == SegmentType.SPRINT)) {
        switch (segmentType) {
          case C1: 
            pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C1_SEGMENT_POINTS);
            break;
          case C2: 
            pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C2_SEGMENT_POINTS);
            break;
          case C3: 
            pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C3_SEGMENT_POINTS);
            break;
          case C4: 
            pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C4_SEGMENT_POINTS);
            break;
          case HC: 
            pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, HC_SEGMENT_POINTS);
            break;
          default: assert false;
        }
        awardSegmentPoints(riderResultsList, segment, segments, pointsToBeAdded, false);
      }
    }
  }

  /**
   * Awards every rider in a given race their total Points classification points.
   *
   * @param race The specified race to sum total (sprint) points within
   * @return Returns the list of riders in the given race
       now with their awarded total (sprint) points.
   */
  private LinkedList<Rider> totalRidersPoints(Race race) {
    LinkedList<Rider> riders = new LinkedList<Rider>();
    for (Rider rider : riderList) {
      rider.resetTotalElapsedTime();
      rider.resetTotalPoints();
    }

    for (Stage stage : race.getStages()) {
      awardPointsInStage(stage);
      for (RiderStageResults riderStageResults : stage.getRiderResultsList()) {
        Rider rider = riderStageResults.getRider();
        rider.addTotalElapsedTime(riderStageResults.getElapsedTimeForStage());
        rider.addTotalPoints(riderStageResults.getRiderPoints());
        if (!riders.contains(rider)) {
          riders.add(rider);
        }
      }
    }
    return riders;
  }

  /**
   * Awards every rider in a given race their total Mountain classification points.
   *
   * @param race The specified race to sum total mountain points within
   * @return Returns the list of riders in the given race, 
       now with their total mountain points awarded
   */
  private LinkedList<Rider> totalRidersMountainPoints(Race race) {
    LinkedList<Rider> riders = new LinkedList<Rider>();
    for (Rider rider : riderList) {
      rider.resetTotalElapsedTime();
      rider.resetTotalMountainPoints();
    }
    for (Stage stage : race.getStages()) {
      awardMountainPointsInStage(stage);
      for (RiderStageResults riderStageResults : stage.getRiderResultsList()) {
        Rider rider = riderStageResults.getRider();
        rider.addTotalElapsedTime(riderStageResults.getElapsedTimeForStage());
        rider.addTotalMountainPoints(riderStageResults.getRiderMountainPoints());
        if (!riders.contains(rider)) {
          riders.add(rider);
        }
      }
    }
    return riders;
  }


  /**
   * Sorts riders by their total elapsed time.
   *
   * @param riders List of riders to be sorted.
   */
  private void sortByTotalElapsedTime(LinkedList<Rider> riders) {
    riders.sort(Comparator.comparing((Rider rider) -> rider.getTotalElapsedTime()));
  }

  /**
   * Sorts riders by their total adjusted time.
   *
   * @param riders List of riders to be sorted.
   */
  private void sortByTotalAdjustedTime(LinkedList<Rider> riders) {
    riders.sort(Comparator.comparing((Rider rider) -> rider.getTotalAdjustedTime()));
  }

  /**
   * Adjusts all rider times within a specified race, and returns them.
   *
   * @param race The specified race.
   * @return A list of riders who competed in the race, sorted by
     their total adjusted time.
   */
  private LinkedList<Rider> ridersTotalAdjustedTime(Race race) {

    LinkedList<Rider> riders = new LinkedList<Rider>();
    for (Rider rider : riderList) {
      rider.resetTotalAdjustedTime();
    }
    for (Stage stage : race.getStages()) {
      LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
      if (riderResultsList.size() == 0) {
        break;
      }
      adjustRiderTimesInStage(riderResultsList);
      for (RiderStageResults riderStageResults : riderResultsList) {
        Rider rider = riderStageResults.getRider();
        rider.addTotalAdjustedTime(riderStageResults.getAdjustedTimeForStage());
        if (!riders.contains(rider)) {
          riders.add(rider);
        }
      }
    }
    sortByTotalAdjustedTime(riders);
    return riders;
  }

  @Override
  public int[] getRaceIds() {
    List<Integer> raceIds = new LinkedList<Integer>();
    for (Race race : raceList) {
      raceIds.add(race.getId());
    }
    return raceIds.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int createRace(String name, String description) 
      throws IllegalNameException, InvalidNameException {
    validNameChecker(name, "Race");
    for (Race race : raceList) {
      if (name == race.getName()) {
        throw new IllegalNameException("Race name already exists in the platform.");
      }
    }
    Race newRace = new Race(name, description);
    raceList.add(newRace);
    return newRace.getId();
  }

  @Override
  public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    String details;
    String name = race.getName();
    String description = race.getDescription();
    Integer numberOfStages = race.getStages().size();
    Double totalLength = race.totalLength();

    details = "Race ID: " + raceId + ", Race Name: " + name + ", Race Description: " 
        + description + ", Number of stages: " + numberOfStages + ", Total Length: " + totalLength;
    return details;
  }

  @Override
  public void removeRaceById(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    deleteRace(race);
  }

  @Override
  public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    return race.getStages().size();
  }

  @Override
  public int addStageToRace(int raceId, String stageName, String description, double length, 
      LocalDateTime startTime, StageType type) throws IDNotRecognisedException, 
      IllegalNameException, InvalidNameException, InvalidLengthException {
    validNameChecker(stageName, "Stage");
    if (length < 5D) {
      throw new InvalidLengthException("Length is less than 5km");
    }
    for (Stage stage : stageList) {
      if (stageName == stage.getStageName()) {
        throw new IllegalNameException("Stage name already exists in the platform.");
      }
    }
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    Stage newStage = new Stage(raceId, stageName, description, length, startTime, type);
    race.addStage(newStage);
    stageList.add(newStage);
    return newStage.getId();
  }

  @Override
  public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    List<Integer> stageIds = new LinkedList<Integer>();
    for (Stage stage : race.getStages()) {
      stageIds.add(stage.getId());
    }
    return stageIds.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public double getStageLength(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    return stage.getLength();
  }

  @Override
  public void removeStageById(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    Race raceContainingStage = correspondingObjectFinder(stage.getRaceId(), raceList, "Race");
    deleteStage(stage, raceContainingStage);
  }

  @Override
  public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, 
      Double averageGradient, Double length) throws IDNotRecognisedException, 
      InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    validStageStateChecker(stage.getStageState());
    if (stage.getType() == StageType.TT) {
      throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");
    }
    if (location > stage.getLength()) {
      throw new InvalidLocationException("Segment location is out of bounds of the stage length.");
    }

    Segment newClimb = new ClimbSegment(stageId, type, location, averageGradient, length);
    stage.addSegment(newClimb);
    segmentList.add(newClimb);
    return newClimb.getId();
  }

  @Override
  public int addIntermediateSprintToStage(int stageId, double location) 
      throws IDNotRecognisedException, InvalidLocationException, 
      InvalidStageStateException, InvalidStageTypeException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    validStageStateChecker(stage.getStageState());
    if (stage.getType() == StageType.TT) {
      throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");
    }
    if (location > stage.getLength()) {
      throw new InvalidLocationException("Segment location is out of bounds of the stage length.");
    }

    Segment newSprint = new Segment(stageId, SegmentType.SPRINT, location);
    stage.addSegment(newSprint);
    segmentList.add(newSprint);
    return newSprint.getId();
  }

  @Override
  public void removeSegment(int segmentId) throws IDNotRecognisedException, 
      InvalidStageStateException {
    Segment segment = correspondingObjectFinder(segmentId, segmentList, "Segment");
    Stage stageContainingSegment = correspondingObjectFinder(segment.getStageId(),
          stageList, "Stage");
    validStageStateChecker(stageContainingSegment.getStageState());
    deleteSegment(segment, stageContainingSegment);
  }

  @Override
  public void concludeStagePreparation(int stageId) throws 
      IDNotRecognisedException, InvalidStageStateException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    validStageStateChecker(stage.getStageState());
    stage.concludeStageState();
  }

  @Override
  public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    List<Integer> segmentIds = new LinkedList<Integer>();
    for (Segment segment : stage.getSegments()) {
      segmentIds.add(segment.getId());
    }
    return segmentIds.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int createTeam(String name, String description) 
      throws IllegalNameException, InvalidNameException {
    validNameChecker(name, "Team");
    for (Team team : teamList) { 
      if (name == team.getTeamName()) {
        throw new IllegalNameException("Team name already exists in the platform");
      }
    }
    Team newTeam = new Team(name, description);
    teamList.add(newTeam);
    return newTeam.getId();
  }

  @Override
  public void removeTeam(int teamId) throws IDNotRecognisedException {
    Team team = correspondingObjectFinder(teamId, teamList, "Team");
    deleteTeam(team);
  }

  @Override
  public int[] getTeams() {
    List<Integer> teamIds = new LinkedList<Integer>();
    for (Team team : teamList) {
      teamIds.add(team.getId());
    }
    return teamIds.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
    List<Rider> ridersInTeam = new LinkedList<Rider>();
    List<Integer> teamRidersIds = new LinkedList<Integer>();
    Team team = correspondingObjectFinder(teamId, teamList, "Team");
    ridersInTeam = team.getRiders();
    for (Rider rider : ridersInTeam) {
      teamRidersIds.add(rider.getId());
    }
    return teamRidersIds.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int createRider(int teamId, String name, int yearOfBirth)
      throws IDNotRecognisedException, IllegalArgumentException {
    if (yearOfBirth < 1900 || name == null) {
      throw new IllegalArgumentException(
          "Name of rider is null or year of birth is less than 1900");
    }
    Team team = correspondingObjectFinder(teamId, teamList, "Team");
    Rider newRider = new Rider(yearOfBirth, name, teamId);
    riderList.add(newRider);
    team.addRider(newRider);
    return newRider.getId();
  }

  @Override
  public void removeRider(int riderId) throws IDNotRecognisedException {
    Rider rider = correspondingObjectFinder(riderId, riderList, "Rider");
    Team teamContainingRider = correspondingObjectFinder(rider.getTeamId(), teamList, "Team");
    deleteRider(rider, teamContainingRider);
  }

  @Override
  public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
      throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException,
      InvalidStageStateException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    if (!(stage.getStageState() == "waiting for results")) {
      throw new InvalidStageStateException("Stage has not concluded preparation.");
    }

    for (RiderStageResults riderStageResults : stage.getRiderResultsList()) {
      if (riderStageResults.getRider().getId() == riderId) {
        throw new DuplicatedResultException(
          "Rider has a result for the stage. A rider can have only one result per stage.");
      }
    }

    if (!(checkpoints.length == stage.getSegments().size() + 2)) {
      throw new InvalidCheckpointsException(
        "The number checkpoint times don't match the number of segments (+2)");
    }

    Rider rider = correspondingObjectFinder(riderId, riderList, "Rider");
    RiderStageResults riderStageResults = new RiderStageResults(rider, stage, checkpoints);
    stage.addRiderResultToStage(riderStageResults);
    riderStageResultsList.add(riderStageResults);
    rider.addStageResults(riderStageResults);
  }


  /**
   * Converts a time in nanoseconds into the h/m/s/nanoseconds LocalTime format.
   *
   * @param nanoseconds The time in nanoseconds to be converted.
   */
  private LocalTime nanoToLocalTime(Long nanoseconds) {
    assert nanoseconds != null;
    int second = (int) (nanoseconds / 1000_000_000); 
    int minute = (int) (second / 60);
    int hour = (int) (minute / 60);

    nanoseconds %= 1000_000_000;
    hour %= 60;
    minute %= 60;
    second %= 60;
    LocalTime time = LocalTime.of(hour, minute, second, nanoseconds.intValue());
    return time;
  }

  @Override
  public LocalTime[] getRiderResultsInStage(int stageId, int riderId) 
      throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    Rider rider = correspondingObjectFinder(riderId, riderList, "Rider");
    LinkedList<Long> times = new LinkedList<Long>();
    LinkedList<LocalTime> results = new LinkedList<LocalTime>();

    for (RiderStageResults riderStageResults : rider.getRiderResultsList()) {
      if (riderStageResults.getStage() == stage) {
        for (Long segmentTime : riderStageResults.getSegmentTimes()) {
          times.add(segmentTime + riderStageResults.getStartTime());
        }
        times.add(riderStageResults.getElapsedTimeForStage());
        break;
      }
    }
    for (Long time : times) {
      results.add(nanoToLocalTime(time));
    }
    return results.toArray(new LocalTime[times.size()]);
  }

  @Override
  public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) 
      throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    Rider rider = correspondingObjectFinder(riderId, riderList, "Rider");

    LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
    adjustRiderTimesInStage(riderResultsList);
    LocalTime adjustedTime = null;

    for (RiderStageResults riderStageResults : riderResultsList) {
      if (riderStageResults.getRider() == rider) {
        adjustedTime = nanoToLocalTime(riderStageResults.getAdjustedTimeForStage());
        break;
      }
    }
    return adjustedTime;
  }

  @Override
  public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {

    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");
    Rider rider = correspondingObjectFinder(riderId, riderList, "Rider");

    LinkedList<RiderStageResults> riderResultsList 
        = new LinkedList<RiderStageResults>(stage.getRiderResultsList());

    for (RiderStageResults riderStageResults : riderResultsList) {
      if (riderStageResults.getRider() == rider) {
        deleteRiderResult(riderStageResults);
        break;
      }
    }
  }

  @Override
  public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
    int[] riderIds = new int[riderResultsList.size()];
    sortRidersByElapsedTime(riderResultsList);

    for (int i = 0; i < riderResultsList.size(); i++) {
      riderIds[i] = riderResultsList.get(i).getRider().getId();
    }
    return riderIds;
  }

  @Override
  public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId)
      throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
    LocalTime[] localTimes = new LocalTime[riderResultsList.size()];
    adjustRiderTimesInStage(riderResultsList);
    for (int i = 0; i < riderResultsList.size(); i++) {
      localTimes[i] = nanoToLocalTime(riderResultsList.get(i).getAdjustedTimeForStage());
    }
    return localTimes;
  }

  @Override
  public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    awardPointsInStage(stage);
    LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();

    int[] riderPoints = new int[riderResultsList.size()];
    sortRidersByElapsedTime(riderResultsList);

    for (int i = 0; i < riderResultsList.size(); i++) {
      riderPoints[i] = riderResultsList.get(i).getRiderPoints();
    }
    return riderPoints;
  }

  @Override
  public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = correspondingObjectFinder(stageId, stageList, "Stage");

    awardMountainPointsInStage(stage);
    LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();

    int[] riderMountainPoints = new int[riderResultsList.size()];
    sortRidersByElapsedTime(riderResultsList);

    for (int i = 0; i < riderResultsList.size(); i++) {
      riderMountainPoints[i] = riderResultsList.get(i).getRiderMountainPoints();
    }
    return riderMountainPoints;
  }

  @Override
  public void eraseCyclingPortal() {
    Rider.resetIdCounter();
    riderList.clear();

    Team.resetIdCounter();
    teamList.clear();


    Race.resetIdCounter();
    raceList.clear();

    Stage.resetIdCounter();
    stageList.clear();

    Segment.resetIdCounter();
    segmentList.clear();

    riderStageResultsList.clear();
  }

  @Override
  public void saveCyclingPortal(String filename) throws IOException {
    if (!filename.endsWith(".ser")) {
      filename += ".ser";
    }
    File file = new File(filename);
    if (file.exists() && !file.isDirectory()) {
      file.delete();
    }
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
      oos.writeObject(riderList);
      oos.writeObject(teamList);
      oos.writeObject(raceList);
      oos.writeObject(stageList);
      oos.writeObject(segmentList);
      oos.writeObject(riderStageResultsList);
      System.out.printf("Saved in %s%n", filename);
      oos.close();
    } catch (IOException e) {
      throw new IOException("Failed to save contents to file");
    }
  }

  @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
    eraseCyclingPortal();
    if (!filename.endsWith(".ser")) {
      filename += ".ser";
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
      Object obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof Rider) {
          riderList = (LinkedList<Rider>) obj;
        }
      }
      obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof Team) {
          teamList = (LinkedList<Team>) obj;
        }
      }
      obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof Race) {
          raceList = (LinkedList<Race>) obj;
        }
      }
      obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof Stage) {
          stageList = (LinkedList<Stage>) obj;
        }
      }
      obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof Segment) {
          segmentList = (LinkedList<Segment>) obj;
        }
      }
      obj = ois.readObject();
      if (obj instanceof LinkedList<?>) {
        if (((LinkedList<?>) obj).get(0) instanceof RiderStageResults) {
          riderStageResultsList = (LinkedList<RiderStageResults>) obj;
        }
      }
    } catch (IOException e) {
      throw new IOException("Failed to load contents from file");
    } catch (ClassNotFoundException e) {
      throw new ClassNotFoundException("Required class files not found");
    }
  }


  @Override
  public void removeRaceByName(String name) throws NameNotRecognisedException {
    Race race = null;
    for (Race raceElement : raceList) {
      if (raceElement.getName().equals(name)) {
        race = raceElement;
        break;
      }
    }
    if (race == null) {
      throw new NameNotRecognisedException(
        "The given Race name does not match to any race in the system");
    }
    deleteRace(race);
  }

  @Override
  public LocalTime[] getGeneralClassificationTimesInRace(int raceId) 
      throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    LinkedList<Rider> riders = ridersTotalAdjustedTime(race);
    LocalTime[] localTimes = new LocalTime[riders.size()];
    for (int i = 0; i < riders.size(); i++) {
      localTimes[i] = nanoToLocalTime(riders.get(i).getTotalAdjustedTime());
    }
    return localTimes;
  }

  @Override
  public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    LinkedList<Rider> riders = totalRidersPoints(race);
    sortByTotalElapsedTime(riders);
    int[] riderPoints = new int[riders.size()];
    for (int i = 0; i < riders.size(); i++) {
      riderPoints[i] = riders.get(i).getTotalPoints();
    }
    return riderPoints;
  }

  @Override
  public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    LinkedList<Rider> riders = totalRidersMountainPoints(race);
    sortByTotalElapsedTime(riders);
    int[] riderMountainPoints = new int[riders.size()];
    for (int i = 0; i < riders.size(); i++) {
      riderMountainPoints[i] = riders.get(i).getTotalMountainPoints();
    }
    return riderMountainPoints;
  }

  @Override
  public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");

    LinkedList<Rider> riders = ridersTotalAdjustedTime(race);

    int[] riderIds = new int[riders.size()];

    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = riders.get(i).getId();
    }
    return riderIds;
  }

  /**
   * Sorts the list of riders by their total (sprint) points.
   *
   * @param riders List of riders to be sorted.
   */
  private void sortByTotalPoints(LinkedList<Rider> riders) {
    riders.sort(Comparator.comparing((Rider rider) -> (rider.getTotalPoints() * -1)));
  }

  @Override
  public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {

    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    LinkedList<Rider> riders = totalRidersPoints(race);
    sortByTotalPoints(riders);
    int[] riderPointsId = new int[riders.size()];
    for (int i = 0; i < riders.size(); i++) {
      riderPointsId[i] = riders.get(i).getId();
    }
    return riderPointsId;
  }

  /**
   * Sorts the list of riders by their total mountain points.
   *
   * @param riders List of riders to be sorted.
   */
  private void sortByTotalMountainPoints(LinkedList<Rider> riders) {
    riders.sort(Comparator.comparing((Rider rider) -> (rider.getTotalMountainPoints() * -1)));
  }

  @Override
  public int[] getRidersMountainPointClassificationRank(int raceId) 
      throws IDNotRecognisedException {
    Race race = correspondingObjectFinder(raceId, raceList, "Race");
    LinkedList<Rider> riders = totalRidersMountainPoints(race);
    sortByTotalMountainPoints(riders);
    int[] riderMountainPointsId = new int[riders.size()];
    for (int i = 0; i < riders.size(); i++) {
      riderMountainPointsId[i] = riders.get(i).getId();
    }
    return riderMountainPointsId;
  }
}