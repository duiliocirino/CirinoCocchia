package controllers;

import java.io.IOException;
import java.util.ArrayList;
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

import src.main.java.model.Grocery;
import src.main.java.model.User;
import src.main.java.services.accountManagement.implementation.LoginModuleImplementation;
import src.main.java.services.groceryManagement.implementation.EmployeesModuleImplementation;

/**
 * Servlet implementation class RemoveEmployee.
 * This servlet is used to remove an employee from the system.
 */
@WebServlet("/RemoveEmployee")
public class RemoveEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected EmployeesModuleImplementation employeesModule;
	@EJB
	protected LoginModuleImplementation loginModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public RemoveEmployee() {
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
	 * This method creates the page server side by getting all employees of a user.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET USERS EMPLOYEES
		
		List<User> employees = new ArrayList<>();
		List<Grocery> groceries = user.getGroceries();
		for(Grocery grocery: groceries) {
			employees.addAll(grocery.getEmployees());
		}
		
		String path = "remove_employee.html";
		getTemplate(request, response, path, employees);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param response
	 * @param path 
	 * @param employees 
	 * @throws IOException
	 */
	protected void getTemplate(HttpServletRequest request, HttpServletResponse response, String path, List<User> employees) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("employees", employees);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * This method performs the action to remove an employee.
	 * It checks that the user doing the operation is a manager, gets the parameters and check their correctness and return
	 * the user to the right view if everything is right, otherwise it sends an error to the user.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// GET AND CHECK PARAMETERS
		
		Integer employeeId = null;
		Integer groceryId = null;
		
		try {
			employeeId = Integer.parseInt(request.getParameter("employeeId"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		try {			
			employeesModule.removeEmployee(employeeId, groceryId);
			user = loginModule.getUserById(user.getIduser());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Employee not deleteable");
			return;
		}

		// RETURN VIEW
		
		postTemplate(request, response, groceryId);
	}
	
	/**
	 * Utility class for unit testing, we don't want to test Thymeleaf.
	 * @param request
	 * @param groceryId 
	 * @param response 
	 * @throws IOException
	 */
	protected void postTemplate(HttpServletRequest request, HttpServletResponse response, Integer groceryId) throws IOException {
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("groceryId", groceryId);
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/RemoveEmployee";
		response.sendRedirect(path);
	}

}
