package src.main.java.services.groceryManagement.interfaces;

import javax.ejb.Stateless;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.EmployeesModuleImplementation;
import src.main.java.services.macrocomponents.GroceryManagement;

/**
 *  This module is accessible for managers only and it serves
 *  the purpose of managing employees, adding or removing them, but
 *  also see a list of them and the respective information.
 */
@Stateless
public abstract class EmployeesModule extends GroceryManagement {
	/**
	 * This method adds a User with an EMPLOYEE role to the grocery passed
	 * as an argument. The User instance of the employee has to be already 
	 * existent with a EMPLOYEE role
	 * @param idemployee iduser of the employee to be added
	 * @param idgrocery id of the grocery in which the employee has to be added
	 * @return persisted instance of the employee added to the system, null if there is
	 * no user with that id on the DB
	 * @throws CLupException if the grocery is not found, or the 
	 * iduser passed is not relative to an employee
	 */
	public abstract User addEmployee(int idemployee, int idgrocery) throws CLupException;
	/**
	 * This method deletes an employee from the groceries passed as an argument 
	 * @param idemployee iduser of the employee to be deleted
	 * @return the no more persisted instance of the user
	 * @throws CLupException if the employee or the grocery are not found, or if the 
	 * employee is not assigned to the grocery passed
	 */
	public abstract User removeEmployee(int idemployee, int idgrocery) throws CLupException;
	
	public static EmployeesModule getInstance() {
		return new EmployeesModuleImplementation();
	}
}
