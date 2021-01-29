package src.test.java.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.utils.Roles;

public class GroceryTest {
	
	private final int USER_ID = 1;
	private final int GROCERY_ID = 1;
	private final String TELEPHONE_NUMBER = "333333333";
	private final Roles ROLE = Roles.VISITOR;
	private User mockUser;
	private Grocery grocery;
	
	public GroceryTest() {	}
	
	@Before
	public void setUp() {
		mockUser = new User();
		mockUser.setIduser(USER_ID);
		mockUser.setTelephoneNumber(TELEPHONE_NUMBER);
		mockUser.setRole(ROLE);
		
		grocery = new Grocery();
		grocery.setIdgrocery(GROCERY_ID);
	}

	@Test
	public void testAddEmployee() {
		
		assertFalse(grocery.getEmployees().contains(mockUser));
		assertFalse(mockUser.getEmployedGroceries().contains(grocery));
		
		grocery.addEmployee(mockUser);
		
		assertTrue(grocery.getEmployees().contains(mockUser));
		assertTrue(mockUser.getEmployedGroceries().contains(grocery));
		
	}
	
	@Test
	public void testNullAddEmployee() {
		
		assertFalse(grocery.getEmployees().contains(mockUser));
		assertFalse(mockUser.getEmployedGroceries().contains(grocery));
		
		grocery.addEmployee(null);
		
		assertFalse(grocery.getEmployees().contains(mockUser));
		assertFalse(mockUser.getEmployedGroceries().contains(grocery));
	}
	
	
	@Test
	public void testRemoveEmployee() {
		
		grocery.addEmployee(mockUser);
		
		assertTrue(grocery.getEmployees().contains(mockUser));
		assertTrue(mockUser.getEmployedGroceries().contains(grocery));
		
		grocery.removeEmployee(mockUser);
		
		assertFalse(grocery.getEmployees().contains(mockUser));
		assertFalse(mockUser.getEmployedGroceries().contains(grocery));
		
	}
	
	@Test
	public void testRemoveNullEmployee() {
		
		grocery.addEmployee(mockUser);
		
		assertTrue(grocery.getEmployees().contains(mockUser));
		assertTrue(mockUser.getEmployedGroceries().contains(grocery));
		
		grocery.removeEmployee(null);
		
		assertTrue(grocery.getEmployees().contains(mockUser));
		assertTrue(mockUser.getEmployedGroceries().contains(grocery));
		
	}

}
