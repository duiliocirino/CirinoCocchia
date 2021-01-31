package src.main.java.services.macrocomponents;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.services.tools.GroceryToolbox;
import src.main.java.services.tools.ReservationToolbox;
import src.main.java.services.tools.UserToolbox;


/**
 * This component serves the purpose to handle the entity 
 * manager for this particular module.
 */
public abstract class ReservationManagement {
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
	 * Set of tools to interact with the entity manager for Reservation class
	 */
	protected ReservationToolbox resTools;
}
