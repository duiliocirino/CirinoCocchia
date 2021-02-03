package controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import src.main.java.services.accountManagement.interfaces.RegistrationModule;

/**
 * Unit test for the RegisterUser class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterUserTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock LoginModule loginModule;
	@Mock RegistrationModule regModule;
	RegisterUser controllerServlet;
	final String manager = "manager";
	final String customer = "customer";
	final String username = "username";
	final String password = "password";
	final String email = "asd@email.com";
	final String telephoneNumber = "3276547899";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new RegisterUser());
		doNothing().when(controllerServlet).postTemplateExist(any(), any());
		doNothing().when(controllerServlet).postTemplate(any(), any());
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {	
		when(req.getParameter("username")).thenReturn(null);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn("asd");
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("telephoneNumber")).thenReturn("3re");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest1() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn("ehi");
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest2() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("telephoneNumber")).thenReturn("3de");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(4)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest3() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn("");
		when(req.getParameter("role")).thenReturn(customer);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest4() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn("");
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(manager);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badParametersTest5() throws ServletException, IOException {
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(manager);
		when(req.getParameter("telephoneNumber")).thenReturn("80");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenThrow(new CLupException(""));
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(manager);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error, retry later");
	}
	
	@Test
	public void userExists() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);

		
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(new User());
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(manager);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplateExist(any(), any());
	}
	
	@Test
	public void dbErrorNotCreated() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		MockedStatic <RegistrationModule> regMock = Mockito.mockStatic( RegistrationModule.class );
		regMock.when( () -> RegistrationModule.getInstance()).thenReturn(regModule);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(null);
		when(regModule.register(any(), anyString(), anyString(), anyString(), anyString())).thenThrow(new CLupException(""));
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(manager);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		regMock.close();
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bad database insertion");
	}
	
	@Test
	public void userRegistered() throws ServletException, IOException, CLupException {
		MockedStatic <LoginModule> logMock = Mockito.mockStatic( LoginModule.class );
		logMock.when( () -> LoginModule.getInstance()).thenReturn(loginModule);
		MockedStatic <RegistrationModule> regMock = Mockito.mockStatic( RegistrationModule.class );
		regMock.when( () -> RegistrationModule.getInstance()).thenReturn(regModule);
		
		when(loginModule.checkCredentials(anyString(), anyString())).thenReturn(null);
		when(regModule.register(any(), anyString(), anyString(), anyString(), anyString())).thenReturn(new User());
		when(req.getParameter("username")).thenReturn(username);
		when(req.getParameter("email")).thenReturn(email);
		when(req.getParameter("password")).thenReturn(password);
		when(req.getParameter("role")).thenReturn(customer);
		when(req.getParameter("telephoneNumber")).thenReturn(telephoneNumber);
		
		controllerServlet.doPost(req, res);
		
		logMock.close();
		regMock.close();
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any());
	}
}