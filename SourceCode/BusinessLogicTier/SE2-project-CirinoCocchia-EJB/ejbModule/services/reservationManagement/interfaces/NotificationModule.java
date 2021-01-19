package services.reservationManagement.interfaces;

import java.util.List;

import javax.ejb.Stateless;

import model.Position;
import model.User;
import services.macrocomponents.ReservationManagement;
import services.reservationManagement.imlpementation.NotificationModuleImplementation;

@Stateless
public abstract class NotificationModule extends ReservationManagement {
	/**
	 * Calculates the estimated ride time from origin to end
	 * @param origin position from which the ride starts
	 * @param end position in which the ride will end
	 * @return estimated time to ride from origin to end
	 */
	public abstract double rideTime(Position origin, Position end);
	/**
	 * Notify a set of users with a String message
	 * @param users list of users to be notified
	 * @param message String to be received from the users
	 */
	public abstract void notify(List<User> users, String message);
	/**
	 * Notify a user with a String message
	 * @param user user to be notified
	 * @param message String to be received from the users
	 */
	public abstract void notify(User user, String message);
	/**
	 * @return gets the instance of the implementation of this class
	 */
	public static NotificationModule getInstance() {
		return new NotificationModuleImplementation();
	}
}
