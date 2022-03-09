import cycling.BadCyclingPortal;
import cycling.BadMiniCyclingPortal;
import cycling.CyclingPortalInterface;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;
import cycling.MiniCyclingPortalInterface;
import cycling.Segment;
import cycling.SegmentType;
import cycling.Team;
import cycling.CyclingPortal;

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
	 * @throws InvalidNameException
	 * @throws IllegalNameException
	 * @throws IDNotRecognisedException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws IllegalNameException, InvalidNameException, IllegalArgumentException, IDNotRecognisedException {
		System.out.println("The system compiled and started the execution...");

		// Segment segment = new Segment(13, SegmentType.SPRINT, 12);

		// int segmentid = segment.getSegmentID();
		// int stageid = segment.getStageID();
		// SegmentType segmentType = segment.getSegmentType();

		// System.out.println(segmentid);
		// System.out.println(stageid);

		// Segment segment2 = new Segment(13, SegmentType.SPRINT, 12);
		// int segment2id = segment2.getSegmentID();

		// System.out.println(segmentType);

		//MiniCyclingPortalInterface portal = new BadMiniCyclingPortal();
		// int[] somethign = portal.getRaceIds();
		// int arrayLength = somethign.length;
		// System.out.println(arrayLength);

		

		CyclingPortal portal = new CyclingPortal();
		portal.createTeam("team1", "description");
		portal.createTeam("team2", "description");
		portal.createTeam("team3", "description");
		portal.createTeam("team4", "description");
		portal.createTeam("team5", "description");
		portal.createTeam("team6", "description");
		portal.createTeam("team7", "description");
		portal.createTeam("team8", "description");
		portal.createTeam("team9", "description");
		portal.createTeam("team10", "description");
		//System.out.println(portal.createTeam("team4", "description"));

		portal.createRider(1, "hi", 1990);
		portal.removeTeam(1);
		portal.createRider(10, "hi", 1990);
		int[] array=portal.getTeams();

		for(int num:array){
			System.out.println(num);
		}

//		CyclingPortalInterface portal = new BadCyclingPortal();
		assert (portal.getRaceIds().length == 3)
				: "Innitial SocialMediaPlatform not empty as required or not returning an empty array.";
		
	}

}
