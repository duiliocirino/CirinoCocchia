package services.reservationManagement.imlpementation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Position;
import model.Queue;
import model.Reservation;
import services.reservationManagement.interfaces.TimeEstimationModule;
import utils.ReservationStatus;

@Stateless
public class TimeEstimationModuleImplementation extends TimeEstimationModule{

	@EJB(name = "services.reservationManagement.implementation/NotificationModuleImplementation")
	private NotificationModuleImplementation notificationMod;
	
	@Override
	public int estimatedVisitTime(Date estimatedTimestamp, Queue queue) {
		
		int sizeInterval = 60;
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(estimatedTimestamp);
		int seconds = (int) Math.ceil(startCal.getTimeInMillis()/1000);
		Calendar endCal = startCal;
		
		startCal.set(Calendar.SECOND, seconds + sizeInterval);
		endCal.set(Calendar.SECOND, seconds + sizeInterval);
		
		Date start = startCal.getTime();
		Date end = endCal.getTime();
		
		List<Reservation> res = em.createNamedQuery("Reservation.findByInterval", Reservation.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
			
		if(res.size() < 3) {
			return 0;
		} else if(res.size() < 6) {
			return 60;
		} else {
			return 120;
		}
	}

	@Override
	public double getEstimatedTimeSeconds(int idreservation) {
		Reservation res = em.find(Reservation.class, idreservation);
		
		Calendar estTime = Calendar.getInstance();
		estTime.setTime(res.getEstimatedTime());

		return estTime.get(Calendar.SECOND);
	}

	@Override
	public Date estimateTime(int idreservation, Position position) {
		Reservation reservation = em.find(Reservation.class, idreservation);
		if(reservation == null) {
			return null;
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
		double fix_time = 60;
		/**
		 *  necessary time to move from the position provided to the selected
		 *  grocery
		 */
		double maps_time = 0;
		// create a position instance to wrap grocery's position
		Position groceryPosition = new Position(
				reservation.getQueue().getGrocery().getLatitude(),
				reservation.getQueue().getGrocery().getLongitude());
		maps_time = notificationMod.rideTime(position, groceryPosition);
		
		double totalEstimatedTime = avg_time + fix_time + maps_time;
		int integerEstimatedTime = (int) Math.ceil(totalEstimatedTime);
		
		Calendar now = Calendar.getInstance();
		int seconds = (int) Math.ceil(now.getTimeInMillis()/1000);
		Calendar estimatedCal = now;
		
		estimatedCal.set(Calendar.SECOND, seconds + integerEstimatedTime);
		Date estimatedTimeIntermediate = estimatedCal.getTime();
				
		/**
		 * time factor that looks at other previously done line-up reservations
		 * and tries to avoid that too many people overlap in a single time
		 */
		int spread_time = estimatedVisitTime(estimatedTimeIntermediate, reservation.getQueue());
		
		seconds = (int) Math.ceil(estimatedCal.getTimeInMillis()/1000);
		estimatedCal.set(Calendar.SECOND, seconds + spread_time);
		
		Date estimatedTime = estimatedCal.getTime();
				
		reservation.setEstimatedTime(estimatedTime);
						
		return estimatedTime;
	}


}
