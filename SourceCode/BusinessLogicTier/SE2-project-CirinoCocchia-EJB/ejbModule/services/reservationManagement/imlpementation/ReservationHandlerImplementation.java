package services.reservationManagement.imlpementation;


import java.sql.Date;

import javax.ejb.Stateless;

import model.Grocery;
import model.Reservation;
import model.User;
import services.reservationManagement.interfaces.ReservationHandlerModule;
import utils.ReservationType;

/**
 * Implementation for the reservation handler module.
 */
@Stateless
public class ReservationHandlerImplementation extends ReservationHandlerModule {

	@Override
	public Reservation addReservation(int iduser, int idgrocery, ReservationType type, Date bookTime) {
		User user = em.find(User.class,  iduser);
		Grocery grocery = em.find(Grocery.class,  idgrocery);
		
		if(grocery == null || user == null) {
			return null;
		}
		Reservation newReservation = new Reservation(user, grocery, type, bookTime);
		em.persist(newReservation);
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
		em.remove(oldReservation);
		em.persist(newReservation);
		
		return newReservation;
		
	}


	@Override
	public Reservation removeReservation(Reservation reservation) {
		em.remove(reservation);
		
		return reservation;
	}

	@Override
	public Reservation getReservation(int idreservation) {
		
		return em.find(Reservation.class, idreservation);
	}

	
}
