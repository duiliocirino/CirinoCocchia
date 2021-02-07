package src.test.java.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.implementation.ReservationHandlerImplementation;
import src.main.java.services.reservationManagement.interfaces.ReservationHandlerModule;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.ReservationToolbox;
import src.main.java.services.tools.UserToolbox;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;

public class ReservationHandlerTest {
	
	private final int IDUSER = 1;
	private final int IDUSER_NOT_DB = 10;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 10;
	private final int IDQUEUE = 1;
	private final int IDRESERVATION = 1;
	private final int IDRESERVATION_OPEN = 2;
	private final int IDRESERVATION_NOT_DB = 10;
	private final Position CUST_POSITION = new Position(0, 0);
	
	private Reservation reservationOpen;
	
	private ReservationHandlerModule resMod;
	private boolean cancelInvoked = false;

	@Before
	public void setUp() throws Exception {
		resMod = new MockReservationHandler();
		cancelInvoked = false;
	}

	@Test
	public void testAddReservation() {
		Reservation reservation = null;
		ReservationType type = ReservationType.NONE;
		Date bookTime = null;
		
		try {
			reservation = resMod.addReservation(IDUSER, IDGROCERY, type, bookTime, CUST_POSITION);
		} catch (CLupException e) {
			fail("Should not throw any exceptin");
		}
		
		assertNotNull(reservation);
		assertEquals(IDUSER, reservation.getCustomer().getIduser());
		assertEquals(IDGROCERY, reservation.getGrocery().getIdgrocery());
		assertNotNull(reservation.getEstimatedTime());		
	}
	
	@Test
	public void testAddReservationWrongGrocery() {
		Reservation reservation = null;
		ReservationType type = ReservationType.NONE;
		Date bookTime = null;
		
		try {
			reservation = resMod.addReservation(IDUSER, IDGROCERY_NOT_DB, type, bookTime, CUST_POSITION);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(reservation);
	}
	
	@Test
	public void testAddReservationWrongUser() {
		Reservation reservation = null;
		ReservationType type = ReservationType.NONE;
		Date bookTime = null;
		
		try {
			reservation = resMod.addReservation(IDUSER_NOT_DB, IDGROCERY, type, bookTime, CUST_POSITION);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(reservation);
	}
	
	@Test
	public void testAddReservationWrongPosition() {
		Reservation reservation = null;
		ReservationType type = ReservationType.NONE;
		Date bookTime = null;
		
		try {
			reservation = resMod.addReservation(IDUSER, IDGROCERY, type, bookTime, null);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(reservation);
	}

	@Test
	public void testRemoveReservation() {
		Reservation reservation = resMod.getReservation(IDRESERVATION);
		Queue queue = reservation.getQueue();
		assertEquals(ReservationStatus.ALLOWED, reservation.getStatus());
		
		try {
			reservation = resMod.removeReservation(reservation);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
		assertFalse(queue.getReservations().contains(reservation));
		assertNull(resMod.getReservation(IDRESERVATION));
	}
	
	@Test
	public void testRemoveReservationOpened() {
		reservationOpen = new Reservation();
		User customer = new User();
		reservationOpen.setIdreservation(IDRESERVATION_OPEN);
		reservationOpen.setStatus(ReservationStatus.OPEN);
		reservationOpen.setQueueTimer(new MockTimer());
		reservationOpen.setCustomer(customer);
		
		try {
			reservationOpen = resMod.removeReservation(reservationOpen);
		} catch (CLupException e) {
			fail("Should not reach this line");
		}
		
		assertEquals(ReservationStatus.CLOSED, reservationOpen.getStatus());
		assertTrue(cancelInvoked);
	}
	
	@Test
	public void testRemoveReservationWrongReservation() {
		Reservation reservation = resMod.getReservation(IDRESERVATION_NOT_DB);
		assertNull(reservation);
		
		try {
			reservation = resMod.removeReservation(reservation);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCloseReservation() {
		Reservation reservation = resMod.getReservation(IDRESERVATION);
		assertEquals(ReservationStatus.ALLOWED, reservation.getStatus());
		reservation.setStatus(ReservationStatus.ENTERED);
		int test = -1;
		
		try {
			test = resMod.closeReservation(reservation.getIdreservation());
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
		assertNotNull(resMod.getReservation(IDRESERVATION));
		assertEquals(0, test);
	}
	
	@Test
	public void testCloseReservationWrongReservation() {
		Reservation reservation = new Reservation();
		reservation.setIdreservation(IDRESERVATION_NOT_DB);
		int test = -1;

		try {
			test = resMod.closeReservation(reservation.getIdreservation());
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertEquals(-1, test);
	}
	
	@Test
	public void testCloseReservationWrongStatus() {
		Reservation reservation = resMod.getReservation(IDRESERVATION);
		Queue queue = reservation.getQueue();
		assertEquals(ReservationStatus.ALLOWED, reservation.getStatus());
		int test = -1;
		
		try {
			test = resMod.closeReservation(reservation.getIdreservation());
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNotEquals(ReservationStatus.CLOSED, reservation.getStatus());
		assertTrue(queue.getReservations().contains(reservation));
		assertNotNull(resMod.getReservation(IDRESERVATION));
		assertEquals(-1, test);
	}
	
	class MockReservationHandler extends ReservationHandlerImplementation{
		
		private Reservation oldReservation;
		
		public MockReservationHandler() {
			oldReservation = new Reservation();
			oldReservation.setIdreservation(IDRESERVATION);
			oldReservation.setStatus(ReservationStatus.OPEN);
			oldReservation.setQueueTimer(new Timer());
			
			this.resTools = new MockResTools(oldReservation);
			this.usrTools = new MockUsrTools();
			this.grocTools = new MockGrocTools(oldReservation);
						
			User user = this.usrTools.findUser(IDUSER);
			oldReservation.setCustomer(user);
			Queue queue = this.grocTools.findGrocery(IDGROCERY).getQueue();
			oldReservation.setQueue(queue);
		}
			
		protected void invokeEstimateTime(Reservation reservation, Position position) {
			reservation.setEstimatedTime(Calendar.getInstance().getTime());
		}
	}
	
	class MockGrocTools extends GroceryToolbox {
		
		private Grocery grocery;
		private Queue queue;
		 
		public MockGrocTools(Reservation old) {
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
			queue = new Queue();
			queue.setIdqueue(IDQUEUE);
			queue.addReservation(old);
			grocery.setQueue(queue);
			queue.setGrocery(grocery);
		}
		
		public Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}

	}
	
	class MockUsrTools extends UserToolbox {
		
		private User user;
		
		public MockUsrTools() {
			user = new User();
			user.setIduser(IDUSER);
		}
		
		public User findUser(int iduser) {
			if(iduser == user.getIduser()) {
				return user;
			}
			return null;
		}
	}
	
	class MockResTools extends ReservationToolbox {
		
		private Reservation oldReservation;
		
		public MockResTools(Reservation res) {
			super();
			this.oldReservation = res;			
		}
		
		public Reservation findReservation(int idreservation) {
			if(idreservation == IDRESERVATION) {
				return oldReservation;
			} else if(idreservation == IDRESERVATION_OPEN) {
				return reservationOpen;
			}
			return null;
		}
		
		public void persistReservation(Reservation reservation) {
		}
		
		public void detachReservation(Reservation reservation) {
		}
		
		public void removeReservation(Reservation reservation) {
			if(reservation.equals(oldReservation)) {
				oldReservation = null;
			}
		}

	}

	class MockTimer extends Timer {
		public void cancel() {
			cancelInvoked = true;
		}
	}
}
