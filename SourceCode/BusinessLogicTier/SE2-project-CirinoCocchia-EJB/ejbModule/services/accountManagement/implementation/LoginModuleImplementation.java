package services.accountManagement.implementation;

import java.util.List;

import model.User;
import services.accountManagement.interfaces.LoginModule;
import exceptions.CLupException;

/**
 * This class implements the LoginModule abstract class
 */
public class LoginModuleImplementation extends LoginModule {

	@Override
	public User checkCredentials(String username, String password) throws CLupException {
		
		if(username == null || password == null ||
				username.isBlank() || password.isBlank()) {
			throw new CLupException("Can't check null or empty credentials");
		}
		
		List<User> result = namedQueryUserCheckCredentials(username, password);
		
		if(!result.isEmpty()) {
			User instance = result.get(0);
			return instance;
		} else {
			return null;
		}
	}
	
	/**
	 * Decouple the invocation of entity manager
	 */
	protected List<User> namedQueryUserCheckCredentials(String usern, String pass){
		return em.createNamedQuery("User.checkCredentials", User.class)
				.setParameter("usern", usern)
				.setParameter("pass", pass)
				.getResultList();
	}

}
