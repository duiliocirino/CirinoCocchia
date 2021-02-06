package src.main.java.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import javax.persistence.*;

import src.main.java.utils.ReservationStatus;
import src.main.java.utils.ReservationType;

import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

/**
 * Javabean class for Entity: Reservation
 * This class represents a reservation made from a user to a
 * certain grocery store (see RASD document for more details
 * on the definition).
 */
@Entity
@NamedQueries({ 
	@NamedQuery(name = "Reservation.findAllByGrocery", query = 
			"SELECT r "
			+ "FROM Reservation r JOIN r.queue q "
			+ "WHERE q.grocery = :grocery"),
	@NamedQuery(name = "Reservation.findByInterval", query = 
	"SELECT r "
	+ "FROM Reservation r "
	+ "WHERE r.queue = :queue AND r.estimatedTime BETWEEN :start AND :end"),
	@NamedQuery(name = "Reservation.findByEndVisitInterval", query = 
	"SELECT r "
	+ "FROM Reservation r "
	+ "WHERE r.queue = :queue AND r.timeExit BETWEEN :start AND :end"),
	@NamedQuery(name = "Reservation.TotalVisitsInInterval", query = 
	"SELECT COUNT(r) "
	+ "FROM Reservation r "
	+ "WHERE r.queue = :queue AND r.timeEntrance > :start AND r.timeExit < :end "),
	@NamedQuery(name = "Reservation.TotalTimeSpentInInterval", query = 
	"SELECT FUNCTION('getVisitMinutes', r.timeEntrance, r.timeExit) "
	+ "FROM Reservation r "
	+ "WHERE r.queue = :queue AND r.timeEntrance > :start AND r.timeExit < :end ")})
public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Id of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idreservation;
	
	/**
	 * Entity representing the customer who did the reservation
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { REFRESH, MERGE })
	@JoinColumn(name = "idcustomer")
	private User customer;
	
	/**
	 * Entity representing the grocery for which the reservation
	 * has been made
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade ={ MERGE, REFRESH } )
	@JoinColumn(name = "idqueue")
	private Queue queue;
	
	/**
	 * Type of the reservation
	 */
	@Enumerated(EnumType.STRING)
	private ReservationType type;

	/**
	 * This attribute identifies the estimated time for which the customer
	 * will be allowed to be lined-up to the grocery's queue
	 */
	@Temporal(TIMESTAMP)
	private Date estimatedTime;
	/**
	 * Status of the reservation
	 */
	@Enumerated(EnumType.STRING)
	private ReservationStatus status;
	
	/**
	 * This attribute tracks the time in which the customer entered into the grocery store
	 */
	@Temporal(TIMESTAMP)
	private Date timeEntrance;
	
	/**
	 * This attribute tracks the time in which the customer got out from the store
	 */
	@Temporal(TIMESTAMP)
	private Date timeExit;
	
	/**
	 * This attribute serves to the book-a-visit functionality to track the 
	 * time in which the customer is allowed to get into the store
	 */
	@Temporal(TIMESTAMP)
	private Date bookTime;
	/**
	 * This attribute is related to the estimation time. When this timer ends,
	 * the reservation has the right to get into the queue's ALLOWED reservations
	 */
	@Transient
	private Timer queueTimer;
	
	/**
	 * Returns a new Reservation (mandatory for JPA rules)
	 */
	public Reservation() {}
	/**
	 * Returns a new Reservation
	 */
	public Reservation(User user, Grocery grocery, ReservationType type, Date bookTime) {
		this.customer = user;
		this.queue = grocery.getQueue();
		this.type = type;
		this.bookTime = bookTime;
		this.status = ReservationStatus.OPEN;
	}

	public int getIdreservation() {
		return idreservation;
	}
	
	public void setIdreservation(int idreservation) {
		this.idreservation = idreservation;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}
	
	public Queue getQueue() {
		return queue;
	}
	public void setQueue(Queue queue) {
		this.queue = queue;
	}
	public Grocery getGrocery() {
		return this.queue.getGrocery();
	}
	
	public Date getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(Date estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	public ReservationType getType() {
		return type;
	}

	public void setType(String type) {
		ReservationType resType = ReservationType.getResTypeByString(type);
		this.type = resType;
	}

	public ReservationStatus getStatus() {
		return status;
	}
	
	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public void setStatus(String status) {
		ReservationStatus resStatus = ReservationStatus.getResStatusByString(status);
		this.status = resStatus;
	}

	public Date getTimeEntrance() {
		return timeEntrance;
	}

	public void setTimeEntrance(Date timeEntrance) {
		this.timeEntrance = timeEntrance;
	}

	public Date getTimeExit() {
		return timeExit;
	}

	public void setTimeExit(Date timeExit) {
		this.timeExit = timeExit;
	}

	public Timer getQueueTimer() {
		return queueTimer;
	}
	public void setQueueTimer(Timer queueTimer) {
		this.queueTimer = queueTimer;
	}
	   
}
