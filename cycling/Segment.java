package cycling;


//abstract class then override
//time, length, 
public class Segment{
    private int segmentID;
    private int stageID;
    private double location;
    private SegmentType type;
    
    private static int numberOfSegments = 0;


    public Segment(int stageID, SegmentType type, double location){
        this.stageID = stageID;
        this.type = type;
        this.location = location;
        this.segmentID = ++numberOfSegments;
    }

    public int getSegmentID() { return segmentID; }
    public int getStageID() { return stageID; }
    public double getLocation() { return location;}
    public SegmentType getSegmentType() { return type; }
}