package model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Javabean class for Entity: Grocery
 * This represents a grocery of a grocery store
 */
@Entity
public class Grocery implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idgrocery;
	
	/**
	 * This attribute represents the User class that owns this grocery.
	 * The user represented yb this attribute is an admin.
	 */
	@ManyToOne(fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
	@JoinColumn(name = "idowner")
	private User owner;
	
	/**
	 * This attribute contains the Reservation entities made upon this grocery.
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grocery")
	private List<Reservation> reservations;

	/**
	 * This attribute represents the latitude coordinate for this grocery
	 */
	private double latitude;
	
	/**
	 * This attribute represents the longitude coordinate for this grocery
	 */
	private double longitude;
	
	/**
	 * This attributes serves as human-readable identifier for this grocery
	 */
	private String name;
	
	/**
	 * This integer number represents how many people are allowed to be simultaneously
	 * into the grocery store
	 */
	private int maxSpotsInside;
	
	/**
	 * This integer number represents the line-up number currently allowed to get into
	 * the store (see RASD&DD for further details on the definition) 
	 */
	private int currentLineUpNumber;

	public int getIdgrocery() {
		return idgrocery;
	}



	public void setIdgrocery(int idgrocery) {
		this.idgrocery = idgrocery;
	}



	public User getOwner() {
		return owner;
	}



	public void setOwner(User owner) {
		this.owner = owner;
	}



	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLongitude() {
		return longitude;
	}



	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getMaxSpotsInside() {
		return maxSpotsInside;
	}



	public void setMaxSpotsInside(int maxSpotInside) {
		this.maxSpotsInside = maxSpotInside;
	}



	public int getCurrentLineUpNumber() {
		return currentLineUpNumber;
	}



	public void setCurrentLineUpNumber(int currentLineUpNumber) {
		this.currentLineUpNumber = currentLineUpNumber;
	}



	public Grocery() {
		super();
	}
	
	public List<Reservation> getReservations() {
		return reservations;
	}



	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

   
}
