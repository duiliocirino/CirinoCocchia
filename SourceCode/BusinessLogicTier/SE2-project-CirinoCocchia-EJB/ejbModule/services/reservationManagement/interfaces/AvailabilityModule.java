package services.reservationManagement.interfaces;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.Stateless;

import model.Grocery;
import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.AvailabilityModuleImplementation;
import services.reservationManagement.imlpementation.ReservationHandlerImplementation;
import utils.ReservationType;

@Stateless
public abstract class AvailabilityModule extends ReservationManagement{
	/**
	 * Checks the availability of a certain grocery for a certain type of reservation
	 * @param resType type of the reservation to be checked
	 * @param grocery grocery for which check the availability
	 * @return List of the reservations available for a certain Grocery
	 */
	public abstract List<Reservation> checkAvailability(ReservationType resType, Grocery grocery, Timestamp timestamp);
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
	