package src.test.java.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.services.reservationManagement.implementation.TimeEstimationModuleImplementation;
import src.main.java.services.reservationManagement.interfaces.TimeEstimationModule;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.ReservationToolbox;
import src.main.java.utils.ReservationStatus;

public class TimeEstimationModuleTest {
	
	private final int IDRESERVATION = 1;
	private final int IDRESERVATION2 = 1;
	private final int IDRESERVATION_NOT_DB = 10;
	private final int IDQUEUE_EMPTY = 1;
	private final int IDQUEUE_ALMOST_FULL = 2;
	private final int IDQUEUE_FULL = 3;
	private final Position GROCERY_POS = new Position(0.0, 0.0);
	private final int OPENING_HOUR = 0;
	private final int CLOSING_HOUR = 24;
	private final int CLOSING_HOUR2 = 0;
	private final double RIDE_TIME = 60.0;
	private Reservation reservation;
	private Reservation reservation_closed;

	private TimeEstimationModule timeMod;
	
	@Before
	public void setUp() throws Exception {
		timeMod = new MockTimeEstimationModule();
	}

	@Test
	public void testEstimatedSpreadTimeEmpty() {
		Queue queue = new Queue();
		queue.setIdqueue(IDQUEUE_EMPTY);
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(Calendar.getInstance().getTime(), queue);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertFalse(-1.0 == spreadTime);
		assertTrue(0.0 == spreadTime);
	}
	
	@Test
	public void testEstimatedSpreadTimeAlmostFull() {
		Queue queue = new Queue();
		queue.setIdqueue(IDQUEUE_ALMOST_FULL);
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(Calendar.getInstance().getTime(), queue);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}	
		
