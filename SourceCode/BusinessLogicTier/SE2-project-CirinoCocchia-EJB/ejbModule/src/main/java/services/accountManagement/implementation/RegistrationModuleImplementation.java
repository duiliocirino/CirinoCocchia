package src.main.java.services.accountManagement.implementation;

import java.util.List;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.RegistrationModule;
import src.main.java.utils.Roles;

/**
 * This class implements the RegistrationModule interface
 */
@Stateless
public class RegistrationModuleImplementation extends RegistrationModule {

	@Override
	public User register(Roles role, String telephoneNum, String username, String password, String email) throws CLupException {
		if(telephoneNum == null || telephoneNum.isEmpty() || role == null) {
			throw new CLupException("Wrong register credentials");
		}
		
		if(role == Roles.NONE) {
			throw new CLupException("Can't add a NONE role");
		}
		
		User newUser = new User();
		
		if(role != Roles.VISITOR) {
			if(username == null || username.isEmpty() 
					|| password == null || password.isEmpty() 
					|| email == null || email.isEmpty()) {
				throw new CLupException("Wrong credentials for registered user");
			}
			
			User userWithUsername = getUserByUsername(username);
			
			if(userWithUsername != null) {
				throw new CLupException("There is another user with that username yet");
			}
			
			newUser.setRole(role);
			newUser.setTelephoneNumber(telephoneNum);
			newUser.setUsername(username);
			newUser.setPassword(password);
			newUser.setEmail(email);
		} else {
			newUser.setRole(role);
			newUser.setTelephoneNumber(telephoneNum);
		}
		
		usrTools.persistUser(newUser);
		
		return newUser;
	}

	@Override
	public User editProfile(int iduser, String telephoneNum, String username, String password, String email) throws CLupException {
		User user = usrTools.findUser(iduser);
		if(user == null) {
			throw new CLupException("Can't find the user");
		}
		
		if(telephoneNum != null) {
			user.setTelephoneNumber(telephoneNum);
		}
		
		if(username != null) {
			User userWithUsername = getUserByUsername(username);
			if(userWithUsername == null) {
				user.setUsername(username);
			} else {
				throw new CLupException("There is already another user with that username");
			}
		}
		
		if(password != null) {
			user.setPassword(password);
		}
		
		if(email != null) {
			user.setEmail(email);
		}
		return user;
	}

	@Override
	public User getUserByUsername(String username) {
		List<User> usernames = usrTools.findByUsername(username);
		if(!usernames.isEmpty()) {
			return usernames.get(0);
		} else {
			return null;
		}
	}
}
