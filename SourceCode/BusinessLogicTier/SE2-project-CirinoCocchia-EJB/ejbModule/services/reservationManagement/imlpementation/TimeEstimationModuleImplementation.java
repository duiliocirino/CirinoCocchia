package services.reservationManagement.imlpementation;

import javax.ejb.Stateless;

import model.Reservation;
import services.reservationManagement.interfaces.TimeEstimationModule;

@Stateless
public class TimeEstimationModuleImplementation extends TimeEstimationModule{

	@Override
	public double estimatedVisitTime(Reservation reservation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEstimatedTime(Reservation reservation) {
		// TODO Auto-generated method stub
		return 0;
	}

}
