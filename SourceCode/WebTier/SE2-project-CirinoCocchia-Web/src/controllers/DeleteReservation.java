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

import src.main.java.exceptions.CLupException;
import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.reservationManagement.implementation.ReservationHandlerImplementation;
import src.main.java.utils.Roles;

/**
 * Servlet implementation class DeleteReservation.
 * This servlet is used to remove a reservation from the system.
 */
@WebServlet("/DeleteReservation")
public class DeleteReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected ReservationHandlerImplementation resModule;
	@EJB
	protected LoginModuleImplementation loginModule;
	
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public DeleteReservation() {
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
	 * This method redirects to the correct one.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * This method serves the function to remove a reservation from the system by taking, as a parameter from 
	 * request, the id of the reservation to delete and the grocery to which it is associated, checking their correctness.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND CHECK PARAMETERS
		
		Integer reservationId = null;
		Integer groceryId = null;
		
		try {
			reservationId = Integer.parseInt(request.getParameter("reservationId"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			if (groceryId == null || reservationId == null) throw new NullPointerException();
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		

		if (user.getRole() == Roles.EMPLOYEE) {
			if (!user.getEmployedGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "You are not allowed to do this operation");
				return;
			}
		}
		
		if(user.getRole() == Roles.MANAGER) {
			if (!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "You are not allowed to do this operation");
				return;
			}
		}

		try {
			Reservation reservation = resModule.getReservation(reservationId);
			if (reservation == null || reservation.getQueue().getGrocery().getIdgrocery() != groceryId) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reservation not found");
				return;
			}
			resModule.removeReservation(reservation);
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch (CLupException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		// RETURN VIEW
		
		postTemplate(request, response, groceryId);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void postTemplate(HttpServletRequest request, HttpServletResponse response, int groceryId) throws IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("groceryId", groceryId);
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomePage";
		response.sendRedirect(path);
	}

}
