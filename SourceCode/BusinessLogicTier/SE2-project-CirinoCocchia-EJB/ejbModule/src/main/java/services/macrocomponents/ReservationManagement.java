package src.main.java.services.macrocomponents;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * This component serves the purpose to handle the entity 
 * manager for this particular module.
 */
@Stateless
public abstract class ReservationManagement {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
}
