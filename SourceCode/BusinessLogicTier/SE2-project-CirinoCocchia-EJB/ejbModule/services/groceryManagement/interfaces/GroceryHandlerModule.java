package services.groceryManagement.interfaces;

import javax.ejb.Stateless;

import model.Grocery;
import model.Position;
import services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import services.macrocomponents.GroceryManagement;

/**
 *  This module is accessible for managers only and it 
 *  lets them update or add informations about the store, such as 
 *  opening hours or grocery capiency, but also let the manager add a 
 *  new store or deleting them
 */
@Stateless
public abstract class GroceryHandlerModule extends GroceryManagement {
	/**
	 * This method adds to the system a new grocery. The request has to be made 
	 * strictly by a manager 
	 * @param name name of the grocery
	 * @param position position of the grocery composed of latitude and longitude
	 * @param maxSpotsInside higher threshold number of people admitted to stay inside the store 
	 * @param idowner iduser of the manager who made the request to add the grocery
	 * @return persisted instance of the grocery just added to the system
	 */
	public abstract Grocery addGrocery(String name, Position position, int maxSpotsInside, int idowner);
	/**
	 * This method allows to edit the name of the grocery and/or the maximum allowed 
	 * number of people to stay inside the store
	 * @param idgrocery id of the grocery to edit
	 * @param name new name of the grocery (if null, will not be edited)
	 * @param maxSpotsInside new higher threshold number of people allowed to stay inside the store 
	 * (if less or equal to zero, will not change)
	 * @return persisted edited instance of the grocery just edited
	 */
	public abstract Grocery editGrocery(int idgrocery, String name, int maxSpotsInside);
	/**
	 * This method allows to remove all the informations related to a certain grocery
	 * @param idgrocery id of the grocery to be deleted
	 * @return no more persisted instance of the grocery just deleted
	 */
	public abstract Grocery removeGrocery(int idgrocery); 
	
	public static GroceryHandlerModule getInstance() {
		return new GroceryHandlerModuleImplementation();
	}
}
