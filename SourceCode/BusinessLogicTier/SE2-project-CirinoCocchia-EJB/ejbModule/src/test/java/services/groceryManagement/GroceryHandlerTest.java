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
import src.main.java.utils.Roles;

public class GroceryHandlerTest {
	
	private final int IDOWNER = 1;
	private final int IDOWNER_NOT_DB = 2;
	private final int IDCUSTOMER = 10;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_DB = 2;
	private final String NAME = "xxxx";
	private final String NEW_NAME = "yyyyy";
	private final int MAX_SPOTS = 3;
	private final int NEW_MAX_SPOTS = 4;
	
	private GroceryHandlerModule grocMod;
	

	@Before
	public void setUp() throws Exception {
		grocMod = new MockGroceryHandlerModule();
	}

	@Test
	public void testAddGrocery() {
		Grocery newGrocery = null;
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, IDOWNER);
		} catch (CLupException e) {
			fail("Should not throw exceptions");
		}
		
		assertNotNull(newGrocery);
		assertEquals(NEW_NAME, newGrocery.getName());
		assertEquals(NEW_MAX_SPOTS, newGrocery.getMaxSpotsInside());
		assertEquals(IDOWNER, newGrocery.getOwner().getIduser());
	}
	
	@Test
	public void testAddGroceryWrongParameters() {

		Grocery newGrocery = null;
		String blank = null;
		
		// user not in the db
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, IDOWNER_NOT_DB);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// name not valid
		try {
			newGrocery = grocMod.addGrocery(blank, new Position(0, 0), NEW_MAX_SPOTS, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// name not valid
		blank = "      ";
		try {
			newGrocery = grocMod.addGrocery(blank, new Position(0, 0), NEW_MAX_SPOTS, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// null position
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, null, NEW_MAX_SPOTS, IDOWNER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// owner not manager
		try {
			newGrocery = grocMod.addGrocery(NEW_NAME, new Position(0, 0), NEW_MAX_SPOTS, IDCUSTOMER);
			fail("Should not reach this line ");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newGrocery);
		
		// name already existent
		try {
			newGrocery = grocMod.addGrocery(NAME, new Position(0, 0), NEW_MAX_SPOTS, IDOWNER);
		} catch (CLupException e) {
			fail("Should not throw exception");
		}
		
		assertNull(newGrocery);
	}
	
	@Test
	public void testEditGrocery() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY, NEW_NAME, NEW_MAX_SPOTS);
		} catch (CLupException e) {
			fail("Should not throw any exception");
		}
		
		assertNotNull(grocery);
		assertEquals(NEW_NAME, grocery.getName());
		assertEquals(NEW_MAX_SPOTS, grocery.getMaxSpotsInside());
	}
	
	@Test
	public void testEditGroceryNotExistentGrocery() {
		Grocery grocery = null;
		
		try {
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, NEW_NAME, NEW_MAX_SPOTS);
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
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, blank, NEW_MAX_SPOTS);
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
			grocery = grocMod.editGrocery(IDGROCERY_NOT_DB, NAME, NEW_MAX_SPOTS);
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
		
		private User owner;
		private User customer;
		private Grocery grocery;
		
		MockGroceryHandlerModule(){
			owner = new User();
			owner.setIduser(IDOWNER);
			owner.setRole(Roles.MANAGER);
			
			customer = new User();
			customer.setIduser(IDCUSTOMER);
			customer.setRole(Roles.REG_CUSTOMER);
			
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
			grocery.setOwner(owner);
			grocery.setName(NAME);
			grocery.setMaxSpotsInside(MAX_SPOTS);
		}
		

		protected User findUser(int iduser) {
			if(iduser == owner.getIduser()) {
				return owner;
			}
			if(iduser == customer.getIduser()) {
				return customer;
			}
			return null;
		}
		
		protected Grocery findGrocery(int idgrocery) {
			if(idgrocery == grocery.getIdgrocery()) {
				return grocery;
			}
			return null;
		}

		protected void persistGrocery(Grocery grocery) {
		}

		protected void removeGrocery(Grocery grocery) {
			grocery = null;
		}

		protected List<Grocery> namedQueryGroceryFindGroceryByName(String name){
			List<Grocery> groceries = new ArrayList<>();
			if(name.equals(grocery.getName())) {
				groceries.add(grocery);
			}
			return groceries;
		}
	}

}
