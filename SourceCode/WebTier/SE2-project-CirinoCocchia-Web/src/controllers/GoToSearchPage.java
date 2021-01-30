package controllers;

import java.io.IOException;
import java.util.List;

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

import services.searchManagement.interfaces.SearchEngineModule;
import utils.Roles;
import model.User;
import model.Grocery;
import model.Position;

/**
 * Servlet implementation class GoToSearchPage.
 * This servlet is used by customers that want to search for groceries.
 */
@WebServlet("/GoToSearchPage")
public class GoToSearchPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * This attribute can be changed to match the number of favourite groceries to return and 
	 * eventually could be dynamically chosen by the user.
	 */
	private static final int nFavourites = 3;
	private TemplateEngine templateEngine;
	@EJB(name = "services/searchManagement/interfaces/SearchEngineModule")
	private SearchEngineModule searchModule;
	
	public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public GoToSearchPage() {
        super();
    }

	/**
	 * This method checks the role of the user that makes the request and if it allowed to get the page, 
	 * gets the request parameters, checks their correctness and proceeds to redirect the customer the the right page 
	 * that he requested.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// CHECKS THE ROLE OF THE USER
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getRole() != Roles.VISITOR && user.getRole() != Roles.REG_CUSTOMER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND CHECK PARAMETERS
		
		Double latitude = null;
		Double longitude = null;
		Double radius = null;
		List<Grocery> favoriteGroceries = null;
		List<Grocery> nearGroceries = null;

		try {
			radius = Double.parseDouble(request.getParameter("address"));
			latitude = Double.parseDouble(request.getParameter("lat"));
			longitude = Double.parseDouble(request.getParameter("long"));
			
			if(radius == null || latitude == null || longitude == null) {
				final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
				String path = getServletContext().getContextPath() + "/GoToHomePage";
				templateEngine.process(path, ctx, response.getWriter());
			}
		} catch (Exception e) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			String path = getServletContext().getContextPath() + "/GoToHomePage";
			templateEngine.process(path, ctx, response.getWriter());
		}
		
		try {
			favoriteGroceries = searchModule.getFavouriteGroceries(user.getIduser(), nFavourites);
			nearGroceries = searchModule.getNearGroceries(new Position(latitude, longitude), radius);
		} catch(Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get groceries");
			return;
		}

		String path = "search_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (favoriteGroceries != null) {
			ctx.setVariable("favGroceries", favoriteGroceries);
		}
		if (nearGroceries != null) {
			ctx.setVariable("nearGroceries", nearGroceries);
		}
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
