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

import src.main.java.model.User;
import src.main.java.services.tools.UserToolbox;
import src.test.resources.TestDataUserToolbox;
import static src.test.resources.TestDataUserToolbox.*;

import java.util.List;

public class UserToolboxTest {
	
	private static EntityManagerFactory emf;
	private EntityManager em;

	private UserToolbox userTools;
	private TestDataUserToolbox testData;
	
	@BeforeClass
	public static void classSetUp() {
		emf = Persistence.createEntityManagerFactory("SE2-project-CirinoCocchia-EJB"); 
	}

	@Before
	public void setUp() {
		em = emf.createEntityManager();
		testData = new TestDataUserToolbox(em);
		testData.createTestData();
		
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
	public void testFindUserSuccessful() {
		User user = null;
		user = userTools.findUser(IDUSER1);
		
		assertNotNull(user);
		assertEquals(IDUSER1, user.getIduser());
		assertEquals(USERNAME1, user.getUsername());
		assertEquals(PASSWORD1, user.getPassword());
		assertEquals(EMAIL1, user.getEmail());
		assertEquals(USER1_ROLE, user.getRole());
		assertEquals(TELEPHONE_NUM1, user.getTelephoneNumber());
	}

	@Test
	public void testFindUserFailure() {
		User user = null;
		user = userTools.findUser(IDUSER_NOT_DB);
		
		assertNull(user);
	}
	
	@Test
	public void testPersistUser() {
		User user = null;

		try {
			user = new User();
			user.setRole(USER_NOT_DB_ROLE);
			user.setTelephoneNumber(TELEPHONE_NUM_NOT_DB);
			assertNull(userTools.findUser(IDUSER_NOT_DB));
			
			em.getTransaction().begin();
			userTools.persistUser(user);
			em.getTransaction().commit();
			
			IDUSER_NOT_DB = user.getIduser();		
			User find = userTools.findUser(IDUSER_NOT_DB);
			assertNotNull(find);
			assertEquals(user, find);			
		} finally {
			if(user != null) {
				userTools.removeUser(user);
			}
		}
		
	}

	@Test
	public void testRemoveGrocery() {
		User user1 = userTools.findUser(IDUSER1);
		assertNotNull(user1);
		
		em.getTransaction().begin();
		userTools.removeUser(user1);
		em.getTransaction().commit();
		
		assertNull(userTools.findUser(IDUSER1));
	}
	
	@Test
	public void testCheckCredentials() {
		User user1 = userTools.findUser(IDUSER1);
		assertNotNull(user1);
		
		List<User> credentials = null;
		credentials = userTools.checkCredentials(USERNAME1, PASSWORD1);
		assertNotNull(credentials);
		assertTrue(1 == credentials.size());
		assertNotNull(credentials.get(0));
		assertEquals(user1, credentials.get(0));
	}
	
	@Test
	public void testCheckWrongCredentials() {
		List<User> credentials = null;
		
		credentials = userTools.checkCredentials(USERNAME1, PASSWORD_NOT_DB);
		assertNotNull(credentials);
		assertTrue(credentials.isEmpty());
		
		credentials = userTools.checkCredentials(USERNAME_NOT_DB, PASSWORD1);
		assertNotNull(credentials);
		assertTrue(credentials.isEmpty());
	}
	
	@Test
	public void testFindByUsername() {
		User user1 = userTools.findUser(IDUSER1);
		assertNotNull(user1);
		
		List<User> usernames = null;
		usernames = userTools.findByUsername(USERNAME1);
		assertNotNull(usernames);
		assertNotNull(usernames.get(0));
		assertEquals(user1, usernames.get(0));
	}
	
	@Test
	public void testFindByWrongUsername() {
		List<User> usernames = null;
		
		usernames = userTools.findByUsername(USERNAME_NOT_DB);
		assertNotNull(usernames);
		assertTrue(usernames.isEmpty());
	}
	
	class MockUserTools extends UserToolbox {
		public MockUserTools(EntityManager em) {
			this.em = em;
		}
	}

}
