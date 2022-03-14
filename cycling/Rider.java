package cycling;
import java.util.ArrayList;
public class Rider extends IdHaver{
    private int id;
    private String riderName;
    private int riderYearOfBirth;
    private int teamID;

    private int totalPoints;
    private int totalMountainPoints;

    private static int numberofRiders=0;

    private ArrayList<RiderStageResults> riderResults = new ArrayList<RiderStageResults>();


    public Rider(int riderYearOfBirth, String riderName, int teamID){
        this.riderYearOfBirth=riderYearOfBirth;
        this.riderName=riderName;
        this.teamID=teamID;
        id=++numberofRiders;
        super.setId(id);
    }

    public void addStageResults(RiderStageResults rider){
        riderResults.add(rider);
    }

    public int getTeamID() {return teamID;}
    public void setTeamID(int teamID) {this.teamID=teamID;}
    public int getId() {return id;}
}
