package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.accountManagement.implementation.RegistrationModuleImplementation;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.accountManagement.interfaces.RegistrationModule;
import src.main.java.services.groceryManagement.implementation.EmployeesModuleImplementation;
import src.main.java.services.groceryManagement.interfaces.EmployeesModule;
import src.main.java.utils.Roles;

/**
 * Unit test for the AddEmployee class.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddEmployeeTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock RegistrationModuleImplementation regModule;
	@Mock EmployeesModuleImplementation employeeModule;
	@Mock LoginModuleImplementation loginModule;
	AddEmployee controllerServlet;
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockAddEmployee(regModule, loginModule, employeeModule));
		doNothing().when(controllerServlet).postTemplate(req, res);
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(null);
		when(req.getParameter("email")).thenReturn("asd@email.com");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("telephoneNumber")).thenReturn("2342342342");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("bill");
		when(req.getParameter("email")).thenReturn("asd@email.com");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "You don't own the given grocery");
	}
	
	@Test
	public void badTelephoneNumber() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("bill");
		when(req.getParameter("email")).thenReturn("asd@email.com");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("telephoneNumber")).thenReturn("-234");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotOwned() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("bill");
		when(req.getParameter("email")).thenReturn("asd@email.com");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn("323");
		when(req.getParameter("telephoneNumber")).thenReturn("234567889");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "You don't own the given grocery");
	}

	@Test
	public void userNotCreatedDBError() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		User employee = new User();
		employee.setRole(Roles.EMPLOYEE);
		employee.setEmail("asd@email.com");
		employee.setIduser(1234);
		employee.setTelephoneNumber("3278906554");
		employee.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("bill");
		when(req.getParameter("email")).thenReturn("asd@email.com");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("telephoneNumber")).thenReturn("234567889");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
	}	
	
	@Test
	public void userCreated() throws ServletException, IOException, CLupException {
		MockedStatic <RegistrationModule> regMock = Mockito.mockStatic( RegistrationModule.class );
		regMock.when( () -> RegistrationModule.getInstance()).thenReturn(regModule);
		MockedStatic <EmployeesModule> empMock = Mockito.mockStatic( EmployeesModule.class );
		empMock.when( () -> EmployeesModule.getInstance()).thenReturn(employeeModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		User employee = new User();
		employee.setRole(Roles.EMPLOYEE);
		employee.setEmail("asd@email.com");
		employee.setIduser(1234);
		employee.setTelephoneNumber("3278906554");
		employee.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		employee.setEmployedGroceries(groceries);
		
		User updateUser = new User();
		updateUser.setRole(Roles.MANAGER);
		updateUser.setEmail("ciao@email.com");
		updateUser.setIduser(1234);
		updateUser.setTelephoneNumber("3278906554");
		updateUser.setUsername("ciao");
		
		grocery.addEmployee(employee);
		groceries.set(0, grocery);
		user.setGroceries(groceries);
		
		when(regModule.register(any(), anyString(), anyString(), anyString(), anyString())).thenReturn(employee);
		when(employeeModule.addEmployee(anyInt(), anyInt())).thenReturn(employee);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("bill");
		when(req.getParameter("email")).thenReturn("asd");
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("telephoneNumber")).thenReturn("234567889");
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		regMock.close();
		empMock.close();
		
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(req, res);
	}
	
	class MockAddEmployee extends AddEmployee {
		
		private static final long serialVersionUID = 1L;

		public MockAddEmployee(RegistrationModuleImplementation regModule, LoginModuleImplementation loginModule, EmployeesModuleImplementation employeesModule) {
			this.regModule = regModule;
			this.loginModule = loginModule;
			this.employeesModule = employeesModule;
		}
	}
}
