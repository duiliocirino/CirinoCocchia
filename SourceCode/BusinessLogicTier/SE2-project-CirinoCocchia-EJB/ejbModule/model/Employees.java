package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Javabean class for Entity: Employees.
 * This entity serves as join table between User and Grocery for
 * what concerns the relationship "works".
 */
@Entity
public class Employees implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 *  Id of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idemployees;
	
	/**
	 * Entity representing the employee
	 */
	@ManyToOne(fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
	@JoinColumn(name = "idemployee")
	private User employee;
	
	/**
	 * Entity representing the grocery
	 */
	@ManyToOne(fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
	@JoinColumn(name = "idgrocery")
	private Grocery grocery;

	public Employees() {
		super();
	}

	public int getIdemployees() {
		return idemployees;
	}

	public void setIdemployees(int idemployees) {
		this.idemployees = idemployees;
	}

	public User getEmployee() {
		return employee;
	}

	public void setEmployee(User employee) {
		this.employee = employee;
	}

	public Grocery getGrocery() {
		return grocery;
	}

	public void setGrocery(Grocery grocery) {
		this.grocery = grocery;
	}
   
}
