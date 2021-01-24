package services.accountManagement.interfaces;

import javax.ejb.Stateless;

import model.User;
import services.accountManagement.implementation.LoginModuleImplementation;
import services.macrocomponents.ReservationManagement;
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
	 * @param password pasword to be checked
	 * @return persisted instance of user in the case in which the credentials
	 * are correct, null otherwise
	 */
	public abstract User checkCredentials(String username, String password);
	
	public static LoginModule getInstance() {
		return new LoginModuleImplementation();
	}
}
