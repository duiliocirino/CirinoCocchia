package services.searchManagement.interfaces;

import java.util.List;

import javax.ejb.Stateless;

import model.Grocery;
import model.Position;
import services.macrocomponents.SearchManagement;

@Stateless
public abstract class SearchEngineModule extends SearchManagement {
	/**
	 * Retrieves all the groceries that are into the radius around the position
	 * @param position centre of the research
	 * @param radius radius of the circle from which get all the groceries that are into it
	 * @return list of groceries into the circle with the radius passed as an argument and with
	 * the centre on the position specified
	 */
	public abstract List<Grocery> getNearGroceries(Position position, double radius);
	/**
	 * Retrieves all the groceries marked as favourite for the user
	 * @param iduser user for which do the research
	 * @param nfavourites maximum number of favourite groceries to retrieve
	 * @return list of favourite groceries
	 */
	public abstract List<Grocery> getFavouriteGroceries(int iduser, int nFavourites);
	/**
	 * Checks if a position is near to a certain grocery given a radius
	 * @param position centre of the research's circle
	 * @param idgrocery id of the grocery to check
	 * @param radius radius of the research's circle
	 * @return true if it is into the circle, false otherwise
	 */
	public abstract boolean isNear(Position position, int idgrocery, double radius);
}
