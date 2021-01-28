package services.reservationManagement.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import exceptions.CLupException;
import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.AvailabilityModuleImplementation;
import utils.ReservationType;

@Stateless
public abstract class AvailabilityModule extends ReservationManagement{
	/**
	 * Checks the availability of a certain grocery for a certain type of reservation for a certain
	 * time. In this application, it reduces to create the reservations and compute the estimated
	 *  time in order to make it visible to the user.
	 * @param idcustomer id of the customer that made the request
	 * @param resType type of the reservation to be checked
	 * @param idgrocery id of the grocery for which check the availability
	 * @param timestamp time for which check the availability
	 * @param lat latitude from which the customer has made the request
	 * @param lon longitude from which the customer has made the request
	 * @return List of the reservations available for a certain Grocery
	 * @throws CLupException if the passed user or grocery is not found on the DB
	 */
	public abstract List<Reservation> checkAvailability(int idcustomer, ReservationType resType, int idgrocery, Date timestamp, double lat, double lon) throws CLupException;
	/**
	 * Checks if a reservation is available 
	 * @param reservation reservation to check
	 * @return
	 */
	public abstract boolean isAvailable(Reservation reservation);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static AvailabilityModule getInstance() {
		return new AvailabilityModuleImplementation();
	}
}
	