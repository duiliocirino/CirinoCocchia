package services;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.User;

@Stateless
public class UserService {
    @PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
    protected EntityManager em;

    public User findEmployee(int id) {
        return em.find(User.class, id);
    }

    public Collection<User> findAllEmployees() {
        Query query = em.createQuery("SELECT e FROM User e");
        return (Collection<User>) query.getResultList();
    }
}
