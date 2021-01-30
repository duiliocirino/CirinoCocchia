package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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

import model.Grocery;
import model.Position;
import model.User;
import services.groceryManagement.interfaces.GroceryHandlerModule;
import services.reservationManagement.imlpementation.ReservationHandlerImplementation;
import services.reservationManagement.interfaces.ReservationHandlerModule;
import services.searchManagement.interfaces.SearchEngineModule;
import utils.ReservationType;
import utils.Roles;

/**
 * Servlet implementation class MakeReservation.
 * This servlet is used to add a new reservation to the system.
 */
@WebServlet("/MakeReservation")
public class MakeReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/reservationManagement/interfaces/ReservationHandlerModule")
	private ReservationHandlerModule resModule;
	@EJB(name = "services/searchManagement/interfaces/SearchEngineModule")
	private SearchEngineModule searchModule;
	
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
	 * This method redirect to the {@link controllers.GetGroceryData#doPost(HttpServletRequest, HttpServletResponse)} method of this class.
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
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		
		// CHECK EXISTENCE
		
		searchModule = SearchEngineModule.getInstance();
		resModule = ReservationHandlerModule.getInstance();
		
		
		if (groceryId != null) {
			try {
				searchModule.getGrocery(groceryId);
			} catch (Exception e) {
				isBadRequest = true;
			}
		} else isBadRequest = true;
		
		if(latitude == null || longitude == null) isBadRequest = true;
		
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
			resModule.addReservation(user.getIduser(), groceryId, ReservationType.LINEUP,
					(java.sql.Date) Calendar.getInstance().getTime(), new Position(latitude, longitude));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create reservation");
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("message", "You created your reservation!");
		String ctxpath = getServletContext().getContextPath();
		String path = "outcome_page.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

}
