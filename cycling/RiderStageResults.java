package cycling;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;



public class RiderStageResults {
    private int id; //riderId
    private int stageId;
    private int raceId;

    Long elapsedTimeForStage;
    Long adjustedTimeForStage;
    ArrayList<Long> segmentTimes = new ArrayList<Long>();     //checkpoint1time - start time, checkpoint2time - start time, checkpoint 3time - start time

    int riderPoints = 0;  //finish pos + sprintPoints
    int riderMountainPoints = 0;

    public RiderStageResults(int id, int stageId, int raceId, LocalTime... checkpoints){
        this.id = id;
        this.stageId = stageId;
        this.raceId = raceId;
        elapsedTimeForStage = checkpoints[0].until(checkpoints[checkpoints.length-1], ChronoUnit.MILLIS);   //stored time in milliseconds
        for (int i = 1; i < checkpoints.length-1; i++){
            segmentTimes.add(checkpoints[0].until(checkpoints[i], ChronoUnit.MILLIS));
        }
    }

    public void addPoints(int points){
        riderPoints += points;
        }

    public void addMountainPoints(int points){
        riderMountainPoints += points;
        }
    

    public ArrayList<Long> getSegmentTimes(){ return segmentTimes; }
    public Long getSegmentTime(int index){ return segmentTimes.get(index);}
    public Long getElapsedTimeForStage(){ return elapsedTimeForStage; }


    public Long getAdjustedTimeForStage(){ return adjustedTimeForStage; }


    public void setAdjustedTimeForStage(Long adjustedTimeForStage){ this.adjustedTimeForStage = adjustedTimeForStage;}



}
