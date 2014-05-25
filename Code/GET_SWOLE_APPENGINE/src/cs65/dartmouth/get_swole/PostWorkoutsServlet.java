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


public class PostWorkoutsServlet extends BaseServlet {
	
	private static final long serialVersionUID = 1L;
	private Logger mLogger = Logger.getLogger(Datastore.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
		mLogger.log(Level.INFO, "Trying to Post Workouts");
		
		// Get parameters for regId and data
		String regId = getParameter(req, "regId");
		
		String workouts = getParameter(req, "workouts");
		
		// Save Workouts
		if (!workouts.isEmpty()) {
			
			mLogger.log(Level.INFO, "Workouts are being added");
			
			try {
				Datastore.saveWorkouts(regId, workouts);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
		// setSuccess
		setSuccess(resp);

	}		
		
}
