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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import src.main.java.services.groceryManagement.implementation.MonitorModuleImplementation;
import src.main.java.utils.GroceryData;
import src.main.java.utils.Roles;

/**
 * Unit test for the GetGroceryData class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetGroceryDataTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock MonitorModuleImplementation monModule;
	GetGroceryData controllerServlet;
	final String groceryData = "groceryData";
	final String date = "2021-02-05";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockGetGroceryData(monModule));
		doNothing().when(controllerServlet).getTemplate(any(), any(), any(), any(), any(), anyInt());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badStartDate() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn(groceryData);
		when(req.getParameter("date")).thenReturn("2140-12-12");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
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
		when(req.getParameter("groceryData")).thenReturn(groceryData);
		when(req.getParameter("date")).thenReturn(date);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Grocery not allowed");
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
		
		when(monModule.getGroceryStats(anyInt(), any())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn("All");
		when(req.getParameter("date")).thenReturn(date);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}
	

	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn(groceryData);
		when(req.getParameter("date")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any(), anyInt());
	}
	
	@Test
	public void groceryDataAllOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		Map<GroceryData, Float> result = new HashMap<>();
		result.put(GroceryData.AVG_TIME_MONTH, 30f);
		result.put(GroceryData.AVG_TIME_WEEK, 43f);
		
		when(monModule.getGroceryStats(anyInt(),any())).thenReturn(result);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn("All");
		when(req.getParameter("date")).thenReturn(date);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any(), anyInt());
	}
	
	@Test
	public void groceryDataSpecificOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(monModule.getGroceryStats(anyInt(), any(), any())).thenReturn(30f);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn("Average time per week");
		when(req.getParameter("date")).thenReturn(date);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any(), anyInt());
	}
	
	@Test
	public void doPostTest() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("groceryData")).thenReturn("All");
		when(req.getParameter("date")).thenReturn(date);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any(), anyInt());
	}
	
	class MockGetGroceryData extends GetGroceryData {

		private static final long serialVersionUID = 1L;
		
		public MockGetGroceryData(MonitorModuleImplementation monitorModule) {
			this.monitorModule = monitorModule;
		}
	}
}
