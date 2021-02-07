package src.main.java.services.reservationManagement.implementation;


import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.interfaces.ReservationHandlerModule;
import src.main.java.services.reservationManagement.interfaces.TimeEstimationModule;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;

/**
 * Implementation for the reservation handler module.
 */
@Stateless
public class ReservationHandlerImplementation extends ReservationHandlerModule {

	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@Override
	public Reservation addReservation(int iduser, int idgrocery, ReservationType type, Date bookTime, Position position) throws CLupException {
		User user =  usrTools.findUser(iduser);
		Grocery grocery = grocTools.findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("Can't find the grocery in new reservation");
		}
				
		if(user == null) {
			throw new CLupException("Can't find the user in new reservation");
		}
		
		if(position == null) {
			throw new CLupException("Position for new reservation is null");
		}
		// create a new Reservation object
		Reservation newReservation = new Reservation(user, grocery, type, bookTime);
		// persist it
		
		// create time estimation
		invokeEstimateTime(newReservation, position);
		// add the reservation to the ones made by the user
		user.addReservation(newReservation);
		
		resTools.persistReservation(newReservation);

		return newReservation;
	}

	@Override
	public Reservation removeReservation(Reservation reservation) throws CLupException {
		if(reservation == null) {
			throw new CLupException("Can't remove a null reservation");
		}
			
		if(reservation.getStatus() == ReservationStatus.ALLOWED ||
				reservation.getStatus() == ReservationStatus.ENTERED) {
			Queue queue = reservation.getQueue();
			queue.removeReservation(reservation);
		} else {
			if(reservation.getStatus() == ReservationStatus.OPEN) {
				if(reservation.getQueueTimer() != null) {
					reservation.getQueueTimer().cancel();
				}
			}
			
			reservation.setStatus(ReservationStatus.CLOSED);
		}
		User customer = reservation.getCustomer();
		customer.removeReservation(reservation);
		resTools.removeReservation(reservation);
		
		return reservation;
	}

	
	@Override
	public Reservation getReservation(int idreservation) {
		return resTools.findReservation(idreservation);
	}


	@Override
	public int closeReservation(int idreservation) throws CLupException {
		Reservation reservation = resTools.findReservation(idreservation);
		if(reservation == null) {
			throw new CLupException("Can't find the reservation to close");
		}
		
		if(reservation.getStatus() != ReservationStatus.ENTERED) {
			throw new CLupException("Can't close the reservation");
		}
		// this operation closes the reservation too
		reservation.getQueue().removeReservation(reservation);
		
		resTools.detachReservation(reservation);
		
		return 0;
	}
	
	@Override
	public List<Reservation> getAllReservationsOfGrocery(int idgrocery) throws CLupException {
		Grocery grocery = grocTools.findGrocery(idgrocery);

		if(grocery == null) {
			throw new CLupException("There is no grocery with that id");
		}
		
		return resTools.findAllByGrocery(grocery);
	}
			
	protected void invokeEstimateTime(Reservation reservation, Position position) throws CLupException {
		timeEstimationMod.estimateTime(reservation, position);
	}


	
}
