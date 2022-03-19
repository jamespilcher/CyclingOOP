package cycling;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.io.Serializable;




public class RiderStageResults implements Serializable{
    private Rider rider;
    private Stage stage;
    private int raceId;

    private Long startTime;

    Long elapsedTimeForStage;
    Long adjustedTimeForStage;
    ArrayList<Long> segmentTimes = new ArrayList<Long>();     //checkpoint1time - start time, checkpoint2time - start time, checkpoint 3time - start time

    int riderPoints = 0;  //finish pos + sprintPoints
    int riderMountainPoints = 0;

    public RiderStageResults(Rider rider, Stage stage, int raceId, LocalTime... checkpoints){
        this.rider = rider;
        this.stage = stage;
        this.raceId = raceId;
        startTime = checkpoints[0].toNanoOfDay();

        elapsedTimeForStage = checkpoints[0].until(checkpoints[checkpoints.length-1], ChronoUnit.NANOS);   //stored time in milliseconds
        for (int i = 1; i < checkpoints.length-1; i++){
            segmentTimes.add(checkpoints[0].until(checkpoints[i], ChronoUnit.NANOS));
        }
    }

    public void setMountainPoints(int points){
        riderPoints = points;
        }

    
    public void setPoints(int points){
        riderPoints = points;
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
    public int getRaceId(){ return raceId; }

    public int getRiderPoints(){ return riderPoints; }
    public int getRiderMountainPoints(){ return riderMountainPoints; }
    

    public Stage getStage(){ return stage; }
    public Rider getRider(){ return rider; }

    public Long getStartTime(){ return startTime; }

    public Long getAdjustedTimeForStage(){ return adjustedTimeForStage; }

    public void setAdjustedTimeForStage(Long adjustedTimeForStage){ this.adjustedTimeForStage = adjustedTimeForStage;}



}
