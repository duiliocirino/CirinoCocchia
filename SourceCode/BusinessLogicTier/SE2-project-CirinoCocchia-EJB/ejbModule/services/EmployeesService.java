package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EmployeesService {
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
    protected EntityManager em;
	
}
