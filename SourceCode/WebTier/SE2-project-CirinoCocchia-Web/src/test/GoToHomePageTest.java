package test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
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

import controllers.GoToHomePage;
import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.services.searchManagement.implementation.SearchEngineModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the GoToHomePage class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GoToHomePageTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock LoginModuleImplementation loginModule;
	@Mock GroceryHandlerModuleImplementation groModule;
	@Mock SearchEngineModuleImplementation searchModule;
	MockGoToHomePage controllerServlet;
	final String groceryData = "groceryData";
	final String date = "2021-02-05";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockGoToHomePage(loginModule, groModule, searchModule));
		doNothing().when(controllerServlet).getTemplateAdmin(any(), any(), anyString(), any());
		doNothing().when(controllerServlet).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void dbErrorIntro() throws ServletException, IOException, CLupException {
		
		User user = new User();
		
		when(loginModule.getUserById(anyInt())).thenReturn(null);
		when(session.getAttribute("user")).thenReturn(user);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(0)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't retrieve data from server");
	}
	
	@Test
	public void homeEmployee() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.EMPLOYEE);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setEmployedGroceries(groceries);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateAdmin(any(), any(), anyString(), any());
	}
	
	@Test
	public void homeManager() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateAdmin(any(), any(), anyString(), any());
	}
	
	@Test
	public void customerWithGroceryNoActiveRes() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	@Test
	public void customerWithErrorGroceryNoActiveRes() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(groModule.getGrocery(anyInt())).thenReturn(null);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	@Test
	public void customerWithNoGroceryNoActiveResNullNear() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("sde3");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	@Test
	public void customerWithNoGroceryNoActiveResOneNear() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(searchModule.getNearGroceries(any(), anyDouble())).thenReturn(groceries);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("sde3");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	@Test
	public void customerWithNoGroceryNoActiveThreeNear() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		Grocery grocery2 = new Grocery();
		grocery2.setIdgrocery(124);
		
		Grocery grocery3 = new Grocery();
		grocery3.setIdgrocery(125);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		groceries.add(grocery2);
		groceries.add(grocery3);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(searchModule.getNearGroceries(any(), anyDouble())).thenReturn(groceries);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("sde3");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	@Test
	public void postOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		
		Grocery grocery2 = new Grocery();
		grocery2.setIdgrocery(124);
		
		Grocery grocery3 = new Grocery();
		grocery3.setIdgrocery(125);
		
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		groceries.add(grocery2);
		groceries.add(grocery3);
		
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(searchModule.getNearGroceries(any(), anyDouble())).thenReturn(groceries);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("sde3");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(controllerServlet, times(1)).getTemplateCustomer(any(), any(), anyString(), any(), any(), any());
	}
	
	class MockGoToHomePage extends GoToHomePage {

		private static final long serialVersionUID = 1L;
		
		public MockGoToHomePage(LoginModuleImplementation loginModule, GroceryHandlerModuleImplementation groModule, SearchEngineModuleImplementation searchModule) {
			this.loginModule = loginModule;
			this.groModule = groModule;
			this.searchModule = searchModule;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doGet(request, response);
		}

		@Override
		protected void getTemplateCustomer(HttpServletRequest request, HttpServletResponse response, String path,
				List<Reservation> activeReservations, List<Grocery> nearGroceries, Grocery grocery) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplateCustomer(request, response, path, activeReservations, nearGroceries, grocery);
		}

		@Override
		protected void getTemplateAdmin(HttpServletRequest request, HttpServletResponse response, String path,
				List<Grocery> userGroceries) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplateAdmin(request, response, path, userGroceries);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doPost(request, response);
		}
		
		
	}
}
