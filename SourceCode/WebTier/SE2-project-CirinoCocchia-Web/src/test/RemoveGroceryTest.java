package test;

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

import controllers.RemoveGrocery;
import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the RemoveGrocery class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoveGroceryTest {

	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock GroceryHandlerModuleImplementation groModule;
	@Mock LoginModuleImplementation loginModule;
	MockRemoveGrocery controllerServlet;
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockRemoveGrocery(groModule, loginModule));
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
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}
	
	@Test
	public void okPost() throws ServletException, IOException, CLupException {
		
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
		
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
	
	@Test
	public void okGet() throws ServletException, IOException, CLupException {
		
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
		
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
	
	class MockRemoveGrocery extends RemoveGrocery {
		
		private static final long serialVersionUID = 1L;

		public MockRemoveGrocery(GroceryHandlerModuleImplementation groModule, LoginModuleImplementation loginModule) {
			this.groModule = groModule;
			this.loginModule = loginModule;
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
		protected void postTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
			// TODO Auto-generated method stub
			super.postTemplate(request, response);
		}
		
		
	}
}
