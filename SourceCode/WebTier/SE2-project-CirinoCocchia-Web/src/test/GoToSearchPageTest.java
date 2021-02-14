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

import controllers.GoToSearchPage;
import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.searchManagement.implementation.SearchEngineModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the GoToSearchPage class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GoToSearchPageTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock SearchEngineModuleImplementation searchModule;
	MockGoToSearchPage controllerServlet;
	final String groceryData = "groceryData";
	final String date = "2021-02-05";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockGoToSearchPage(searchModule));
		doNothing().when(controllerServlet).getTemplateExc(any(), any());
		doNothing().when(controllerServlet).getTemplate(any(), any(), anyString(), any(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	
	@Test
	public void nullParametersTest1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void nullParametersTest2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void badRadiusNumber1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("-10");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void badRadiusNumber2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("drg");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void badLatitudeNumber1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("100");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void badLatitudeNumber2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("grg");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(2)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void badLongitudeNumber1() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("200");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}

	@Test
	public void badLongitudeNumber2() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("sdf");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplateExc(any(), any());
	}
	
	@Test
	public void dbError() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(100);
		
		when(searchModule.getFavouriteGroceries(anyInt(), anyInt())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}
	
	@Test
	public void getOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(100);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(searchModule.getFavouriteGroceries(anyInt(), anyInt())).thenReturn(groceries);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any());
	}
	
	@Test
	public void postOk() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.REG_CUSTOMER);
		user.setIduser(100);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(searchModule.getFavouriteGroceries(anyInt(), anyInt())).thenReturn(groceries);
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("radius")).thenReturn("1000");
		when(req.getParameter("latitude")).thenReturn("50");
		when(req.getParameter("longitude")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(3)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), anyString(), any(), any());
	}
	
	class MockGoToSearchPage extends GoToSearchPage {

		private static final long serialVersionUID = 1L;
		
		public MockGoToSearchPage(SearchEngineModuleImplementation searchModule) {
			this.searchModule = searchModule;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doGet(request, response);
		}

		@Override
		protected void getTemplateExc(HttpServletRequest request, HttpServletResponse response) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplateExc(request, response);
		}

		@Override
		protected void getTemplate(HttpServletRequest request, HttpServletResponse response, String path,
				List<Grocery> favoriteGroceries, List<Grocery> nearGroceries) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplate(request, response, path, favoriteGroceries, nearGroceries);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doPost(request, response);
		}
		
		
	}
}