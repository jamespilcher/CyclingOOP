package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.Collections;

/**
 * CyclingPortal is an implementor of the CyclingPortalInterface interface.
 * 
 * @authors James Pilcher & Daniel Moulton
 * @version 1.0
 *
 */
public class CyclingPortal implements CyclingPortalInterface {

	//list of riders
    private ArrayList<Rider> riderList=new ArrayList<Rider>();

	//list of teams
    private ArrayList<Team> teamList=new ArrayList<Team>();

	//list of segments
    private ArrayList<Segment> segmentList=new ArrayList<Segment>();
	public ArrayList<Segment> getSegmentList() {return segmentList;}

	//list of stages
    private ArrayList<Stage> stageList=new ArrayList<Stage>();
	public ArrayList<Stage> getStageList() {return stageList;}

	//list of races
    private ArrayList<Race> raceList=new ArrayList<Race>();
	public ArrayList<Race> getRaceList() {return raceList;}

	
    private <T extends IdHaver> T correspondingObjectFinder(int id, ArrayList<T> objectList){
        T correspondingObject = null;
        for (T object : objectList) {
            if (id == object.getId()){
                correspondingObject = object;
                break;
            }
        }
        return correspondingObject;
    }

	private void deleteTeam(Team team){
		teamList.remove(team.getId());
		ArrayList<Rider>riders = new ArrayList<Rider>(team.getRiders());
		for (Rider rider : riders){
			deleteRider(rider, team);
		}
		team = null;
	}

	private void deleteRider(Rider rider, Team team){
		riderList.remove(rider.getId());
		team.removeRider(rider);
		//remove rider's Results
		rider = null;
	}

	private void deleteRace(Race race){
		raceList.remove(race);
		ArrayList<Stage>raceStages = new ArrayList<Stage>(race.getStages());
		for (Stage stage : raceStages) {
			deleteStage(stage, race);
		}
		race = null;
	}
	private void deleteStage(Stage stage, Race race){
		stageList.remove(stage);
		race.removeStage(stage);
		ArrayList<Segment>stageSegments = new ArrayList<Segment>(stage.getSegments());
		for (Segment segment : stageSegments) {
			deleteSegment(segment, stage);
		}
		stage = null;
	}

	private void deleteSegment(Segment segment, Stage stage){
		segmentList.remove(segment);
		stage.removeSegment(segment);
		segment = null;
		}


	@Override
	public int[] getRaceIds() {
		/**
		 * Get the races currently created in the platform.
		 * 
		 * @return An array of race IDs in the system or an empty array if none exists.
		 */
			List<Integer> raceIds = new ArrayList<Integer>();
			for (Race race: raceList) {
				raceIds.add(race.getId());
			}
			return raceIds.stream().mapToInt(i -> i).toArray();
		}
	

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        /**
         * The method creates a staged race in the platform with the given name and
         * description.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         * 
         * @param name        Race's name.
         * @param description Race's description (can be null).
         * @throws IllegalNameException If the name already exists in the platform.
         * @throws InvalidNameException If the name is null, empty, has more than 30
         *                              characters, or has white spaces.
         * @return the unique ID of the created race.
         * 
         */        

        if ( (name == null) || name == "" || name.length() > 30 || name.contains(" ")){
            throw new InvalidNameException("Race name is null, empty, has more than 30 chars, or has whitespaces");
        }  
		//what if array is empty?, did some testing it is fine.
        for (Race race : raceList) {
            if (name == race.getName()){
                throw new IllegalNameException("Race name already exists in the platform.");
            }
        }
        Race newRace = new Race(name, description);
        raceList.add(newRace);
		return newRace.getId();
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
	/**
	 * Get the details from a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return Any formatted string containing the race ID, name, description, the
	 *         number of stages, and the total length (i.e., the sum of all stages'
	 *         length).
	 * @throws IDNotRecognisedException If the ID does not match to any race in the
	 *                                  system.
	 */		

	 // generic idmatch function
	 Race race = correspondingObjectFinder(raceId, raceList);
	 if (race == null){
		 throw new IDNotRecognisedException("The Race ID does not match to any race in the system.");
	 }
	 String details;
	 String name = race.getName();
	 String description = race.getDescription();
	 Integer numberOfStages = race.getStages().size();
	 Double totalLength = race.totalLength();

