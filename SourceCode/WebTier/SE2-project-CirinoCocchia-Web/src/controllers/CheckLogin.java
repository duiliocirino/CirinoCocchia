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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import services.accountManagement.interfaces.LoginModule;
import services.accountManagement.interfaces.RegistrationModule;
import model.User;
import exceptions.CLupException;
import javax.persistence.NonUniqueResultException;

/**
 * Servlet implementation class CheckLogin.
 * This servlet is used to let the people get into the web application both as a visitor or as a register user.
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/accountManagement/interfaces/LoginModule")
	private LoginModule loginModule;
	@EJB(name = "services/accountManagement/interfaces/RegistrationModule")
	private RegistrationModule regModule;

	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
	public CheckLogin() {
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
	 * This method is called when a customer wants to login as a visitor and 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		//TODO : CREATE NEW VISITOR USER
	}

	/**
	 * This method is called to log in a registered user and letting him go to his 
	 * homepage, checking the correctness credentials passed as parameters in the request.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// OBTAIN AND ESCAPE PARAMETERS
		
		String usrn = null;
		String pwd = null;
		try {
			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		
		//AUTHENTICATE USER
		User user = null;
		try {
			loginModule = LoginModule.getInstance();
			user = loginModule.checkCredentials(usrn, pwd);
		} catch (NonUniqueResultException | CLupException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
			return;
		}

		// IF THE USER EXISTS, ADD INFO TO THE SESSION AND GO TO HOMEPAGE, OTHERWISE
		// SHOW LOGIN PAGE WITH ERROR MESSAGE

		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "login.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/GoToHomePage";
			response.sendRedirect(path);
		}
	}

	public void destroy() {
	}
}