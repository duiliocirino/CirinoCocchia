package src.test.resources;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import src.main.java.model.User;
import src.main.java.utils.Roles;

public class TestDataUserToolbox {

	protected EntityManager em;
	
	public static int IDUSER1;
	public static String USERNAME1 = "usernamee-1";
	public static String PASSWORD1 = "psw-1";
	public static String EMAIL1 = "user1@email";
	public static final Roles USER1_ROLE = Roles.MANAGER;
	public static final String TELEPHONE_NUM1 = "000000000";
	
	public static int IDUSER_NOT_DB = 2;
	public static final String USERNAME_NOT_DB = "usrn-not-db";
	public static final String PASSWORD_NOT_DB = "psw-not-db";
	public static final Roles USER_NOT_DB_ROLE = Roles.VISITOR;
	public static final String TELEPHONE_NUM_NOT_DB = "11111111";
	
	public TestDataUserToolbox(EntityManager em) {
		this.em = em;
	}
	
	
	
	public TestDataUserToolbox() {
		super();
	}



	public void createTestData() {
		User user1 = new User();
		user1.setUsername(USERNAME1);
		user1.setPassword(PASSWORD1);
		user1.setEmail(EMAIL1);
		user1.setRole(USER1_ROLE);
		user1.setTelephoneNumber(TELEPHONE_NUM1);
		
		em.getTransaction().begin();
		
		em.persist(user1);
		
		em.getTransaction().commit();
		
		IDUSER1 = user1.getIduser();
	}
	
	public void removeTestData() {
		User user1 = em.find(User.class, IDUSER1);
		
		em.getTransaction().begin();
		
		if(user1 != null) {
			em.remove(user1);
		}
		
		em.getTransaction().commit();
		
	}
	
}
