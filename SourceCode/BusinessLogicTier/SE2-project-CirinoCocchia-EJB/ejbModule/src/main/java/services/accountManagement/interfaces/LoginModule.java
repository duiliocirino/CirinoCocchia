package src.main.java.services.accountManagement.interfaces;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.macrocomponents.ReservationManagement;
/**
 * this module will be used to access accounts by each user of 
 * the software, by checking the correctness of the data filled to 
 * authenticate
 */
@Stateless
public abstract class LoginModule extends ReservationManagement {
	/**
	 * Checks the correctness of the credentials of the registered user
	 * @param username username to be checked
	 * @param password password to be checked
	 * @return persisted instance of user in the case in which the credentials
	 * are correct, null otherwise
	 * @throws CLupException with a detailed message attached in case of exceptions
	 */
	public abstract User checkCredentials(String username, String password) throws CLupException;
	
	public static LoginModule getInstance() {
		return new LoginModuleImplementation();
	}
}
