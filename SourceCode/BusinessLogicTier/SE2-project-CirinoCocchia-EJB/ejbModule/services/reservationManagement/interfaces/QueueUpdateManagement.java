package services.reservationManagement.interfaces;

import java.util.Date;

import javax.ejb.Stateless;

import model.Grocery;
import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.QueueUpdateManagementImplementation;

@Stateless
public abstract class QueueUpdateManagement extends ReservationManagement {
	
	/**
	 * Refreshes the queue. This method is called mostly after 
	 * @param grocery grocery for which update the queue
	 * @return 0 if no problem arises, -1 otherwise
	 */
	public abstract int refreshQueue(Grocery grocery);
	/**
	 * Sets the timer at the end of which the reservation will be added to
	 * the queue
	 * @param reservation reservation to be set
	 */
	public abstract void setReservationTimer(Reservation reservation);
	/**
	 * Line up a new reservation
	 * @param iduser id of user that wishes to line up
	 * @param idgrocery id of the grocery in which the user is wishing to line up
	 * @param lat latitude from which the request has been made
	 * @param lon longitude from which the request has been made
	 * @return 0 if no problem arises, -1 otherwise
	 */
	public abstract int lineUp(int iduser, int idgrocery, double lat, double lon);
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
