package services.reservationManagement.interfaces;

import javax.ejb.Stateless;

import model.Reservation;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.TimeEstimationModuleImplementation;

@Stateless
public abstract class TimeEstimationModule extends ReservationManagement {
	/**
	 * Calculates the estimated time for a certain reservation 
	 * @param reservation reservation for which calculate the estimated time of visit
	 * @return the estimated time measured in seconds
	 */
	public abstract double estimatedVisitTime(Reservation reservation);
	// public abstract double estimatedVisitTime(Grocery grocery, Position position, Timestamp timestamp);
	/**
	 * Calculates the estimated time of an opened reservation
	 * @param reservation 
	 * @return the estimated time measured in seconds
	 */
	public abstract double getEstimatedTime(Reservation reservation);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static TimeEstimationModule getInstance() {
		return new TimeEstimationModuleImplementation();
	}
}
