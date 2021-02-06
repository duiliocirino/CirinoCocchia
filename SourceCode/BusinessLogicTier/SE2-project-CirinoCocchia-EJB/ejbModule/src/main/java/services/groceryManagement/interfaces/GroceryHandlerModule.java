package src.main.java.services.groceryManagement.interfaces;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.services.macrocomponents.GroceryManagement;

/**
 *  This module is accessible for managers only and it 
 *  lets them update or add informations about the store, such as 
 *  opening hours or grocery capiency, but also let the manager add a 
 *  new store or deleting them
 */
public abstract class GroceryHandlerModule extends GroceryManagement {
	/**
	 * This method adds to the system a new grocery. The request has to be made 
	 * strictly by a manager 
	 * @param name name of the grocery
	 * @param position position of the grocery composed of latitude and longitude
	 * @param maxSpotsInside higher threshold number of people admitted to stay inside the store 
	 * @param openingHour hour from which the grocery will be open
	 * @param closingHour hour from which the grocery will be closed
	 * @param idowner iduser of the manager who made the request to add the grocery
	 * @return persisted instance of the grocery just added to the system, null if the name already exists
	 * @throws CLupException if the user that is doing the request is not existed or allowed to do this 
	 */
	public abstract Grocery addGrocery(String name, Position position, int maxSpotsInside, int openingHour, int closingHour, int idowner) throws CLupException;
	/**
	 * This method allows to edit the name of the grocery and/or the maximum allowed 
	 * number of people to stay inside the store
	 * @param idgrocery id of the grocery to edit
	 * @param name new name of the grocery (if null, will not be edited)
	 * @param maxSpotsInside new higher threshold number of people allowed to stay inside the store 
	 * (if less or equal to zero, will not change, it is trated as null)
	 * @return persisted edited instance of the grocery just edited, null if the name was already in
	 *  the database
	 * @throws CLupException in the case in which the grocery is not found 
	 */
	public abstract Grocery editGrocery(int idgrocery, String name, int maxSpotsInside) throws CLupException;
	/**
	 * This method allows to remove all the informations related to a certain grocery
	 * @param idgrocery id of the grocery to be deleted
	 * @return no more persisted instance of the grocery just deleted
	 * @throws CLupException if the grocery to remove is not found
	 */
	public abstract Grocery removeGrocery(int idgrocery) throws CLupException; 
	/**
	 * This method retrieves a grocery basing on its id
	 * @param idgrocery id of the grocery to retrieve
	 * @return Grocery instance if found, null if not
	 */
	public abstract Grocery getGrocery(int idgrocery);
	public static GroceryHandlerModule getInstance() {
		return new GroceryHandlerModuleImplementation();
	}
}
