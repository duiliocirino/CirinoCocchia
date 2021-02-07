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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import controllers.RemoveEmployee;

import java.util.List;

import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.EmployeesModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the RemoveEmployee class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoveEmployeeTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock EmployeesModuleImplementation employeeModule;
	@Mock LoginModuleImplementation loginModule;
	MockRemoveEmployee controllerServlet;
	
	
	@Before
	public void setup() throws IOException, ServletException {
		
		controllerServlet = spy(new MockRemoveEmployee(employeeModule, loginModule));
		doNothing().when(controllerServlet).postTemplate(any(), any(), anyInt());
		doNothing().when(controllerServlet).getTemplate(any(), any(), anyString(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void getOkNoGroceries() throws ServletException, IOException {
		
		User user = new User();
		
		when(session.getAttribute("user")).thenReturn(user);
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any());
	}
	
	@Test
	public void getOkWithGroceries() throws ServletException, IOException {
		
		User user = new User();
		Grocery grocery = new Grocery();
		List<Grocery> groceries = new ArrayList<>();
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any());
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("employeeId")).thenReturn("323");
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("employeeId")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(100);
		
		when(employeeModule.removeEmployee(anyInt(), anyInt())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("employeeId")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}
	
	@Test
	public void okPost() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(100);
		
		when(employeeModule.removeEmployee(anyInt(), anyInt())).thenReturn(new User());
		when(loginModule.getUserById(anyInt())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("employeeId")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyInt());
	}
	
	class MockRemoveEmployee extends RemoveEmployee {

		private static final long serialVersionUID = 1L;

		public MockRemoveEmployee(EmployeesModuleImplementation employeesModule, LoginModuleImplementation loginModule) {
			this.employeesModule = employeesModule;
			this.loginModule = loginModule;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doGet(request, response);
		}

		@Override
		protected void getTemplate(HttpServletRequest request, HttpServletResponse response, String path,
				List<User> employees) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplate(request, response, path, employees);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doPost(request, response);
		}

		@Override
		protected void postTemplate(HttpServletRequest request, HttpServletResponse response, Integer groceryId)
				throws IOException {
			// TODO Auto-generated method stub
			super.postTemplate(request, response, groceryId);
		}
		
		
	}
}
