package test.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import exceptions.CLupException;
import model.Grocery;
import model.Position;
import model.Queue;
import model.Reservation;
import services.reservationManagement.imlpementation.QueueUpdateManagementImplementation;
import services.reservationManagement.interfaces.QueueUpdateManagement;
import utils.ReservationStatus;
import utils.ReservationType;

public class QueueUpdateManagementTest {
	
	private final int IDRESERVATION = 1;
	private final int IDRESERVATION_OPEN = 2;
	private final int IDRESERVATION_NOT_DB = 10;
	private final int IDGROCERY = 1;
	private final int GROCERY_CAPACITY = 2;
	private Grocery grocery;
	private Queue queue;
	private Reservation reservationOpen;
	
	private QueueUpdateManagement queueMod;

	@Before
	public void setUp() throws Exception {
		grocery = new Grocery();
		grocery.setIdgrocery(IDGROCERY);
		queue = new Queue();
		grocery.setQueue(queue);
		queue.setGrocery(grocery);
		grocery.setMaxSpotsInside(GROCERY_CAPACITY);
		queueMod = new MockQueueUpdateManagement();
		
		reservationOpen = new Reservation();
		reservationOpen.setIdreservation(IDRESERVATION_OPEN);
		reservationOpen.setQueue(queue);
	}

	@Test
	public void testRefreshQueue() {
		Reservation testReservation = new Reservation();
		testReservation.setTimeExit(Calendar.getInstance().getTime());
		queue.addReservation(testReservation);
		assertTrue(queue.getReservations().contains(testReservation));
		assertEquals(ReservationStatus.ALLOWED,
				testReservation.getStatus());
		
		queueMod.refreshQueue(grocery);
		
		assertTrue(!queue.getReservations().contains(testReservation));
		assertEquals(ReservationStatus.CLOSED,
				testReservation.getStatus());
	}
	
	@Test
	public void testSetIntoTheStore() {
		boolean test = false;
		
		try {
			test = queueMod.setIntoTheStore(IDRESERVATION);
		} catch (CLupException e) {
			fail("should not throw any exception");
		}
		
		assertTrue(test);
		assertEquals(ReservationStatus.ENTERED,
				queue.getReservations().get(0).getStatus());
	}
	
	@Test
	public void testSetIntoTheStoreWrongReservation() {
		boolean test = false;
		
		try {
			test = queueMod.setIntoTheStore(IDRESERVATION_NOT_DB);
			fail("should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertFalse(test);
	}
	
	@Test
	public void testSetIntoTheStoreWrongReservationStatus() {
		boolean test = false;
		
		try {
			test = queueMod.setIntoTheStore(IDRESERVATION_OPEN);
			fail("should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertFalse(test);
	}
	
	@Test
	public void testSetIntoTheStoreFullStore() {
		boolean test = true;
		
		// fill grocery
		for(int i = 0; i < GROCERY_CAPACITY; i++) {
			Reservation res = new Reservation();
			res.setIdreservation(IDRESERVATION + i + 1000);
			res.setQueue(queue);
			queue.addReservation(res);
			res.setStatus(ReservationStatus.ENTERED);
		}
		
		try {
			test = queueMod.setIntoTheStore(IDRESERVATION);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertFalse(test);
	}
	
	@Test
	public void testLineUp() {	
		assertNull(reservationOpen.getQueueTimer());
		Calendar estTime = Calendar.getInstance();
		estTime.set(Calendar.SECOND, estTime.get(Calendar.SECOND) + 1);
		reservationOpen.setEstimatedTime(estTime.getTime());
		
		try {
			reservationOpen = queueMod.lineUp(0, IDGROCERY, 0, 0);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}		
		
		assertNotNull(reservationOpen.getQueueTimer());
	}

	@Test
	public void testSetReservationTimer() {
		assertNull(reservationOpen.getQueueTimer());
		Calendar estTime = Calendar.getInstance();
		estTime.set(Calendar.SECOND, estTime.get(Calendar.SECOND) + 1);
		reservationOpen.setEstimatedTime(estTime.getTime());
		
		reservationOpen = queueMod.setReservationTimer(reservationOpen);
		assertNotNull(reservationOpen.getQueueTimer());		
	}

	class MockQueueUpdateManagement extends QueueUpdateManagementImplementation{
				
		Reservation reservation;
		Reservation reservation2;
		
		public MockQueueUpdateManagement() {
			reservation = new Reservation();
			reservation.setIdreservation(IDRESERVATION);
			reservation.setQueue(queue);
			
			queue.addReservation(reservation);
		}
		
		protected Reservation findReservation(int idreservation) {
			if(idreservation == reservation.getIdreservation()) {
				return reservation;
			} if(idreservation == reservationOpen.getIdreservation()) {
				return reservationOpen;
			}
			return null;
		}
		
		public Reservation invokeAddReservation(int iduser, int idgrocery, ReservationType type, Date date, Position position) {
			if(idgrocery == grocery.getIdgrocery()) {

				return reservationOpen;
			}
			return null;
		}
		
		protected void refresh (Reservation reservation) {
			
		}
	}
}
