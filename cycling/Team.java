package cycling;
import java.util.ArrayList;
public class Team {

    private int teamID;
    private String teamName;
    private String teamDescription;

    private ArrayList<Rider> riders=new ArrayList<Rider>();
    private ArrayList<Team> teams=new ArrayList<Team>();

    private static int numberOfTeams=0;

    public Team(String teamName, String teamDescription){
        this.teamName=teamName;
        this.teamDescription=teamDescription;
        this.teamID=++numberOfTeams;
    }

    public ArryList<Rider> getRiders(){return riders;}
    public ArryList<Team> getTeams(){return teams;}
    public int getTeamID(){return teamID;}
    public String getTeamName(){return teamName;}
    public String getTeamDescription(){return teamDescription;}

}
