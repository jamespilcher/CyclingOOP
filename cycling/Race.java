package cycling;
import java.util.ArrayList;
class Race extends IdHaver{
    private int id;
    private String name;
    private String description;

    ArrayList<Stage> stages = new ArrayList<Stage>();
    private static int numberOfRaces = 0;



    public Race(String name, String description){
        this.name = name;
        this.description = description;
        id = ++numberOfRaces;
        super.setId(id);
    }


    public void addStage(Stage stage){
        stages.add(stage);
    }
    

    public void removeStage(Stage stage){
        stages.remove(stage);
    }

    public double totalLength(){ 
        double totLength = 0D;
        for (Stage stage : stages){
            totLength += stage.getLength();
        }
        return totLength;
    }

    public ArrayList<Stage> getStages(){
        //sort them here
        stages.sort((o1, o2)
        -> o1.getStartTime().compareTo(
            o2.getStartTime()));
        return stages; 
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
}
