package controllers;

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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import src.main.java.exceptions.CLupException;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.utils.Roles;

/**
 * Unit test for the CheckLogin class.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckLoginTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock LoginModule loginModule;
	CheckLogin controllerServlet;
	final String username = "username";
	final String password = "password";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new CheckLogin());
		doNothing().when(controllerServlet).postTemplate(req, res);
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
		//verify(loginModule, times(1)).checkCredentials(anyString(), anyString());
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
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenThrow(new CLupException(""));
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
	}
	
	@Test
	public void userNotFound() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(null);
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("password")).thenReturn(password);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(req, res);
	}
	
	@Test
	public void userFound() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
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
		
		logMock.close();
		verify(req, times(2)).getParameter(anyString());
		verify(res, times(1)).sendRedirect(anyString());
		assertEquals(session.getAttribute("user"), user);
	}
}