package src.main.java.services.tools;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.model.User;

/**
 * Utility class that serves as an interface with the 
 * entity manager for the interactions related to Reservation
 */
@Stateless
public class UserToolbox {
	/**
	 * Instance of entity manager
	 */
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	/**
	 * Finds a user basing on its id
	 * @param iduser id of the user to be found
	 * @return User instance if the user is found, null otherwise
	 */
	public User findUser(int iduser) {
		return em.find(User.class, iduser);
	}
	/**
	 * Persits a user on the persistence context
	 * @param user user to be persisted
	 */
	public void persistUser(User user) {
		em.persist(user);
	}
	/**
	 * Removes a user from the persistence context
	 * @param user user to be removed
	 */
	public void removeUser(User user) {
		em.remove(user);
	}
	/**
	 * Retrieves the user from the database basing on its username and password
	 * @param username username of the user
	 * @param password password of the user
	 * @return collection of user with that username and that password. That means
	 *  that if there is a result, then that result is unique and the list will have
	 *   onlt an element
	 */
	public List<User> checkCredentials(String username, String password){
		return em.createNamedQuery("User.checkCredentials", User.class)
				.setParameter("usern", username)
				.setParameter("pass", password)
				.getResultList();
	}
	/**
	 * Retrieves the user from the peristence context basing on its username
	 * @param username username of the user
	 * @return collection of users with that username, that means that if there is a 
	 * result, then the list received will contain only an element
	 */
	public List<User> findByUsername(String username){
		return em.createNamedQuery("User.findByUsername", User.class)
				.setParameter("usrn", username)
				.getResultList();
	}
}
