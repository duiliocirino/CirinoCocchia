package services.reservationManagement.interfaces;

import java.util.Date;

import javax.ejb.Stateless;

import exceptions.CLupException;
import model.Position;
import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.ReservationHandlerImplementation;
import utils.ReservationType;

@Stateless
public abstract class ReservationHandlerModule extends ReservationManagement{
	
	/**
	 * Adds a reservation into the system. It assigns the OPEN status and an estimated time
	 * @param iduser id of the user that makes the reservation
	 * @param idgrocery id of the grocery in which the user is doing the reservation
	 * @param type type of reservation
	 * @param bookTime time of the book-a-visit (can be null)
	 * @param position position from which the user is doing the reservation
	 * @return New instance of Reservation created, null if it encountered some problem
	 * @throws CLupException id some parameter is not valid, such as iduser is not relative to a user 
	 * stored in the DB, idgrocery is not relative to a grocery, or position is null
	 */
	public abstract Reservation addReservation(int iduser, int idgrocery, ReservationType type, Date bookTime, Position position) throws CLupException;
	/**
	 * Removes a reservation to the ones tracked by the system.
	 * @param reservation reservation to be deleted
	 * @return reservation just deleted, no more persisted
	 * @throws CLupException if the passed reservation is null
	 */
	public abstract Reservation removeReservation(Reservation reservation) throws CLupException;
	/**
	 * Gets a reservation basing on its id
	 * @param idreservation id of the reservation to get
	 * @return Reservation persisted instance if something is found, null
	 *  otherwise
	 */
	public abstract Reservation getReservation(int idreservation);
	/**
	 * Closes a reservation basing on its id. The reservation, in any case, is still 
	 * on the DB for statistics purposes
	 * @param idreservation id of the reservation to close
	 * @return 0 if no problem arises
	 * @throws CLupException if the reservation with that id is not found
	 */
	public abstract int closeReservation(int idreservation) throws CLupException;
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static ReservationHandlerModule getInstance() {
		return new ReservationHandlerImplementation();
	}
	
	/**
	 * 
	 * Edits a reservation by removing the one passed and adding another one with the parameters passed. 
	 * If this would not be possible, then no change is made and null is returned.
	 * @param idreservation id of the reservation to edit
	 * @param iduser id of the user that wants to edit the reservation
	 * @param idgrocery id of the grocery for which the reservation will be made
	 * @param type type of the reservation to do
	 * @param bookTime time of the book-a-visit (can be null)
	 * @return if nothing goes wrong, it returns the new Reservation class, otherwise it returns null
	 *
		public abstract Reservation editReservation(int idreservation, int iduser, int idgrocery, ReservationType type, Date bookTime);;
	 */
	
}
