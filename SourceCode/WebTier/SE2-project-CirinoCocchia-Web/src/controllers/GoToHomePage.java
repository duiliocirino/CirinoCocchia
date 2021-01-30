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

import utils.ReservationStatus;
import utils.Roles;
import model.*;
import services.accountManagement.interfaces.LoginModule;
import services.searchManagement.interfaces.SearchEngineModule;

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
	@EJB(name = "services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
	@EJB(name = "services/searchManagement/interfaces/SearchEngineModule")
	private SearchEngineModule searchModule;
	
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
			loginModule = LoginModule.getInstance();
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch(Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The server didn't respond well");
			return;
		}
		
		// GET AND PARSE PARAMETERS
		
		Double latitude = null;
		Double longitude = null;
		Integer groceryId = null;
		
		try {
			latitude = Double.parseDouble(request.getParameter("latitude"));
			longitude = Double.parseDouble(request.getParameter("longitude"));
		} catch (Exception e) {
			latitude = null;
			longitude = null;
		}
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (Exception e) {
			
		}
		
		// RETURN THE USER TO THE RIGHT VIEW
		
		if(user.getRole() == Roles.REG_CUSTOMER || user.getRole() == Roles.VISITOR) {
			
			Grocery grocery = null;
			List<Grocery> nearGroceries = null;
			
			if(groceryId != null) {
				try {
					searchModule.getInstance();
					grocery = searchModule.getGrocery(groceryId);
				} catch (Exception e) {
					grocery = null;
				}
			} else {
				try {
					//searchModule = SearchEngineModule.getInstance();
					int min = MIN;
					
					nearGroceries = searchModule.getNearGroceries(new Position(latitude, longitude), 2000);
					
					if(nearGroceries.size() < min) min = nearGroceries.size();
					nearGroceries.subList(0, min);
					
				} catch (Exception e) {
					nearGroceries = null;
				}
			}
			
			List<Reservation> activeReservations = null;
			activeReservations = user.getReservations().stream().filter(x -> x.getStatus() == ReservationStatus.OPEN).collect(Collectors.toList());
			
			String path = "main_page_user.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			if (activeReservations != null)
				ctx.setVariable("activeReservations", activeReservations);
			if(nearGroceries != null) {
				ctx.setVariable("groceries", nearGroceries);
			}
			if(grocery != null) {
				ctx.setVariable("groceries", grocery);
			}
			templateEngine.process(path, ctx, response.getWriter());
		} else if(user.getRole() == Roles.EMPLOYEE || user.getRole() == Roles.MANAGER) {
			
			List<Grocery> userGroceries = null;
					
			if(user.getRole() == Roles.EMPLOYEE) {
				userGroceries = user.getEmployedGroceries();
			} else {
				userGroceries = user.getGroceries();
			}
			String path = "admin_home_page.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			if (userGroceries != null)
				ctx.setVariable("groceries", userGroceries);
			templateEngine.process(path, ctx, response.getWriter());
		}

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
