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
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.utils.Roles;

/**
 * Unit test for the RemoveGrocery class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoveGrocerytest {

	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock GroceryHandlerModule groModule;
	@Mock LoginModule loginModule;
	RemoveGrocery controllerServlet;
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new RemoveGrocery());
		doNothing().when(controllerServlet).postTemplate(any(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
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
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
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
		
		when(groModule.removeGrocery(anyInt())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doPost(req, res);
		
		groMock.close();
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Grocery not deleteable");
	}
	
	@Test
	public void okPost() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
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
		
		when(groModule.removeGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		groMock.close();
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
	
	@Test
	public void okGet() throws ServletException, IOException, CLupException {
		MockedStatic <GroceryHandlerModule> groMock = Mockito.mockStatic( GroceryHandlerModule.class );
		groMock.when( () -> GroceryHandlerModule.getInstance()).thenReturn(groModule);
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
		
		when(groModule.removeGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		groMock.close();
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
}
