package services.groceryManagement.implementation;

import model.Employees;
import model.Grocery;
import model.User;
import services.groceryManagement.interfaces.EmployeesModule;

public class EmployeesModuleImplementation extends EmployeesModule {

	@Override
	public User addEmployee(int idemployee, int idgrocery) {
		User emp = em.find(User.class, idemployee);
		Grocery grocery = em.find(Grocery.class, idgrocery);
		
		if(emp == null || grocery == null) {
			return null;
		} 
		
		grocery.addEmployee(emp);
				
		return emp;
	}

	@Override
	public User removeEmployee(int idemployee, int idgrocery) {
		User emp = em.find(User.class, idemployee);
		Grocery grocery = em.find(Grocery.class, idgrocery);
		
		if(emp == null || grocery == null) {
			return null;
		} 
		
		grocery.removeEmployee(emp);
		
		return emp;
	}

}
