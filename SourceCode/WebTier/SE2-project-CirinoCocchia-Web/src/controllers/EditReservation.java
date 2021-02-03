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

import src.main.java.model.Reservation;
import src.main.java.model.User;
import src.main.java.services.reservationManagement.interfaces.ReservationHandlerModule;
import src.main.java.utils.Roles;

/**
 * Servlet implementation class EditReservation.
 * This servlet is called to update the status of a reservation.
 */
@WebServlet("/EditReservation")
public class EditReservation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "src/main/java/services/reservationManagement/interfaces/ReservationHandlerModule")
	private ReservationHandlerModule resModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public EditReservation() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getRole() != Roles.EMPLOYEE || user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// get and check params
		Integer idreservation = null;
		Integer groceryId = null;
		try {
			idreservation = Integer.parseInt(request.getParameter("reservationid"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (NumberFormatException | NullPointerException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		try {
			Reservation reservation = resModule.getReservation(idreservation);
			if (reservation == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Reservation not found");
				return;
			}
			//TODO : DOVREBBE ESSERE FATTA PER FARE AVANZARE LO STATUS
			//resModule.editReservation(idreservation, user.getIduser(), reservation.getGrocery().getIdgrocery(), reservation.getType(), null);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reservation not closable");
			return;
		}

		// Return view
		postTemplate(request, response, groceryId);
	}

	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void postTemplate(HttpServletRequest request, HttpServletResponse response, int groceryId) throws IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("groceryId", groceryId);
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetReservationPage";
		templateEngine.process(path, ctx, response.getWriter());
	}
}
