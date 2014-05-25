package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import cs65.dartmouth.get_swole.data.ProfileObject;
import cs65.dartmouth.get_swole.data.Workout;


public class GetFriendWorkouts extends BaseServlet {
	
	private Logger mLogger = Logger.getLogger(Datastore.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
		mLogger.log(Level.INFO, "Trying to get friend workouts");
		
		// Get parameters for regId and data
		String regId = getParameter(req, "regId");
		String friendRegId = getParameter(req, "getWorkouts");
		
		// Get Friend Profiles
		if (!friendRegId.isEmpty()) {
			
			ArrayList<Workout> workouts = (ArrayList<Workout>) Datastore.getWorkouts(friendRegId, null);

			JSONArray jsonArray = new JSONArray();
			
			for (Workout workout : workouts) {		
				jsonArray.put(workout.toJSONObject());
			}
			
			// Convert the jsonArray to string
			String jsonArrayString = jsonArray.toString();
			
			mLogger.log(Level.INFO, "All Workouts: " + jsonArrayString);
			
			PrintWriter out = resp.getWriter();
			out.append(jsonArrayString);
			
		}

	}		
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}

		
}
