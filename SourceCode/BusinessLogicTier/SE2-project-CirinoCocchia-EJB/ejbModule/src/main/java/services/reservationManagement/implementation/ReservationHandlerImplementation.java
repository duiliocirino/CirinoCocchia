package src.main.java.services.reservationManagement.implementation;


import java.util.Date;
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
		User user = findUser(iduser);
		Grocery grocery = findGrocery(idgrocery);
		
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
		persistReservation(newReservation);
		
		// create time estimation
		invokeEstimateTime(newReservation, position);
		
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
				reservation.getQueueTimer().cancel();
			}
			
			reservation.setStatus(ReservationStatus.CLOSED);
		}
		
		emRemoveReservation(reservation);
		
		return reservation;
	}

	
	@Override
	public Reservation getReservation(int idreservation) {
		return findReservation(idreservation);
	}


	@Override
	public int closeReservation(int idreservation) throws CLupException {
		Reservation reservation = findReservation(idreservation);
		if(reservation == null) {
			throw new CLupException("Can't find the reservation to close");
		}
		
		if(reservation.getStatus() != ReservationStatus.ENTERED) {
			throw new CLupException("Can't close the reservation");
		}
		// this operation closes the reservation too
		reservation.getQueue().removeReservation(reservation);
		
		detachReservation(reservation);
		
		return 0;
	}
	
	protected User findUser(int iduser) {
		return em.find(User.class, iduser);
	}
	
	protected Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class, idgrocery);
	}
	
	protected Reservation findReservation(int idreservation) {
		return em.find(Reservation.class, idreservation);
	}
	
	protected void persistReservation(Reservation reservation) {
		em.persist(reservation);
	}
	
	protected void emRemoveReservation(Reservation reservation) {
		em.remove(reservation);
	}
	
	protected void detachReservation(Reservation reservation) {
		em.detach(reservation);
	}
		
	protected void invokeEstimateTime(Reservation reservation, Position position) throws CLupException {
		timeEstimationMod.estimateTime(reservation, position);
	}
	
	/*
	@Override
	public Reservation editReservation(int idreservation, int iduser, int idgrocery, ReservationType type,
			Date bookTime) {
		Reservation oldReservation = findReservation(idreservation);
		
		if(oldReservation == null) {
			throw new CLupException("Can't ");
		}
		
		User user = oldReservation.getCustomer();
		Grocery grocery = oldReservation.getGrocery();
		Timer timer = oldReservation.getQueueTimer();
		
		Reservation newReservation = new Reservation(user, grocery, type, bookTime);
		oldReservation.setStatus(ReservationStatus.CLOSED);
		if(oldReservation.getStatus() == ReservationStatus.OPEN) {
			oldReservation.getQueueTimer().cancel();
		}
		
		emRemoveReservation(newReservation);
		persistReservation(newReservation);
		
		newReservation.setQueueTimer(timer);		
		
		return newReservation;
		
	}
*/
	
}
