package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import src.main.java.model.User;
import src.main.java.services.accountManagement.interfaces.LoginModule;
import src.main.java.services.accountManagement.interfaces.RegistrationModule;
import src.main.java.utils.Roles;

/**
 * Servlet implementation class RegisterUser/
 * This servlet is used by unregistered users that want to register to the system.
 */
@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "src/main/java/services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
	@EJB(name = "src/main/java/services/accountManagement/interfaces/RegistrationModule")
	private RegistrationModule regModule;

	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public RegisterUser() {
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
	 * This method redirect to the {@link controllers.GetGroceryData#doPost(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
    }
    
    /**
	 * This method is called when a user wants to register.
	 * It checks the correctness and completeness of the request's parameters, then checks if a user with 
	 * the same username and password exists, if not it adds the user to the system and redirects the user to the 
	 * login page. If it exists it does the same. If anything goes wrong it sends an error to the user.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		boolean isBadRequest = false;
		String username = null;
		String password = null;
		String telephoneNum = null;
		String email = null;
		String roles = null;
		
		try {
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		telephoneNum = StringEscapeUtils.escapeJava(request.getParameter("telephoneNum"));
		roles = StringEscapeUtils.escapeJava(request.getParameter("role"));
		if(telephoneNum != null) Integer.parseInt(telephoneNum);
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		
		if (isBadRequest || username == null || password == null || email == null || roles == null || 
				((!roles.equals("manager") || roles.equals("customer")) && (roles.equals("manager") || !roles.equals("customer") )) || 
				telephoneNum == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters");
			return;
		}
		
		//CREATE ROLE
		Roles role = null;
		if(role.equals("manager")) role = Roles.MANAGER;
		else role = Roles.REG_CUSTOMER;
		
		User user = null;
		
		try {
			user = loginModule.checkCredentials(username, password);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		if (user == null) {
			String error = null;
			try {
				regModule.register(role, telephoneNum, username, password, email);
			} catch (Exception e) {
				error = "Bad database insertion input";
			}
			if(error != null) {
				response.sendError(505, error);
				return;
			} else {
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("Registration success");
				String path = getServletContext().getContextPath() + "login.html";
				response.sendRedirect(path);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setCharacterEncoding("UTF-8");
			String path = getServletContext().getContextPath() + "login.html";
			response.sendRedirect(path);
		}
	}

	public void destroy() {
	}
}
