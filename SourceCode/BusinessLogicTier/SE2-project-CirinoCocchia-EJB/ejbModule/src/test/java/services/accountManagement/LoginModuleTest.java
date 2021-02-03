package src.test.java.services.accountManagement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.tools.UserToolbox;

/**
 * Unit test for the LoginModuleImplementation class
 *
 */
public class LoginModuleTest {
	
	private final String USERNAME = "testUsername";
	private final String PASSWORD = "testPassword";
	private final String WRONG_CREDENTIAL = "xxx";
	private LoginModule logMod;
	
	@Before
	public void setUp() {
		logMod = new MockLoginModule();
	}
	

	@Test
	public void testCheckRightCredentials() {
		try {
			User loggedUser = logMod.checkCredentials(USERNAME, PASSWORD);
		} catch (CLupException e) {
			fail("should not throw exceptions");
		}
	}
	
	@Test
	public void testCheckWrongCredentials() {
		try {
			User loggedUser = logMod.checkCredentials(USERNAME, WRONG_CREDENTIAL);
			assertNull(loggedUser);
		} catch (CLupException e) {
			fail("should not throw exceptions");
		}
	}
	
	@Test
	public void testCheckCredentialsNullArguments() {
		User loggedUser;
		
		try {
			loggedUser = logMod.checkCredentials(null, null);
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		try {
			loggedUser = logMod.checkCredentials(USERNAME, null);
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		try {
			loggedUser = logMod.checkCredentials(null, PASSWORD);
		} catch (CLupException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCheckCredentialsBlankArguments() {
		User loggedUser;
		final String blank = "     ";
		try {
			loggedUser = logMod.checkCredentials(blank, blank);
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		try {
			loggedUser = logMod.checkCredentials(USERNAME, blank);
		} catch (CLupException e) {
			assertTrue(true);
		}
		
		try {
			loggedUser = logMod.checkCredentials(blank, PASSWORD);
		} catch (CLupException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * LoginModule mock which only overrides the entity manager call
	 */
	class MockLoginModule extends LoginModuleImplementation {
		public MockLoginModule(){
			this.usrTools = new MockUsrTools();
		}
		
	}
	
	class MockUsrTools extends UserToolbox {
		private User user;
		
		public MockUsrTools() {
			user = new User();
			user.setUsername(USERNAME);
			user.setPassword(PASSWORD);
		}
		
		public List<User> checkCredentials(String usern, String pass){
			List<User> usersList = new ArrayList<>();
			if(usern.equals(user.getUsername()) && 
					pass.equals(user.getPassword())) {
				usersList.add(user);
			}
			
			return usersList;
		}

	}

}
