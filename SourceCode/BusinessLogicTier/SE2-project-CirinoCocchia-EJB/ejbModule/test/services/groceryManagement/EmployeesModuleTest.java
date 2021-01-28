package test.services.groceryManagement;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import exceptions.CLupException;
import model.Grocery;
import model.User;
import services.groceryManagement.implementation.EmployeesModuleImplementation;
import services.groceryManagement.interfaces.EmployeesModule;
import utils.Roles;

public class EmployeesModuleTest {
	
	private final int IDEMP = 1;
	private final int IDUSER_NOT_IN_THE_DB = 2;
	private final int IDCUSTOMER = 10;
	private final int IDGROCERY = 1;
	private final int IDGROCERY_NOT_IN_THE_DB = 2;
	private EmployeesModule empMod;
	private Grocery grocery;

	@Before
	public void setUp() throws Exception {
		empMod = new MockEmployeesModule();
	}

	@Test
	public void testAddEmployee() {
		assertEquals(0, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.addEmployee(IDEMP, IDGROCERY);
		} catch (CLupException e) {
			fail("Should not throw an exception");
		}
		
		assertNotNull(emp);
		assertEquals(1, grocery.getEmployees().size());
		assertTrue(grocery.getEmployees().contains(emp));
		assertTrue(emp.getEmployedGroceries().contains(grocery));
	}
	
	@Test
	public void testAddEmployeeWrongGrocery() {
		assertEquals(0, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.addEmployee(IDEMP, IDGROCERY_NOT_IN_THE_DB);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(0, grocery.getEmployees().size());
	}
	
	@Test
	public void testAddEmployeeNoEmp() {
		assertEquals(0, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.addEmployee(IDUSER_NOT_IN_THE_DB, IDGROCERY);
		} catch (CLupException e) {
			fail("Should not throw an exception");
		}
		
		assertNull(emp);
		assertEquals(0, grocery.getEmployees().size());
	}
	
	@Test
	public void testAddEmployeeWrongRole() {
		assertEquals(0, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.addEmployee(IDCUSTOMER, IDGROCERY);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(0, grocery.getEmployees().size());
	}
	
	private void setRemove() {
		try {
			empMod.addEmployee(IDEMP, IDGROCERY);
		} catch (CLupException e) {
			fail();
		}
	}

	@Test
	public void testRemoveEmployee() {
		setRemove();
		assertEquals(1, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.removeEmployee(IDEMP, IDGROCERY);
		} catch (CLupException e) {
			fail("Should not throw an exception");
		}
		
		assertNotNull(emp);
		assertEquals(0, grocery.getEmployees().size());
		assertFalse(grocery.getEmployees().contains(emp));
		assertFalse(emp.getEmployedGroceries().contains(grocery));
	}
	
	@Test
	public void testRemoveEmployeeWrongEmpOrGrocery() {
		setRemove();
		assertEquals(1, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.removeEmployee(IDUSER_NOT_IN_THE_DB, IDGROCERY);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(1, grocery.getEmployees().size());
		
		try {
			emp = empMod.removeEmployee(IDUSER_NOT_IN_THE_DB, IDGROCERY_NOT_IN_THE_DB);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(1, grocery.getEmployees().size());
		
		try {
			emp = empMod.removeEmployee(IDEMP, IDGROCERY_NOT_IN_THE_DB);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(1, grocery.getEmployees().size());
	}
	
	@Test
	public void testRemoveEmployeeNotEmployee() {
		setRemove();
		assertEquals(1, grocery.getEmployees().size());
		User emp = null;
		
		try {
			emp = empMod.removeEmployee(IDCUSTOMER, IDGROCERY);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(emp);
		assertEquals(1, grocery.getEmployees().size());
	}
	
	class MockEmployeesModule extends EmployeesModuleImplementation {
		
		private User employee;
		private User customer;
		
		public MockEmployeesModule() {
			employee = new User();
			employee.setIduser(IDEMP);
			employee.setRole(Roles.EMPLOYEE);
			
			customer = new User();
			customer.setIduser(IDCUSTOMER);
			customer.setRole(Roles.REG_CUSTOMER);
			
			grocery = new Grocery();
			grocery.setIdgrocery(IDGROCERY);
		}
		
		/**
		 * just to test purposes
		 */
		Grocery getGrocery() {
			return grocery;
		}
		
		protected User findUser(int iduser) {
			if(iduser == employee.getIduser()) {
				return employee;
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
	}

}
