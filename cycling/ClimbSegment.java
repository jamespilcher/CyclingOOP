package cycling;
public class ClimbSegment extends Segment {
    private double averageGradient;
    private double length;

    public ClimbSegment(int stageID, SegmentType type, double location, double averageGradient, double length){
        super(stageID, type, location);
        this.averageGradient = averageGradient;
        this.length = length;
    }

    public double getAverageGradient() {return averageGradient;}
    public double getLength() {return length;}

    
}