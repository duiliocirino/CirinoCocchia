package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import model.User;
import services.groceryManagement.interfaces.MonitorModule;
import utils.GroceryData;
import utils.Roles;

/**
 * Servlet implementation class GetGroceryData.
 * This servlet is used to inspect data about the grocery.
 */
@WebServlet("/GetGroceryData")
public class GetGroceryData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/groceryManagement/implementationMonitorModule")
	private MonitorModule monitorModule;
	
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public GetGroceryData() {
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
	 * This method is called to get the grocery data from the database.
	 * The groceryId is needed for it to work. When no other parameters are passed from request it simply returns the page.
	 * When it is given a startDate and a set groceryData to look for it returns them and displays them into the page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// CHECKS THE ROLE OF THE USER
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND CHECK PARAMETERS
		
		Integer groceryId = null;
		String groceryData = null;
		Date startDate = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			groceryData = StringEscapeUtils.escapeJava(request.getParameter("groceryData"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = (Date) sdf.parse(request.getParameter("date"));
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		if(groceryId == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No grocery was selected");
			return;
		}
		
		final Integer id = groceryId;
		if (!user.getGroceries().stream().map(x -> x.getIdgrocery()).anyMatch(x -> x == id)) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Grocery not allowed");
			return;
		}

		// GET THE DATA FROM THE DATABASE
		
		Map<GroceryData, Float> res = null;
		
		try {
			
			if(groceryData.equals("All")) res = monitorModule.getGroceryStats(groceryId, startDate);
			else if(groceryData != null){
				res = new HashMap<GroceryData, Float>();
				res.put(GroceryData.valueOf(groceryData), monitorModule.getGroceryStats(groceryId, GroceryData.valueOf(groceryData), startDate));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Data not retrievable");
			return;
		}
		
		// RETURN THE RIGHT VIEW TO THE USER
		
		Map<String, Object> dataMap = new HashMap<>();
		
		for(Map.Entry<GroceryData, Float> entry: res.entrySet()) {
			dataMap.put(entry.getKey().getValue(), entry.getValue());
		}
		final WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
		if(groceryData != null) {
			ctx.setVariable("dataMap", dataMap);
			ctx.setVariable("startDate", startDate);
		}
		String path = "grocery_inspection.html";
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
