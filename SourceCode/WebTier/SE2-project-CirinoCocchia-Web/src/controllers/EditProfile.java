package controllers;

import java.io.IOException;

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
import src.main.java.services.accountManagement.implementation.RegistrationModuleImplementation;

/**
 * Servlet implementation class EditProfile.
 * This servlet is used to edit profile info.
 */
@WebServlet("/EditProfile")
public class EditProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB
	protected RegistrationModuleImplementation regModule;
	
	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
    public EditProfile() {
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
     * This method generates server-side the page to show.
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "editprofile.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	/**
	 * This method is called to apply changes that the user wants to make on his account.
	 * It checks the correctness of the request's parameters, if there's at least one to change it tries to apply
	 * it, updates the session and returns the user to the right view.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		// GET AND PARSE ALL PARAMETERS FROM REQUEST AND CHECK THAT THERE IS SOMETHING TO EDIT, CORRECTNESS AND APPLY ALL NULL VALUES
		boolean isBadRequest = false;
		String username = null;
		String password = null;
		String email = null;
		String telephoneNum = null;
		
		try {
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		telephoneNum = StringEscapeUtils.escapeJava(request.getParameter("telephoneNumber"));
		
		
		
		if(username != null) {
			if(username.isEmpty()) username = null;
		} else username = user.getUsername();
		
		if(email != null) {
			if(email.isEmpty()) email = null;
		} else email = user.getEmail();
		
		if(password != null) {
			if(password.isEmpty()) password = null;
		} else password = user.getPassword();
		
		if(telephoneNum != null) {
			if(!telephoneNum.isEmpty()) {
				if(Double.parseDouble(telephoneNum) < 10000000) throw new NumberFormatException();
			} else telephoneNum = null;
			
		} else telephoneNum = user.getTelephoneNumber();
		
		isBadRequest = isBadRequest || (username == null && email == null && password == null && telephoneNum == null);
		
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
		}
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// UPDATE USER DETAILS AND SESSION
		
		try {
			user = regModule.editProfile(user.getIduser(), telephoneNum, username, password, email);
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
	protected void postTemplate(HttpServletRequest request, HttpServletResponse response, String path,
			String successMessage) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		templateEngine.process(path, ctx, response.getWriter());	
	}

	public void destroy() {
		
	}
}
