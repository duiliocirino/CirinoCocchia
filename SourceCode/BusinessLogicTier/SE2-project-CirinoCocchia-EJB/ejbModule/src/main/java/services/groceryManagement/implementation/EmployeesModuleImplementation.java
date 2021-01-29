package src.main.java.services.groceryManagement.implementation;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.interfaces.EmployeesModule;
import src.main.java.utils.Roles;

public class EmployeesModuleImplementation extends EmployeesModule {

	@Override
	public User addEmployee(int idemployee, int idgrocery) throws CLupException {
		User emp = findUser(idemployee);
		Grocery grocery = findGrocery(idgrocery);
		
		if(grocery == null) {
			throw new CLupException("The grocery instance have to be already "
					+ "on the DB when adding an employee");
		} 
		
		if(emp == null) {
			return null;
		}
		
		if(emp.getRole() != Roles.EMPLOYEE) {
			throw new CLupException("User found to add employee was not an employee");
		}
		
		grocery.addEmployee(emp);
				
		return emp;
	}

	@Override
	public User removeEmployee(int idemployee, int idgrocery) throws CLupException {
		User emp = findUser(idemployee);
		Grocery grocery = findGrocery(idgrocery);
		
		if(emp == null || grocery == null) {
			throw new CLupException("Can't find the user and/or grocery to remove "
					+ "the employee");
		} 
		
		if(!grocery.getEmployees().contains(emp)) {
			throw new CLupException("The employee is not an employee of this grocery, "
					+ "can't remove him");
		}
		
		grocery.removeEmployee(emp);
		
		return emp;
	}
	
	
	/**
	 * Decouple the invocation of entity manager
	 * @param iduser id of the user to be searched
	 * @return User instance if found, null otherwise
	 */
	protected User findUser(int iduser) {
		return em.find(User.class, iduser);
	}
	/**
	 * Decouple the invocation of entity manager
	 * @param idgrocery id of the grocery to be searched
	 * @return Grocery instance if found, null otherwise
	 */
	protected Grocery findGrocery(int idgrocery) {
		return em.find(Grocery.class, idgrocery);
	}


}
