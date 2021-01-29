package src.main.java.services.searchManagement.interfaces;

import java.util.List;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.services.macrocomponents.SearchManagement;

@Stateless
public abstract class SearchEngineModule extends SearchManagement {
	/**
	 * Retrieves all the groceries that are into the radius around the position
	 * @param position centre of the research
	 * @param radius radius of the circle from which get all the groceries that are into it
	 * @return list of groceries into the circle with the radius passed as an argument and with
	 * the centre on the position specified
	 * @throws CLupException in the case in which it is passed a null position or a negative radius
	 */
	public abstract List<Grocery> getNearGroceries(Position position, double radius) throws CLupException;
	/**
	 * Retrieves all the groceries marked as favourite for the user
	 * @param iduser user for which do the research
	 * @param nfavourites maximum number of favourite groceries to retrieve
	 * @return list of favourite groceries
	 * @throws CLupException if the user is not found on the DB
	 */
	public abstract List<Grocery> getFavouriteGroceries(int iduser, int nFavourites) throws CLupException;
	/**
	 * Checks if a position is near to a certain grocery given a radius
	 * @param position centre of the research's circle
	 * @param idgrocery id of the grocery to check
	 * @param radius radius of the research's circle
	 * @return true if it is into the circle, false otherwise
	 * @throws CLupException in the case in which it is passed a null position, a negative radius 
	 * or the grocery is not found
	 */
	public abstract boolean isNear(Position position, int idgrocery, double radius) throws CLupException;
}
