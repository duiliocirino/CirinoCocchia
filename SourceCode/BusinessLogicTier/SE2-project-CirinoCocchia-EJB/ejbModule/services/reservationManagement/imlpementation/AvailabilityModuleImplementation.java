package services.reservationManagement.imlpementation;

import java.sql.Timestamp;
import java.util.List;

import model.Grocery;
import model.Reservation;
import services.reservationManagement.interfaces.AvailabilityModule;
import utils.ReservationType;


public class AvailabilityModuleImplementation extends AvailabilityModule{

	@Override
	public List<Reservation> checkAvailability(ReservationType resType, Grocery grocery, Timestamp timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAvailable(Reservation reservation) {
		// TODO Auto-generated method stub
		return false;
	}

}
