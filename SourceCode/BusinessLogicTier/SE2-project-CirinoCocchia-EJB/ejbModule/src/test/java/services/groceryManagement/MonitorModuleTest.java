package src.test.java.services.groceryManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Queue;
import src.main.java.services.groceryManagement.implementation.MonitorModuleImplementation;
import src.main.java.services.groceryManagement.interfaces.MonitorModule;
import src.main.java.utils.GroceryData;

public class MonitorModuleTest {
	
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 10;
	private final Integer[] VISITSTIMEMIN = {2, 3, 4, 5};
	private final int VISITSNUM = 4;
	
	private Queue queue;
	private MonitorModule monMod;

	@Before
	public void setUp() throws Exception {
		monMod = new MockMonitorModule();
	}

	@Test
	public void testGetGroceryStats() {
		Map<GroceryData, Float> groceryStats = null;
		Calendar calOneMonthAgo = Calendar.getInstance();
		calOneMonthAgo.set(Calendar.MONTH, calOneMonthAgo.get(Calendar.MONTH) - 1);
		Date date = calOneMonthAgo.getTime();
		
		
		try {
			groceryStats = monMod.getGroceryStats(IDGROCERY, date);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		int num_month_customers = (int) ((float)groceryStats
				.get(GroceryData.NUM_MONTH_CUSTOMERS));
		
		assertNotNull(groceryStats);
		assertEquals(VISITSNUM, num_month_customers);
		
		int sumOfMinutes = 0;
		for(int i : VISITSTIMEMIN) {
			sumOfMinutes += i;
		}
		
		Float avgTime = (float) (sumOfMinutes / VISITSNUM);
		
		assertEquals(avgTime, groceryStats
				.get(GroceryData.AVG_TIME_WEEK));
	}
	
	@Test
	public void testGetGroceryStatsWithDateSuccessive() {
		Map<GroceryData, Float> groceryStats = null;
		Calendar calOneMonthAgo = Calendar.getInstance();
		calOneMonthAgo.set(Calendar.MONTH, calOneMonthAgo.get(Calendar.MONTH) + 1);
		Date date = calOneMonthAgo.getTime();
		
		
		try {
			groceryStats = monMod.getGroceryStats(IDGROCERY, date);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		int num_month_customers = (int) ((float)groceryStats
				.get(GroceryData.NUM_MONTH_CUSTOMERS));
		
		assertNotNull(groceryStats);
		assertEquals(VISITSNUM, num_month_customers);
		
		int sumOfMinutes = 0;
		for(int i : VISITSTIMEMIN) {
			sumOfMinutes += i;
		}
		
		Float avgTime = (float) (sumOfMinutes / VISITSNUM);
		
		assertEquals(avgTime, groceryStats
				.get(GroceryData.AVG_TIME_WEEK));
	}
	
	@Test
	public void testGetGroceryStatsWithNullDate() {
		Map<GroceryData, Float> groceryStats = null;
		
		try {
			groceryStats = monMod.getGroceryStats(IDGROCERY, null);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		int num_month_customers = (int) ((float)groceryStats
				.get(GroceryData.NUM_MONTH_CUSTOMERS));
		
		assertNotNull(groceryStats);
		assertEquals(VISITSNUM, num_month_customers);
		
		int sumOfMinutes = 0;
		for(int i : VISITSTIMEMIN) {
			sumOfMinutes += i;
		}
		
		Float avgTime = (float) (sumOfMinutes / VISITSNUM);
		
		assertEquals(avgTime, groceryStats
				.get(GroceryData.AVG_TIME_WEEK));
	}
	
	@Test
	public void testGetGroceryStatsWrongGrocery() {
		Map<GroceryData, Float> groceryStats = null;
		
		try {
			groceryStats = monMod.getGroceryStats(IDGROCERY_NOT_DB, null);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(groceryStats);
	}
	
	@Test
	public void testGetGroceryStatsWrongData() {
		Float groceryStats = null;
		
		try {
			groceryStats = monMod.getGroceryStats(IDGROCERY, null, null);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(groceryStats);
	}

	
	class MockMonitorModule extends MonitorModuleImplementation {
		private List<Integer> visitIntervals;
		private List<Integer> numOfVisits;
		private Grocery grocery;
		
		public MockMonitorModule() {
			grocery = new Grocery();
			queue = new Queue();
			grocery.setIdgrocery(IDGROCERY);
			grocery.setQueue(queue);
			
			visitIntervals = new ArrayList<>();
			Collections.addAll(visitIntervals, VISITSTIMEMIN);
			
			numOfVisits = new ArrayList<>();
			Collections.addAll(numOfVisits, VISITSNUM);
		}
		
		protected Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}
		
		protected List<Integer> namedQueryReservationTotalVisitsInInterval(Queue queue, Date start, Date end) {
			if(queue.equals(grocery.getQueue())) {
				return numOfVisits;
			}
			return null;
		}
		
		protected List<Integer> namedQueryReservationTotalTimeSpentInInterval(Queue queue, Date start, Date end) {
			if(queue.equals(grocery.getQueue())) {
				return visitIntervals;
			}
			return null;
		}
		
	}

}
