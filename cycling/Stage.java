package cycling;
import java.time.LocalDateTime;
import java.util.ArrayList;
class Stage {
    private int stageID;
    private int raceID; //parent race id
    private StageType type;
    private String stageName;
    private String description;
    private double length;
    private LocalDateTime startTime;

    ArrayList<Segment> segments = new ArrayList<Segment>();

    private static int numberOfStages = 0;



    public Stage(int raceID, String stageName, String description, double length,
        LocalDateTime startTime, StageType type){
        this.raceID = raceID;
        this.stageName = stageName;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        this.stageID = ++numberOfStages;
    }

    public void addSegment(Segment segment){
        segments.add(segment);
    }
    
    public void removeStage(Stage stage){
        stages.remove(stage);
    }

    public ArrayList<Segment> getSegments() { return segments; }
    public int getStageID() { return stageID; }
    public int getRaceID() { return raceID; }
    public StageType getType() { return type; }
    public String getStageName() { return stageName; }
    public String getDescription() { return description; }
    public double getLength() {return length;}
    public LocalDateTime getStartTime() { return startTime; }
}
