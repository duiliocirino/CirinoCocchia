package services.reservationManagement.interfaces;

import java.util.Date;

import javax.ejb.Stateless;

import model.Position;
import model.Queue;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.TimeEstimationModuleImplementation;

@Stateless
public abstract class TimeEstimationModule extends ReservationManagement {
	/**
	 * Calculates the estimated time for a certain period of time, accordingly to other
	 * Reservations already made and for a specific queue.
	 * @param estimatedTiemstamp time-stamp for which calculate the estimated time of visit
	 * @param queue queue for which estimate the visit time
	 * @return the estimated time measured in seconds
	 */
	public abstract int estimatedVisitTime(Date estimatedTimestamp, Queue queue);
	// public abstract double estimatedVisitTime(Grocery grocery, Position position, Timestamp timestamp);
	/**
	 * Returns the estimated time expressed in how many seconds the allowance to get inside the store will be
	 * @param idreservation id of the reservation already registered into the system
	 * @return the estimated time measured in seconds, if negative the reservation is not OPEN or is not existent
	 */
	public abstract double getEstimatedTimeSeconds(int idreservation);
	/**
	 * Calculates the estimated time of an opened reservation and assigns that value to the reservation
	 * @param idreservation id of the reservation to estimate the time
	 * @param position position from which the reservation has been made
	 * @return date of the estimation expressed as a timestamp
	 */
	public abstract Date estimateTime(int idreservation, Position position);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static TimeEstimationModule getInstance() {
		return new TimeEstimationModuleImplementation();
	}
}
