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

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import src.main.java.model.Position;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;

/**
 * Servlet implementation class AddGrocery.
 * This servlet is used to add a grocery to the system.
 */
@WebServlet("/AddGrocery")
public class AddGrocery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected GroceryHandlerModuleImplementation groModule;
	@EJB
	protected LoginModuleImplementation loginModule;
	
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND PARSE ALL PARAMETERS FROM REQUEST
		
		boolean isBadRequest = false;
		String name = null;
		Integer maxSpotsInside = null;
		Double latitude = null;
		Double longitude = null;
		Integer openingHour = null;
		Integer closingHour = null;
		
		try {
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		maxSpotsInside = Integer.parseInt(request.getParameter("maxSpots"));
		latitude = Double.parseDouble(request.getParameter("latitude"));
		longitude = Double.parseDouble(request.getParameter("longitude"));
		openingHour = Integer.parseInt(request.getParameter("openHour"));
		closingHour = Integer.parseInt(request.getParameter("closeHour"));
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
		}
		
		isBadRequest = isBadRequest || (name == null) || name.isEmpty() || (maxSpotsInside == null) || (maxSpotsInside < 1) || (latitude == null) || (longitude == null) ||
				 latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180 || closingHour < 0 || closingHour > 24 || closingHour < openingHour || openingHour < 0 || openingHour > 24 ;
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// CREATE NEW GROCERY WITH GIVEN DETAILS
		
		try {
			groModule.addGrocery(name, new Position(latitude, longitude), maxSpotsInside, openingHour, closingHour, user.getIduser());
			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		postTemplate(request, response);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void postTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String successMessage = "You successfully added a new grocery!";
		String path = "outcome_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
