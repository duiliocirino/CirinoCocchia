package test.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.junit.Before;
import org.junit.Test;

import exceptions.CLupException;
import model.Grocery;
import model.Position;
import model.Queue;
import model.Reservation;
import model.User;
import services.reservationManagement.imlpementation.ReservationHandlerImplementation;
import services.reservationManagement.interfaces.ReservationHandlerModule;
import utils.ReservationStatus;
import utils.ReservationType;

public class ReservationHandlerTest {
	
	private final int IDUSER = 1;
	private final int IDUSER_NOT_DB = 10;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 10;
	private final int IDQUEUE = 1;
	private final int IDRESERVATION = 1;
	private final int IDRESERVATION_NOT_DB = 10;
	private final Position CUST_POSITION = new Position(0, 0);
	
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
		Reservation reservation = new Reservation();
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setQueueTimer(new MockTimer());
		
		try {
			reservation = resMod.removeReservation(reservation);
		} catch (CLupException e) {
			fail("Should not reach this line");
		}
		
		assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
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
		Queue queue = reservation.getQueue();
		assertEquals(ReservationStatus.ALLOWED, reservation.getStatus());
		reservation.setStatus(ReservationStatus.ENTERED);
		int test = -1;
		
		try {
			test = resMod.closeReservation(reservation.getIdreservation());
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
		assertFalse(queue.getReservations().contains(reservation));
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
		private User user;
		private Reservation oldReservation;
		private Grocery grocery;
		private Queue queue;
		
		public MockReservationHandler() {
			user = new User();
			user.setIduser(IDUSER);
			oldReservation = new Reservation();
			oldReservation.setIdreservation(IDRESERVATION);
			oldReservation.setStatus(ReservationStatus.OPEN);
			oldReservation.setQueueTimer(new Timer());
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
			queue = new Queue();
			queue.setIdqueue(IDQUEUE);
			queue.addReservation(oldReservation);
			grocery.setQueue(queue);
			queue.setGrocery(grocery);
			
			oldReservation.setCustomer(user);
			oldReservation.setQueue(queue);
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
		
		protected Reservation findReservation(int idreservation) {
			if(idreservation == IDRESERVATION) {
				return oldReservation;
			}
			return null;
		}
		
		protected void persistReservation(Reservation reservation) {
		}
		
		protected void emRemoveReservation(Reservation reservation) {
			if(reservation.equals(oldReservation)) {
				oldReservation = null;
			}
		}
		
		protected void detachReservation(Reservation reservation) {
		}
			
		protected void invokeEstimateTime(Reservation reservation, Position position) {
			reservation.setEstimatedTime(Calendar.getInstance().getTime());
		}
	}

	class MockTimer extends Timer {
		public void cancel() {
			cancelInvoked = true;
		}
	}
}
