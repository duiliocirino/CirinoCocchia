package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

import src.main.java.model.User;
import src.main.java.services.groceryManagement.implementation.MonitorModuleImplementation;
import src.main.java.utils.GroceryData;

/**
 * Servlet implementation class GetGroceryData.
 * This servlet is used to inspect data about the grocery.
 */
@WebServlet("/GetGroceryData")
public class GetGroceryData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected MonitorModuleImplementation monitorModule;
	
       
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
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Date today = Calendar.getInstance().getTime();
		
		// GET AND CHECK PARAMETERS
		
		Integer groceryId = null;
		String groceryData = null;
		Date startDate = null;
		
		try {
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
			groceryData = StringEscapeUtils.escapeJava(request.getParameter("groceryData"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = (Date) sdf.parse(request.getParameter("date"));
			if(startDate.compareTo(today) > 0) {
				groceryId = null;
				throw new NumberFormatException();
			}
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			if(groceryId != null) groceryData = "Init";
			else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
				return;
			}	
		}
		
		if (!user.getGroceries().stream().map(x -> x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Grocery not allowed");
			return;
		}

		// GET THE DATA FROM THE DATABASE
		
		Map<GroceryData, Float> res = null;
		
		try {
			if(groceryData.equals("Init")) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -7);
				Date weekAgo = cal.getTime();
				res = monitorModule.getGroceryStats(groceryId, weekAgo);
				startDate = weekAgo;
			}
			else if(groceryData.equals("All")) res = monitorModule.getGroceryStats(groceryId, startDate);
			else if(groceryData != null){
				res = new HashMap<GroceryData, Float>();
				res.put(GroceryData.getMissionStatusFromInt(groceryData), monitorModule.getGroceryStats(groceryId, GroceryData.getMissionStatusFromInt(groceryData), startDate));
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
		getTemplate(request, response, groceryData, dataMap, startDate, groceryId);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @param startDate 
	 * @param dataMap 
	 * @param groceryData 
	 * @param groceryId 
	 * @throws IOException
	 */
	protected void getTemplate(HttpServletRequest request, HttpServletResponse response, String groceryData, Map<String, Object> dataMap, Date startDate, Integer groceryId) throws IOException {
		final WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
		if(groceryData != null) {
			ctx.setVariable("dataMap", dataMap);
			ctx.setVariable("startDate", startDate);
			ctx.setVariable("groceryId", groceryId);
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
