package cycling;
import java.util.ArrayList;
class Race {
    private int raceID;
    private String name;
    private String description;

    ArrayList<Stage> stages = new ArrayList<Stage>();
    private static int numberOfRaces = 0;



    public Race(String name, String description){
        this.name = name;
        this.description = description;
        this.raceID = ++numberOfRaces;
    }

    public void addStage(Stage stage){
        this.stages.add(stage);
    }

    public ArrayList<Stage> getSegments() { return stages; }
    public int getRaceID() { return raceID; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
}
