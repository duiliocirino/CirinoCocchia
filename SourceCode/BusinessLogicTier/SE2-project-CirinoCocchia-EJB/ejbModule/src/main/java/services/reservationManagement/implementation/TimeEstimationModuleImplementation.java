package src.main.java.services.reservationManagement.implementation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.services.reservationManagement.interfaces.TimeEstimationModule;
import src.main.java.utils.ReservationStatus;

@Stateless
public class TimeEstimationModuleImplementation extends TimeEstimationModule{
	
	@EJB(name = "services.reservationManagement.implementation/NotificationModuleImplementation")
	private NotificationModuleImplementation notificationMod;
	
	@Override
	public double estimatedSpreadTime(Date estimatedTimestamp, Queue queue) throws CLupException {
		
		if(estimatedTimestamp == null) {
			throw new CLupException("Can't compute the spread time, null Date");
		}
		
		if(queue == null) {
			throw new CLupException("Can't compute the spread time, null Queue");
		}
		
		int sizeInterval = INTERVAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC;
		
		Calendar startCal = Calendar.getInstance();
		int minutes = startCal.get(Calendar.MINUTE);

		startCal.set(Calendar.MINUTE, minutes - MINIMUM_THRESHOLD_MINUTES);
		
		if(startCal.getTime().after(estimatedTimestamp)) {
			throw new CLupException("Can't estimate the spread time, time preceeding now");
		}
		
		startCal.setTime(estimatedTimestamp);
		int seconds = (int) Math.ceil(startCal.getTimeInMillis()/1000);
		Calendar endCal = startCal;
		
		startCal.set(Calendar.SECOND, seconds + sizeInterval);
		endCal.set(Calendar.SECOND, seconds + sizeInterval);
		
		Date start = startCal.getTime();
		Date end = endCal.getTime();
		
		List<Reservation> res = resTools.findByInterval(queue, start, end);
			
		if(res.size() < INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME) {
			return 0.0;
		} else if(res.size() < INTERVAL_NUMBER_RESERVATIONS_COMPUTATION_SREAD_TIME * 2) {
			return ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC;
		} else {
			return 2 * ADDITIONAL_TIME_COMPUTATION_SPREAD_TIME_IN_SEC;
		}
	}

	@Override
	public double getEstimatedTimeSeconds(int idreservation) throws CLupException {
		Reservation res = resTools.findReservation(idreservation);
		
		if(res == null) {
			throw new CLupException("Can't get esitmated time, no reservation with that id");
		}
		
		Calendar estTime = Calendar.getInstance();
		estTime.setTime(res.getEstimatedTime());

		return estTime.get(Calendar.SECOND);
	}

	@Override
	public Date estimateTime(Reservation reservation, Position position) throws CLupException {

		if(reservation == null) {
			throw new CLupException("Can't estimate the time of a null reservation");
		}
		
		if(position == null) {
			throw new CLupException("Can't estimate the time from a null position");
		}
		
		if(reservation.getStatus() != ReservationStatus.OPEN) {
			return null;
		}
		/**
		 *  in the case in which the grocery is full, a factor concerning the
		 *  average duration of shopping by other customers in the same period
		 *  of the day
		 */
		double avg_time = 0;
		/**
		 *  fixed time to be set in order to do pessimistic estimates
		 */
		double fix_time = FIX_TIME;
		/**
		 *  necessary time to move from the position provided to the selected
		 *  grocery
		 */
		double maps_time = 0;
		// create a position instance to wrap grocery's position
		Position groceryPosition = new Position(
				reservation.getQueue().getGrocery().getLatitude(),
				reservation.getQueue().getGrocery().getLongitude());
		// invoke ridetime to estimate the movement time
		maps_time = invokeRideTime(position, groceryPosition);
		// partial result
		double totalEstimatedTimeSeconds = avg_time + fix_time + maps_time;
		int integerEstimatedTimeSeconds = (int) Math.ceil(totalEstimatedTimeSeconds);
		// transform the result in a date instance to compute spread time
		Calendar now = Calendar.getInstance();
		int seconds = (int) Math.ceil(now.getTimeInMillis()/1000);
		Calendar estimatedCal = now;	
		estimatedCal.set(Calendar.SECOND, seconds + integerEstimatedTimeSeconds);
		Date estimatedTimeIntermediate = estimatedCal.getTime();
				
		/**
		 * time factor that looks at other previously done line-up reservations
		 * and tries to avoid that too many people overlap in a single time
		 */
		int spread_time = (int) Math.ceil(estimatedSpreadTime(estimatedTimeIntermediate, reservation.getQueue()));
		// add the spread time to  the partial result of before
		seconds = (int) Math.ceil(estimatedCal.getTimeInMillis()/1000);
		estimatedCal.set(Calendar.SECOND, seconds + spread_time);
		
		Date estimatedTime = estimatedCal.getTime();
		// set the estimated time to the reservation
		reservation.setEstimatedTime(estimatedTime);
						
		return estimatedTime;
	}

	protected double invokeRideTime(Position origin, Position end) {
		return notificationMod.rideTime(origin, end);
	}


}
