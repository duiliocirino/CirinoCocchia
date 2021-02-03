package src.main.java.services.tools;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import src.main.java.model.Grocery;
import src.main.java.model.Queue;
import src.main.java.model.Reservation;

/**
 * Utility class that serves as an interface with the 
 * entity manager for the interactions related to Reservation
 */
@Stateless
public class ReservationToolbox {
	/**
	 * Instance of Entity Manager
	 */
	@PersistenceContext(unitName="SE2-project-CirinoCocchia-EJB")
	protected EntityManager em;
	
	/**
	 * Finds a reservation given its id
	 * @param idreservation id of the reservation to be found
	 * @return Reservation instance if found, null if not
	 */
	public Reservation findReservation(int idreservation) {
		return em.find(Reservation.class, idreservation);
	}
	/**
	 * Perists a reservation given its instance
	 * @param reservation instance of the reservation to be persisted
	 */
	public void persistReservation(Reservation reservation) {
		em.persist(reservation);
	}
	/**
	 * Detaches an instance from the persistence context
	 * @param reservation reservation to be detached
	 */
	public void detachReservation(Reservation reservation) {
		em.refresh(reservation);
	}
	/**
	 * Refreshes the state of the reservation as is on the DB
	 * @param reservation reservation to e refreshed
	 */
	public void refreshReservation(Reservation reservation) {
		em.refresh(reservation);
	}
	/**
	 * Removes an instance of reservation from the persistence context
	 * @param reservation reservation to be removed
	 */
	public void removeReservation(Reservation reservation) {
		em.remove(reservation);
	}
	/**
	 * Creates a query to retrieve all the reservation made for a certain grocery
	 * @param grocery grocery for which the reservation are applied
	 * @return Collection of reservations made for the grocery passed as an argument
	 */
	public List<Reservation> findAllByGrocery(Grocery grocery){
		return em.createNamedQuery("Reservation.findAllByGrocery", Reservation.class)
				.setParameter("grocery", grocery)
				.getResultList();
	}
	/**
	 * Retrieves the reservations basing on an interval applied to the estimatedTime 
	 * related to the reservation
	 * @param queue queue related to the grocery for which the reservation was applied
	 * @param start start of the interval
	 * @param end end of the interval
	 * @return collection of reservations with estimationTime included in the interval, made 
	 * for the grocery related to the queue
	 */
	public List<Reservation> findByInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.findByInterval", Reservation.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
	/**
	 * Retrieves the reservations basing on an interval applied to the timeExit 
	 * related to the reservation
	 * @param queue queue related to the grocery for which the reservation was applied
	 * @param start start of the interval
	 * @param end end of the interval
	 * @return collection of reservations with timeExit included in the interval, made 
	 * for the grocery related to the queue
	 */
	public List<Reservation> findByEndVisitInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.findByEndVisitInterval", Reservation.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
	/**
	 * Retrieves the number of reservations made in a certain grocery for a certain 
	 * interval of time
	 * @param queue queue related to the grocery to be monitored
	 * @param start start of the interval
	 * @param end end of the interval
	 * @return list with one component, thus the number of reservations made in a certain
	 *  interval of time for a certain grocery. The list is made by Long numbers.
	 */
	public List<Long> totalVisitsInInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.TotalVisitsInInterval", Long.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
	/**
	 * Retrieves a collection of number representing the number of minutes spent inside the 
	 * store in a certain time interval
	 * @param queue queue related to the grocery to be monitored
	 * @param start start of the interval 
	 * @param end end of the interval
	 * @return collection of integer numbers containing the number of minutes inside the store 
	 * related to each reservation were made in a certain interval
	 *  made in the interval
	 */
	public List<Integer> totalTimeSpentInInterval(Queue queue, Date start, Date end){
		return em.createNamedQuery("Reservation.TotalTimeSpentInInterval", Integer.class)
				.setParameter("queue", queue)
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultList();
	}
}
