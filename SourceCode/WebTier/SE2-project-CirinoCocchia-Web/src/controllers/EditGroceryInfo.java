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
import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.GroceryHandlerModuleImplementation;

/**
 * Servlet implementation class EditGroceryInfo.
 * This servlet is used to edit grocery info.
 */
@WebServlet("/EditGroceryInfo")
public class EditGroceryInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected GroceryHandlerModuleImplementation groModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public EditGroceryInfo() {
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
	 * This method is called to create the page to edit the grocery server-side.
	 * It checks that the user can do the operation and checks if the user owns the grocery.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND CHECK PARAMETERS
		
		Integer groceryId = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			if(!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) throw new NullPointerException();
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		// GET THE GROCERY AND RETURN THE RIGHT VIEW
		
		final Integer id = groceryId;
		Grocery grocery = user.getGroceries().stream().filter(x -> x.getIdgrocery() == id).findFirst().get();
		String path = "editgrocery.html";
		getTemplate(request, response, grocery, path);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void getTemplate(HttpServletRequest request, HttpServletResponse response, Grocery grocery, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("grocery", grocery);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * This method is called to apply the requested edits in the case that at least one parameter must be changed, the 
	 * user is the owner of the grocery, and that the 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND PARSE ALL PARAMETERS FROM REQUEST
		
		boolean isBadRequest = false;
		String name = null;
		Integer groceryId = null;
		String maxSpots = null;
		
		try {
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			maxSpots = StringEscapeUtils.escapeJava(request.getParameter("maxSpots"));
			if(name.isEmpty()) name = null;
			if(!maxSpots.isEmpty()) {
				if(Integer.parseInt(maxSpots) < 1) maxSpots = null;
			} else maxSpots = null;
			isBadRequest = isBadRequest || groceryId == null || ((name == null) && (maxSpots == null)) || 
					!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId);
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
		}
		
		
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// UPDATE GROCERY INFO AND OF THE USER
		
		final Integer id = groceryId;
		Integer maxSpotsI = 0;
		if(maxSpots != null) maxSpotsI = Integer.parseInt(maxSpots);
		
		try {
			Grocery editedGro = groModule.editGrocery(groceryId, name, maxSpotsI);
			//List<Grocery> groceries = user.getGroceries();
			//groceries.removeIf(x -> x.getIdgrocery() == id);
			//groceries.add(editedGro);
			//user.setGroceries(groceries);
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		String successMessage = "Your edits were applied successfully!";
		String path = "outcome_page.html";
		postTemplate(request, response, path, successMessage);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void postTemplate(HttpServletRequest request, HttpServletResponse response, String path, String successMessage) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
