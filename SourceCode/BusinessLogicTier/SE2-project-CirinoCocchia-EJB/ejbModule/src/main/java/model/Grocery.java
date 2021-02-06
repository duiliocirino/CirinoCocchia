package src.main.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.CascadeType.ALL;

/**
 * Javabean class for Entity: Grocery
 * This represents a grocery of a grocery store
 */
@Entity
@NamedQuery(name = "Grocery.findAll", query = "SELECT g FROM Grocery g")
@NamedQuery(name = "Grocery.findGroceryByName", query = "SELECT g FROM Grocery g  WHERE g.name = :name")
@NamedQuery(name = "Grocery.findCustomersFavourites", query = 
	"SELECT q.grocery "
	+ "FROM Reservation r JOIN r.queue q "
	+ "WHERE r.customer = :customer "
	+ "GROUP BY r.queue "
	+ "ORDER BY COUNT(r.customer) DESC ")
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
	 * The user represented by this attribute is an admin.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idowner")
	private User owner;
	/**
	 * This attribute represents the queue in which the people can line-up
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = ALL, mappedBy = "grocery")
	private Queue queue;
	/**
	 * This attribute contains all the employees instances for this grocery
	 */
	@ManyToMany
	@JoinTable(name = "employees", 
		joinColumns = @JoinColumn(name = "idgrocery"),
		inverseJoinColumns = @JoinColumn(name ="idemployee"))
	private List<User> employees = new ArrayList<User>();

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
		owner.addOwnedGrocery(this);
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


	public Grocery() {
	}



	public Queue getQueue() {
		return queue;
	}



	public void setQueue(Queue queue) {
		this.queue = queue;
	}



	public List<User> getEmployees() {
		return employees;
	}
	
	
	public void addEmployee(User employee) {
		if(employee != null) {
			this.employees.add(employee);
			employee.addEmployedGrocery(this);
		}
	}
	
	public void removeEmployee(User employee) {
		if(employee != null) {
			this.employees.remove(employee);
			employee.removeEmployedGrocery(this);
		}
	}

   
}
