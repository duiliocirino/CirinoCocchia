package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Logout.
 * This servlet is used by users that want to end the current session.
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
     * Class constructor.
     * @see HttpServlet#HttpServlet()
     */
	public Logout() {
		super();
	}

	/**
	 * This method clears the current session and redericts the user to the login page.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		String path = getServletContext().getContextPath() + "/login.html";
		response.sendRedirect(path);
	}
	
	/**
	 * This method redirect to the {@link controllers.GetGroceryData#doGet(HttpServletRequest, HttpServletResponse)} method of this class.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
