package cycling;


//abstract class then override
//time, length, 
public class Segment extends IdHaver{
    private int id;
    private int stageID;
    private Double location;
    private SegmentType type;
    
    private static int numberOfSegments = 0;


    public Segment(int stageID, SegmentType type, double location){
        this.stageID = stageID;
        this.type = type;
        this.location = location;
        id = ++numberOfSegments;
        super.setId(id);

    }

    public int getId() { return id; }
    public int getStageID() { return stageID; }
    public Double getLocation() { return location;}
    public SegmentType getSegmentType() { return type; }
}
