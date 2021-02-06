package controllers;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;
import src.main.java.services.reservationManagement.implementation.QueueUpdateManagementImplementation;
import src.main.java.services.reservationManagement.implementation.ReservationHandlerImplementation;
import src.main.java.utils.Roles;

/**
 * Servlet implementation class MakeReservation.
 * This servlet is used to add a new reservation to the system.
 */
@WebServlet("/MakeReservation")
public class MakeReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	private ReservationHandlerImplementation resModule;
	@EJB
	private QueueUpdateManagementImplementation queueModule;
	@EJB
	private GroceryHandlerModuleImplementation groModule;
	@EJB
	private LoginModuleImplementation loginModule;
	
	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public MakeReservation() {
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
	 * This method redirect to the {@link controllers.MakeReservation#doPost(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * This method is called when a user wants to make a reservation.
	 * It gets the parameters checks their correctness and if the user can make a reservation for the given grocery, 
	 * then proceeds to create the reservation. If it succeed it redirects the user to the outcome page, otherwise it 
	 * sends an error.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND PARSE ALL PARAMETERS FROM REQUEST
		
		boolean isBadRequest = false;
		Integer groceryId= null;
		Double latitude = null;
		Double longitude = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			latitude = Double.parseDouble(request.getParameter("latitude"));
			longitude = Double.parseDouble(request.getParameter("longitude"));
			
			if(latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		// CHECK EXISTENCE		
		
		try {
			Grocery grocery = groModule.getGrocery(groceryId);
			
			if(grocery == null) throw new Exception();
		} catch (Exception e) {
			isBadRequest = true;
		}
		
		if(user.getRole() == Roles.MANAGER) {
			if(!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) isBadRequest = true;
		}
		
		if(user.getRole() == Roles.EMPLOYEE) {
			if(!user.getEmployedGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) isBadRequest = true;
		}
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		// CREATE RESERVATION IN DB
		
		try {
			queueModule.lineUp(user.getIduser(), groceryId, latitude, longitude);
			
			user = loginModule.getUserById(user.getIduser());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create reservation");
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		String path = "outcome_page.html";
		getTemplate(request, response, path);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @param path 
	 * @throws IOException
	 */
	protected void getTemplate(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("message", "You created your reservation!");
		templateEngine.process(path, ctx, response.getWriter());
	}
}
