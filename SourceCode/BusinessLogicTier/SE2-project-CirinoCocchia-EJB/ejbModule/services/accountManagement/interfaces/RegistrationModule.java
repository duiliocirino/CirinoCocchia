package services.accountManagement.interfaces;

import javax.ejb.Stateless;

import model.User;
import services.accountManagement.implementation.RegistrationModuleImplementation;
import services.macrocomponents.AccountManagement;
import utils.Roles;

/**
 * This module will take care of every register request, be it 
 * a user or a manager (we remember that employees are created and 
 * added to the system by the store manager)
 */
@Stateless
public abstract class RegistrationModule extends AccountManagement {
	/**
	 * This method allows the registration of a new user 
	 * @param role role of the user to be added
	 * @param telephoneNum user's telephone number
	 * @param username user's username
	 * @param password user's password
	 * @param email user's email
	 * @return persisted instance of User just created
	 */
	public abstract User register(Roles role, String telephoneNum, String username, String password, String email);
	/**
	 * This method allows to edit a user's registration field. If parameters
	 * are left null, then no modification will be made for that field
	 * @param iduser id of the user to modify
	 * @param telephoneNum new telephone number
	 * @param username new username. The uniqueness of the username is checked.
	 * @param password new password
	 * @param email new email
	 * @return persisted instance of User just edited if everything goes right, null otherwise
	 */
	public abstract User editProfile(int iduser, String telephoneNum, String username, String password, String email);
	/**
	 * Retrieves a User given his username
	 * @param username username of the user to be retrieved
	 * @return persisted instance of user if such username is found, null otherwise
	 */
	public abstract User getUserByUsername(String username);
	
	public static RegistrationModule getInstance() {
		return new RegistrationModuleImplementation();
	}
}
