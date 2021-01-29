package src.main.java.services.groceryManagement.implementation;

import java.util.List;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.utils.Roles;

public class GroceryHandlerModuleImplementation extends GroceryHandlerModule {

	@Override
	public Grocery addGrocery(String name, Position position, int maxSpotsInside, int idowner) throws CLupException {
		Grocery grocery = new Grocery();
		User owner = findUser(idowner);
		
		if(owner == null) {
			throw new CLupException("Future owner of the grocery does "
					+ "not exist");
		}
		
		if(name == null || name.isBlank()) {
			throw new CLupException("Name can't be null or blank");
		}
		
		if(position == null) {
			throw new CLupException("Name can't be null or blank");
		}
		
		if(owner.getRole() != Roles.MANAGER) {
			throw new CLupException("Not-manager users can't add new groceries");
		}
		
		List<Grocery> names = namedQueryGroceryFindGroceryByName(name);
		
		if(!names.isEmpty()) {
			return null;
		}
		
		grocery.setName(name);
		grocery.setLatitude(position.getLat());
		grocery.setLongitude(position.getLon());
		grocery.setMaxSpotsInside(maxSpotsInside);
		grocery.setOwner(owner);
		
		persistGrocery(grocery);
		
		return grocery;
	}

	@Override
	public Grocery editGrocery(int idgrocery, String name, int maxSpotsInside) throws CLupException {
		Grocery grocery = findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("Can't find the grocery to edit");
		}
		
		if(name != null) {
			if(name.isBlank()) {
				throw new CLupException("Can't edit the name of a grocery with a blank string");
			}
			List<Grocery> names = namedQueryGroceryFindGroceryByName(name);
			
			if(names.isEmpty()) {
				grocery.setName(name);
			} else {
				return null;
			}
		}
		
		if(maxSpotsInside > 0) {
			grocery.setMaxSpotsInside(maxSpotsInside);
		}
		
		return grocery;
	}

	@Override
	public Grocery removeGrocery(int idgrocery) throws CLupException {
		Grocery grocery = findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("Can't find the grocery to remove");
		}
		
		removeGrocery(grocery);
		
		return grocery;
	}
	
	/**
	 * Decouple the invocation of entity manager
	 * @param iduser id of the user to be searched
	 * @return User instance if found something, null otherwise
	 */
	protected User findUser(int iduser) {
		return em.find(User.class, iduser);
	}
	/**
	 * Decouple the invocation of entity manager
	 * @param idgrocery id of the grocery to be searched
	 * @return Grocery instance if found something, null otherwise
	 */
	protected Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class,  idgrocery);
	}
	/**
	 * Decouple the invocation of entity manager
	 * @param grocery grocery to be persisted
	 */
	protected void persistGrocery(Grocery grocery) {
		em.persist(grocery);
	}
	/**
	 * Decouple the invocation of entity manager
	 * @param grocery grocery to be removed
	 */
	protected void removeGrocery(Grocery grocery) {
		em.remove(grocery);
	}
	/**
	 * Decouple the invocation of entity manager
	 * @param name name to be passed to the query
	 * @return result of the named query Grocery.findGroceryByName
	 */
	protected List<Grocery> namedQueryGroceryFindGroceryByName(String name){
		return em.createNamedQuery("Grocery.findGroceryByName", Grocery.class)
				.setParameter("name", name)
				.getResultList();
	}


}