	 details = String.format("Race ID: %s, Race Name: %s, Race Description: %s, Number of Stages: %s, Total Race Length: %s", raceId, name, description, numberOfStages, totalLength);
	 return details;
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
	/**
	 * The method removes the race and all its related information, i.e., stages,
	 * segments, and results.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race to be removed.
	 * @throws IDNotRecognisedException If the ID does not match to any race in the
	 *                                  system.
	 */
	Race race = correspondingObjectFinder(raceId, raceList);
	if (race == null){
		throw new IDNotRecognisedException("The given Race ID does not match to any race in the system");
	}
	deleteRace(race);
	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		/**
		 * The method queries the number of stages created for a race.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param raceId The ID of the race being queried.
		 * @return The number of stages created for the race.
		 * @throws IDNotRecognisedException If the ID does not match to any race in the
		 *                                  system.
		 */
			Race race = correspondingObjectFinder(raceId, raceList);
			if (race == null) {
				throw new IDNotRecognisedException("Race Id not recognised");
			}
			return race.getStages().size();
		}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
        /**
         * Creates a new stage and adds it to the race.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         * 
         * @param raceId      The race which the stage will be added to.
         * @param stageName   An identifier name for the stage.
         * @param description A descriptive text for the stage.
         * @param length      Stage length in kilometres.
         * @param startTime   The date and time in which the stage will be raced. It
         *                    cannot be null.
         * @param type        The type of the stage. This is used to determine the
         *                    amount of points given to the winner.
         * @return the unique ID of the stage.
         * @throws IDNotRecognisedException If the ID does not match to any race in the
         *                                  system.
         * @throws IllegalNameException     If the name already exists in the platform.
         * @throws InvalidNameException     If the name is null, empty, has more than 30
         *                              	characters, or has white spaces.
         * @throws InvalidLengthException   If the length is less than 5km.
         */	
        //ADD TO RACE OBJECTS LIST.

        if ( (stageName == null) || stageName == "" || stageName.length() > 30 || stageName.contains(" ")){
            throw new InvalidNameException("Stage name is null, empty, has more than 30 chars, or has whitespaces");
        }  
 		if (length < 5D){
			throw new InvalidLengthException("Length is less than 5km");
		}
        for (Stage stage : stageList) {
            if (stageName == stage.getStageName()) {
                throw new IllegalNameException("Stage name already exists in the platform.");
            }
        }
		Race race = correspondingObjectFinder(raceId, raceList);
        if (race == null){
            throw new IDNotRecognisedException("The ID does not match to any race in the system.");
        }
        Stage newStage = new Stage(raceId, stageName, description, length, startTime, type);
        race.addStage(newStage);
        stageList.add(newStage);
        return newStage.getId();
	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {

	/**
	 * Retrieves the list of stage IDs of a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return An array of stage IDs ordered (from first to last) by their sequence in the
	 *         race or an empty array if none exists.
	 * @throws IDNotRecognisedException If the ID does not match to any race in the
	 *                                  system.
	 */

	Race race = correspondingObjectFinder(raceId, raceList);
	if (race == null){
		throw new IDNotRecognisedException("The race ID is not recognised in the system");
	}
	List<Integer> stageIds = new ArrayList<Integer>();
	for (Stage stage : race.getStages()) {
		stageIds.add(stage.getId());
	}
	return stageIds.stream().mapToInt(i->i).toArray();
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		/**
		 * Gets the length of a stage in a race, in kilometres.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param stageId The ID of the stage being queried.
		 * @return The stage's length.
		 * @throws IDNotRecognisedException If the ID does not match to any stage in the
		 *                                  system.
		 */
		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("Stage ID not recognized");
		}

		return stage.getLength();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		/**
		 * Removes a stage and all its related data, i.e., segments and results.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param stageId The ID of the stage being removed.
		 * @throws IDNotRecognisedException If the ID does not match to any stage in the
		 *                                  system.
		 */
		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("The ID does not match any Stage");
		}
		Race raceContainingStage = correspondingObjectFinder(stage.getRaceID(), raceList);
		deleteStage(stage, raceContainingStage);
	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
	/**
	 * Adds a climb segment to a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId         The ID of the stage to which the climb segment is
	 *                        being added.
	 * @param location        The kilometre location where the climb finishes within
	 *                        the stage.
	 * @param type            The category of the climb - {@link SegmentType#C4},
	 *                        {@link SegmentType#C3}, {@link SegmentType#C2},
	 *                        {@link SegmentType#C1}, or {@link SegmentType#HC}.
	 * @param averageGradient The average gradient for the climb.
	 * @param length          The length of the climb in kilometre.
	 * @return The ID of the segment created.
	 * @throws IDNotRecognisedException   If the ID does not match to any stage in
	 *                                    the system.
	 * @throws InvalidLocationException   If the location is out of bounds of the
	 *                                    stage length.
	 * @throws InvalidStageStateException If the stage is "waiting for results".
	 * @throws InvalidStageTypeException  Time-trial stages cannot contain any
	 *                                    segment.
	 */
	Stage stage = correspondingObjectFinder(stageId, stageList);

	if (stage == null){
		throw new IDNotRecognisedException("The ID does not match to any race in the system.");
	}
	if (stage.getStageState() == "waiting for results"){
		throw new InvalidStageStateException("Stage preparation has been concluded.");
	}
	if (stage.getType() == StageType.TT){
		throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");                
	}
	if (location > stage.getLength()){
		throw new InvalidLocationException("Segment location is out of bounds of the stage length.");
	}

	Segment newClimb = new ClimbSegment(stageId, type, location, averageGradient, length);
	stage.addSegment(newClimb);
	segmentList.add(newClimb);
	return newClimb.getId();
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {

	/**
	 * Adds an intermediate sprint to a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId  The ID of the stage to which the intermediate sprint segment
	 *                 is being added.
	 * @param location The kilometre location where the intermediate sprint finishes
	 *                 within the stage.
	 * @return The ID of the segment created.
	 * @throws IDNotRecognisedException   If the ID does not match to any stage in
	 *                                    the system.
	 * @throws InvalidLocationException   If the location is out of bounds of the
	 *                                    stage length.
	 * @throws InvalidStageStateException If the stage is "waiting for results".
	 * @throws InvalidStageTypeException  Time-trial stages cannot contain any
	 *                                    segment.
	 */		

	Stage stage = correspondingObjectFinder(stageId, stageList);
	if (stage == null){
		throw new IDNotRecognisedException("The ID does not match to any stage in the system.");
	}
	if (stage.getStageState() == "waiting for results"){
		throw new InvalidStageStateException("Stage preparation has been concluded.");
	}
	if (stage.getType() == StageType.TT){
		throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");                
	}
	if (location > stage.getLength()){
		throw new InvalidLocationException("Segment location is out of bounds of the stage length.");
	}

	Segment newSprint = new Segment(stageId, SegmentType.SPRINT, location);
	stage.addSegment(newSprint);
	segmentList.add(newSprint);
	return newSprint.getId();
	}

	@Override
	public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
	/**
	 * Removes a segment from a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param segmentId The ID of the segment to be removed.
	 * @throws IDNotRecognisedException   If the ID does not match to any segment in
	 *                                    the system.
	 * @throws InvalidStageStateException If the stage is "waiting for results".
	 */
	Segment segment = correspondingObjectFinder(segmentId, segmentList);
	if (segment == null){
		throw new IDNotRecognisedException("The given segment ID does not match any segment in the system");
	}
	Stage stageContainingSegment = correspondingObjectFinder(segment.getStageID(), stageList);
	if (stageContainingSegment.getStageState() == "waiting for results"){
		throw new InvalidStageStateException("Stage preparation has been concluded.");
	}
	deleteSegment(segment, stageContainingSegment);
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
	/**
	 * Concludes the preparation of a stage. After conclusion, the stage's state
	 * should be "waiting for results".
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage to be concluded.
	 * @throws IDNotRecognisedException   If the ID does not match to any stage in
	 *                                    the system.
	 * @throws InvalidStageStateException If the stage is "waiting for results".
	 */

	Stage stage = correspondingObjectFinder(stageId, stageList);
	if (stage == null) {
		throw new IDNotRecognisedException("The stageId does not match any stage in the system.");
	}
	if (stage.getStageState() == "waiting for results"){
		throw new InvalidStageStateException("Stage preparation has already been concluded.");
	}
	stage.setStageState("waiting for results");
	}

	@Override
	public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
		/**
		 * Retrieves the list of segment (mountains and sprints) IDs of a stage.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param stageId The ID of the stage being queried.
		 * @return The list of segment IDs ordered (from first to last) by their location in the
		 *         stage.
		 * @throws IDNotRecognisedException If the ID does not match to any stage in the
		 *                                  system.
		 */		
			Stage stage = correspondingObjectFinder(stageId, stageList);
			if (stage == null){
				throw new IDNotRecognisedException("The stage ID is not recognised in the system");
			}
			List<Integer> segmentIds = new ArrayList<Integer>();
			for (Segment segment : stage.getSegments()) {
				segmentIds.add(segment.getId());
			}
			return segmentIds.stream().mapToInt(i->i).toArray();
		}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
			/**
	 * Creates a team with name and description.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param name        The identifier name of the team.
	 * @param description A description of the team.
	 * @return The ID of the created team.
	 * @throws IllegalNameException If the name already exists in the platform.
	 * @throws InvalidNameException If the name is null, empty, has more than 30
	 *                              characters, or has white spaces.
	 */
		if (name == null || name=="" || name.length()>30 || name.contains(" ")){
			throw new InvalidNameException("Team name is null, empty, has more than 30 characters, or has whitespaces");
		}
		for (Team team : teamList){
			if (name==team.getTeamName()){
				throw new IllegalNameException("Team name already exists in the platform");
			}
		}
        Team newTeam = new Team(name, description);
        teamList.add(newTeam);
		return newTeam.getId();
	}

	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		/**
		 * Removes a team from the system.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param teamId The ID of the team to be removed.
		 * @throws IDNotRecognisedException If the ID does not match to any team in the
		 *                                  system.
		 */
			Team team = correspondingObjectFinder(teamId, teamList);
			if (team == null) {
				throw new IDNotRecognisedException("No team found with ID " + teamId);
			}
			deleteTeam(team);
			return;
		}

	@Override
	public int[] getTeams() {
	/**
	 * Get the list of teams' IDs in the system.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @return The list of IDs from the teams in the system. An empty list if there
	 *         are no teams in the system.
	 * 
	 */	//if (teamList.size()==0){
		// return null; // This potentially needs to be changed to an empty int[] instead of null
	 	//}
	 	List<Integer> teamIDs= new ArrayList<Integer>();
		for (Team team : teamList){
			teamIDs.add(team.getId());
		}
		return teamIDs.stream().mapToInt(i -> i).toArray();
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		/**
		 * Get the riders of a team.
		 * <p>
		 * The state of this MiniCyclingPortalInterface must be unchanged if any
		 * exceptions are thrown.
		 * 
		 * @param teamId The ID of the team being queried.
		 * @return A list with riders' ID.
		 * @throws IDNotRecognisedException If the ID does not match to any team in the
		 *                                  system.
		 */	
			List<Rider> ridersInTeam=new ArrayList<Rider>();
			List<Integer> teamRidersIDs=new ArrayList<Integer>();
			Team team = correspondingObjectFinder(teamId, teamList);
			if (team == null) {
				throw new IDNotRecognisedException("No team found with ID " + teamId);
			}
			ridersInTeam=team.getRiders();
			for (Rider rider : ridersInTeam){
				teamRidersIDs.add(rider.getId());
			}
			return teamRidersIDs.stream().mapToInt(i->i).toArray();
		}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
	/**
	 * Creates a rider.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param teamID      The ID rider's team.
	 * @param name        The name of the rider.
	 * @param yearOfBirth The year of birth of the rider.
	 * @return The ID of the rider in the system.
	 * @throws IDNotRecognisedException If the ID does not match to any team in the
	 *                                  system.
	 * @throws IllegalArgumentException If the name of the rider is null or the year
	 *                                  of birth is less than 1900.
	 */
		if (yearOfBirth<1900 || name==null){
			throw new IllegalArgumentException("Name of rider is null or year of birth is less than 1900");
		}
		Team team = correspondingObjectFinder(teamID, teamList);
		if (team == null){
			throw new IDNotRecognisedException("The given team ID does not match to any team in the system");
		}

		Rider newRider= new Rider(yearOfBirth, name, teamID);
		riderList.add(newRider);
		team.addRider(newRider);
		return newRider.getId();
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
	/**
	 * Removes a rider from the system. When a rider is removed from the platform,
	 * all of its results should be also removed. Race results must be updated.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param riderId The ID of the rider to be removed.
	 * @throws IDNotRecognisedException If the ID does not match to any rider in the
	 *                                  system.
	 */
	Rider rider = correspondingObjectFinder(riderId, riderList);
	if (rider == null){
		throw new IDNotRecognisedException("The ID does not match any rider in the system");
	}
	Team teamContainingRider = correspondingObjectFinder(rider.getTeamID(), teamList);
	deleteRider(rider, teamContainingRider);
	}

	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException,
			InvalidStageStateException {

	/**
	 * Record the times of a rider in a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId     The ID of the stage the result refers to.
	 * @param riderId     The ID of the rider.
	 * @param checkpoints An array of times at which the rider reached each of the
	 *                    segments of the stage, including the start time and the
	 *                    finish line.
	 * @throws IDNotRecognisedException    If the ID does not match to any rider or
	 *                                     stage in the system.
	 * @throws DuplicatedResultException   Thrown if the rider has already a result
	 *                                     for the stage. Each rider can have only
	 *                                     one result per stage.
	 * @throws InvalidCheckpointsException Thrown if the length of checkpoints is
	 *                                     not equal to n+2, where n is the number
	 *                                     of segments in the stage; +2 represents
	 *                                     the start time and the finish time of the
	 *                                     stage.
	 * @throws InvalidStageStateException  Thrown if the stage is not "waiting for
	 *                                     results". Results can only be added to a
	 *                                     stage while it is "waiting for results".
	 */
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
/**
	 * Get the times of a rider in a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage the result refers to.
	 * @param riderId The ID of the rider.
	 * @return The array of times at which the rider reached each of the segments of
	 *         the stage and the total elapsed time. The elapsed time is the
	 *         difference between the finish time and the start time. Return an
	 *         empty array if there is no result registered for the rider in the
	 *         stage.
	 * @throws IDNotRecognisedException If the ID does not match to any rider or
	 *                                  stage in the system.
	 */		return null;
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
/**
	 * For the general classification, the aggregated time is based on the adjusted
	 * elapsed time, not the real elapsed time. Adjustments are made to take into
	 * account groups of riders finishing very close together, e.g., the peloton. If
	 * a rider has a finishing time less than one second slower than the
	 * previous rider, then their adjusted elapsed time is the smallest of both. For
	 * instance, a stage with 200 riders finishing "together" (i.e., less than 1
	 * second between consecutive riders), the adjusted elapsed time of all riders
	 * should be the same as the first of all these riders, even if the real gap
	 * between the 200th and the 1st rider is much bigger than 1 second. There is no
	 * adjustments on elapsed time on time-trials.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage the result refers to.
	 * @param riderId The ID of the rider.
	 * @return The adjusted elapsed time for the rider in the stage. Return null if 
	 * 		  there is no result registered for the rider in the stage.
	 * @throws IDNotRecognisedException   If the ID does not match to any rider or
	 *                                    stage in the system.
	 */		return null;
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
	/**
* Removes the stage results from the rider.
* <p>
* The state of this MiniCyclingPortalInterface must be unchanged if any
* exceptions are thrown.
* 
* @param stageId The ID of the stage the result refers to.
* @param riderId The ID of the rider.
* @throws IDNotRecognisedException If the ID does not match to any rider or
*                                  stage in the system.
*/
	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
	/**
	 * Get the riders finished position in a a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage being queried.
	 * @return A list of riders ID sorted by their elapsed time. An empty list if
	 *         there is no result for the stage.
	 * @throws IDNotRecognisedException If the ID does not match any stage in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
	/**
	 * Get the adjusted elapsed times of riders in a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage being queried.
	 * @return The ranked list of adjusted elapsed times sorted by their finish
	 *         time. An empty list if there is no result for the stage. These times
	 *         should match the riders returned by
	 *         {@link #getRidersRankInStage(int)}.
	 * @throws IDNotRecognisedException If the ID does not match any stage in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
	/**
	 * Get the number of points obtained by each rider in a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage being queried.
	 * @return The ranked list of points each riders received in the stage, sorted
	 *         by their elapsed time. An empty list if there is no result for the
	 *         stage. These points should match the riders returned by
	 *         {@link #getRidersRankInStage(int)}.
	 * @throws IDNotRecognisedException If the ID does not match any stage in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {

	/**
	 * Get the number of mountain points obtained by each rider in a stage.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param stageId The ID of the stage being queried.
	 * @return The ranked list of mountain points each riders received in the stage,
	 *         sorted by their finish time. An empty list if there is no result for
	 *         the stage. These points should match the riders returned by
	 *         {@link #getRidersRankInStage(int)}.
	 * @throws IDNotRecognisedException If the ID does not match any stage in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public void eraseCyclingPortal() {
	/**
	 * Method empties this MiniCyclingPortalInterface of its contents and resets all
	 * internal counters.
	 */
	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
        /**
         * Method saves this MiniCyclingPortalInterface contents into a serialised file,
         * with the filename given in the argument.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         *
         * @param filename Location of the file to be saved.
         * @throws IOException If there is a problem experienced when trying to save the
         *                     store contents to the file.
         */
	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
	/**
	 * Method should load and replace this MiniCyclingPortalInterface contents with the
	 * serialised contents stored in the file given in the argument.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 *
	 * @param filename Location of the file to be loaded.
	 * @throws IOException            If there is a problem experienced when trying
	 *                                to load the store contents from the file.
	 * @throws ClassNotFoundException If required class files cannot be found when
	 *                                loading.
	 */
	}

	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
        /**
         * The method removes the race and all its related information, i.e., stages,
         * segments, and results.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         * 
         * @param name The name of the race to be removed.
         * @throws NameNotRecognisedException If the name does not match to any race in
         *                                    the system.
         */
		Race race = null;
		for (Race raceElement : raceList) {
			if (raceElement.getName().equals(name)){
				race = raceElement;
				break;
			}
		}
		if (race == null){
			throw new NameNotRecognisedException("The given Race name does not match to any race in the system");
		}
		deleteRace(race);
	}

	@Override
	public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
	/**
	 * Get the general classification times of riders in a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return A list of riders' times sorted by the sum of their adjusted elapsed
	 *         times in all stages of the race. An empty list if there is no result
	 *         for any stage in the race. These times should match the riders
	 *         returned by {@link #getRidersGeneralClassificationRank(int)}.
	 * @throws IDNotRecognisedException If the ID does not match any race in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {

        /**
         * Get the overall points of riders in a race.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         * 
         * @param raceId The ID of the race being queried.
         * @return A list of riders' points (i.e., the sum of their points in all stages
         *         of the race), sorted by the total elapsed time. An empty list if
         *         there is no result for any stage in the race. These points should
         *         match the riders returned by {@link #getRidersGeneralClassificationRank(int)}.
         * @throws IDNotRecognisedException If the ID does not match any race in the
         *                                  system.
         */		return null;
	}

	@Override
	public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {

	/**
	 * Get the overall mountain points of riders in a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return A list of riders' mountain points (i.e., the sum of their mountain
	 *         points in all stages of the race), sorted by the total elapsed time.
	 *         An empty list if there is no result for any stage in the race. These
	 *         points should match the riders returned by
	 *         {@link #getRidersGeneralClassificationRank(int)}.
	 * @throws IDNotRecognisedException If the ID does not match any race in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
        /**
         * Get the general classification rank of riders in a race.
         * <p>
         * The state of this MiniCyclingPortalInterface must be unchanged if any
         * exceptions are thrown.
         * 
         * @param raceId The ID of the race being queried.
         * @return A ranked list of riders' IDs sorted ascending by the sum of their
         *         adjusted elapsed times in all stages of the race. That is, the first
         *         in this list is the winner (least time). An empty list if there is no
         *         result for any stage in the race.
         * @throws IDNotRecognisedException If the ID does not match any race in the
         *                                  system.
         */		
        return null;
	}

	@Override
	public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
	/**
	 * Get the ranked list of riders based on the points classification in a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return A ranked list of riders' IDs sorted descending by the sum of their
	 *         points in all stages of the race. That is, the first in this list is
	 *         the winner (more points). An empty list if there is no result for any
	 *         stage in the race.
	 * @throws IDNotRecognisedException If the ID does not match any race in the
	 *                                  system.
	 */		return null;
	}

	@Override
	public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
	/**
	 * Get the ranked list of riders based on the mountain classification in a race.
	 * <p>
	 * The state of this MiniCyclingPortalInterface must be unchanged if any
	 * exceptions are thrown.
	 * 
	 * @param raceId The ID of the race being queried.
	 * @return A ranked list of riders' IDs sorted descending by the sum of their
	 *         mountain points in all stages of the race. That is, the first in this
	 *         list is the winner (more points). An empty list if there is no result
	 *         for any stage in the race.
	 * @throws IDNotRecognisedException If the ID does not match any race in the
	 *                                  system.
	 */		return null;
	}

}
