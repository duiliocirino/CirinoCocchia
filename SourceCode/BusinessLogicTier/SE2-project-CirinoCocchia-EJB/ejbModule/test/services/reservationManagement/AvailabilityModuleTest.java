package test.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exceptions.CLupException;
import model.Grocery;
import model.Position;
import model.Reservation;
import model.User;
import services.reservationManagement.imlpementation.AvailabilityModuleImplementation;
import services.reservationManagement.interfaces.AvailabilityModule;
import utils.ReservationType;

public class AvailabilityModuleTest {
	
	private final int IDUSER = 1;
	private final int IDUSER_NOT_DB = 2;
	private final int IDGROCERY = 1;	
	private final int IDGROCERY_NOT_DB = 2;
	
	private AvailabilityModule avMod;

	@Before
	public void setUp() throws Exception {
		avMod = new MockAvailabilityModule();
	}

	@Test
	public final void testCheckAvailability() {
		List<Reservation> availables = null;
		
		try {
			availables = avMod.checkAvailability(IDUSER, ReservationType.NONE, IDGROCERY, null, -1, -1);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertEquals(1, availables.size());
		assertNotNull(availables.get(0).getEstimatedTime());
	}
	
	@Test
	public final void testCheckAvailabilityWrongCredentials() {
		List<Reservation> availables = null;
		
		try {
			availables = avMod.checkAvailability(IDUSER_NOT_DB, ReservationType.NONE, IDGROCERY, null, -1, -1);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(availables);
		
		try {
			availables = avMod.checkAvailability(IDUSER, ReservationType.NONE, IDGROCERY_NOT_DB, null, -1, -1);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(availables);
	}
	
	class MockAvailabilityModule extends AvailabilityModuleImplementation {
		private User user;
		private Grocery grocery;
		
		public MockAvailabilityModule() {
			user = new User();
			user.setIduser(IDUSER);
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
		}
		
		protected User findUser(int iduser) {
			if(iduser == user.getIduser()) {
				return user;
			}
			return null;
		}
		
		protected Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}
		
		protected void invokeEstimateTime(Reservation reservation, Position position) {			
			reservation.setEstimatedTime(Calendar.getInstance().getTime());
		}
	}

}
