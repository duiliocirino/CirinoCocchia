package test.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import model.Grocery;
import model.Queue;
import model.Reservation;
import utils.ReservationStatus;

public class QueueTest {
	
	private final int QUEUE_ID = 1;
	private final int GROCERY_ID = 1;
	private final int MAX_SPOTS_GROCERY = 10;
	// to not throw OutOfMemeoryErrors
	private final int MAX_RES_THRESHOLD = 20;
	private Queue queue;
	private Grocery grocery;
	
	@Before
	public void setUp() {
		queue = new Queue();
		queue.setIdqueue(QUEUE_ID);
		
		grocery = new Grocery();
		grocery.setIdgrocery(GROCERY_ID);
		grocery.setMaxSpotsInside(MAX_SPOTS_GROCERY);
		
		grocery.setQueue(queue);
		queue.setGrocery(grocery);
	}
	
	private void setFullTest() {
		for(int i = 0; i < grocery.getMaxSpotsInside(); i++) {
			Reservation res = new Reservation();
			res.setIdreservation(i);
			queue.addReservation(res);
			// mock the behaviour of the turnstile
			res.setStatus(ReservationStatus.ENTERED);
		}
	}

	@Test
	public void testIsFull() {
		
		assertFalse(queue.isFull());
		
		setFullTest();
		
		assertTrue(queue.isFull());
	}
	
	private List<Reservation> setOnTheQueueTest() {
		List<Reservation> testReservations = new ArrayList<>();
				
		for(int i = grocery.getMaxSpotsInside(); i < MAX_RES_THRESHOLD; i++) {
			Reservation res = new Reservation();
			res.setIdreservation(i);
			queue.addReservation(res);
			testReservations.add(res);
		}
		
			
		return testReservations;
	}

	@Test
	public void testReservationsOnTheQueue() {
		
		assertEquals(0, queue.reservationsOnTheQueue().size());
		
		List<Reservation> mockReservations = setOnTheQueueTest();
		
		assertEquals(mockReservations, queue.reservationsOnTheQueue());
	}
	
	@Test
	public void testReservationsOnTheFullQueue() {
		
		assertEquals(0, queue.reservationsOnTheQueue().size());
		
		setFullTest();
				
		assertEquals(MAX_SPOTS_GROCERY, queue.getReservations().size());
		
		List<Reservation> mockReservations = setOnTheQueueTest();
		
		assertNotEquals(MAX_SPOTS_GROCERY, queue.getReservations().size());
		assertTrue(queue.isFull());
		assertNotEquals(MAX_SPOTS_GROCERY, queue.getReservations().size());
		assertNotEquals(mockReservations.size(), queue.getReservations().size());
		assertEquals(MAX_SPOTS_GROCERY + mockReservations.size(), 
				queue.getReservations().size());
	}

}
