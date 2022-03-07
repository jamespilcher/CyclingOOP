import cycling.BadCyclingPortal;
import cycling.BadMiniCyclingPortal;
import cycling.CyclingPortalInterface;
import cycling.MiniCyclingPortalInterface;
import cycling.Segment;
import cycling.SegmentType;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortalInterface interface -- note you
 * will want to increase these checks, and run it on your CyclingPortal class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Diogo Pacheco
 * @version 1.0
 */
public class CyclingPortalInterfaceTestApp {

	/**
	 * Test method.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("The system compiled and started the execution...");

		Segment segment = new Segment(13, SegmentType.SPRINT);

		int segmentid = segment.getSegmentID();
		int stageid = segment.getStageID();
		SegmentType segmentType = segment.getSegmentType();

		System.out.println(segmentid);
		System.out.println(stageid);

		Segment segment2 = new Segment(13, SegmentType.SPRINT);
		int segment2id = segment2.getSegmentID();

		System.out.println(segmentType);

		MiniCyclingPortalInterface portal = new BadMiniCyclingPortal();
		int[] somethign = portal.getRaceIds();
		int arrayLength = somethign.length;
		System.out.println(arrayLength);
//		CyclingPortalInterface portal = new BadCyclingPortal();
		assert (portal.getRaceIds().length == 3)
				: "Innitial SocialMediaPlatform not empty as required or not returning an empty array.";
		
	}

}
