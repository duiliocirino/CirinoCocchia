package services.searchManagement.implementation;

import java.util.ArrayList;
import java.util.List;

import model.Grocery;
import model.Position;
import model.User;
import services.searchManagement.interfaces.SearchEngineModule;

public class SearchEngineModuleImplementation extends SearchEngineModule {

	@Override
	public List<Grocery> getNearGroceries(Position position, double radius) {
		
		List<Grocery> allGroceries = em.createNamedQuery("Grocery.findAll", Grocery.class)
				.getResultList();
		List<Grocery> nearGroceries = new ArrayList<Grocery>();
		
		for(Grocery grocery : allGroceries) {
			if(isNear(position, grocery.getIdgrocery(), radius)) {
				nearGroceries.add(grocery);
			}
		}
	
		return nearGroceries;
	}

	@Override
	public List<Grocery> getFavouriteGroceries(int iduser, int nFavourites) {
		User customer = em.find(User.class, iduser);
		
		if(customer == null) {
			return null;
		}
		
		if(nFavourites < 1) {
			return null;
		}
		
		List<Grocery> favouriteGroceries = em.createNamedQuery("Reservation.findCustomersFavourites", Grocery.class)
				.setParameter("customer", customer)
				.setParameter("nFav", nFavourites)
				.getResultList();
		
				
		return favouriteGroceries;
	}

	@Override
	public boolean isNear(Position position, int idgrocery, double radius) {
		
		Grocery grocery = em.find(Grocery.class, idgrocery);
		if(grocery == null) {
			return false; //TODO : exception
		}
		
		double maxLat = position.getLat() + radius;
		double minLat = position.getLat() - radius;
		double maxLon = position.getLon() + radius;
		double minLon = position.getLon() - radius;
				
		return (minLat < grocery.getLatitude() && 
						grocery.getLatitude() < maxLat) &&
				(minLon < grocery.getLongitude() &&
						grocery.getLongitude() < maxLon);
	}

}
