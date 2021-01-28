package services.searchManagement.implementation;

import java.util.ArrayList;
import java.util.List;

import exceptions.CLupException;
import model.Grocery;
import model.Position;
import model.User;
import services.searchManagement.interfaces.SearchEngineModule;

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
		return em.find(User.class, iduser);
	}
	
	protected Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class, idgrocery);
	}
	
	protected List<Grocery> namedQueryGroceryFindAll(){
		return em.createNamedQuery("Grocery.findAll", Grocery.class)
				.getResultList();
	}
	
	protected List<Grocery> namedQueryReservationFindCustomersFavourites(User customer, int nFav) {
		return em.createNamedQuery("Reservation.findCustomersFavourites", Grocery.class)
				.setParameter("customer", customer)
				.setParameter("nFav", nFav)
				.getResultList();
	}

}
