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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import model.Reservation;
import model.User;
import services.reservationManagement.interfaces.ReservationHandlerModule;
import utils.Roles;

/**
 * Servlet implementation class DeleteReservation.
 * This servlet is used to remove a reservation from the system.
 */
@WebServlet("/DeleteReservation")
public class DeleteReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/reservationManagement/imlpementation/ReservationHandlerModule")
	private ReservationHandlerModule resModule;
       
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
		
		//ALLOW ONLY EMPLOYEES AND MANAGERS TO DO THIS OPERATION
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(user.getRole() != Roles.EMPLOYEE || user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND CHECK PARAMETERS
		
		Integer reservationId = null;
		Integer groceryId = null;
		
		try {
			reservationId = Integer.parseInt(request.getParameter("reservationId"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (NumberFormatException | NullPointerException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		try {
			Reservation reservation = resModule.getReservation(reservationId);
			if (reservation == null || reservation.getGrocery().getIdgrocery() != groceryId) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Reservation not found");
				return;
			}
			resModule.removeReservation(reservation);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reservation not closable");
			return;
		}

		// RETURN VIEW
		
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("groceryId", groceryId);
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetReservationPage";
		templateEngine.process(path, ctx, response.getWriter());
	}

}
