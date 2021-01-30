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

import model.User;
import services.accountManagement.interfaces.RegistrationModule;

/**
 * Servlet implementation class EditProfile.
 * This servlet is used to edit profile info.
 */
@WebServlet("/EditProfile")
public class EditProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/accountManagement/interfaces/RegistrationModule")
	private RegistrationModule regModule;
	
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

		// GET AND PARSE ALL PARAMETERS FROM REQUEST
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
		if(telephoneNum != null) if(!telephoneNum.isBlank()) Integer.parseInt(telephoneNum);
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		
		// CHECK THAT THERE IS SOMETHING TO EDIT, CORRECTNESS AND APPLY ALL NULL VALUES
		isBadRequest = isBadRequest || (username == null && email == null && password == null && telephoneNum == null);
		
		if(username != null) {
			if(!username.isEmpty() && username.isBlank()) isBadRequest = true;
			else username = user.getUsername();
		} else username = user.getUsername();
		
		if(email != null) {
			if(!email.isEmpty() && email.isBlank()) isBadRequest = true;
			else email = user.getEmail();
		} else email = user.getEmail();
		
		if(password != null) {
			if(!password.isEmpty() && password.isBlank()) isBadRequest = true;
			else password = user.getPassword();
		} else password = user.getPassword();
		
		if(telephoneNum != null) {
			if(!telephoneNum.isEmpty() && telephoneNum.isBlank()) isBadRequest = true;
			else telephoneNum = user.getTelephoneNumber();
		} else telephoneNum = user.getTelephoneNumber();
		
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
			
		// UPDATE USER DETAILS AND SESSION
		
		try {
			regModule.editProfile(user.getIduser(), telephoneNum, username, password, email);
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			user.setTelephoneNumber(telephoneNum);
			session.setAttribute("user", user);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to update profile");
			return;
		}

		// RETURN THE USER TO THE RIGHT VIEW
		
		String successMessage = "Your edits were applied successfully!";
		String path = "outcome_page.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("message", successMessage);
		response.sendRedirect(path);
	}
	
	public void destroy() {
		
	}
}
