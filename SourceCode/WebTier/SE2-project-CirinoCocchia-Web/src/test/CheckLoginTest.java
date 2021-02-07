package test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import controllers.CheckLogin;
import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.accountManagement.implementation.RegistrationModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the CheckLogin class.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckLoginTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock LoginModuleImplementation loginModule;
	@Mock RegistrationModuleImplementation regModule;
	MockCheckLogin controllerServlet;
	final String username = "username";
	final String password = "password";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockCheckLogin(loginModule, regModule));
		doNothing().when(controllerServlet).postTemplate(any(), any(), anyString());
		doReturn("").when(controllerServlet).getContext(any(), any());
		doNothing().when(res).sendRedirect(anyString());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException, CLupException {
		when(req.getParameter("username")).thenReturn(null);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty credential value");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException, CLupException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty credential value");
	}
	
	@Test
	public void badUsername() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn("");
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty credential value");
	}
	
	@Test
	public void badPassword() throws ServletException, IOException {	
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn("");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty credential value");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenThrow(new CLupException(""));
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "");
	}
	
	@Test
	public void userNotFound() throws ServletException, IOException, CLupException {
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(null);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyString());
	}
	
	@Test
	public void userFound() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(1234);
		user.setUsername(username);
		user.setPassword(password);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(user);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(res, times(1)).sendRedirect(anyString());
		assertEquals(session.getAttribute("user"), user);
	}
	
	@Test
	public void nullParameterVisitor () throws IOException, CLupException, ServletException {
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyString());
	}
	
	@Test
	public void badParametersVisitor() throws IOException, CLupException, ServletException {
		
		when(req.getParameter("telephoneNumber")).thenReturn("234");
				
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyString());
	}
	
	@Test
	public void visitorLogNegated () throws IOException, CLupException, ServletException {
		
		when(regModule.register(any(), anyString(), anyString(), any(), any())).thenThrow(new CLupException(""));
		when(req.getParameter("telephoneNumber")).thenReturn("333333333333");
		
		controllerServlet.doGet(req, res);
		
		verify(controllerServlet, times(1)).postTemplate(any(), any(), anyString());
	}
	
	@Test
	public void visitorLogOk () throws IOException, CLupException, ServletException {
		
		when(regModule.register(any(), anyString(), anyString(), any(), any())).thenReturn(new User());
		
		when(req.getParameter("telephoneNumber")).thenReturn("333333333333");
		controllerServlet.doGet(req, res);
		
		verify(res).sendRedirect(anyString());
	}
	
	class MockCheckLogin extends CheckLogin {
		
		private static final long serialVersionUID = 1L;

		public MockCheckLogin (LoginModuleImplementation loginModule, RegistrationModuleImplementation regModule) {
			this.loginModule = loginModule;
			this.regModule = regModule;
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
		protected String getContext(HttpServletRequest request, HttpServletResponse response) throws IOException {
			// TODO Auto-generated method stub
			return super.getContext(request, response);
		}

		@Override
		protected void postTemplate(HttpServletRequest request, HttpServletResponse response, String error)
				throws IOException {
			// TODO Auto-generated method stub
			super.postTemplate(request, response, error);
		}
		
		
	}
}
