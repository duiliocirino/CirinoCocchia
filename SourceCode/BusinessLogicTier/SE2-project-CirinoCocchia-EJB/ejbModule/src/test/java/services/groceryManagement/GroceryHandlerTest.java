package src.test.java.services.groceryManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.UserToolbox;
import src.main.java.utils.Roles;

public class GroceryHandlerTest {
	
	private final int IDOWNER = 1;
	private final int IDCUSTOMER = 10;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 2;
	private final String NAME = "xxxx";
	private final String NEW_NAME = "yyyyy";
	private final int MAX_SPOTS = 3;
	private final int NEW_MAX_SPOTS = 4;
	private final int OPENING_HOUR = 8;
	private final int NEW_OPENING_HOUR = 7;
	private final int CLOSING_HOUR = 20;
	private final int NEW_CLOSING_HOUR = 21;
	private final int WRONG_CLOSING_HOUR = 7;
	private final int WRONG_HOUR1 = -1;
	private final int WRONG_HOUR2 = 25;
	private final int WRONG_CLOSING_HOUR1 = 7;
	private final int WRONG_CLOSING_HOUR2 = 8;
	
	private GroceryHandlerModule grocMod;
	

	@Before
	public void setUp() throws Exception {
		grocMod = new MockGroceryHandlerModule();
	}

	@Test
	public void testAddGrocery() {
		Grocery newGrocery = null;
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDOWNER);
		} catch (CLupException e) {
			System.out.println(e.getMessage());
			fail("Should not throw exceptions");
		}
		
		assertNotNull(newGrocery);
		assertNotNull(newGrocery.getQueue());
		assertEquals(NEW_NAME, newGrocery.getName());
		assertEquals(NEW_MAX_SPOTS, newGrocery.getMaxSpotsInside());
		assertEquals(OPENING_HOUR, newGrocery.getOpeningHour());
		assertEquals(CLOSING_HOUR, newGrocery.getClosingHour());
		assertEquals(IDOWNER, newGrocery.getOwner().getIduser());
	}
	
	@Test
	public void testAddGroceryWrongParameters() {

		Grocery newGrocery = null;
		String blank = null;
		
		 
		
		assertNull(newGrocery);
		
		// name not valid
		try {
			newGrocery = grocMod.addGrocery(blank, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// name not valid
		blank = "";
		try {
			newGrocery = grocMod.addGrocery(blank, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		 
		assertNull(newGrocery);
		
		// null position
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, null, NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// owner not manager
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDCUSTOMER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// name already existent
		try {
			newGrocery = grocMod.addGrocery(NAME, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR, IDOWNER);
		} catch (CLupException e) {
			System.out.println(e.getMessage());
			fail("Should not throw exception");
		}
		
		assertNull(newGrocery);
		
		// negative hour
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, WRONG_HOUR1, CLOSING_HOUR, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// hour > 24
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, WRONG_HOUR2, CLOSING_HOUR, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// closing hour less than opening hour
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, WRONG_CLOSING_HOUR1, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// closing hour equal to opening hour
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, OPENING_HOUR, WRONG_CLOSING_HOUR2, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
	}
	
	@Test
	public void testEditGrocery() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY, NEW_NAME, NEW_MAX_SPOTS, NEW_OPENING_HOUR, NEW_CLOSING_HOUR);
		} catch (CLupException e) {
			System.out.println(e.getMessage());
			fail("Should not throw any exception");
		}
		
		assertNotNull(grocery);
		assertEquals(NEW_NAME, grocery.getName());
		assertEquals(NEW_MAX_SPOTS, grocery.getMaxSpotsInside());
		assertEquals(NEW_OPENING_HOUR, grocery.getOpeningHour());
		assertEquals(NEW_CLOSING_HOUR, grocery.getClosingHour());
	}
	
	@Test
	public void testEditGroceryNotExistentGrocery() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, NEW_NAME, NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
	}
	
	@Test
	public void testEditGroceryBlankName() {
		Grocery grocery = null;
		String blank = "    ";
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, blank, NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
	}
	
	@Test
	public void testEditGroceryNameAlreadyOnTheDB() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, NAME, NEW_MAX_SPOTS, OPENING_HOUR, CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
	}
	
	@Test
	public void testEditGroceryWrongHour() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY, NAME, NEW_MAX_SPOTS, WRONG_HOUR1, CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY, NAME, NEW_MAX_SPOTS, WRONG_HOUR2, CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
	}
	
	@Test
	public void testEditGroceryUncoherentHours() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, NAME, NEW_MAX_SPOTS, NEW_OPENING_HOUR, WRONG_CLOSING_HOUR);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(grocery);
	}

	@Test
	public void testRemoveGrocery() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.removeGrocery(IDGROCERY);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNotNull(grocery);
		assertEquals(IDGROCERY, grocery.getIdgrocery());
		assertEquals(NAME, grocery.getName());
		assertEquals(MAX_SPOTS, grocery.getMaxSpotsInside());
	}
	
	@Test
	public void testRemoveGroceryNotOnTheDB() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.removeGrocery(IDGROCERY_NOT_DB);
			fail("Should not reach this line");
		} catch (CLupException e) {
		}
		
		assertNull(grocery);
	}
	
	class MockGroceryHandlerModule extends GroceryHandlerModuleImplementation {
				
		MockGroceryHandlerModule(){
			this.usrTools = new MockUsrTools();
			this.grocTools = new MockGrocTools(
					this.usrTools.findUser(IDOWNER));
		}
	}
	
	class MockUsrTools extends UserToolbox {
		private User owner;
		private User customer;
		
		public MockUsrTools() {
			owner = new User();
			owner.setIduser(IDOWNER);
			owner.setRole(Roles.MANAGER);
			
			customer = new User();
			customer.setIduser(IDCUSTOMER);
			customer.setRole(Roles.REG_CUSTOMER);
		}
		
		public User findUser(int iduser) {
			if(iduser == owner.getIduser()) {
				return owner;
			}
			if(iduser == customer.getIduser()) {
				return customer;
			}
			return null;
		}
		
	}

	class MockGrocTools extends GroceryToolbox {
		private Grocery grocery;

		public MockGrocTools(User owner) {
			super();
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
			grocery.setOwner(owner);
			grocery.setName(NAME);
			grocery.setMaxSpotsInside(MAX_SPOTS);
		}
		
		public Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}

		public void persistGrocery(Grocery grocery) {
		}

		public void removeGrocery(Grocery grocery) {
			grocery = null;
		}

		public List<Grocery> findGroceryByName(String name){
			List<Grocery> groceries = new ArrayList<>();
			if(name.equals(grocery.getName())) {
				groceries.add(grocery);
			}
			return groceries;
		}
	}
}
