package src.main.java.services.reservationManagement.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.services.reservationManagement.interfaces.QueueUpdateManagement;
import src.main.java.services.reservationManagement.interfaces.TimeEstimationModule;
import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;

@Stateless
public class QueueUpdateManagementImplementation extends QueueUpdateManagement{

	@EJB(name = "services.reservationManagement.implementation/ReservationHandlerImplementation")
	private ReservationHandlerImplementation reservationHandler;
	
	@EJB(name = "services.reservationManagement.implementation/TimeEstimationModuleImplementation")
	private TimeEstimationModule timeEstimationMod;
	
	@Override
	public void refreshQueue(Grocery grocery) {
		Queue queue = grocery.getQueue();
		
		List<Reservation> toRemove = new ArrayList<>();
		
		for(Reservation res : queue.getReservations()) {		
			if(res.getTimeExit() != null) {
				// close the reservation
				toRemove.add(res);
			}
		}
		
		for(Reservation res : toRemove) {
			queue.removeReservation(res);
		}
	}
	
	@Override
	public boolean setIntoTheStore(int idreservation) throws CLupException {
		Reservation reservation = resTools.findReservation(idreservation);
		
		if(reservation == null) {
			throw new CLupException("id of the reservation passed not existent on the DB");
		}		
		if(reservation.getStatus() != ReservationStatus.ALLOWED) {
			throw new CLupException("Can't get into the store without having a ALLOWED reservation");
		}
		
		Queue queue = reservation.getQueue();
		
		if(queue.isFull()) {
			return false;
		}
		
		reservation.setStatus(ReservationStatus.ENTERED);
		System.out.println("reservation " + reservation.getIdreservation() + " is now entered");
		return true;
		
	}
	
	@Override
	public void setOutTheStore(int idreservation) throws CLupException {
		Reservation reservation = resTools.findReservation(idreservation);
		
		if(reservation == null) {
			throw new CLupException("id of the reservation passed not existent on the DB");
		}
		
		invokeCloseReservation(idreservation);
		System.out.println("reservation " + reservation.getIdreservation() + " is now outside the store");
	}

	@Override
	public Reservation lineUp(int iduser, int idgrocery, double lat, double lon) throws CLupException {
		Position position = new Position(lat, lon);		
		Reservation reservation = invokeAddReservation(iduser, idgrocery, ReservationType.LINEUP, null, position);

		if(reservation == null) {
			throw new CLupException("There was some error in creating the reservation");
		} else {
			return setReservationTimer(reservation);
		}
	}
	
	@Override
	public Reservation setReservationTimer(Reservation reservation) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				// align the reservation with the one on the DB
				Reservation reservationGet = resTools.findReservation(reservation.getIdreservation());
				if(reservationGet != null) {
					Queue queueGet = reservationGet.getQueue();
					if(reservationGet.getStatus() == ReservationStatus.OPEN) {
						resTools.setAllowedState(reservationGet);
						invokeAddReservationOnQueue(queueGet, reservationGet);
						System.out.println("reservation " + reservationGet.getIdreservation() + 
								" is now allowed");
					}
				}
			}
		};
		// schedule a check when the estimated time will be ended
		timer.schedule(task, reservation.getEstimatedTime());
		reservation.setQueueTimer(timer);
		return reservation;
	}

	@Override
	public int bookAVisit(int iduser, int idgrocery, Date bookTime) {
		// TODO: delete, out of focus with the scope
		return 0;
	}
	
	protected void invokeCloseReservation(int idreservation) throws CLupException {
		reservationHandler.closeReservation(idreservation);
		
	}
	
	private void invokeAddReservationOnQueue(Queue queue, Reservation reservation) {
		System.out.println(reservation.getStatus());
		queue.addReservation(reservation);
		System.out.println(reservation.getStatus());
	}
	
	protected Reservation invokeAddReservation(int iduser, int idgrocery, ReservationType type, Date date, Position position) throws CLupException {
		return reservationHandler
				.addReservation(iduser, idgrocery, ReservationType.LINEUP, null, position);
	}
	
	protected void invokeEstimateTime(Reservation reservation, Position position) throws CLupException {
		timeEstimationMod.estimateTime(reservation, position);
	}

}
