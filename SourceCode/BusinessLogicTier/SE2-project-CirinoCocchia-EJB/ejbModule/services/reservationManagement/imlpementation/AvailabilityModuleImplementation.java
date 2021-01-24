package services.reservationManagement.imlpementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import model.Grocery;
import model.Position;
import model.Reservation;
import model.User;
import services.reservationManagement.interfaces.AvailabilityModule;
import services.reservationManagement.interfaces.TimeEstimationModule;
import utils.ReservationType;


public class AvailabilityModuleImplementation extends AvailabilityModule{

	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@Override
	public List<Reservation> checkAvailability(int idcustomer, ReservationType resType, int idgrocery, Date timestamp, double lat, double lon) {
		User customer = em.find(User.class, idcustomer);
		Grocery grocery = em.find(Grocery.class, idgrocery);		
		Position position = new Position(lat, lon);
		List<Reservation> reservations = new ArrayList<Reservation>();
				
		Reservation reservation = new Reservation(customer, grocery, resType, timestamp);
		timeEstimationMod.estimateTime(reservation.getIdreservation(), position);
		
		reservations.add(reservation);
		
		return reservations;
	}

	@Override
	public boolean isAvailable(Reservation reservation) {
		// TODO Auto-generated method stub
		return false;
	}

}
