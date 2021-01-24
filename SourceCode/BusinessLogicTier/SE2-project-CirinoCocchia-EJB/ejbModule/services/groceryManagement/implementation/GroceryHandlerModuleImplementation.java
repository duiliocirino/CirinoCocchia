package services.groceryManagement.implementation;

import java.util.List;

import model.Grocery;
import model.Position;
import model.User;
import services.groceryManagement.interfaces.GroceryHandlerModule;
import utils.Roles;

public class GroceryHandlerModuleImplementation extends GroceryHandlerModule {

	@Override
	public Grocery addGrocery(String name, Position position, int maxSpotsInside, int idowner) {
		Grocery grocery = new Grocery();
		User owner = em.find(User.class,  idowner);
		
		if(owner == null) {
			return null;
		}
		
		if(owner.getRole() != Roles.MANAGER) {
			return null;
		}
		
		List<Grocery> names = em.createNamedQuery("Grocery.findGroceryByName", Grocery.class)
				.setParameter("name", name)
				.getResultList();
		
		if(!names.isEmpty()) {
			return null;
		}
		
		grocery.setName(name);
		grocery.setLatitude(position.getLat());
		grocery.setLongitude(position.getLon());
		grocery.setMaxSpotsInside(maxSpotsInside);
		grocery.setOwner(owner);
		
		em.persist(grocery);
		
		return grocery;
	}

	@Override
	public Grocery editGrocery(int idgrocery, String name, int maxSpotsInside) {
		Grocery grocery = em.find(Grocery.class,  idgrocery);
		
		if(grocery == null) {
			return null;
		}
		
		if(name != null) {
			List<Grocery> names = em.createNamedQuery("Grocery.findGroceryByName", Grocery.class)
					.setParameter("name", name)
					.getResultList();
			
			if(!names.isEmpty()) {
				grocery.setName(name);
			}
		}
		
		if(maxSpotsInside > 0) {
			grocery.setMaxSpotsInside(maxSpotsInside);
		}
		
		return null;
	}

	@Override
	public Grocery removeGrocery(int idgrocery) {
		Grocery grocery = em.find(Grocery.class,  idgrocery);
		
		if(grocery == null) {
			return null;
		}
		
		em.remove(grocery);
		
		return grocery;
	}


}
