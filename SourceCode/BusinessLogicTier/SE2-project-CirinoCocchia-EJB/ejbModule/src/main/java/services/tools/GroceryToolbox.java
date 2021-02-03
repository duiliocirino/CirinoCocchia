package src.main.java.services.tools;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.model.Grocery;
import src.main.java.model.User;

/**
 * Utility class that serves as an interface with the 
 * entity manager for the interactions related to Grocery
 */
@Stateless
public class GroceryToolbox {
	/**
	 * Instance of entity manager
	 */
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	
	/**
	 * Find a grocery basing on its id
	 * @param idgrocery id of the grocery to be searched
	 * @return Grocery instance if something is found, null otherwise
	 */
	public Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class, idgrocery);
	}
	/**
	 * Persists a grocery into the persistence context
	 * @param grocery grocery to be persisted
	 */
	public void persistGrocery(Grocery grocery) {
		em.persist(grocery);
	}
	/**
	 * Removes a grocery from the persistence context
	 * @param grocery grocery ro be removed
	 */
	public void removeGrocery(Grocery grocery) {
		em.remove(grocery);
	}
	/**
	 * Retrieves all the groceries that are present in the system
	 * @return Collection of groceries present in the DB
	 */
	public List<Grocery> findAllGroceries(){
		return em.createNamedQuery("Grocery.findAll", Grocery.class)
				.getResultList();
	}
	/**
	 * Retrieves a grocery basing on its name
	 * @param name name of the grocery to be searched
	 * @return Collection of groceries with the name specified
	 */
	public List<Grocery> findGroceryByName(String name) {
		return em.createNamedQuery("Grocery.findGroceryByName", Grocery.class)
				.setParameter("name", name)
				.getResultList();
	}
	
	/**
	 * Finds the numberFavorites groceries of a certain customer basing on how
	 *  many reservation were previously done 
	 * @param customer customer for which the favourite groceries are wanted
	 * @param numberFavourites number of groceries to retrieve
	 * @return a number of groceries which can be maximum numberFavourites of groceries
	 *  for which the customer made a reservation, basing on how many reservation were 
	 *  done
	 */
	public List<Grocery> findCustomersFavourites(User customer, int numberFavourites) {
		return em.createNamedQuery("Grocery.findCustomersFavourites", Grocery.class)
				.setParameter("customer", customer)
				.setParameter("nFav", numberFavourites)
				.getResultList();
	}
}
