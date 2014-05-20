package cs65.dartmouth.get_swole;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

public class ApiKeyInitializer implements ServletContextListener {

	public static String API_KEY = "AIzaSyDCfNMlghkn9GtcNjecs-2AjLbPxzk-paQ";
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		// Get the datastore service
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		// Create the key
		Key k = KeyFactory.createKey("Settings", "MyKey");
		
		Entity entity;
		
		// Try: get the datastore entity at key - datastore.get(k) 
		try {
			entity = datastore.get(k);
		}
		
		catch (EntityNotFoundException e) {
			
			// If the entity doesn’t exist, create new one
			entity = new Entity(k);
			
			// Because you cannot change entities on the local server, you need to hardcode your API key if you are running app locally
			entity.setProperty("your api key", "YOUR API KEY text from GAE");
			
			// Put the entity into the database
			datastore.put(entity);
		}
		
		// Get the access key 
		String string = (String) entity.getProperty("API KEY");
		
		// Set the attribute of servlet from event to be the access key
		event.getServletContext().setAttribute("your access key", "accessKey");

	}

}
