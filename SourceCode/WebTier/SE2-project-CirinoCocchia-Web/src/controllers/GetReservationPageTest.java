package controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.utils.Roles;

/**
 * Unit test for the GetReservationPage class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetReservationPageTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock LoginModule loginModule;
	GetReservationPage controllerServlet;
	final String groceryData = "groceryData";
	final String date = "2021-02-05";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new GetReservationPage());
		doNothing().when(controllerServlet).getTemplate(any(), any(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("2dd");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotOwned() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("323");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(loginModule.checkCredentials(any(), any())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		logMock.close();
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't retrieve data from server");
	}
	

	@Test
	public void getOk() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(loginModule.checkCredentials(any(), any())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		logMock.close();
		verify(req, times(1)).getParameter(anyString());
		assertEquals(session.getAttribute("user"), user);
		verify(controllerServlet, times(1)).getTemplate(any(), any(), any());
	}
	
	@Test
	public void postOk() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(loginModule.checkCredentials(any(), any())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		verify(req, times(1)).getParameter(anyString());
		assertEquals(session.getAttribute("user"), user);
		verify(controllerServlet, times(1)).getTemplate(any(), any(), any());
	}
}
