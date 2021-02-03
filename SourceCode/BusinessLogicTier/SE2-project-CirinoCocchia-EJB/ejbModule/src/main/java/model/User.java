package src.main.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import src.main.java.utils.Roles;

/**
 * Javabean class for Entity: User
 * This entity represent an instance of User. The user's allowed functionalities
 * depend on their role.
 */
@Entity
@Table(name = "user", schema = "db_project_se2")
@NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = :usern and r.password = :pass")
@NamedQuery(name = "User.findByUsername", query = "SELECT r FROM User r  WHERE r.username = :usrn")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Id of the entity 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int iduser;
	
	/**
	 * Role of the user
	 */
	@Enumerated(EnumType.STRING)
	private Roles role;	
	
	/**
	 * Telephone number related to this user
	 */
	private String telephoneNumber;

	/**
	 * Username related to this user
	 */
	private String username;

	/**
	 * Password related to this user
	 */
	private String password;

	/**
	 * Email related to this user
	 */
	private String email;
	
	/**
	 * List of reservations made by this user
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
	private List<Reservation> reservations = new ArrayList<Reservation>();
	
	/**
	 * List of groceries owned by this user
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
	private List<Grocery> groceries = new ArrayList<Grocery>();
	
	/**
	 * List of groceries in which this user is employed
	 */
	@ManyToMany(mappedBy = "employees")
	private List<Grocery> employedGroceries = new ArrayList<Grocery>();
	
	public User() {	}


	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	
	public Roles getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = Roles.getRoleByString(role);
		if(this.role == Roles.NONE) {
			System.err.println(this.iduser + " user:: can't understand role");
		}
	}
	
	public void setRole(Roles role) {
		this.role = role;
	}
	
	public String getTelephoneNumber() {
		return telephoneNumber;
	}


	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public List<Reservation> getReservations() {
		return reservations;
	}


	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}


	public List<Grocery> getGroceries() {
		return groceries;
	}


	public void setGroceries(List<Grocery> groceries) {
		this.groceries = groceries;
	}


	public List<Grocery> getEmployedGroceries() {
		return employedGroceries;
	}


	public void setEmployedGroceries(List<Grocery> employedGroceries) {
		this.employedGroceries = employedGroceries;
	}
	
	public void addEmployedGrocery(Grocery grocery) {
		this.employedGroceries.add(grocery);
	}
	
	public void removeEmployedGrocery(Grocery grocery) {
		this.employedGroceries.remove(grocery);
	}
}

