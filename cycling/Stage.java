package cycling;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
public class Stage extends IdHaver implements Serializable {
    private int id;
    private int raceID; //parent race id
    private StageType type;
    private String stageName;
    private String description;
    private String stageState;
    private Double length;
    private LocalDateTime startTime;

    ArrayList<Segment> segments = new ArrayList<Segment>();


    ArrayList<RiderStageResults> riderResultsList = new ArrayList<RiderStageResults>();

    private static int numberOfStages = 0;



    public Stage(int raceID, String stageName, String description, double length,
        LocalDateTime startTime, StageType type){
        this.raceID = raceID;
        this.stageName = stageName;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        id = ++numberOfStages;
        stageState = "in preparation";
        super.setId(id);

    }

    public void addRiderResultToStage(RiderStageResults rider){
        riderResultsList.add(rider);
    }

    public void addSegment(Segment segment){
        segments.add(segment);
    }

    public void removeSegment(Segment segment){ 
        segments.remove(segment);
    }

    public void deleteSegments(){ //not needed anymore
        segments.clear();
    }

    public ArrayList<Segment> getSegments() { 
        segments.sort((o1, o2)
        -> o1.getLocation().compareTo(
            o2.getLocation()));
        return segments;
    }
        
    public int getId() { return id; }
    public int getRaceID() { return raceID; }
    public StageType getType() { return type; }
    public String getStageName() { return stageName; }
    public String getDescription() { return description; }
    public double getLength() {return length;}
    public LocalDateTime getStartTime() { return startTime; }
    public String getStageState() { return stageState; }
    public ArrayList<RiderStageResults> getRiderResultsList(){ return riderResultsList; }

    public void setStageState(String stageState){
        this.stageState = stageState;
    }
}
