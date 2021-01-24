package services.reservationManagement.imlpementation;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Grocery;
import model.Position;
import model.Queue;
import model.Reservation;
import services.reservationManagement.interfaces.QueueUpdateManagement;
import services.reservationManagement.interfaces.TimeEstimationModule;
import utils.ReservationStatus;
import utils.ReservationType;

@Stateless
public class QueueUpdateManagementImplementation extends QueueUpdateManagement{

	@EJB(name = "services.reservationManagement.implementation/ReservationHandlerImplementation")
	private ReservationHandlerImplementation reservationHandler;
	
	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@Override
	public int refreshQueue(Grocery grocery) {
		Queue queue = grocery.getQueue();
		
		for(Reservation res : queue.getReservations()) {
			
			if(res.getTimeExit() != null) {
				// close the reservation
				queue.removeReservation(res);
			}
		}
		
		return 0;
	}

	@Override
	public int lineUp(int iduser, int idgrocery, double lat, double lon) {
		Position position = new Position(lat, lon);
		Reservation reservation = reservationHandler
				.addReservation(iduser, idgrocery, ReservationType.LINEUP, null, position);
		if(reservation == null) {
			return -1;
		} else {
			setReservationTimer(reservation);
			return 0;
		}
	}
	
	@Override
	public void setReservationTimer(Reservation reservation) {
		Timer timer = new Timer();
		Queue queue = reservation.getQueue();
		TimerTask task = new TimerTask() {
			public void run() {
				// align the reservation with the one on the DB
				em.refresh(reservation);
				if(reservation.getStatus() == ReservationStatus.OPEN) {
					queue.addReservation(reservation);
				}
			}
		};
		// schedule a check when the estimated time will be ended
		timer.schedule(task, reservation.getEstimatedTime());
		reservation.setQueueTimer(timer);	
	}

	@Override
	public int bookAVisit(int iduser, int idgrocery, Date bookTime) {
		// not implemented for specifications reasons
		return 0;
	}

	
}
