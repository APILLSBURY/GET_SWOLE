package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// BaseServlet serves as base for other servlets of app
// This version is taken from MyRuns6
public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	boolean DEBUG = true;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		// If GET calls are allowed, doPost
		if (DEBUG) {
			// do Post and catch Exceptions
			doPost(req, resp);
		}
		else {
			super.doGet(req, resp);
		}
	}
	
	protected String getParameter(HttpServletRequest req, String parameter) throws ServletException {
		
		String value = req.getParameter(parameter);
		
		if (isEmptyOrNull(value)) {
			
			if (DEBUG) {
				
				// Build a new string
				StringBuilder parameters = new StringBuilder();
				
				// Get the list of parameter names
				Enumeration<String> names = req.getParameterNames();
					
				String name;
				String param;
				
				// Loop through all the parameters (while names.hasMoreElements())
				while(names.hasMoreElements()) {
					
					// Append the name and parameter to the string builder 
					name = names.nextElement();
					param = req.getParameter(name);
					parameters.append(name).append("=").append(param).append("\n");
					
				}
	
			}
			throw new ServletException("Parameter not found");
			
		}
		return value.trim();
	}
	
	protected String getParameter(HttpServletRequest req, String parameter, String defaultValue)  {
		
		// Get the parameter 
		String value = req.getParameter(parameter);
		
		if (isEmptyOrNull(value)) {

			value = defaultValue;

		}
		
		return value.trim();

	}
	
	protected void setSuccess(HttpServletResponse resp) {
		setSuccess(resp, 0);
	}
	
	protected void setSuccess(HttpServletResponse resp, int size) {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");
		resp.setContentLength(size);
	}
	
	protected boolean isEmptyOrNull(String value) {
		return (value == null || value.trim().length() == 0);
	}

}
