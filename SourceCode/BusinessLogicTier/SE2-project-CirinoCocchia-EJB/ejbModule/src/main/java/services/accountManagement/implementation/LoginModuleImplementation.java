package src.main.java.services.accountManagement.implementation;

import java.util.List;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;

/**
 * This class implements the LoginModule abstract class
 */
@Stateless
public class LoginModuleImplementation extends LoginModule {

	@Override
	public User checkCredentials(String username, String password) throws CLupException {
		
		if(username == null || password == null ||
				username.isEmpty() || password.isEmpty()) {
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
	
	@Override
	public User getUserById(int iduser) {
		return findUser(iduser);
	}
	
	/**
	 * Decouple the invocation of entity manager
	 */
	protected User findUser(int iduser) {
		return usrTools.findUser(iduser);
	}
	/**
	 * Decouple the invocation of entity manager
	 */
	protected List<User> namedQueryUserCheckCredentials(String usern, String pass){
		return usrTools.checkCredentials(usern, pass);
	}


}
