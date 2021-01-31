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

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import src.main.java.model.Grocery;
import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.accountManagement.interfaces.RegistrationModule;
import src.main.java.services.groceryManagement.interfaces.EmployeesModule;
import src.main.java.services.groceryManagement.interfaces.GroceryHandlerModule;
import src.main.java.services.searchManagement.interfaces.SearchEngineModule;
import src.main.java.utils.Roles;

/**
 * Servlet implementation class AddGrocery.
 * This servlet is used to add a grocery to the system.
 */
@WebServlet("/AddGrocery")
public class AddGrocery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "src/main/java/services/groceryManagement/interfaces/GrocerhyHandlerModule")
	private GroceryHandlerModule groModule;
	@EJB(name = "src/main/java/services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
	
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public AddGrocery() {
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
	 * This method redirect to the {@link controllers.AddGrocery#doPost(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * This method adds a grocery to the system.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//ALLOW ONLY MANAGERS TO DO THIS OPERATION
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND PARSE ALL PARAMETERS FROM REQUEST
		
		boolean isBadRequest = false;
		String name = null;
		Integer maxSpotsInside = null;
		Double latitude = null;
		Double longitude = null;
		
		try {
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		maxSpotsInside = Integer.parseInt(request.getParameter("maxSpots"));
		latitude = Double.parseDouble(request.getParameter("latitude"));
		longitude = Double.parseDouble(request.getParameter("longitude"));
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		
		isBadRequest = isBadRequest || (name == null) || (maxSpotsInside == null) || (maxSpotsInside < 1) || (latitude == null) || (longitude == null);
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// CREATE NEW GROCERY WITH GIVEN DETAILS
		
		try {
			groModule = GroceryHandlerModule.getInstance();
			loginModule = LoginModule.getInstance();
			groModule.addGrocery(name, new Position(latitude, longitude), maxSpotsInside, user.getIduser());
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create profile");
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		String successMessage = "You successfully added a new grocery!";
		String path = "outcome_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
