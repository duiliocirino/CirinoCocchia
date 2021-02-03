package controllers;

import static org.junit.Assert.assertEquals;
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
import java.util.Calendar;
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
import src.main.java.model.Queue;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.reservationManagement.interfaces.ReservationHandlerModule;
import src.main.java.utils.ReservationType;
import src.main.java.utils.Roles;

/**
 * Unit test for the DeleteReservation class.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteReservationTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock ReservationHandlerModule resModule;
	@Mock LoginModule loginModule;
	DeleteReservation controllerServlet;
	final Integer groceryId = 123;
	final Integer reservationId = 789;
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new DeleteReservation());
		doNothing().when(controllerServlet).postTemplate(any(), any(), anyInt());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("reservationId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badGroceryId() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("1d3");
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badReservationId() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("reservationId")).thenReturn("l90");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badGroceryEmployee() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.EMPLOYEE);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(groceryId + 1);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setEmployedGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "You are not allowed to do this operation");
	}
	
	@Test
	public void badGroceryManager() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(groceryId + 1);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "You are not allowed to do this operation");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		Queue queue = new Queue();
		queue.setGrocery(grocery);
		grocery.setQueue(queue);
		
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		Reservation reservation = new Reservation(user, grocery, ReservationType.LINEUP, Calendar.getInstance().getTime());
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		when(resModule.removeReservation(any())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		resMock.close();
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reservation not closable");
	}
	
	@Test
	public void reservationNotFound() throws ServletException, IOException {
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(null);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		resMock.close();
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Reservation not found");
	}
	
	@Test
	public void reservationGroceryNoMatch() throws ServletException, IOException {
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		User user2 = new User();
		user2.setIduser(12321);
		
		Grocery grocery2 = new Grocery();
		grocery2.setIdgrocery(2);
		grocery2.setOwner(user2);
		
		Queue queue2 = new Queue();
		queue2.setGrocery(grocery2);
		grocery2.setQueue(queue2);
		
		List<Grocery> groceries2 = new ArrayList<>();
		
		Reservation reservation = new Reservation(user2, grocery2, ReservationType.LINEUP, Calendar.getInstance().getTime());
		
		groceries.add(grocery);
		groceries2.add(grocery2);
		user.setGroceries(groceries);
		user2.setGroceries(groceries2);
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		resMock.close();
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Reservation not found");
	}
	
	

	@Test
	public void reservationDeleted() throws ServletException, IOException, CLupException {
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		Queue queue = new Queue();
		queue.setGrocery(grocery);
		grocery.setQueue(queue);
		
		User user2 = new User();
		user2.setIduser(12321);
		
		Reservation reservation = new Reservation(user2, grocery, ReservationType.LINEUP, Calendar.getInstance().getTime());
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		when(resModule.removeReservation(any())).thenReturn(reservation);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doPost(req, res);
		
		resMock.close();
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		assertEquals(session.getAttribute("user"), user);
		verify(controllerServlet, times(1)).postTemplate(req, res, groceryId);
	}	
	
	@Test
	public void doGetWorks() throws ServletException, IOException, CLupException {
		MockedStatic <ReservationHandlerModule> resMock = Mockito.mockStatic( ReservationHandlerModule.class );
		resMock.when( () -> ReservationHandlerModule.getInstance()).thenReturn(resModule);
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		

		
		Queue queue = new Queue();
		queue.setGrocery(grocery);
		grocery.setQueue(queue);
		
		User user2 = new User();
		user2.setIduser(12321);
		
		Reservation reservation = new Reservation(user2, grocery, ReservationType.LINEUP, Calendar.getInstance().getTime());
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		when(resModule.removeReservation(any())).thenReturn(reservation);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(groceryId.toString());
		when(req.getParameter("reservationId")).thenReturn(reservationId.toString());
		
		controllerServlet.doGet(req, res);
		
		resMock.close();
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(req, res, groceryId);
	}	
}
