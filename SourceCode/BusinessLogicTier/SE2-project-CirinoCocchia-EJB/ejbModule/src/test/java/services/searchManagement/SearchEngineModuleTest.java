package src.test.java.services.searchManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.searchManagement.implementation.SearchEngineModuleImplementation;
import src.main.java.services.searchManagement.interfaces.SearchEngineModule;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.UserToolbox;

public class SearchEngineModuleTest {
	
	private final int IDUSER = 1;
	private final int IDUSER_NOT_DB = 2;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 2;
	private final Position GROC_POSITION = new Position(1.0, 1.0);
	private final double RADIUS = 1.0;
	private final int NUM_FAVOURITES = 3;
	private final Position POSITION_1 = new Position(2.0, 2.0);
	private final Position POSITION_2 = new Position(3.0, 3.0);
	
	private SearchEngineModule searchMod;

	@Before
	public void setUp() throws Exception {
		searchMod = new MockSearchEngineModule();
	}

	@Test
	public final void testIsNear() {
		boolean test = false;
		
		try {
			 test = searchMod.isNear(POSITION_1, IDGROCERY, RADIUS);
		} catch (CLupException e) {
			fail("Should not throw an exception");
		}
		
		assertTrue(test);
		
		test = true;
		
		try {
			 test = searchMod.isNear(POSITION_2, IDGROCERY, RADIUS);
		} catch (CLupException e) {
			fail("Should not throw an exception");
		}
		
		assertFalse(test);
	}
	
	@Test
	public final void testIsNearWrongGrocery() {
		boolean test = false;
		
		try {
			 test = searchMod.isNear(POSITION_1, IDGROCERY_NOT_DB, -1);
			 fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertFalse(test);
	}
	
	@Test
	public final void testIsNearWrongPosition() {
		boolean test = false;
		
		try {
			 test = searchMod.isNear(null, IDGROCERY_NOT_DB, RADIUS);
			 fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertFalse(test);
	}
	
	@Test
	public final void testIsNearWrongRadius() {
		boolean test = false;
		
		try {
			 test = searchMod.isNear(POSITION_1, IDGROCERY_NOT_DB, RADIUS);
			 fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertFalse(test);
	}
	
	@Test
	public final void testGetNearGroceries() {
		List<Grocery> nearGroceries = null;
		
		try {
			nearGroceries = searchMod.getNearGroceries(POSITION_1, RADIUS);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNotNull(nearGroceries);
		assertEquals(IDGROCERY, nearGroceries.get(0).getIdgrocery());		
	}
	
	@Test
	public final void testGetNearGroceriesFarAway() {
		List<Grocery> nearGroceries = null;
		
		try {
			nearGroceries = searchMod.getNearGroceries(POSITION_2, RADIUS);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNotNull(nearGroceries);
		assertEquals(0, nearGroceries.size());	
	}
	
	@Test
	public final void testGetNearGroceriesWrongPosition() {
		List<Grocery> nearGroceries = null;
		
		try {
			nearGroceries = searchMod.getNearGroceries(null, RADIUS);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(nearGroceries);
	}
	
	@Test
	public final void testGetNearGroceriesWrongRadius() {
		List<Grocery> nearGroceries = null;
		
		try {
			nearGroceries = searchMod.getNearGroceries(POSITION_1, -1);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(nearGroceries);
	}

	@Test
	public final void testGetFavouriteGroceries() {
		List<Grocery> favGroceries = null;
		
		try {
			favGroceries = searchMod.getFavouriteGroceries(IDUSER, NUM_FAVOURITES);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNotNull(favGroceries);
		assertEquals(IDGROCERY, favGroceries.get(0).getIdgrocery());	
	}

	@Test
	public final void testGetFavouriteGroceriesWrongCustomer() {
		List<Grocery> favGroceries = null;
		
		try {
			favGroceries = searchMod.getFavouriteGroceries(IDUSER_NOT_DB, NUM_FAVOURITES);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(favGroceries);
	}
	
	@Test
	public final void testGetFavouriteGroceriesWrongNumFavourites() {
		List<Grocery> favGroceries = null;
		
		try {
			favGroceries = searchMod.getFavouriteGroceries(IDUSER_NOT_DB, 0);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(favGroceries);
	}
	
	class MockSearchEngineModule extends SearchEngineModuleImplementation {
		public MockSearchEngineModule() {
			this.usrTools = new MockUsrTools();
			this.grocTools = new MockGrocTools();
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
	
	class MockGrocTools extends GroceryToolbox {
		private Grocery grocery;

		public MockGrocTools() {
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
			grocery.setLatitude(GROC_POSITION.getLat());
			grocery.setLongitude(GROC_POSITION.getLon());
		}
		
		public Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}
		
		public List<Grocery> findAllGroceries(){
			List<Grocery> groceries = new ArrayList<>();
			groceries.add(grocery);
			return groceries;
		}
		
		public List<Grocery> findCustomersFavourites(User customer, int numberFavourites){
			return findAllGroceries();
		}
	}

}
