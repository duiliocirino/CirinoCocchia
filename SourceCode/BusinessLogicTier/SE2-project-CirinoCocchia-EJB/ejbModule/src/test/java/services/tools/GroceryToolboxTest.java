package src.test.java.services.tools;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import src.main.java.model.*;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.UserToolbox;
import src.test.resources.TestDataGroceryToolbox;
import static src.test.resources.TestDataGroceryToolbox.*;

import java.util.List;

public class GroceryToolboxTest {
	
	private static EntityManagerFactory emf;
	private EntityManager em;
	private TestDataGroceryToolbox testData;
	private GroceryToolbox grocTools;

	private UserToolbox userTools;
	
	@BeforeClass
	public static void classSetUp() {
		emf = Persistence.createEntityManagerFactory("SE2-project-CirinoCocchia-EJB"); 
	}

	@Before
	public void setUp() {
		em = emf.createEntityManager();
		testData = new TestDataGroceryToolbox(em);
		testData.createTestData();
		
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
	public void testFindGrocerySuccessful() {
		Grocery grocery1 = grocTools.findGrocery(IDGROCERY1);
		
		assertNotNull(grocery1);
		assertEquals(IDGROCERY1, grocery1.getIdgrocery());
		assertEquals(NAME_GROCERY1, grocery1.getName());
		assertEquals(IDOWNER, grocery1.getOwner().getIduser());
	}
	
	@Test
	public void testFindGroceryFailure() {
		Grocery grocery1 = grocTools.findGrocery(IDGROCERY_NOT_DB);
		
		assertNull(grocery1);
	}

	@Test
	public void testPersistGrocery() {
		User owner = userTools.findUser(IDOWNER);
		assertNotNull(owner);
		
		Grocery newGrocery = grocTools.findGrocery(IDGROCERY_NOT_DB);
		assertNull(newGrocery);
		newGrocery = new Grocery();
		newGrocery.setOwner(owner);
		newGrocery.setName(NAME_GROCERY_NOT_DB);
		newGrocery.setLatitude(POS_GROCERY_NOT_DB.getLat());
		newGrocery.setLongitude(POS_GROCERY_NOT_DB.getLon());
		
		em.getTransaction().begin();
		grocTools.persistGrocery(newGrocery);
		em.getTransaction().commit();
		
		IDGROCERY_NOT_DB = newGrocery.getIdgrocery();
		Grocery findGrocery = grocTools.findGrocery(IDGROCERY_NOT_DB);
		assertNotNull(findGrocery);
		assertEquals(newGrocery, findGrocery);
		
		grocTools.removeGrocery(newGrocery);
	}

	@Test
	public void testRemoveGrocery() {
		Grocery grocery1 = grocTools.findGrocery(IDGROCERY1);
		assertNotNull(grocery1);
		User owner = grocery1.getOwner();
		
		em.getTransaction().begin();
		grocTools.removeGrocery(grocery1);
		em.getTransaction().commit();
		
		assertNull(grocTools.findGrocery(IDGROCERY1));
		assertNotNull(userTools.findUser(owner.getIduser()));
	}

	@Test
	public void testFindAllGroceries() {
		List<Grocery> groceries = null;
		
		Grocery grocery1 = grocTools.findGrocery(IDGROCERY1);
		Grocery grocery2 = grocTools.findGrocery(IDGROCERY2);
		
		groceries = grocTools.findAllGroceries();
		
		assertNotNull(groceries);
		assertTrue(groceries.contains(grocery1));
		assertTrue(groceries.contains(grocery2));
	}

	@Test
	public void testFindGroceryByName() {
		List<Grocery> groceries = null;
		
		groceries = grocTools.findGroceryByName(NAME_GROCERY1);
		assertNotNull(groceries);
		assertNotNull(groceries.get(0));
		Grocery grocery1 = groceries.get(0);
		
		assertEquals(NAME_GROCERY1, grocery1.getName());
		
		Grocery findGrocery = grocTools.findGrocery(IDGROCERY1);
		
		assertEquals(grocery1, findGrocery);
	}
	
	@Test
	public void testFindGroceryByUnexistentName() {
		List<Grocery> groceries = null;
		
		groceries = grocTools.findGroceryByName(NAME_GROCERY_NOT_DB);
		assertNotNull(groceries);
		assertTrue(0 == groceries.size());
	}

	@Test
	public void testFindCustomersFavourites() {
		testData.createFavouriteGroceriesData();
		
		try {
			User customer = userTools.findUser(ID_CUSTOMER);
			List<Grocery> favourites = null;
			
			int numFavourites = 1;
			favourites = grocTools.findCustomersFavourites(customer, numFavourites);
			assertNotNull(favourites);
			assertEquals(numFavourites, favourites.size());
			assertNotNull(favourites.get(0));
			assertEquals(IDGROCERY1, favourites.get(0).getIdgrocery());
			
			numFavourites = 2;
			favourites = grocTools.findCustomersFavourites(customer, numFavourites);
			assertNotNull(favourites);
			assertTrue(numFavourites == favourites.size());
			assertNotNull(favourites.get(0));
			assertEquals(IDGROCERY1, favourites.get(0).getIdgrocery());
			assertNotNull(favourites.get(1));
			assertEquals(IDGROCERY2, favourites.get(1).getIdgrocery());

			numFavourites = 3;
			favourites = grocTools.findCustomersFavourites(customer, numFavourites);
			assertNotNull(favourites);
			assertTrue(numFavourites == favourites.size());
			assertNotNull(favourites.get(0));
			assertEquals(IDGROCERY1, favourites.get(0).getIdgrocery());
			assertNotNull(favourites.get(1));
			assertEquals(IDGROCERY2, favourites.get(1).getIdgrocery());
			assertNotNull(favourites.get(2));
			assertEquals(ID_GROCERY3, favourites.get(2).getIdgrocery());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw any exception");
		} finally {
			testData.removeFavouriteGroceriesData();
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

/*
Map props = new HashMap();

// Ensure RESOURCE_LOCAL transactions is used.
props.put(TRANSACTION_TYPE,
  PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

props.put(JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
props.put(JDBC_URL, "jdbc:mysql://localhost:3306/db_project_se2");
props.put(JDBC_USER, "root");
props.put(JDBC_PASSWORD, "password");
 
// Configure logging. FINE ensures all SQL is shown
props.put(LOGGING_LEVEL, "FINE");
props.put(LOGGING_TIMESTAMP, "false");
props.put(LOGGING_THREAD, "false");
props.put(LOGGING_SESSION, "false");
 
// Ensure that no server-platform is configured
props.put(TARGET_SERVER, TargetServer.None);

EntityManagerFactory emf = Persistence.createEntityManagerFactory("SE2-project-CirinoCocchia-EJB"); 
System.out.println(emf);
EntityManager em = emf.createEntityManager();
System.out.println(em);
testData = new TestDataGroceryToolbox(em);
// testData.createTestData();

System.out.println(em.find(Grocery.class, testData.IDGROCERY1));
*/

