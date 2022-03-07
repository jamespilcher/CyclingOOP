package cycling;
import java.util.ArrayList;
public class Team {

    private int teamID;
    private String teamName;
    private String teamDescription;

    private ArrayList<Rider> riders=new ArrayList<Rider>();

    private static int numberOfTeams=0;

    public Team(String teamName, String teamDescription){
        this.teamName=teamName;
        this.teamDescription=teamDescription;
        this.teamID=++numberOfTeams;
    }

    public void addRider(Rider rider){
        riders.add(rider);
    }

    public ArrayList<Rider> getRiders(){return riders;}
    public int getTeamID(){return teamID;}
    public String getTeamName(){return teamName;}
    public String getTeamDescription(){return teamDescription;}

}
