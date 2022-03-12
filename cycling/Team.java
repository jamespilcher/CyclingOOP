package cycling;
import java.util.ArrayList;
public class Team extends IdHaver{

    private int id;
    private String teamName;
    private String teamDescription;

    private ArrayList<Rider> riders=new ArrayList<Rider>();

    private static int numberOfTeams=0;

    public Team(String teamName, String teamDescription){
        this.teamName=teamName;
        this.teamDescription=teamDescription;
        id=++numberOfTeams;
        super.setId(id);
    }

    public void addRider(Rider rider){
        riders.add(rider);
    }

    public void removeRider(Rider rider){
        riders.remove(rider);
    }

    //not needed now?
    public void deleteRiders(){
        riders.clear();
    }


    public ArrayList<Rider> getRiders(){return riders;}
    public int getId(){return id;}
    public String getTeamName(){return teamName;}
    public String getTeamDescription(){return teamDescription;}


}
