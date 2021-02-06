package controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.junit.MockitoJUnitRunner;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the AddGrocery class.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddGroceryTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock GroceryHandlerModuleImplementation groModule;
	AddGrocery controllerServlet;
	final String groceryName = "GroceryName";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockAddGrocery(groModule));
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
		when(req.getParameter("name")).thenReturn(null);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest3() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badMaxSpotsNumber1() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("-4");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");		
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badMaxSpotsNumber2() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("drg");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLatitudeNumber1() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("100");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLatitudeNumber2() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("grg");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLongitudeNumber1() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("200");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}

	@Test
	public void badLongitudeNumber2() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("sdf");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badOpeningNumber1() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("fr4");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badOpeningNumber2() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("-1");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badClosingNumber1() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("25");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badClosingNumber2() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("ghr4");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badClosingNumber3() throws ServletException, IOException {
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
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("8");
		when(req.getParameter("closeHour")).thenReturn("7");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotCreatedDBError() throws ServletException, IOException, CLupException {
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
		
		when(groModule.addGrocery(anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create grocery");
	}
	
	@Test
	public void firstGroceryCreated() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		user.setGroceries(null);
		
		Grocery newGrocery = new Grocery();
		newGrocery.setIdgrocery(456);
		
		when(groModule.addGrocery(anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(newGrocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(6)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}	

	@Test
	public void groceryCreated() throws ServletException, IOException, CLupException {
		
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
		
		Grocery newGrocery = new Grocery();
		newGrocery.setIdgrocery(456);
		newGrocery.setOwner(user);
		
		groceries.set(0, grocery);
		user.setGroceries(groceries);
		
		when(groModule.addGrocery(anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(newGrocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		groceries.add(newGrocery);
		user.setGroceries(groceries);
		
		verify(req, times(6)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}	
	
	@Test
	public void doGetWorks() throws ServletException, IOException, CLupException {
		
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
		
		Grocery newGrocery = new Grocery();
		newGrocery.setIdgrocery(456);
		newGrocery.setOwner(user);
		
		groceries.set(0, grocery);
		user.setGroceries(groceries);
		
		when(groModule.addGrocery(anyString(), any(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(newGrocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doGet(req, res);
		
		groceries.add(newGrocery);
		user.setGroceries(groceries);
		
		verify(req, times(6)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
	
	class MockAddGrocery extends AddGrocery {
		
		private static final long serialVersionUID = 1L;

		public MockAddGrocery(GroceryHandlerModuleImplementation groModule) {
			this.groModule = groModule;
		}
	}
}