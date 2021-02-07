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
import src.main.java.services.accountManagement.implementation.RegistrationModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the EditGroceryInfo class.
 */
@RunWith(MockitoJUnitRunner.class)
public class EditProfileTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock RegistrationModuleImplementation regModule;
	EditProfile controllerServlet;
	final String username = "username";
	final String email = "email";
	final String password = "password";
	final String telephoneNumber = "3285657889";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockEditProfile(regModule));
		doNothing().when(controllerServlet).postTemplate(any(), any(), any(), any());
		when(req.getSession()).thenReturn(session, session);
	}	
	
	@Test
	public void nullParametersTest() throws ServletException, IOException {
		User user = new User();
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(null);
		when(req.getParameter("email")).thenReturn(null);
		when(req.getParameter("password")).thenReturn(null);
		when(req.getParameter("telephoneNumber")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badTelephoneNumber() throws ServletException, IOException {
		User user = new User();
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(null);
		when(req.getParameter("password")).thenReturn(null);
		when(req.getParameter("telephoneNumber")).thenReturn("80");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void allFieldsEmpty() throws ServletException, IOException {
		User user = new User();
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn("");
		when(req.getParameter("email")).thenReturn("");
		when(req.getParameter("password")).thenReturn("");
		when(req.getParameter("telephoneNumber")).thenReturn("");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(regModule.editProfile(anyInt(), any(), any(), any(), any())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("telephoneNumber")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}

	@Test
	public void profileEdited() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		user.setTelephoneNumber(telephoneNumber);
		
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
		
		when(regModule.editProfile(anyInt(),  any(), any(), any(), any())).thenReturn(user);
		doNothing().when(session).setAttribute(any(), any());
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("telephoneNumber")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), any(), any());
	}
	
	class MockEditProfile extends EditProfile {

		private static final long serialVersionUID = 1L;
		
		public MockEditProfile (RegistrationModuleImplementation regModule) {
			this.regModule = regModule;
		}
	}
}
