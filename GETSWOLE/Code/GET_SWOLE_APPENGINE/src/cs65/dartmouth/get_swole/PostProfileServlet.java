package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;


public class PostProfileServlet extends BaseServlet {
	
	private Logger mLogger = Logger.getLogger(Datastore.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
		mLogger.log(Level.INFO, "Trying to Post Profile");
		
		// Get parameters for regId and data
		String regId = getParameter(req, "regId");
		
		String profile = getParameter(req, "profile");
		
		// Save Workouts
		if (!profile.isEmpty()) {
			
			mLogger.log(Level.INFO, "Workouts are being added");
			
			try {
				Datastore.saveProfile(regId, profile);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
		// setSuccess
		setSuccess(resp);

	}		
		
}
