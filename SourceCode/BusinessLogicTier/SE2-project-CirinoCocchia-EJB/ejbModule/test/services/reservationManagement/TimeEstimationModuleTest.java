package test.services.reservationManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exceptions.CLupException;
import model.Grocery;
import model.Position;
import model.Queue;
import model.Reservation;
import services.reservationManagement.imlpementation.TimeEstimationModuleImplementation;
import services.reservationManagement.interfaces.TimeEstimationModule;
import utils.ReservationStatus;

public class TimeEstimationModuleTest {
	
	private final int IDRESERVATION = 1;
	private final int IDRESERVATION_NOT_DB = 10;
	private final int IDQUEUE_EMPTY = 1;
	private final int IDQUEUE_ALMOST_FULL = 2;
	private final int IDQUEUE_FULL = 3;
	private final Position GROCERY_POS = new Position(0.0, 0.0);
	private final double RIDE_TIME = 60.0;
	private Reservation reservation;

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
		assertTrue(timeMod.ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC == spreadTime);
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
		assertTrue(2 * timeMod.ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC == spreadTime);
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

	class MockTimeEstimationModule extends TimeEstimationModuleImplementation{
		private Queue queue_empty;
		private Queue queue_almost_full;
		private Queue queue_full;
		private Grocery grocery;
		
		public MockTimeEstimationModule() {
			reservation = new Reservation();
			reservation.setIdreservation(IDRESERVATION);
			queue_empty = new Queue();
			queue_empty.setIdqueue(IDQUEUE_EMPTY);
			grocery = new Grocery();
			grocery.setLatitude(GROCERY_POS.getLat());
			grocery.setLongitude(GROCERY_POS.getLon());
			
			grocery.setQueue(queue_empty);
			queue_empty.setGrocery(grocery);
			
			reservation.setQueue(queue_empty);
			
			queue_almost_full = new Queue();
			queue_almost_full.setIdqueue(IDQUEUE_ALMOST_FULL);
			queue_full = new Queue();
			queue_full.setIdqueue(IDQUEUE_FULL);
		}
		
		protected Reservation findReservation(int idreservation) {
			if(idreservation == reservation.getIdreservation()) {
				return reservation;
			}
			return null;
		}
		
		protected List<Reservation> namedQueryReservationFindByInterval(Queue queue, Date start, Date end) {
			List<Reservation> newList = new ArrayList<>();
			int id = queue.getIdqueue();
			
			if(id == queue_almost_full.getIdqueue()) {
				for(int i = 0; i < this.INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME; i++) {
					Reservation res = new Reservation();
					newList.add(res);
				}
				return newList;
			} else if(id == queue_full.getIdqueue()) {
				for(int i = 0; i < 2* this.INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME; i++) {
					Reservation res = new Reservation();
					newList.add(res);
				}
				return newList;
			} else if(id == queue_empty.getIdqueue()) {
				return newList;
			}
			
			
			return null;
		}
		
		protected double invokeRideTime(Position origin, Position end) {
			return RIDE_TIME;
		}
		
	}

}
