package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.PersistenceContext;

@Stateless
public class ReservationService {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
    protected EntityManager em;
	
}
