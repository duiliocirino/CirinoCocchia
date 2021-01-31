package src.main.java.services.reservationManagement.interfaces;

import java.util.List;

import javax.ws.rs.ProcessingException;

import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.macrocomponents.ReservationManagement;
import src.main.java.services.reservationManagement.implementation.NotificationModuleImplementation;

public abstract class NotificationModule extends ReservationManagement {
	/**
	 * Calculates the estimated ride time from origin to end
	 * @param origin position from which the ride starts
	 * @param end position in which the ride will end
	 * @return estimated time to ride from origin to end
	 * @throws ProcessingException in the case in which the server
	 * is unable to establish an Internet Connection
	 */
	public abstract double rideTime(Position origin, Position end) 
		throws ProcessingException;
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
