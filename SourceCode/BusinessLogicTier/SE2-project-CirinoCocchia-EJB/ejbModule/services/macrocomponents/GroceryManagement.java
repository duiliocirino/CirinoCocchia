package services.macrocomponents;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public abstract class GroceryManagement {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
}

