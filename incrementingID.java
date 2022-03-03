//time, length, 
public class Segment{
    private int segmentID;
    private int stageID;
    private SegmentType segmentType;
    
    private static int numberOfSegments = 0;


    public Segment(int stageID, SegmentType segmentType){
        this.stageID = stageID;
        this.segmentType = segmentType;
        this.segmentID = ++numberOfSegments;
    }

    public int getSegmentID() { return segmentID; }
    public int getStageID() { return stageID; }
    public SegmentType getSegmentType() { return segmentType; }
    
}


// array list looks like (from stage.java):

    ArrayList<Segment> segments = new ArrayList<Segment>();
    public ArrayList<Segment> getSegments() { return segments; }
