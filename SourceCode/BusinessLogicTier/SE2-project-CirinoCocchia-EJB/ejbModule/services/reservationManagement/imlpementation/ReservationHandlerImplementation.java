package services.reservationManagement.imlpementation;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Grocery;
import model.Position;
import model.Reservation;
import model.User;
import services.reservationManagement.interfaces.QueueUpdateManagement;
import services.reservationManagement.interfaces.ReservationHandlerModule;
import services.reservationManagement.interfaces.TimeEstimationModule;
import utils.ReservationStatus;
import utils.ReservationType;

/**
 * Implementation for the reservation handler module.
 */
@Stateless
public class ReservationHandlerImplementation extends ReservationHandlerModule {

	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@EJB(name = "services.reservationManagement.implementation/QueueUpdateManagementImplementation")
	private QueueUpdateManagement queueUpdateMod;
	
	@Override
	public Reservation addReservation(int iduser, int idgrocery, ReservationType type, Date bookTime, Position position) {
		User user = em.find(User.class,  iduser);
		Grocery grocery = em.find(Grocery.class,  idgrocery);
		
		if(grocery == null || user == null) {
			return null;
		}
		// create a new Reservation object
		Reservation newReservation = new Reservation(user, grocery, type, bookTime);
		// persist it
		em.persist(newReservation);
		
		// create time estimation
		timeEstimationMod.estimateTime(newReservation.getIdreservation(), position);
		
		return newReservation;
	}
	

	@Override
	public Reservation editReservation(int idreservation, int iduser, int idgrocery, ReservationType type,
			Date bookTime) {
		Reservation oldReservation = em.find(Reservation.class,  idreservation);
		User user = em.find(User.class, iduser);
		Grocery grocery = em.find(Grocery.class, idgrocery);
		
		if(oldReservation == null|| grocery == null || user == null) {
			return null;
		}
		
		Reservation newReservation = new Reservation(user, grocery, type, bookTime);
		oldReservation.setStatus(ReservationStatus.CLOSED);
		if(oldReservation.getStatus() == ReservationStatus.OPEN) {
			oldReservation.getQueueTimer().cancel();
		}
		em.remove(oldReservation);
		em.persist(newReservation);
		
		return newReservation;
		
	}


	@Override
	public Reservation removeReservation(Reservation reservation) {
		reservation.setStatus(ReservationStatus.CLOSED);
		if(reservation.getStatus() == ReservationStatus.OPEN) {
			reservation.getQueueTimer().cancel();
		}
		em.remove(reservation);
		
		return reservation;
	}

	
	@Override
	public Reservation getReservation(int idreservation) {
		return em.find(Reservation.class, idreservation);
	}


	@Override
	public int closeReservation(int idreservation) {
		Reservation reservation = em.find(Reservation.class, idreservation);
		if(reservation == null) {
			return -1;
		}
		// this operation closes the reservation too
		reservation.getQueue().removeReservation(reservation);
		
		em.detach(reservation);
		
		return 0;
	}

	
}
