package src.main.java.services.macrocomponents;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.ReservationToolbox;
import src.main.java.services.tools.UserToolbox;

public abstract class GroceryManagement {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	/**
	 * Set of tools to interact with the entity manager for User class
	 */
	protected UserToolbox usrTools;
	/**
	 * Set of tools to interact with the entity manager for Grocery class
	 */
	protected GroceryToolbox grocTools;
	/**
	 * Set of tools to interact with the entity manager for Grocery class
	 */
	protected ReservationToolbox resTools;
}

