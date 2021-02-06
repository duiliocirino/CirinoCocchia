package src.main.java.services.macrocomponents;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.services.tools.UserToolbox;

public abstract class AccountManagement {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	/**
	 * Set of tools to interact with the entity manager for User class
	 */
	@EJB
	protected UserToolbox usrTools;
}
