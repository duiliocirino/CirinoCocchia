package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import src.main.java.utils.ReservationStatus;
import src.main.java.utils.Roles;
import utils.GroceryAdapter;
import src.main.java.model.*;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.services.reservationManagement.implementation.TimeEstimationModuleImplementation;
import src.main.java.services.searchManagement.implementation.SearchEngineModuleImplementation;

import java.util.List;
import java.util.stream.Collectors;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Servlet implementation class GoToHomePage.
 * This servlet is used to get the right home page for the user.
 */
@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected LoginModuleImplementation loginModule;
	@EJB
	protected SearchEngineModuleImplementation searchModule;
	@EJB
	protected GroceryHandlerModuleImplementation groModule;
	@EJB
	protected TimeEstimationModuleImplementation timeModule;
	
	/**
	 * This attribute is editable based on the number of groceries to display on the map when the page is accessed.
	 */
	final int MIN = 2;

	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
	public GoToHomePage() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// GET THE UP TO DATE USER INSTANCE
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			user = loginModule.getUserById(user.getIduser());
			if(user == null) throw new Exception();
			session.setAttribute("user", user);
		} catch(Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't retrieve data from server");
			return;
		}
		
		// GET AND PARSE PARAMETERS
		
		Double latitude = null;
		Double longitude = null;
		Integer groceryId = null;
		
		try {
			latitude = Double.parseDouble(request.getParameter("latitude"));
			longitude = Double.parseDouble(request.getParameter("longitude"));
			if(latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) throw new Exception();
		} catch (Exception e) {
			latitude = null;
			longitude = null;
		}
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (Exception e) {
			groceryId = null;
		}
		
		// RETURN THE USER TO THE RIGHT VIEW
		
		if(user.getRole() == Roles.REG_CUSTOMER || user.getRole() == Roles.VISITOR) {
			
			Grocery grocery = null;
			List<Grocery> nearGroceries = null;
			
			if(groceryId != null) {
				try {
					grocery = groModule.getGrocery(groceryId);
					if(grocery == null) throw new Exception();
				} catch (Exception e) {
					grocery = null;
				}
			} else {
				try {
					
					int min = MIN;
					
					nearGroceries = searchModule.getNearGroceries(new Position(latitude, longitude), 2000);
					
					if(nearGroceries.size() < min) min = nearGroceries.size();
					nearGroceries.subList(0, min);
					
				} catch (Exception e) {
					nearGroceries = null;
				}
			}
			
			List<Reservation> activeReservations = null;
			activeReservations = user.getReservations().stream().filter(x -> x.getStatus() != ReservationStatus.CLOSED).collect(Collectors.toList());
			
			String path = "main_page_user.html";
			getTemplateCustomer(request, response, path, activeReservations, nearGroceries, grocery);
		}
		
		if(user.getRole() == Roles.EMPLOYEE || user.getRole() == Roles.MANAGER) {
			
			List<Grocery> userGroceries = null;
					
			if(user.getRole() == Roles.EMPLOYEE) {
				userGroceries = user.getEmployedGroceries();
			} else {
				userGroceries = user.getGroceries();
			}
			String path = "admin_home_page.html";
			getTemplateAdmin(request, response, path, userGroceries);
		}
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @param path 
	 * @param grocery 
	 * @param nearGroceries 
	 * @param activeReservations 
	 * @throws IOException
	 */
	protected void getTemplateCustomer(HttpServletRequest request, HttpServletResponse response, String path, List<Reservation> activeReservations, List<Grocery> nearGroceries, Grocery grocery) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if (activeReservations != null) ctx.setVariable("activeReservations", activeReservations);
		
		if(nearGroceries != null) ctx.setVariable("groceries", nearGroceries.stream().map(x -> new GroceryAdapter(x)).collect(Collectors.toList()));

		if(grocery != null) {
			ctx.setVariable("grocery", new GroceryAdapter(grocery));
		}
		
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @param userGroceries 
	 * @param path 
	 * @throws IOException
	 */
	protected void getTemplateAdmin(HttpServletRequest request, HttpServletResponse response, String path, List<Grocery> userGroceries) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (userGroceries != null)
			ctx.setVariable("groceries", userGroceries);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * This method redirect to the {@link controllers.GetGroceryData#doGet(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
