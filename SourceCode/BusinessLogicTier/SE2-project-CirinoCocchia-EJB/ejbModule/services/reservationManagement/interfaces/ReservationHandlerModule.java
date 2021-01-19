package services.reservationManagement.interfaces;

import java.sql.Date;

import javax.ejb.Stateless;

import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.ReservationHandlerImplementation;
import utils.ReservationType;

@Stateless
public abstract class ReservationHandlerModule extends ReservationManagement{
	
	/**
	 * Adds a reservation into the system. It assigns the QR code and the right status
	 * @param iduser id of the user that makes the reservation
	 * @param idgrocery id of the grocery in which the user is doing the reservation
	 * @param type type of reservation
	 * @param bookTime time of the book-a-visit (can be null)
	 * @return New instance of Reservation created, null if it encountered some problem
	 */
	public abstract Reservation addReservation(int iduser, int idgrocery, ReservationType type, Date bookTime);
	/**
	 * Edits a reservation by removing the one passed and adding another one with the parameters passed. 
	 * If this would not be possible, then no change is made and null is returned.
	 * @param idreservation id of the reservation to edit
	 * @param iduser id of the user that wants to edit the reservation
	 * @param idgrocery id of the grocery for which the reservation will be made
	 * @param type type of the reservation to do
	 * @param bookTime time of the book-a-visit (can be null)
	 * @return if nothing goes wrong, it returns the new Reservation class, otherwise it returns null
	 */
	public abstract Reservation editReservation(int idreservation, int iduser, int idgrocery, ReservationType type, Date bookTime);;
	/**
	 * Removes a reservation to the ones tracked by the system
	 * @param reservation reservation to be deleted
	 * @return reservation just deleted, no more persisted
	 */
	public abstract Reservation removeReservation(Reservation reservation);
	/**
	 * Gets a reservation basing on its id
	 * @param idreservation id of the reservation to get
	 * @return Reservation persisted instance if something is found, null
	 *  otherwise
	 */
	public abstract Reservation getReservation(int idreservation);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static ReservationHandlerModule getInstance() {
		return new ReservationHandlerImplementation();
	}
	
}
