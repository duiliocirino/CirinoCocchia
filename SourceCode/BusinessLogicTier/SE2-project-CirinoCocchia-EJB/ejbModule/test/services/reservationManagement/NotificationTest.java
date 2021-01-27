package test.services.reservationManagement;

import static org.junit.Assert.*;

import javax.ws.rs.ProcessingException;

import org.junit.Test;

import model.Position;
import services.reservationManagement.interfaces.NotificationModule;

public class NotificationTest {
	
	private NotificationModule notModule = NotificationModule.getInstance();

	/**
	 * It has been proved experimentally that the ride time between positions
	 * (37.573242, 55.801279) and
	 * (115.665017, 38.100717) is
	 * 316820.88
	 */
	@Test
	public void testRideTime() {
		Position origin = new Position(37.573242, 55.801279);
		Position end = new Position(115.665017, 38.100717);
		
		try {
			assertTrue(316820.88 == notModule.rideTime(origin, end));
		} catch(ProcessingException e) {
			fail("It has been not possible to establish an Internet Connection "
					+ "with the Maps Provider");
		}
	}

}
