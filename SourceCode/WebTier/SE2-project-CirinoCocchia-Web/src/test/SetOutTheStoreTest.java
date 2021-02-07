package test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import controllers.SetOutTheStore;
import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.implementation.QueueUpdateManagementImplementation;
import src.main.java.services.reservationManagement.implementation.ReservationHandlerImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the SetOutTheStore class.
 */
@RunWith(MockitoJUnitRunner.class)
public class SetOutTheStoreTest {
	
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock QueueUpdateManagementImplementation queueModule;
	@Mock ReservationHandlerImplementation resModule;
	MockSetOutTheStore controllerServlet;
	
	
	@Before
	public void setup() throws IOException, ServletException {
		
		controllerServlet = spy(new MockSetOutTheStore(queueModule, resModule));
		doNothing().when(controllerServlet).postTemplate(any(), any(), anyInt());
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(req.getParameter("reservationId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(req.getParameter("reservationId")).thenReturn("123");
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
	}
	
	@Test
	public void badParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(req.getParameter("reservationId")).thenReturn("2dd");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
	}
	
	@Test
	public void badParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(req.getParameter("reservationId")).thenReturn("100");
		when(req.getParameter("groceryId")).thenReturn("2dd");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
	}
	
	@Test
	public void badParametersTest3() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(req.getParameter("reservationId")).thenReturn("100");
		when(req.getParameter("groceryId")).thenReturn("");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
	}
	
	@Test
	public void dbError1() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(null);
		
		when(req.getParameter("reservationId")).thenReturn("100");
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Reservation not found");
	}
	
	@Test
	public void dbError2() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		Reservation reservation = new Reservation();
		reservation.setIdreservation(100);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		doThrow(new CLupException("")).when(queueModule).setOutTheStore(anyInt());;
		when(req.getParameter("reservationId")).thenReturn("100");
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}
	
	@Test
	public void setIntoOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		Reservation reservation = new Reservation();
		reservation.setIdreservation(100);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(resModule.getReservation(anyInt())).thenReturn(reservation);
		doNothing().when(queueModule).setOutTheStore(anyInt());
		when(req.getParameter("reservationId")).thenReturn("100");
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyInt());
	}
	
	class MockSetOutTheStore extends SetOutTheStore {

		private static final long serialVersionUID = 1L;

		public MockSetOutTheStore(QueueUpdateManagementImplementation queueModule, ReservationHandlerImplementation resModule) {
			this.queueModule = queueModule;
			this.resModule = resModule;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doGet(request, response);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doPost(request, response);
		}

		@Override
		protected void postTemplate(HttpServletRequest request, HttpServletResponse response, int groceryId)
				throws IOException {
			// TODO Auto-generated method stub
			super.postTemplate(request, response, groceryId);
		}
		
	}
	
}
