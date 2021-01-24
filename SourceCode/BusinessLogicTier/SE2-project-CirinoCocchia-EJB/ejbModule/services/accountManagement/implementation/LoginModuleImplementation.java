package services.accountManagement.implementation;

import java.util.List;

import model.User;
import services.accountManagement.interfaces.LoginModule;

/**
 * This class implements the LoginModule abstract class
 */
public class LoginModuleImplementation extends LoginModule {

	@Override
	public User checkCredentials(String username, String password) {
		List<User> result = em.createNamedQuery("User.checkCredentials", User.class)
				.setParameter("usern", username)
				.setParameter("pass", password)
				.getResultList();
		
		if(!result.isEmpty()) {
			User instance = result.get(0);
			return instance;
		} else {
			return null;
		}
	}

}
