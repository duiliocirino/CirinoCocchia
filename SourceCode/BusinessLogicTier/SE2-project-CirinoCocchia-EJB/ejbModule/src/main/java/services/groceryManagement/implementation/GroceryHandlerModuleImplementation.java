package src.main.java.services.groceryManagement.implementation;

import java.util.List;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.model.Queue;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.utils.Roles;

@Stateless
public class GroceryHandlerModuleImplementation extends GroceryHandlerModule {

	@Override
	public Grocery addGrocery(String name, Position position, int maxSpotsInside, int openingHour, int closingHour, int idowner) throws CLupException {
		Grocery grocery = new Grocery();
		User owner = usrTools.findUser(idowner);
		Queue queue = new Queue();
		
		if(owner == null) {
			throw new CLupException("Future owner of the grocery does "
					+ "not exist");
		}
		
		if(name == null || name.isEmpty()) {
			throw new CLupException("Name can't be null or blank");
		}
		
		if(position == null) {
			throw new CLupException("Name can't be null or blank");
		}
		
		if(owner.getRole() != Roles.MANAGER) {
			throw new CLupException("Not-manager users can't add new groceries");
		}
		
		if(openingHour < 0 || openingHour > 24 || closingHour < 0 || closingHour > 24) {
			throw new CLupException("You did not insert valid hours");
		}
		
		if(openingHour >= closingHour) {
			throw new CLupException("Hours are not coherent");
		}
		
		List<Grocery> names = grocTools.findGroceryByName(name);
		
		if(!names.isEmpty()) {
			return null;
		}
		
		grocery.setName(name);
		grocery.setLatitude(position.getLat());
		grocery.setLongitude(position.getLon());
		grocery.setMaxSpotsInside(maxSpotsInside);
		grocery.setOwner(owner);
		grocery.setQueue(queue);
		queue.setGrocery(grocery);
		
		grocTools.persistGrocery(grocery);
		
		return grocery;
	}

	@Override
	public Grocery editGrocery(int idgrocery, String name, int maxSpotsInside) throws CLupException {
		Grocery grocery = grocTools.findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("Can't find the grocery to edit");
		}
		
		if(name != null) {
			if(name.isEmpty()) {
				throw new CLupException("Can't edit the name of a grocery with a blank string");
			}
			List<Grocery> names = grocTools.findGroceryByName(name);
			
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
		Grocery grocery = grocTools.findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("Can't find the grocery to remove");
		}
		
		grocTools.removeGrocery(grocery);
		
		return grocery;
	}
	
	@Override
	public Grocery getGrocery(int idgrocery) {
		return grocTools.findGrocery(idgrocery);
	}

}
