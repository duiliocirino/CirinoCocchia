package src.main.java.services.reservationManagement.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.interfaces.AvailabilityModule;
import src.main.java.services.reservationManagement.interfaces.TimeEstimationModule;
import src.main.java.utils.ReservationType;

@Stateless
public class AvailabilityModuleImplementation extends AvailabilityModule{

	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@Override
	public List<Reservation> checkAvailability(int idcustomer, ReservationType resType, int idgrocery, Date timestamp, double lat, double lon) throws CLupException {
		User customer = findUser(idcustomer);
		Grocery grocery = findGrocery(idgrocery);		
		Position position = new Position(lat, lon);
		List<Reservation> reservations = new ArrayList<Reservation>();
				
		if(customer == null) {
			throw new CLupException("There is no user with that id");
		}
		
		if(grocery == null) {
			throw new CLupException("There is no grocery with that id");
		}
		
		Reservation reservation = new Reservation(customer, grocery, resType, timestamp);
		invokeEstimateTime(reservation, position);
		reservations.add(reservation);
		
		return reservations;
	}

	@Override
	public boolean isAvailable(Reservation reservation) {
		// TODO delete, method out of focus
		return false;
	}
	
	protected User findUser(int iduser) {
		return usrTools.findUser(iduser);
	}
	
	protected Grocery findGrocery(int idgrocery) {
		return grocTools.findGrocery(idgrocery);
	}
	
	protected void invokeEstimateTime(Reservation reservation, Position position) throws CLupException {
		timeEstimationMod.estimateTime(reservation, position);
	}

}
