package model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.*;

import utils.ReservationStatus;
import utils.ReservationType;

/**
 * Javabean class for Entity: Reservation
 * This class represents a reservation made from a user to a
 * certain grocery store (see RASD document for more details
 * on the definition).
 */
@Entity
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
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "idcustomer")
	private User customer;
	
	/**
	 * Entity representing the grocery for which the reservation
	 * has been made
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "idgrocery")
	private Grocery grocery;
	
	/**
	 * Type of the reservation
	 */
	@Enumerated(EnumType.STRING)
	private ReservationType type;
	
	/**
	 * This attribute identifies the reservation in a machine-readable way
	 * and serves to the grocery's specific assets to recognize this specific 
	 * reservation
	 */
	private int QRcode;
	
	/**
	 * Status of the reservation
	 */
	@Enumerated(EnumType.STRING)
	private ReservationStatus status;
	
	/**
	 * This attribute represents the lineup number (see RASD for definition)
	 * assigned to this reservation
	 */
	private int lineUpNumber;
	
	/**
	 * This attribute tracks the time in which the customer entered into the grocery store
	 */
	private Date timeEntrance;
	
	/**
	 * This attribute tracks the time in which the customer got out from the store
	 */
	private Date timeExit;
	
	/**
	 * This attribute serves to the book-a-visit functionality to track the 
	 * time in which the customer is allowed to get into the store
	 */
	private Date bookTime;

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

	public Grocery getGrocery() {
		return grocery;
	}

	public void setGrocery(Grocery grocery) {
		this.grocery = grocery;
	}

	public ReservationType getType() {
		return type;
	}

	public void setType(String type) {
		ReservationType resType = ReservationType.getResTypeByString(type);
		this.type = resType;
	}

	public int getQRcode() {
		return QRcode;
	}

	public void setQRcode(int qRcode) {
		QRcode = qRcode;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(String status) {
		ReservationStatus resStatus = ReservationStatus.getResStatusByString(status);
		this.status = resStatus;
	}

	public int getLineUpNumber() {
		return lineUpNumber;
	}

	public void setLineUpNumber(int lineUpNumber) {
		this.lineUpNumber = lineUpNumber;
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

	public Date getBookTime() {
		return bookTime;
	}

	public void setBookTime(Date bookTime) {
		this.bookTime = bookTime;
	}
	
	
   
}
