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

import model.Grocery;
import model.User;
import services.groceryManagement.interfaces.EmployeesModule;
import utils.Roles;

/**
 * Servlet implementation class RemoveEmployee.
 * This servlet is used to remove an employee from the system.
 */
@WebServlet("/RemoveEmployee")
public class RemoveEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/groceryManagement/interfaces/EmployeesModule")
	private EmployeesModule regModule;
       
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
		if(user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET USERS EMPLOYEES
		
		List<User> employees = new ArrayList<>();
		List<Grocery> groceries = user.getGroceries();
		for(Grocery grocery: groceries) {
			employees.addAll(grocery.getEmployees());
		}
		
		String path = "remove_employee.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("employees", employees);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//ALLOW ONLY EMPLOYEES AND MANAGERS TO DO THIS OPERATION
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		// GET AND CHECK PARAMETERS
		
		Integer employeeId = null;
		Integer groceryId = null;
		
		try {
			employeeId = Integer.parseInt(request.getParameter("employeeId"));
			groceryId = Integer.parseInt(request.getParameter("groceryId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		try {
			regModule = EmployeesModule.getInstance();
			regModule.removeEmployee(employeeId, groceryId);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Employee not deleteable");
			return;
		}

		// RETURN VIEW
		
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("groceryId", groceryId);
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/RemoveEmployee";
		templateEngine.process(path, ctx, response.getWriter());
	}

}
