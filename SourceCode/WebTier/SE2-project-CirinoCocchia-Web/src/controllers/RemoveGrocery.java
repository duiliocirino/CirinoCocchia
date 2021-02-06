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

import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;

/**
 * Servlet implementation class RemoveGrocery.
 * This servlet is used to remove a grocery.
 */
@WebServlet("/RemoveGrocery")
public class RemoveGrocery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	private GroceryHandlerModuleImplementation groModule;
	@EJB
	private LoginModuleImplementation loginModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public RemoveGrocery() {
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
	 * This method redirect to the {@link controllers.RemoveGrocery#doPost(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * This method is called to remove a given grocery.
	 * It succeed only if the user requesting it is a manager and it owns the grocery, otherwise the user is sent an error.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND CHECK PARAMETERS
		
		Integer groceryId = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		if(!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}

		try {
			groModule.removeGrocery(groceryId);
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Grocery not deleteable");
			return;
		}

		// RETURN VIEW
		
		postTemplate(request, response);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param groceryId 
	 * @throws IOException
	 */
	protected void postTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomePage";
		response.sendRedirect(path);
	}

}
