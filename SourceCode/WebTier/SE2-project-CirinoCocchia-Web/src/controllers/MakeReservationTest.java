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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.services.reservationManagement.interfaces.ReservationHandlerModule;
import src.main.java.utils.Roles;

/**
 * Unit test for the MakeReservation class.
 */
@RunWith(MockitoJUnitRunner.class)
public class MakeReservationTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock GroceryHandlerModule groModule;
	@Mock ReservationHandlerModule resModule;
	@Mock LoginModule loginModule;
	MakeReservation controllerServlet;
	final String groceryData = "groceryData";
	final String date = "2021-02-05";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MakeReservation());
		doNothing().when(controllerServlet).getTemplate(any(), any(), anyString());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLatitudeNumber1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("100");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLatitudeNumber2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("grg");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badLongitudeNumber1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("200");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}

	@Test
	public void badLongitudeNumber2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("sdf");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbErrorGetGrocery() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(100);
		
		when(groModule.getGrocery(anyInt())).thenReturn(null);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		groMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotOwnedManager() throws ServletException, IOException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("124");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		groMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotOwnedEmployeeWith() throws ServletException, IOException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		
		User user = new User();
		user.setRole(Roles.EMPLOYEE);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("124");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		groMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbErrorMakeReservation() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(100);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		groMock.close();
		resMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create reservation");
	}
	
	@Test
	public void makeReservationTest() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.EMPLOYEE);
		user.setIduser(202);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		user.setEmployedGroceries(groceries);
		
		Reservation reservation = Mockito.mock(Reservation.class);
		
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(resModule.addReservation(anyInt(), anyInt(), any(), any(), any())).thenReturn(reservation);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		groMock.close();
		resMock.close();
		logMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString());
	}
	
	@Test
	public void makeReservationGetTest() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(202);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		Reservation reservation = Mockito.mock(Reservation.class);
		
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(resModule.addReservation(anyInt(), anyInt(), any(), any(), any())).thenReturn(reservation);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		groMock.close();
		resMock.close();
		logMock.close();
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString());
	}
}
