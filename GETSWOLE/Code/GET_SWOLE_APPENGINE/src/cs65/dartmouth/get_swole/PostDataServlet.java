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


public class PostDataServlet extends BaseServlet {
	
	private Logger mLogger = Logger.getLogger(Datastore.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	
		mLogger.log(Level.INFO, "Trying to Post Data");
		
		// Get parameters for regId and data
		String regId = getParameter(req, "regId");
		
		if (regId == null)
			mLogger.log(Level.INFO, "regId is null!");
		
		String data = getParameter(req, "data");
		
		if (data == null) {
			mLogger.log(Level.INFO, "Data supposedly null");
		}
		
		// Save data to datastore
		try {
			Datastore.saveData(regId, data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// setSuccess
		setSuccess(resp);

	}		
		
}
