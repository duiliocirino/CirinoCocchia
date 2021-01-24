package services.accountManagement.implementation;

import java.util.List;

import model.User;
import services.accountManagement.interfaces.RegistrationModule;
import utils.Roles;

public class RegistrationModuleImplementation extends RegistrationModule {

	@Override
	public User register(Roles role, String telephoneNum, String username, String password, String email) {
		if(role == Roles.NONE) {
			return null;
		}
		
		User newUser = new User();
		newUser.setRole(role);
		newUser.setTelephoneNumber(telephoneNum);
		newUser.setUsername(username);
		newUser.setPassword(password);
		newUser.setEmail(email);
		
		em.persist(newUser);
		
		return newUser;
	}

	@Override
	public User editProfile(int iduser, String telephoneNum, String username, String password, String email) {
		User user = em.find(User.class, iduser);
		if(user == null) {
			return null;
		}
		
		if(telephoneNum != null) {
			user.setTelephoneNumber(telephoneNum);
		}
		
		if(username != null) {
			List<User> usernames = em.createNamedQuery("User.findUserByUsername", User.class)
					.setParameter("usrn", username)
					.getResultList();
			if(usernames.isEmpty()) {
				user.setUsername(username);
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
		List<User> usernames = em.createNamedQuery("User.findUserByUsername", User.class)
				.setParameter("usrn", username)
				.getResultList();
		if(usernames.isEmpty()) {
			return usernames.get(0);
		} else {
			return null;
		}
	}
}
