package services.groceryManagement.interfaces;

import javax.ejb.Stateless;

import model.User;
import services.groceryManagement.implementation.EmployeesModuleImplementation;
import services.macrocomponents.GroceryManagement;

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
	 * @return persisted instance of the employee added to the system
	 */
	public abstract User addEmployee(int idemployee, int idgrocery);
	/**
	 * This method deletes an employee from the groceries passed as an argument 
	 * @param idemployee iduser of the employee to be deleted
	 * @return the no more persisted instance of the user
	 */
	public abstract User removeEmployee(int idemployee, int idgrocery);
	
	public static EmployeesModule getInstance() {
		return new EmployeesModuleImplementation();
	}
}
