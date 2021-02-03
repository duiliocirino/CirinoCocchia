package src.test.java.services.tools;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import src.main.java.model.Grocery;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.ReservationToolbox;
import src.main.java.services.tools.UserToolbox;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;
import src.test.resources.TestDataReservationToolbox;

public class ReservationToolboxTest {
	
	public final ReservationType TYPE_t = ReservationType.LINEUP;
	public final ReservationStatus STATUS_t = ReservationStatus.OPEN; 
	
	private static EntityManagerFactory emf;
	private EntityManager em;
	private TestDataReservationToolbox testData;
	private ReservationToolbox resTools; 
	
	private GroceryToolbox grocTools;
	private UserToolbox userTools;	
	
	@BeforeClass
	public static void classSetUp() {
		emf = Persistence.createEntityManagerFactory("SE2-project-CirinoCocchia-EJB"); 

	}
	
	@Before
	public void setUp() {
		em = emf.createEntityManager();
		testData = new TestDataReservationToolbox(em);
		testData.createTestData();
		
		resTools = new MockReservationTools(em);
		grocTools = new MockGroceryTools(em);
		userTools = new MockUserTools(em);
	}
	
	@After
	public void tearDown() {
		testData.removeTestData();
		
		if(em != null) {
			em.close();
		}
		
		if(emf != null) {
			emf.getCache().evictAll();
		}
	}
	
	@AfterClass
	public static void classCleanUp() {
		if(emf != null) {
			emf.close();
		}
	}
	
	@Test
	public void testFindReservation() {
		Reservation res = resTools.findReservation(testData.IDRESERVATION1);
		assertNotNull(res);
		assertEquals(testData.IDRESERVATION1, res.getIdreservation());
	}
	
	@Test
	public void testFindReservationFailure() {
		Reservation res = resTools.findReservation(testData.IDRESERVATION_NOT_DB);
		assertNull(res);
	}
	
	@Test
	public void testPersistReservation() {
		
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Queue queue = grocery.getQueue();
		User user = userTools.findUser(testData.IDCUSTOMER1);
		assertNotNull(grocery);
		assertNotNull(queue);
		assertNotNull(user);

		Reservation newRes = new Reservation();
		newRes.setQueue(queue);
		newRes.setCustomer(user);
		newRes.setType(TYPE_t.name());
		newRes.setStatus(STATUS_t);
		
		em.getTransaction().begin();
		resTools.persistReservation(newRes);
		em.getTransaction().commit();
		
		int idreservation_created = newRes.getIdreservation();
		
		assertNotNull(resTools.findReservation(idreservation_created));
		
		resTools.removeReservation(newRes);
	}
	
	@Test
	public void testRemoveReservation() {
		Reservation res = resTools.findReservation(testData.IDRESERVATION1);
		assertNotNull(res);
		Grocery grocery = res.getGrocery();
		assertNotNull(grocery);
		User customer = res.getCustomer();
		assertNotNull(customer);
		
		resTools.removeReservation(res);
		assertNotNull(res);
		
		res = resTools.findReservation(testData.IDRESERVATION1);
		assertNull(res);
		grocery = grocTools.findGrocery(grocery.getIdgrocery());
		assertNotNull(grocery);
		customer = userTools.findUser(customer.getIduser());
		assertNotNull(customer);
	}

	@Test
	public void testFindAllByGrocery() {
	
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Reservation old_res = resTools.findReservation(testData.IDRESERVATION1);
		assertNotNull(grocery);
		
		List<Reservation> reservations = resTools.findAllByGrocery(grocery);
		assertTrue(reservations.contains(old_res));
		
	}
	
	@Test
	public void testFindAllWithRemoval() {
		Reservation old_res = resTools.findReservation(testData.IDRESERVATION1);
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		
		em.getTransaction().begin();
		resTools.removeReservation(old_res);
		em.getTransaction().commit();
		
		List<Reservation>reservations = resTools.findAllByGrocery(grocery);
		assertFalse(reservations.contains(old_res));
	}
	
