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

import model.User;
import services.accountManagement.interfaces.LoginModule;
import services.accountManagement.interfaces.RegistrationModule;
import services.groceryManagement.interfaces.EmployeesModule;
import utils.Roles;

/**
 * Servlet implementation class AddEmployee.
 * This servlet is used to add a new Employee to a grocery.
 */
@WebServlet("/AddEmployee")
public class AddEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/accountManagement/interfaces/RegistrationModule")
	private RegistrationModule regModule;
	@EJB(name = "services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
	@EJB(name = "services/groceryManagement/interfaces/EmployeesModule")
	private EmployeesModule employeesModule;
       
    /**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public AddEmployee() {
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
	 * This method creates the page server side.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getRole() != Roles.MANAGER) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not allowed to do this operation");
			return;
		}
		
		String path = "add_employee.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * This method adds a new employee if the parameters given in the request are correct, the user is 
	 * allowed to do the operation and the server successfully do his operations.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		// GET AND PARSE ALL PARAMETERS FROM REQUEST
		
		boolean isBadRequest = false;
		String username = null;
		String password = null;
		String email = null;
		Integer telephoneNum = null;
		Integer groceryId = null;
		
		try {
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		groceryId = Integer.parseInt(request.getParameter("groceryId"));
		telephoneNum = Integer.parseInt(request.getParameter("telephoneNumber"));
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		
		//CHECK THAT THE MANAGER OWNS THE GROCERY AND THE GROCERY EXISTS
		
		if(!user.getGroceries().stream().map(x->x.getIdgrocery()).collect(Collectors.toList()).contains(groceryId)) {
			isBadRequest = true;
		}
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// CREATE NEW USER WITH GIVEN DETAILS
		
		try {
			regModule = RegistrationModule.getInstance();
			employeesModule = EmployeesModule.getInstance();
			loginModule = LoginModule.getInstance();
			
			User newEmployee = regModule.register(Roles.EMPLOYEE, telephoneNum.toString(), username, password, email);
			newEmployee = employeesModule.addEmployee(newEmployee.getIduser(), groceryId);
			user = loginModule.checkCredentials(user.getUsername(), user.getPassword());
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create profile");
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		String successMessage = "You successfully added a new employee!";
		String path = "outcome_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
