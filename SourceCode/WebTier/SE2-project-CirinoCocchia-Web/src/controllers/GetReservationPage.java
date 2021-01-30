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

import model.Grocery;
import model.Queue;
import model.User;
import services.accountManagement.interfaces.LoginModule;
import utils.Roles;

/**
 * Servlet implementation class GetReservationPage.
 * This servlet is used to get the reservation page of a given grocery, where the user can see the current reservations.
 */
@WebServlet("/GetReservationPage")
public class GetReservationPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public GetReservationPage() {
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
	 * This method creates the reservation page for the selected grocery for the user that requests it, if allowed to 
	 * see it, and proceeds to creating it by updating the status of the user and getting the up to date queue from the server.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// CHECKS THE ROLE OF THE USER
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getRole() != Roles.EMPLOYEE && user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND CHECK PARAMETERS
		
		Integer groceryId = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			if(!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId) ||
					!user.getEmployedGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
				throw new Exception("The user doesn't have the given grocery");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// GET UP TO DATE QUEUE OF THE GROCERY
		
		try {
			loginModule = LoginModule.getInstance();
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't retrieve data from server");
		}
		
		final Integer id = groceryId;
		
		Grocery grocery = user.getGroceries().stream().filter(x -> x.getIdgrocery() == id).findFirst().get();
		Queue queue = grocery.getQueue();
		
		// RETURN THE USER TO THE RIGHT VIEW
		
		final WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
		ctx.setVariable("reservations", queue.getReservations());
		String path = "grocery_search_page.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * This method redirect to the {@link controllers.GetGroceryData#doGet(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