	@Test
	public void testFindAllWithAddition() {
		Reservation old_res = resTools.findReservation(testData.IDRESERVATION1);
		Reservation newRes = new Reservation();
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		newRes.setQueue(grocery.getQueue());
		newRes.setCustomer(old_res.getCustomer());
		newRes.setType(TYPE_t.name());
		newRes.setStatus(STATUS_t);
		
		em.getTransaction().begin();
		resTools.persistReservation(newRes);
		em.getTransaction().commit();
		
		List<Reservation> reservations = resTools.findAllByGrocery(grocery);
		assertTrue(reservations.contains(newRes));
		assertTrue(reservations.contains(old_res));
		resTools.removeReservation(newRes);

	}

	@Test
	public void testFindByInterval() {
		testData.createAdditionalReservation();
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Queue queue = grocery.getQueue();
		assertNotNull(queue);
		
		Reservation intoInterval = resTools.findReservation(testData.IDRESERVATION1);
		assertNotNull(intoInterval);
		Reservation notIntoInterval = resTools.findReservation(testData.IDRESERVATION2);
		assertNotNull(notIntoInterval);
		
		List<Reservation> reservations = resTools.findByInterval(queue, testData.START_INTERVAL, testData.END_INTERVAL);
		assertTrue(reservations.contains(intoInterval));
		assertFalse(reservations.contains(notIntoInterval));
		
		testData.removeAdditionalReservation();
	}
	
	@Test
	public void testFindByEndVisitInterval() {
		testData.createAdditionalReservation();
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Queue queue = grocery.getQueue();
		assertNotNull(queue);
		
		Reservation intoInterval = resTools.findReservation(testData.IDRESERVATION1);
		assertNotNull(intoInterval);
		Reservation notIntoInterval = resTools.findReservation(testData.IDRESERVATION2);
		assertNotNull(notIntoInterval);
		
		List<Reservation> reservations = resTools.findByEndVisitInterval(queue, testData.START_INTERVAL, testData.END_INTERVAL);
		assertTrue(reservations.contains(intoInterval));
		assertFalse(reservations.contains(notIntoInterval));
		
		testData.removeAdditionalReservation();
	}

	@Test
	public void testTotalVisitsInInterval() {
		testData.createAdditionalReservation();
				
		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Queue queue = grocery.getQueue();
		assertNotNull(queue);
		
		List<Integer> reservations = resTools.totalTimeSpentInInterval(queue, testData.START_INTERVAL, testData.END_INTERVAL);
		List<Long> reservationsNum = resTools.totalVisitsInInterval(queue, testData.START_INTERVAL, testData.END_INTERVAL);
		
		assertNotNull(reservationsNum);
		assertTrue(1 == reservationsNum.size());
		Long numReservations = reservationsNum.get(0);
		assertNotNull(numReservations);
		
		assertTrue((long)reservations.size() == numReservations);
		
		testData.removeAdditionalReservation();
	}
	
	@Test
	public void testTotalTimeSpentInInterval() {
		int difference = 20;		
		testData.createAdditionalReservation(difference);

		Grocery grocery = grocTools.findGrocery(testData.IDGROCERY1);
		Queue queue = grocery.getQueue();
		assertNotNull(queue);
		
		List<Integer> timesSpent = resTools.totalTimeSpentInInterval(queue, testData.START_INTERVAL, testData.END_INTERVAL);
		assertNotNull(timesSpent);
		assertNotNull(timesSpent.get(0));
		
		int timeSpent = timesSpent.get(0);
		assertTrue(difference == timeSpent);
		
		testData.removeAdditionalReservation();
	}

	class MockReservationTools extends ReservationToolbox {
		public MockReservationTools(EntityManager em) {
			this.em = em;
		}
	}
	
	class MockGroceryTools extends GroceryToolbox {
		public MockGroceryTools(EntityManager em) {
			this.em = em;
		}
	}
	
	class MockUserTools extends UserToolbox {
		public MockUserTools(EntityManager em) {
			this.em = em;
		}
	}

}