		assertFalse(-1.0 == spreadTime);		
		assertTrue(TimeEstimationModule.ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC == spreadTime);
	}
	
	@Test
	public void testEstimatedSpreadTimeFull() {
		Queue queue = new Queue();
		queue.setIdqueue(IDQUEUE_FULL);
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(Calendar.getInstance().getTime(), queue);
		} catch (CLupException e) {
			System.err.println(e.getMessage());
			fail("Should not throw any exception");
		}
		
		assertFalse(-1.0 == spreadTime);
		assertTrue(2 * TimeEstimationModule.ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC == spreadTime);
	}

	@Test
	public void testEstimatedSpreadNullDate() {
		Queue queue = new Queue();
		queue.setIdqueue(IDQUEUE_FULL);
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(null, queue);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertTrue(-1.0 == spreadTime);
	}
	
	@Test
	public void testEstimatedSpreadPreceedingDate() {
		Queue queue = new Queue();
		queue.setIdqueue(IDQUEUE_FULL);
		
		Calendar cDate = Calendar.getInstance();
		int hours = cDate.get(Calendar.HOUR);
		cDate.set(Calendar.HOUR, hours - 1);
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(cDate.getTime(), queue);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertTrue(-1.0 == spreadTime);
	}
	
	@Test
	public void testEstimatedSpreadNullQueue() {
		Queue queue = null;
		
		double spreadTime = -1.0;
		try {
			spreadTime = timeMod.estimatedSpreadTime(Calendar.getInstance().getTime(), queue);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertTrue(-1.0 == spreadTime);
	}
	
	@Test
	public void testGetEstimatedTimeSeconds() {
		Calendar nowCal = Calendar.getInstance();
		reservation.setEstimatedTime(nowCal.getTime());
		
		double seconds = -1.0;
		try {
			seconds = timeMod.getEstimatedTimeSeconds(IDRESERVATION);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		assertTrue(nowCal.get(Calendar.SECOND) == seconds);
	}
	
	@Test
	public void testGetEstimatedTimeSecondsWrongReservation() {
		Calendar nowCal = Calendar.getInstance();
		reservation.setEstimatedTime(nowCal.getTime());
		
		double seconds = -1.0;
		try {
			seconds = timeMod.getEstimatedTimeSeconds(IDRESERVATION_NOT_DB);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertTrue(-1.0 == seconds);
	}

	@Test
	public void testEstimateTime() {
		reservation.setStatus(ReservationStatus.OPEN);
		assertNull(reservation.getEstimatedTime());
		
		Date estimatedTime = null;
		try {
			estimatedTime = timeMod.estimateTime(reservation, new Position(1, 1));
		} catch (CLupException e) {
			System.out.println(e.getMessage());
			fail("Should not throw any exception");
		}
		
		assertNotNull(estimatedTime);
		assertNotNull(reservation.getEstimatedTime());
	}
	
	@Test
	public void testEstimateTimeWrongStatus() {
		assertNull(reservation.getEstimatedTime());
		
		Date estimatedTime = null;
		try {
			estimatedTime = timeMod.estimateTime(reservation, new Position(1, 1));
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNull(estimatedTime);
		assertNull(reservation.getEstimatedTime());
	}
	
	@Test
	public void testEstimateTimeWrongReservation() {
		reservation = null;
		
		Date estimatedTime = null;
		try {
			estimatedTime = timeMod.estimateTime(reservation, new Position(1, 1));
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(estimatedTime);
	}
	
	@Test
	public void testEstimateTimeWrongPosition() {
		reservation.setStatus(ReservationStatus.OPEN);
		assertNull(reservation.getEstimatedTime());
		
		Date estimatedTime = null;
		try {
			estimatedTime = timeMod.estimateTime(reservation, null);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(estimatedTime);
		assertNull(reservation.getEstimatedTime());
	}
	
	@Test
	public void testEstimateTimeWrongHour() {
		reservation_closed.setStatus(ReservationStatus.OPEN);
		assertNull(reservation_closed.getEstimatedTime());
		
		Date estimatedTime = null;
		try {
			estimatedTime = timeMod.estimateTime(reservation_closed, new Position(0,0));
			fail("Should not reach this line");
		} catch (CLupException e) {
			System.out.println(e.getMessage());
			assertTrue(true);
		}
		
		assertNull(estimatedTime);
		assertNull(reservation.getEstimatedTime());
	}

	class MockTimeEstimationModule extends TimeEstimationModuleImplementation{
		
		public MockTimeEstimationModule() {
			this.grocTools = new MockGrocTools();
			this.resTools = new MockResTools();
		}
	
		
		protected double invokeRideTime(Position origin, Position end) {
			return RIDE_TIME;
		}
		
	}
	
	class MockResTools extends ReservationToolbox {
		
		public MockResTools() {
			
		}
		
		public Reservation findReservation(int idreservation) {
			if(idreservation == reservation.getIdreservation()) {
				return reservation;
			}
			return null;
		}
		
		public List<Reservation> findByInterval(Queue queue, Date start, Date end ){
			List<Reservation> newList = new ArrayList<>();
			int id = queue.getIdqueue();
			
			if(id == IDQUEUE_ALMOST_FULL) {
				for(int i = 0; i < TimeEstimationModule.INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME; i++) {
					Reservation res = new Reservation();
					newList.add(res);
				}
				return newList;
			} else if(id == IDQUEUE_FULL) {
				for(int i = 0; i < 2* TimeEstimationModule.INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME; i++) {
					Reservation res = new Reservation();
					newList.add(res);
				}
				return newList;
			} else if(id == IDQUEUE_EMPTY) {
				return newList;
			}
			
			
			return null;
		}
	}
	
	class MockGrocTools extends GroceryToolbox {
		
		private Queue queue_empty;
		private Queue queue_empty2;
		private Queue queue_almost_full;
		private Queue queue_full;
		private Grocery grocery;
		private Grocery grocery2;
		
		public MockGrocTools() {
			reservation = new Reservation();
			reservation.setIdreservation(IDRESERVATION);
			reservation_closed = new Reservation();
			reservation_closed.setIdreservation(IDRESERVATION2);
			queue_empty = new Queue();
			queue_empty.setIdqueue(IDQUEUE_EMPTY);
			queue_empty2 = new Queue();
			queue_empty2.setIdqueue(IDQUEUE_EMPTY);
			grocery = new Grocery();
			grocery.setLatitude(GROCERY_POS.getLat());
			grocery.setLongitude(GROCERY_POS.getLon());
			grocery.setOpeningHour(OPENING_HOUR);
			grocery.setClosingHour(CLOSING_HOUR);
			grocery2 = new Grocery();
			grocery2.setLatitude(GROCERY_POS.getLat());
			grocery2.setLongitude(GROCERY_POS.getLon());
			grocery2.setOpeningHour(OPENING_HOUR);
			grocery2.setClosingHour(CLOSING_HOUR2);
			
			grocery.setQueue(queue_empty);
			grocery.setQueue(queue_empty2);
			queue_empty.setGrocery(grocery);
			queue_empty2.setGrocery(grocery2);
			
			reservation.setQueue(queue_empty);
			reservation_closed.setQueue(queue_empty2);
			
			queue_almost_full = new Queue();
			queue_almost_full.setIdqueue(IDQUEUE_ALMOST_FULL);
			queue_full = new Queue();
			queue_full.setIdqueue(IDQUEUE_FULL);
		}
	}

}
