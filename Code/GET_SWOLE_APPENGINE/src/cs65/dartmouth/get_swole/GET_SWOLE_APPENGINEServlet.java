package cs65.dartmouth.get_swole;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class GET_SWOLE_APPENGINEServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
