package src.main.java.services.searchManagement.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.searchManagement.interfaces.SearchEngineModule;

@Stateless
public class SearchEngineModuleImplementation extends SearchEngineModule {

	@Override
	public List<Grocery> getNearGroceries(Position position, double radius) throws CLupException {
		
		List<Grocery> allGroceries = namedQueryGroceryFindAll();
		List<Grocery> nearGroceries = new ArrayList<Grocery>();
		
		if(position == null) {
			throw new CLupException("Can't compute a null position");
		}
		
		if(radius < 0) {
			throw new CLupException("Can't compute a radius less than 0");
		}
		
		for(Grocery grocery : allGroceries) {
			if(isNear(position, grocery.getIdgrocery(), radius)) {
				nearGroceries.add(grocery);
			}
		}
	
		return nearGroceries;
	}

	@Override
	public List<Grocery> getFavouriteGroceries(int iduser, int nFavourites) throws CLupException {
		User customer = findUser(iduser);
		
		if(customer == null) {
			throw new CLupException("Can't find the user in the DB");
		}
		
		if(nFavourites < 1) {
			throw new CLupException("Can't retrieve a number of favourite grociries"
					+ " less than 0");
		}
		
		List<Grocery> favouriteGroceries = namedQueryReservationFindCustomersFavourites(customer, nFavourites);	
				
		return favouriteGroceries;
	}

	@Override
	public boolean isNear(Position position, int idgrocery, double radius) throws CLupException {
		
		Grocery grocery = findGrocery(idgrocery);
		if(grocery == null) {
			throw new CLupException("Can't find the grocery");
		}
		
		if(position == null) {
			throw new CLupException("Can't compute a null position");
		}
		
		if(radius < 0) {
			throw new CLupException("Can't compute a radius less than 0");
		}
		
		double maxLat = position.getLat() + radius;
		double minLat = position.getLat() - radius;
		double maxLon = position.getLon() + radius;
		double minLon = position.getLon() - radius;
				
		return (minLat <= grocery.getLatitude() && 
						grocery.getLatitude() <= maxLat) &&
				(minLon <= grocery.getLongitude() &&
						grocery.getLongitude() <= maxLon);
	}
	
	protected User findUser(int iduser) {
		return usrTools.findUser(iduser);
	}
	
	protected Grocery findGrocery(int idgrocery) {
		return grocTools.findGrocery(idgrocery);
	}
	
	protected List<Grocery> namedQueryGroceryFindAll(){
		return grocTools.findAllGroceries();
	}
	
	protected List<Grocery> namedQueryReservationFindCustomersFavourites(User customer, int nFav) {
		return grocTools.findCustomersFavourites(customer, nFav);
	}

}
