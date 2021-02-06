package src.test.java.services.reservationManagement;

import static org.junit.Assert.*;

import javax.ws.rs.ProcessingException;

import org.junit.Test;

import src.main.java.model.Position;
import src.main.java.services.reservationManagement.interfaces.NotificationModule;

public class NotificationTest {
	
	private NotificationModule notModule = NotificationModule.getInstance();

	/** 
	 * It has been proven that ride time between
	 * (9.218935, 45.465129) and
	 * (9.000001, 45.0) is
	 * 3150.9
	 */
	@Test
	public void testRideTime() {
		Position origin = new Position(9.218935, 45.465129);
		Position end = new Position(9.000001, 45.0);
		
		try {
			assertTrue(3150.9 == notModule.rideTime(origin, end));
		} catch(ProcessingException e) {
			fail("It has been not possible to establish an Internet Connection "
					+ "with the Maps Provider");
		}
	}
	
	/**
	 * This tests that an uncoherent position search (too far away) is not
	 * validated
	 */
	@Test
	public void testRideTimeFarAway() {
		Position origin = new Position(9.218935, 45.465129);
		Position end = new Position(5,5);
		double time = 0;
		
		try {
			time = notModule.rideTime(origin, end);
			fail("Should not reach this line");
		} catch(UnsupportedOperationException e) {
		} finally {
			if(time != 0) {
				fail();
			}
		}
	}


}

/*
 * It has been proved experimentally that the ride time between positions
 * (37.573242, 55.801279) and
 * (115.665017, 38.100717) is
 * 316820.88
 */
