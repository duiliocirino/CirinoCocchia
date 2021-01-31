package src.main.java.services.reservationManagement.interfaces;

import java.util.Date;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.services.macrocomponents.ReservationManagement;
import src.main.java.services.reservationManagement.implementation.TimeEstimationModuleImplementation;

public abstract class TimeEstimationModule extends ReservationManagement {
	/**
	 * How many seconds more and less we should consider when computing the 
	 * estimated time considering the other reservations.
	 */
	protected final int INTERVAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC = 60;
	/**
	 * Number of reservations for which we can consider a certain grocery 
	 * almost full for a certain instant of time. Will be considered as full 
	 * when the number of reservations will be the double with respect of 
	 * this value.
	 */
	public final int INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME = 3;
	/**
	 * How much more time is added for each interval.
	 */
	public final double ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC = 60;
	/**
	 * How many minutes preceeding the moment of the request are acceptable 
	 * by the program, if a request arrives before of these minutes, it is 
	 * classified as a "past" request and an exception will arise
	 */
	public final int MINIMUM_THRESHOLD_MINUTES = 1;

	/**
	 * Fixed time set in order to do pessimistic estimations.
	 */
	protected final double FIX_TIME = 60.0;
	/**
	 * Calculates the spread time (see DD) for a certain period of time, accordingly to other
	 * Reservations already made and for a specific queue.
	 * @param estimatedTiemstamp time-stamp for which calculate the estimated time of visit
	 * @param queue queue for which estimate the visit time
	 * @return the estimated time measured in seconds
	 * @throws CLupException if parameters are wrong. For instance, the date inserted is preceeding 
	 * the current date, or some parameter is null
	 */
	public abstract double estimatedSpreadTime(Date estimatedTimestamp, Queue queue) throws CLupException;
	// public abstract double estimatedVisitTime(Grocery grocery, Position position, Timestamp timestamp);
	/**
	 * Returns the estimated time expressed in how many seconds the allowance to get inside the store will be
	 * @param idreservation id of the reservation already registered into the system
	 * @return the estimated time measured in seconds, if negative the reservation is not OPEN or is not existent
	 * @throws CLupException when there is no Reservation with the id passed in the DB
	 */
	public abstract double getEstimatedTimeSeconds(int idreservation) throws CLupException;
	/**
	 * Calculates the estimated time of an opened reservation and assigns that value to the reservation
	 * @param reservation to estimate the time
	 * @param position position from which the reservation has been made
	 * @return date of the estimation expressed as a timestamp, null if the estimation is no more possible
	 *  e.g. when the reservation status is not OPEN
	 * @throws CLupException if the passed reservation is null
	 */
	public abstract Date estimateTime(Reservation reservation, Position position) throws CLupException;
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static TimeEstimationModule getInstance() {
		return new TimeEstimationModuleImplementation();
	}
}
