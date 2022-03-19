package cycling;
import java.io.Serializable;
import java.util.ArrayList;
public class Rider extends IdHaver implements Serializable {
    
    private int id;
    private String riderName;
    private int riderYearOfBirth;
    private int teamID;

    private Long totalAdjustedTime = 0L;
    private Long totalElapsedTime = 0L;


    private int totalPoints = 0;
    private int totalMountainPoints = 0;

    private static int numberofRiders=0;

    private ArrayList<RiderStageResults> riderResultsList = new ArrayList<RiderStageResults>();


    public Rider(int riderYearOfBirth, String riderName, int teamID){
        this.riderYearOfBirth=riderYearOfBirth;
        this.riderName=riderName;
        this.teamID=teamID;
        id=++numberofRiders;
        super.setId(id);
    }

    public void addStageResults(RiderStageResults rider){
        riderResultsList.add(rider);
    }

    public void addTotalPoints(int points){
        totalPoints += points;}
    public void addTotalMountainPoints(int points){
        totalMountainPoints += points;
    }
    public void addTotalAdjustedTime(Long time){
        totalAdjustedTime += time;
    }

    public void addTotalElapsedTime(Long time){
        totalElapsedTime += time;
    }

    public int getTeamID() {return teamID;}
    public void setTeamID(int teamID) {this.teamID=teamID;}
    public int getId() {return id;}

    public Long getTotalElapsedTime(){
        return totalElapsedTime;
    }
    public Long getTotalAdjustedTime(){
        return totalAdjustedTime;
    }

    public ArrayList<RiderStageResults> getRiderResultsList() {return riderResultsList;}

    public void setTotalAdjustedTime(Long time){
        totalAdjustedTime=time;
    }

    public void setTotalElapsedTime(Long time){
        totalElapsedTime=time;
    }

    public void setTotalPoints(int points) {totalPoints=points;}
    public void setTotalMountainPoints(int points) {totalMountainPoints=points;}
    
    public int getTotalPoints() {return totalPoints;}
    public int getTotalMountainPoints() {return totalMountainPoints;}

}
