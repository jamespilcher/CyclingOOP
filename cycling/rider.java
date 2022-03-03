package cycling;
import java.util.ArrayList;
public class Rider{
    private int riderID;
    private String riderName;
    private int riderYearOfBirth;
    private int teamID;

    private static int numberofRiders=0;

    public Rider(int riderYearOfBirth, String riderName, int teamID){
        this.riderYearOfBirth=riderYearOfBirth;
        this.riderName=riderName;
        this.teamID=teamID;
        riderID=++numberofRiders;
    }

    public int getTeamID() {return teamID;}
    public int setTeamID(int teamID) {this.teamID=teamID;}
    public int getRiderID() {return riderID;}
}