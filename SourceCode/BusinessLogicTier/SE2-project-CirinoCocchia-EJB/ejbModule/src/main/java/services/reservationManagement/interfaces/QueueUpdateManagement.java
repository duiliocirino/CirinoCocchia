package src.main.java.services.reservationManagement.interfaces;

import java.util.Date;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Reservation;
import src.main.java.services.macrocomponents.ReservationManagement;
import src.main.java.services.reservationManagement.implementation.QueueUpdateManagementImplementation;

@Stateless
public abstract class QueueUpdateManagement extends ReservationManagement {
	
	/**
	 * Refreshes the queue. This method is called mostly after 
	 * @param grocery grocery for which update the queue
	 * @return 0 if no problem arises, -1 otherwise
	 */
	public abstract void refreshQueue(Grocery grocery);
	/**
	 * Sets the timer at the end of which the reservation will be added to
	 * the queue
	 * @param reservation reservation to be set
	 * @return instance of reservation with the timer set
	 */
	public abstract Reservation setReservationTimer(Reservation reservation);
	/**
	 * This method has to be invoked when a customer which has to be allowed 
	 * by the system before, gets inside the store
	 * @param idreservation id of the reservation for which the customer got 
	 * into the store
	 * @return true if the the user can get actually inside the store, false if 
	 * in the last check of the fullness of the grocery goes wrong 
	 * @throws CLupException if the reservation passed is not existent on the DB, or 
	 * it is not allowed to get into the store
	 */
	public abstract boolean setIntoTheStore(int idreservation) throws CLupException;
	/**
	 * Line up a new reservation
	 * @param iduser id of user that wishes to line up
	 * @param idgrocery id of the grocery in which the user is wishing to line up
	 * @param lat latitude from which the request has been made
	 * @param lon longitude from which the request has been made
	 * @return instance of the just-created Reservation
	 * @throws CLupException if there are problems in creating the reservation
	 */
	public abstract Reservation lineUp(int iduser, int idgrocery, double lat, double lon) throws CLupException;
	/**
	 * Make a new Book-a-Visit reservation
	 * @param reservation reservation to be lined-up
	 * @return 0 if no problem arises, -1 otherwise
	 */
	public abstract int bookAVisit(int iduser, int idgrocery, Date bookTime);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static QueueUpdateManagement getInstance() {
		return new QueueUpdateManagementImplementation();
	}
}
