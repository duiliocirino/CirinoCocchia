package services.macrocomponents;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.User;

@Stateless
public abstract class AccountManagement {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	
	/**
	 * Allows a user to register into the system
	 * @param user user to register
	 * @return the persisted entity of the user registered, if the registration is not 
	 *  successful, it returns null 
	 */
	public abstract User register (User user);
}
