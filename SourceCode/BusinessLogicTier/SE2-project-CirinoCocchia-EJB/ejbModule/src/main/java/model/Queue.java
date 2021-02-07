package src.main.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import src.main.java.utils.ReservationStatus;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.DETACH;


/**
 * The persistent class for the queue database table.
 * 
 */
@Entity
@NamedQuery(name="Queue.findAll", query="SELECT q FROM Queue q")
public class Queue implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id of this class
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idqueue;
	/**
	 * This attribute represents the grocery of which this queue
	 * is assigned to
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = { MERGE, REMOVE, REFRESH, DETACH })
	@JoinColumn(name = "idgrocery")
	private Grocery grocery;
	/**
	 * This attribute contains the Reservation entities made upon this grocery.
	 */
	@OneToMany(fetch = FetchType.EAGER, 
			mappedBy = "queue", 
			orphanRemoval = true)
	private List<Reservation> reservations = new ArrayList<Reservation>();

	public Queue() {
	}

	public int getIdqueue() {
		return this.idqueue;
	}

	public void setIdqueue(int idqueue) {
		this.idqueue = idqueue;
	}

	public Grocery getGrocery() {
		return grocery;
	}

	public void setGrocery(Grocery grocery) {
		this.grocery = grocery;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public void addReservation(Reservation reservation) {
		reservation.setStatus(ReservationStatus.ALLOWED);
		this.reservations.add(reservation);
	}
	
	public void removeReservation(Reservation reservation) {
		reservation.setStatus(ReservationStatus.CLOSED);
		this.reservations.remove(reservation);
	}
	
	/**
	 * Checks whether there is the maximum allowed people inside the grocery or not
	 * @return true if the grocery is already full, false if not
	 */
	public boolean isFull() {
		int inside = reservations.stream()
				.filter(x -> x.getStatus() == ReservationStatus.ENTERED)
				.collect(Collectors.toList())
				.size();
		int max = grocery.getMaxSpotsInside();
		
		return(inside == max);
	}
	/**
	 * Returns the reservations that are allowed to get into the store
	 * @return List of Reservation with status set to ALLOWED
	 */
	public List<Reservation> reservationsOnTheQueue(){
		return reservations.stream()
				.filter(x -> x.getStatus() == ReservationStatus.ALLOWED)
				.collect(Collectors.toList());
	}

}