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

import controllers.EditGroceryInfo;
import src.main.java.exceptions.CLupException;
import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.utils.Roles;

/**
 * Unit test for the EditGroceryInfo class.
 */
@RunWith(MockitoJUnitRunner.class)
public class EditGroceryInfoTest {
	@Mock HttpServletRequest req;
	@Mock HttpServletResponse res;
	@Mock HttpSession session;
	@Mock GroceryHandlerModuleImplementation groModule;
	MockEditGroceryInfo controllerServlet;
	final String groceryName = "GroceryName";
	
	
	@Before
	public void setup() throws IOException, ServletException {
		controllerServlet = spy(new MockEditGroceryInfo(groModule));
		doNothing().when(controllerServlet).getTemplate(any(), any(), any(), any());
		doNothing().when(controllerServlet).postTemplate(any(), any(), any(), any());
		when(req.getSession()).thenReturn(session, session);
	}
	

	
	@Test
	public void nullParametersTestGet() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn(null);
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	

	@Test
	public void groceryNotOwnedGet() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("120");
		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void pageCreationGet() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");		
		controllerServlet.doGet(req, res);
		
		verify(req, times(1)).getParameter(anyString());
		verify(controllerServlet, times(1)).getTemplate(any(), any(), any(), anyString());
	}
	
	
	@Test
	public void nullParametersTest1Post() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("name")).thenReturn(null);
		when(req.getParameter("maxSpots")).thenReturn("50");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void nullParametersTest2Post() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("maxSpots")).thenReturn(null);
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badMaxSpotsNumberBadName() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn("");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("0");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badOpeningHour() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn("");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("0");
		when(req.getParameter("openHour")).thenReturn("-2");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void badClosingHour() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn("");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("0");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("25");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void emptyParameters() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setEmail("ciao@email.com");
		user.setIduser(1234);
		user.setTelephoneNumber("3278906554");
		user.setUsername("ciao");
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn("");
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("");
		when(req.getParameter("openHour")).thenReturn("");
		when(req.getParameter("closeHour")).thenReturn("");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
	}
	
	@Test
	public void groceryNotOwnedPost() throws ServletException, IOException {
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("groceryId")).thenReturn("120");
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
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
		
		when(groModule.editGrocery(anyInt(), anyString(), anyInt(), anyInt(), anyInt())).thenThrow(new CLupException(""));
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		verify(req, times(5)).getParameter(anyString());
		verify(res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
	}

	@Test
	public void groceryEdited1() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
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
		
		when(session.getAttribute("user")).thenReturn(user);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("groceryId")).thenReturn("123");
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("24");
		
		controllerServlet.doPost(req, res);
		
		groceries.add(newGrocery);
		user.setGroceries(groceries);
		
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), any(), any());
	}
	
	@Test
	public void groceryEdited2() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOpeningHour(0);
		grocery.setClosingHour(24);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		Grocery newGrocery = new Grocery();
		newGrocery.setIdgrocery(456);
		newGrocery.setOwner(user);
		
		groceries.set(0, grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("groceryId")).thenReturn("456");
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("0");
		when(req.getParameter("closeHour")).thenReturn("");
		
		controllerServlet.doPost(req, res);
		
		groceries.add(newGrocery);
		user.setGroceries(groceries);
		
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), any(), any());
	}
	
	@Test
	public void groceryEdited3() throws ServletException, IOException, CLupException {
		
		User user = new User();
		user.setRole(Roles.MANAGER);
		user.setIduser(1234);
		
		List<Grocery> groceries = new ArrayList<>();
		
		Grocery grocery = new Grocery();
		grocery.setIdgrocery(123);
		grocery.setOpeningHour(0);
		grocery.setClosingHour(24);
		grocery.setOwner(user);
		
		groceries.add(grocery);
		user.setGroceries(groceries);
		
		Grocery newGrocery = new Grocery();
		newGrocery.setIdgrocery(456);
		newGrocery.setOwner(user);
		
		groceries.set(0, grocery);
		user.setGroceries(groceries);
		
		when(session.getAttribute("user")).thenReturn(user);
		when(groModule.getGrocery(anyInt())).thenReturn(grocery);
		when(req.getParameter("name")).thenReturn(groceryName);
		when(req.getParameter("groceryId")).thenReturn("456");
		when(req.getParameter("maxSpots")).thenReturn("50");
		when(req.getParameter("openHour")).thenReturn("");
		when(req.getParameter("closeHour")).thenReturn("23");
		
		controllerServlet.doPost(req, res);
		
		groceries.add(newGrocery);
		user.setGroceries(groceries);
		
		verify(req, times(5)).getParameter(anyString());
		verify(controllerServlet, times(1)).postTemplate(any(), any(), any(), any());
	}
	
	class MockEditGroceryInfo extends EditGroceryInfo {

		private static final long serialVersionUID = 1L;
		
		public MockEditGroceryInfo (GroceryHandlerModuleImplementation groModule) {
			this.groModule = groModule;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doGet(request, response);
		}

		@Override
		protected void getTemplate(HttpServletRequest request, HttpServletResponse response, Grocery grocery,
				String path) throws IOException {
			// TODO Auto-generated method stub
			super.getTemplate(request, response, grocery, path);
		}

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			// TODO Auto-generated method stub
			super.doPost(request, response);
		}

		@Override
		protected void postTemplate(HttpServletRequest request, HttpServletResponse response, String path,
				String successMessage) throws IOException {
			// TODO Auto-generated method stub
			super.postTemplate(request, response, path, successMessage);
		}
		
		
	}
}
