package services.reservationManagement.imlpementation;

import java.sql.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import model.Grocery;
import model.Reservation;
import services.reservationManagement.interfaces.QueueUpdateManagement;
import utils.ReservationType;

@Stateless
public class QueueUpdateManagementImplementation extends QueueUpdateManagement{

	@EJB(name = "services.reservationManagement.implementation/ReservationHandlerImplementation")
	private ReservationHandlerImplementation reservationHandler;
	
	@Override
	public int refreshQueue(Grocery grocery) {
		// TODO JPQL call to refresh the groceries' related times
		return 0;
	}

	@Override
	public int lineUp(int iduser, int idgrocery) {
		Reservation reservation = reservationHandler.addReservation(iduser, idgrocery, ReservationType.LINEUP, null);
		if(reservation == null) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public int bookAVisit(int iduser, int idgrocery, Date bookTime) {
		// not implemented for specifications reasons
		return 0;
	}


	
}
