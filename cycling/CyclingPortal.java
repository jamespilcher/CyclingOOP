package cycling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * CyclingPortal is an implementor of the CyclingPortalInterface interface.
 * 
 * @authors James Pilcher & Daniel Moulton
 * @version 1.0
 *
 */
public class CyclingPortal implements CyclingPortalInterface {
//CONSTANTS
	private static final Integer[] SPRINT_SEGMENT_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
	private static final Integer[] HC_SEGMENT_POINTS = {20,15,12,10,8,6,4,2};
	private static final Integer[] C1_SEGMENT_POINTS = {10,8,6,4,2,1};
	private static final Integer[] C2_SEGMENT_POINTS = {5,3,2,1};
	private static final Integer[] C3_SEGMENT_POINTS = {2,1};
	private static final Integer[] C4_SEGMENT_POINTS = {1};

	//points awarded depending on position in stage

	private final Integer[] FLAT_STAGE_POINTS = {50,30,20,18,16,14,12,10,8,7,6,5,4,3,2};
	private final Integer[] MM_STAGE_POINTS = {30,25,22,19,17,15,13,11,9,7,6,5,4,3,2};
	private final Integer[] HM_STAGE_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
	private final Integer[] TT_STAGE_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};

	//list of riders
    private LinkedList<Rider> riderList=new LinkedList<Rider>();

	//list of teams
    private LinkedList<Team> teamList=new LinkedList<Team>();

	//list of segments
    private LinkedList<Segment> segmentList=new LinkedList<Segment>();
	public LinkedList<Segment> getSegmentList() {return segmentList;}

	//list of stages
    private LinkedList<Stage> stageList=new LinkedList<Stage>();
	public LinkedList<Stage> getStageList() {return stageList;}

	//list of races
    private LinkedList<Race> raceList=new LinkedList<Race>();
	public LinkedList<Race> getRaceList() {return raceList;}

	private LinkedList<RiderStageResults> riderStageResultsList=new LinkedList<RiderStageResults>();


    private <T extends IdHaver> T correspondingObjectFinder(int id, LinkedList<T> objectList){
        T correspondingObject = null;
        for (T object : objectList) {
            if (id == object.getId()){
                correspondingObject = object;
                break;
            }
        }
        return correspondingObject;
    }

	void deleteAllRiderResults(Rider rider){
		LinkedList<RiderStageResults> riderResultsList = new LinkedList<RiderStageResults>(rider.getRiderResultsList());
		for (RiderStageResults riderStageResults : riderResultsList){
			deleteRiderResult(riderStageResults);
		}
	}

	void deleteRiderResult(RiderStageResults riderStageResults){
		riderStageResults.getStage().getRiderResultsList().remove(riderStageResults);
		riderStageResults.getRider().getRiderResultsList().remove(riderStageResults);
		riderStageResultsList.remove(riderStageResults);
	}

	void deleteAllStageResults(Stage stage){
		LinkedList<RiderStageResults> riderResultsList = new LinkedList<RiderStageResults>(stage.getRiderResultsList());
		for (RiderStageResults riderStageResults : riderResultsList){
			deleteRiderResult(riderStageResults);
		}
	}

	private void deleteTeam(Team team){
		teamList.remove(team.getId());
		LinkedList<Rider>riders = new LinkedList<Rider>(team.getRiders());
		for (Rider rider : riders){
			deleteRider(rider, team);
		}
		team = null;
	}

	private void deleteRider(Rider rider, Team team){
		riderList.remove(rider);

		deleteAllRiderResults(rider);

		team.removeRider(rider);
	}

	private void deleteRace(Race race){
		raceList.remove(race);
		LinkedList<Stage>raceStages = new LinkedList<Stage>(race.getStages());
		for (Stage stage : raceStages) {
			deleteStage(stage, race);
		}
		race = null;
	}
	private void deleteStage(Stage stage, Race race){
		stageList.remove(stage);
		race.removeStage(stage);
		LinkedList<Segment>stageSegments = new LinkedList<Segment>(stage.getSegments());
		for (Segment segment : stageSegments) {
			deleteSegment(segment, stage);
		}
		deleteAllStageResults(stage);
		stage = null;
	}

	private void deleteSegment(Segment segment, Stage stage){
		segmentList.remove(segment);
		stage.removeSegment(segment);
		segment = null;
		}

		
	private void sortRidersByElapsedTime(LinkedList<RiderStageResults> competingRiders){
		competingRiders.sort(Comparator.comparing( (RiderStageResults rider) -> rider.getElapsedTimeForStage()));

	}
	
	private void adjustRiderTimesInStage(LinkedList<RiderStageResults> competingRiders){

		sortRidersByElapsedTime(competingRiders);

		competingRiders.get(0).setAdjustedTimeForStage(competingRiders.get(0).getElapsedTimeForStage()); //rider[0].adjustedtime = elapsed time

		for (int i = 0; i < competingRiders.size()-1; i++){
			if (competingRiders.get(i+1).getElapsedTimeForStage() - competingRiders.get(i).getElapsedTimeForStage() < 1000_000_000L){
				competingRiders.get(i+1).setAdjustedTimeForStage(competingRiders.get(i).getAdjustedTimeForStage());
			} else{
				competingRiders.get(i+1).setAdjustedTimeForStage(competingRiders.get(i+1).getElapsedTimeForStage());
			}
		}
	}


	private LinkedList<Integer> pointsToBeAddedFormatter(int numRiders, Integer[] rankPoints){
		int rankPointsSize = rankPoints.length;
		LinkedList<Integer> pointsToBeAdded = new LinkedList<Integer>(Arrays.asList(rankPoints));
		if (numRiders > rankPointsSize){
			int sizeDifference = numRiders - rankPointsSize;
			for (int i = 0; i < sizeDifference; i++) {
				pointsToBeAdded.add(0);
			}
		}
		return pointsToBeAdded;
	}

	private void awardSegmentPoints(LinkedList<RiderStageResults> competingRiders, Segment segment, LinkedList<Segment> stageSegments, LinkedList<Integer> segmentPointsToBeAdded, boolean isASprintSegment){
		int indexForSegment = stageSegments.indexOf(segment);

		LinkedList<RiderStageResults> ridersInSegment = new LinkedList<RiderStageResults>(competingRiders);
		ridersInSegment.sort(Comparator.comparing( (RiderStageResults rider) -> rider.getSegmentTime(indexForSegment)));

		for (RiderStageResults rider : ridersInSegment) {
			int indexForPoints = ridersInSegment.indexOf(rider);
			int points = segmentPointsToBeAdded.get(indexForPoints);
			if (isASprintSegment){
				rider.addPoints(points);

			}
			else{
				rider.addMountainPoints(points);
				//System.out.println("rider = " + rider.getRider().getId());
				//System.out.println("points = " + points);
			}
		}
		return;
	}





	void awardPointsInStage(Stage stage){
		//zero out the points - segment included!
		LinkedList<RiderStageResults> riderResultsList = new LinkedList<RiderStageResults>(stage.getRiderResultsList());

		LinkedList<Integer> pointsToBeAdded = new LinkedList<Integer>();
		sortRidersByElapsedTime(riderResultsList);
		int riderResultsListSize = riderResultsList.size();
		StageType stageType = stage.getType();
		switch (stageType) {
			case FLAT: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, FLAT_STAGE_POINTS);
				break;
			case MEDIUM_MOUNTAIN: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, MM_STAGE_POINTS);
				break;
			case HIGH_MOUNTAIN: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, HM_STAGE_POINTS);
				break;
			case TT: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, TT_STAGE_POINTS);
				break;
			default: assert false;

		}

		for (RiderStageResults riderStageResults : riderResultsList){
			int indexForPoints = riderResultsList.indexOf(riderStageResults);
			int points = pointsToBeAdded.get(indexForPoints);
			riderStageResults.setPoints(points);
		}
		LinkedList<Segment> segments = new LinkedList<Segment>(stage.getSegments());
		LinkedList<Integer> segmentPointsToBeAdded = new LinkedList<Integer>();
		for (Segment segment : segments){
			if (segment.getSegmentType() == SegmentType.SPRINT){
				segmentPointsToBeAdded = pointsToBeAddedFormatter(riderResultsList.size(), SPRINT_SEGMENT_POINTS);
				awardSegmentPoints(riderResultsList, segment, segments, segmentPointsToBeAdded, true);
			}
		}
		return;
		}			


	void awardMountainPointsInStage(Stage stage){
		LinkedList<RiderStageResults> riderResultsList = new LinkedList<RiderStageResults>(stage.getRiderResultsList());
		LinkedList<Integer> pointsToBeAdded = new LinkedList<Integer>();
		sortRidersByElapsedTime(riderResultsList);

		for (RiderStageResults riderStageResults : riderResultsList){
			riderStageResults.setMountainPoints(0);
		}

		LinkedList<Segment> segments = new LinkedList<Segment>(stage.getSegments());

		for (Segment segment : segments){
			SegmentType segmentType = segment.getSegmentType();
			int riderResultsListSize = riderResultsList.size();
			if (!(segmentType == SegmentType.SPRINT)){
				switch (segmentType){
					case C1: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C1_SEGMENT_POINTS);
						break;
					case C2: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C2_SEGMENT_POINTS);
						break;
					case C3: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C3_SEGMENT_POINTS);
						break;
					case C4: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, C4_SEGMENT_POINTS);
						break;
					case HC: pointsToBeAdded = pointsToBeAddedFormatter(riderResultsListSize, HC_SEGMENT_POINTS);
						break;
					default: assert false;
				}
				awardSegmentPoints(riderResultsList, segment, segments, pointsToBeAdded, false);
			}
		}
	}


	



	private LinkedList<Rider> totalRidersPoints(Race race){

		//maybe store riders just in race instead? this is inneficient.
		LinkedList<Rider> riders = new LinkedList<Rider>();
		for (Rider allRider : riderList){
			allRider.setTotalElapsedTime(0L);
			allRider.setTotalPoints(0);
		}

		for (Stage stage : race.getStages()){
			awardPointsInStage(stage);
			for (RiderStageResults riderStageResults : stage.getRiderResultsList()){
				Rider rider = riderStageResults.getRider();
				rider.addTotalElapsedTime(riderStageResults.getElapsedTimeForStage());   //note doesnt 0 out the points.
				rider.addTotalPoints(riderStageResults.getRiderPoints());
				if (!riders.contains(rider)){
					riders.add(rider);
				}
			}
		}
		return riders;
	}

	private LinkedList<Rider> totalRidersMountainPoints(Race race){

		//maybe store riders just in race instead? this is inneficient.
		LinkedList<Rider> riders = new LinkedList<Rider>();
		for (Rider allRider : riderList){
			allRider.setTotalElapsedTime(0L);
			allRider.setTotalMountainPoints(0);
		}
		for (Stage stage : race.getStages()){
			awardMountainPointsInStage(stage);
			for (RiderStageResults riderStageResults : stage.getRiderResultsList()){
				Rider rider = riderStageResults.getRider();
				//System.out.println("Rider" + riderStageResults.getRider().getId());
				//System.out.println("Points to be added " + riderStageResults.getRiderMountainPoints());
				rider.addTotalElapsedTime(riderStageResults.getElapsedTimeForStage());   //note doesnt 0 out the points.
				rider.addTotalMountainPoints(riderStageResults.getRiderMountainPoints()); //adding tot mountain points twice
				//System.out.println("rider total mountain points" + rider.getTotalMountainPoints());
				if (!riders.contains(rider)){
					riders.add(rider);
				}
			}

		}
		return riders;
	}


	private void sortByTotalElapsedTime(LinkedList<Rider> riders){
		riders.sort(Comparator.comparing( (Rider rider) -> rider.getTotalElapsedTime()));
	}

	private void sortByTotalAdjustedTime(LinkedList<Rider> riders){
		riders.sort(Comparator.comparing( (Rider rider) -> rider.getTotalAdjustedTime()));
	}


	private LinkedList<Rider> ridersTotalAdjustedTime(Race race){

		//maybe store riders just in race instead? this is inneficient.
		LinkedList<Rider> riders = new LinkedList<Rider>();
		for (Rider allRider : riderList){
			allRider.setTotalAdjustedTime(0L);
		}
		for (Stage stage : race.getStages()){
			LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
			if (riderResultsList.size() == 0){
				break;
			}
			adjustRiderTimesInStage(riderResultsList);
			for (RiderStageResults riderStageResults : riderResultsList){
				Rider rider = riderStageResults.getRider();
				rider.addTotalAdjustedTime(riderStageResults.getAdjustedTimeForStage());   //note doesnt 0 out the points.
				if (!riders.contains(rider)){
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
			for (Race race: raceList) {
				raceIds.add(race.getId());
			}
			return raceIds.stream().mapToInt(i -> i).toArray();
		}
	
	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
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
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The given Race ID does not match to any race in the system");
		}
		deleteRace(race);
	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
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
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The race ID is not recognised in the system");
		}
		List<Integer> stageIds = new LinkedList<Integer>();
		for (Stage stage : race.getStages()) {
			stageIds.add(stage.getId());
		}
		return stageIds.stream().mapToInt(i->i).toArray(); // could just make an arrayofsize[race.getStages().size]
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("Stage ID not recognized");
		}

		return stage.getLength();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
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
		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("The stage ID is not recognised in the system");
		}
		List<Integer> segmentIds = new LinkedList<Integer>();
		for (Segment segment : stage.getSegments()) {
			segmentIds.add(segment.getId());
		}
		return segmentIds.stream().mapToInt(i->i).toArray();
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
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
		Team team = correspondingObjectFinder(teamId, teamList);
		if (team == null) {
			throw new IDNotRecognisedException("No team found with ID " + teamId);
		}
		deleteTeam(team);
		return;
	}

	@Override
	public int[] getTeams() {
	 	List<Integer> teamIDs= new LinkedList<Integer>();
		for (Team team : teamList){
			teamIDs.add(team.getId());
		}
		return teamIDs.stream().mapToInt(i -> i).toArray();   //again array[teamlist.size]
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		List<Rider> ridersInTeam=new LinkedList<Rider>();
		List<Integer> teamRidersIDs=new LinkedList<Integer>();
		Team team = correspondingObjectFinder(teamId, teamList);
		if (team == null) {
			throw new IDNotRecognisedException("No team found with ID " + teamId);
		}
		ridersInTeam=team.getRiders();
		for (Rider rider : ridersInTeam){
			teamRidersIDs.add(rider.getId());
		}
		return teamRidersIDs.stream().mapToInt(i->i).toArray(); //again array[teamlist.size]
	}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
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

		Rider rider = correspondingObjectFinder(riderId, riderList);
		if (rider == null){
			throw new IDNotRecognisedException("The  ID does not match to any rider system.");
		}

		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("The  ID does not match to any stage system.");
		}

		if (!(stage.getStageState() == "waiting for results")){
			throw new IDNotRecognisedException("Stage has not finished preparation");
		}

		for (RiderStageResults riderStageResults : stage.getRiderResultsList()){
				if (riderStageResults.getRider().getId() == riderId){
					throw new DuplicatedResultException("The rider has already a result for the stage. Each rider can have only one result per stage.");
				}
			}

		if (!(checkpoints.length == stage.getSegments().size() + 2)){
			throw new InvalidCheckpointsException("The number checkpoint times don't match the number of segments (+2)");
		}

		int raceId = stage.getRaceID();
		RiderStageResults riderStageResults = new RiderStageResults(rider, stage, raceId, checkpoints);
		stage.addRiderResultToStage(riderStageResults);
		riderStageResultsList.add(riderStageResults);
		rider.addStageResults(riderStageResults);

	}


	private LocalTime nanoToLocalTime(Long nanoseconds){
		int second = (int) (nanoseconds / 1000_000_000); 
        int minute = (int) (second / 60);
        int hour = (int) (minute / 60);
		//LocalTime localTime = new LocalTime();

        nanoseconds %= 1000_000_000;
        second %= 60;
        minute %= 60;
        hour %= 60;
		LocalTime time = LocalTime.of(hour, minute, second, nanoseconds.intValue());
		return time;
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage= correspondingObjectFinder(stageId, stageList);
		Rider rider = correspondingObjectFinder(riderId, riderList);

		if (stage==null){
			throw new IDNotRecognisedException("The  ID does not match any stage in the system.");

		}

		if (rider == null){
			throw new IDNotRecognisedException("The  ID does not match any rider in the system.");
		}
		LinkedList<Long> times = new LinkedList<Long>();
		LinkedList<LocalTime> results = new LinkedList<LocalTime>();

		//throw exceptions///

		for (RiderStageResults riderStageResults : rider.getRiderResultsList())
			if (riderStageResults.getStage() == stage){
				for (Long segmentTime : riderStageResults.getSegmentTimes()){
					times.add(segmentTime + riderStageResults.getStartTime());
				}
				times.add(riderStageResults.getElapsedTimeForStage());
				break;
			}
				
		for (Long time : times){
			results.add(nanoToLocalTime(time));
		}
		return results.toArray(new LocalTime[times.size()]);
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {

		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("The stage ID does not match any stage in the system");
		}

		Rider rider = correspondingObjectFinder(riderId, riderList);
		if (rider == null){
			throw new IDNotRecognisedException("The rider ID does not match any rider in the system");
		}

		LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList();
		adjustRiderTimesInStage(riderResultsList);
		LocalTime adjustedTime = null;

		for (RiderStageResults riderStageResults : riderResultsList){
			if (riderStageResults.getRider() == rider){
				adjustedTime = nanoToLocalTime(riderStageResults.getAdjustedTimeForStage());
				break;
			}
		}
		return adjustedTime;
}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {

		Stage stage = correspondingObjectFinder(stageId, stageList);
		if (stage == null){
			throw new IDNotRecognisedException("The ID does not match any stage in the system");
		}
		Rider rider = correspondingObjectFinder(riderId, riderList);
		if (rider == null){
			throw new IDNotRecognisedException("The ID does not match any rider in the system");
		}

		LinkedList<RiderStageResults> riderResultsList = new LinkedList<RiderStageResults>(stage.getRiderResultsList());

		for (RiderStageResults riderStageResults : riderResultsList) {
			if (riderStageResults.getRider() == rider){
				deleteRiderResult(riderStageResults);
				break;
			}
		}
	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = correspondingObjectFinder(stageId, stageList);

		if (stage == null) {
			throw new IDNotRecognisedException("The stage ID does not match any stage in the system");
		}

		LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList(); //if no results this will be size 0
		int[] riderIds = new int[riderResultsList.size()];
		sortRidersByElapsedTime(riderResultsList);

		for (int i = 0; i < riderResultsList.size(); i++){
			riderIds[i] = riderResultsList.get(i).getRider().getId();
		}
		return riderIds;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = correspondingObjectFinder(stageId, stageList);

		if (stage == null) {
			throw new IDNotRecognisedException("The stage ID does not match any stage in the system");
		}

		LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList(); //if no results this will be size 0
		LocalTime[] localTimes = new LocalTime[riderResultsList.size()];
		adjustRiderTimesInStage(riderResultsList);
		for (int i = 0; i < riderResultsList.size(); i++){
			localTimes[i] = nanoToLocalTime(riderResultsList.get(i).getAdjustedTimeForStage());
			}
		return localTimes;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = correspondingObjectFinder(stageId, stageList);

		if (stage == null) {
			throw new IDNotRecognisedException("The stage ID does not match any stage in the system");
		}

		awardPointsInStage(stage);
		LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList(); //if no results this will be size 0

		int[] riderPoints = new int[riderResultsList.size()];
		sortRidersByElapsedTime(riderResultsList);

		for (int i = 0; i < riderResultsList.size(); i++){
			riderPoints[i] = riderResultsList.get(i).getRiderPoints();
		}
		return riderPoints;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = correspondingObjectFinder(stageId, stageList);

		if (stage == null) {
			throw new IDNotRecognisedException("The stage ID does not match any stage in the system");
		}
	
		awardMountainPointsInStage(stage);
		LinkedList<RiderStageResults> riderResultsList = stage.getRiderResultsList(); //if no results this will be size 0
	
		int[] riderMountainPoints = new int[riderResultsList.size()];
		sortRidersByElapsedTime(riderResultsList);
	
		for (int i = 0; i < riderResultsList.size(); i++){
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
			if (!filename.endsWith(".ser")){
				filename+= ".ser";
			}
			File file = new File(filename);
			if (file.exists() && !file.isDirectory()){
				file.delete();
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
				oos.writeObject(riderList);
				oos.writeObject(teamList);
				oos.writeObject(raceList);
				oos.writeObject(stageList);
				oos.writeObject(segmentList);
				oos.writeObject(riderStageResultsList);
				System.out.printf("Saved in %s%n",filename);
				oos.close();
			}
			catch (IOException e){
				throw new IOException("Failed to save contents to file");
			}
		}

	@Override
		public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		eraseCyclingPortal();
		if (!filename.endsWith(".ser")){
			filename+= ".ser";
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){
			Object obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof Rider)
				{
					riderList=(LinkedList<Rider>)obj;
				}		
			}
			obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof Team){
					teamList=(LinkedList<Team>)obj;
				}
			}
			obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof Race){
					raceList=(LinkedList<Race>)obj;
				}
			}
			obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof Stage){
					stageList=(LinkedList<Stage>)obj;
				}
			}
			obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof Segment){
					segmentList=(LinkedList<Segment>)obj;
				}
			}
			obj=ois.readObject();
			if (obj instanceof LinkedList<?>){
				if(((LinkedList<?>)obj).get(0) instanceof RiderStageResults){
					riderStageResultsList=(LinkedList<RiderStageResults>)obj;
				}
			}
		}
		catch(IOException e){
			throw new IOException("Failed to load contents from file");
		}
		catch(ClassNotFoundException e)
		{
			throw new ClassNotFoundException("Required class files not found");
		}
	}


	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
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
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The ID does not match any race in the system.");
		}
		LinkedList<Rider> riders = ridersTotalAdjustedTime(race);
		LocalTime[] localTimes = new LocalTime[riders.size()];
		for (int i = 0; i < riders.size(); i++){
			localTimes[i] = nanoToLocalTime(riders.get(i).getTotalAdjustedTime());
			}
		return localTimes;
	}

	@Override
	public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The race ID does not match any race in the system");
		}
		LinkedList<Rider> riders = totalRidersPoints(race);
		sortByTotalElapsedTime(riders);
		int[] riderPoints = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++){
			riderPoints[i] = riders.get(i).getTotalPoints();
			}
		return riderPoints;
	}

	@Override
	public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The race ID does not match any race in the system");
		}
		LinkedList<Rider> riders = totalRidersMountainPoints(race);
		sortByTotalElapsedTime(riders);
		int[] riderMountainPoints = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++){
			riderMountainPoints[i] = riders.get(i).getTotalMountainPoints();
			}
		return riderMountainPoints;
	}

	@Override
	public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {	
		Race race = correspondingObjectFinder(raceId, raceList);

		if (race == null){
			throw new IDNotRecognisedException("The ID does not match any race in the system.");
		}
		LinkedList<Rider> riders = ridersTotalAdjustedTime(race);

		int[] riderIds = new int[riders.size()];

		for (int i = 0; i < riders.size(); i++){
			riderIds[i] = riders.get(i).getId();
			}
		return riderIds;
	}

	private void sortByTotalPoints(LinkedList<Rider> riders){
		riders.sort(Comparator.comparing( (Rider rider) -> (rider.getTotalPoints()*-1 )));
	}

	@Override
	public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {

		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The race ID does not match any race in the system");
		}
		LinkedList<Rider> riders = totalRidersPoints(race);
		sortByTotalPoints(riders);
		int[] riderPointsId = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++){
			riderPointsId[i] = riders.get(i).getId();
			}
		return riderPointsId;
	}

	private void sortByTotalMountainPoints(LinkedList<Rider> riders){
		riders.sort(Comparator.comparing( (Rider rider) -> (rider.getTotalMountainPoints()*-1)));
	}

	@Override
	public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
		Race race = correspondingObjectFinder(raceId, raceList);
		if (race == null){
			throw new IDNotRecognisedException("The race ID does not match any race in the system");
		}
		LinkedList<Rider> riders = totalRidersMountainPoints(race);
		sortByTotalMountainPoints(riders);
		int[] riderMountainPointsId = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++){
			riderMountainPointsId[i] = riders.get(i).getId();
			}
		return riderMountainPointsId;
	}

}
