package src.test.java.services.accountManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.RegistrationModuleImplementation;
import src.main.java.services.accountManagement.interfaces.RegistrationModule;
import src.main.java.utils.Roles;

public class RegistrationModuleTest {
	
	private final Roles ROLE = Roles.REG_CUSTOMER;
	private final int IDUSER1 = 1;
	private final int IDUSER2 = 2;
	private final String TELEPHONE_NUM = "111111111";
	private final String USERNAME1 = "uniqueUsername1";
	private final String USERNAME2 = "uniqueUsername2";
	private final String PASSWORD = "secretPassword1";
	private final String EMAIL = "student1@polimi.it";
	private RegistrationModule regMod;

	@Before
	public void setUp() throws Exception {
		regMod = new MockRegistrationModule();
	}

	@Test
	public void registerSuccessfulTest() {
		
		User newUser = null;
		
		try {
			newUser = regMod.register(ROLE, TELEPHONE_NUM, USERNAME1, PASSWORD, EMAIL);
		} catch (CLupException e) {
			fail("Should not throw exception");
		}
		
		assertNotNull(newUser);
		
	}
	
	@Test
	public void registerWrongRoleTest() {
		
		User newUser = null;
		
		try {
			newUser = regMod.register(Roles.NONE, TELEPHONE_NUM, USERNAME1, PASSWORD, EMAIL);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		try {
			newUser = regMod.register(Roles.VISITOR, TELEPHONE_NUM, USERNAME1, PASSWORD, EMAIL);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newUser);
				
	}
	
	@Test
	public void registerYetExistentUsernameTest() {
		
		User newUser = null;
		
		try {
			newUser = regMod.register(ROLE, TELEPHONE_NUM, USERNAME2, PASSWORD, EMAIL);
			fail("Should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		assertNull(newUser);
				
	}

	@Test
	public void editProfileUsernameTest() {
		User editedUser = regMod.getUserByUsername(USERNAME2);
		assertEquals(USERNAME2, editedUser.getUsername());
		try {
			editedUser = regMod.editProfile(IDUSER2, null, USERNAME1, null, null);
		} catch (CLupException e) {
			fail("should not throw an exception");
		}
		
		assertNotEquals(USERNAME2, editedUser.getUsername());
		assertEquals(USERNAME1, editedUser.getUsername());
	}
	
	@Test
	public void editProfileWrongUsernameTest() {
		User editedUser = regMod.getUserByUsername(USERNAME2);
		try {
			editedUser = regMod.editProfile(IDUSER2, null, USERNAME2, null, null);
			fail("should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void editProfileWrongIdTest() {
		User editedUser = regMod.getUserByUsername(USERNAME2);

		try {
			editedUser = regMod.editProfile(IDUSER1, null, USERNAME1, null, null);
			fail("should not reach this line");
		} catch (CLupException e) {
			assertTrue(true);
		}
		
	}
	
	/**
	 * Empty because relies entirely on a method that involves the
	 * DB. This method has to be tested through Integration Testing
	 */
	@Test
	public void getUserByUsernameTest() {	
	}
	
	/**
	 * Mocks the behaviour of RegistrationModule by 
	 * overriding the methods that involve the entity
	 * manager
	 */
	class MockRegistrationModule extends RegistrationModuleImplementation {
		private User user;
		
		public MockRegistrationModule() {
			user = new User();
			user.setIduser(IDUSER2);
			user.setUsername(USERNAME2);
		}
		
		protected User findUser(int iduser) {
			if(iduser == user.getIduser()) {
				return user;
			} else {
				return null;
			}
		}
		
		protected void persistUser(User user) {
		}
		
		protected List<User> namedQueryUserFindUserByUsername(String usrn) {
			List<User> users = new ArrayList<>();
			
			if(usrn.equals(user.getUsername())) {
				users.add(user);
			}
			
			return users;
		}
	}
}
