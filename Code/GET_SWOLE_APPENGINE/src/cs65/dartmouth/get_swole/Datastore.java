package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.api.datastore.Transaction;


import cs65.dartmouth.get_swole.data.EntityConverter;
import cs65.dartmouth.get_swole.data.ProfileObject;
import cs65.dartmouth.get_swole.data.Workout;


// Simple implementation of datastore
// Not persistent (will lose data when app is restarted) nor threadsafe
public final class Datastore {
	
	private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder.withPrefetchSize(1000).chunkSize(1000);
	private static final Logger logger = Logger.getLogger(Datastore.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	private static final String ENTITY_KIND_DEVICE = "Device";
	private static final String ENTITY_KIND_PROFILE = "Profile";
	//private static final String ENTITY_KIND_FRIENDS = "Friends";
	private static final String ENTITY_KIND_WORKOUT = "Workout";
	private static final String DEVICE_REG_ID_PROPERTY = "regId";

	public static void register(String regId) {
		Entity entity = new Entity(ENTITY_KIND_DEVICE, regId);
		entity.setProperty(DEVICE_REG_ID_PROPERTY, regId);
		datastore.put(entity);
		
		logger.log(Level.INFO, "Registered the device");
	}

	public static void unregister(String regId) {
		Key k = KeyFactory.createKey(ENTITY_KIND_DEVICE, regId);
		datastore.delete(k);
	}
	
	public static List<String> getDevices() {
		List<String> devices = new ArrayList<String>();

		Query query = new Query(ENTITY_KIND_DEVICE);
		Iterable<Entity> entities = datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);

		for (Entity entity : entities) {
			String device = (String) entity.getProperty(DEVICE_REG_ID_PROPERTY);
			devices.add(device);
		}

		return devices;
	}
	
	public static Entity findDeviceByRegId(String regId) {
		Entity entity;
		Key k = getRegDeviceKey(regId);
		
		try {
			entity = datastore.get(k);
		}
		catch (Exception e) {
			entity = null;
		}
		
		return entity;
	}

	public static Key getRegDeviceKey(String regId) {
		return KeyFactory.createKey(ENTITY_KIND_DEVICE, regId);
	}
	
	public static void saveProfile(String regId, String profile) throws JSONException {
		
		logger.log(Level.INFO, "saveProfile");
		
		JSONArray profileJSONArray = null;
		Transaction txn = datastore.beginTransaction();
		
		logger.log(Level.INFO, profile);
		
		try {
			
			// Get the JSONArraydata
			profileJSONArray = new JSONArray(profile);
			
			logger.log(Level.INFO, profileJSONArray.toString());
			
			Entity parentEntity = findDeviceByRegId(regId);
			
			// Check that device is registered properly
			if (parentEntity == null)
				return;
			
			// Clear Profile
			List<ProfileObject> oldRecord = getProfiles(regId, "check");
			
			for (ProfileObject entry : oldRecord) {
				deleteProfile(regId, entry.getId());
			}
			
			ProfileObject entry = new ProfileObject();
			entry.fromJSONObject(profileJSONArray.getJSONObject(0));
			
			logger.log(Level.INFO, entry.getFirstName() + " " + entry.getLastName());
			
			Entity profileEntity = EntityConverter.toEntityfromProfile((ProfileObject) entry, ENTITY_KIND_PROFILE, parentEntity.getKey());
			
			datastore.put(profileEntity);
			
			txn.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			
			if (txn.isActive())
				txn.rollback();
		}

	}
	
	public static List<ProfileObject> getProfiles(String regId, String check) {
		
		logger.log(Level.INFO, "getProfiles");
		
		
		// Get existing data from datastore
		
		// Create new arraylist
		List<ProfileObject> result = new ArrayList<ProfileObject>();
		
		// Set up query 
		if (check != null) {
			
			Query regQuery = new Query(ENTITY_KIND_PROFILE);
			regQuery.setAncestor(getRegDeviceKey(regId));

			Iterable<Entity> regEntities = datastore.prepare(regQuery).asIterable(DEFAULT_FETCH_OPTIONS);
			
			for (Entity regEntity : regEntities)
				result.add(EntityConverter.fromEntitytoProfile(regEntity));
			
		}
		
		else if (check == null) {
			
			Query query = new Query(ENTITY_KIND_DEVICE);
			query.setKeysOnly();
			
			Iterable<Entity> entities = datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
			
			for (Entity entity : entities) {
				
				logger.log(Level.INFO, "regId: " + "Device(\"" + regId + "\")" + "\ndevice: " + entity.getKey().toString());
				
				if (entity.getKey().toString().equals("Device(\"" + regId + "\")")) {
				
					Query entityQuery = new Query(ENTITY_KIND_PROFILE);
						
					query.setAncestor(entity.getKey());
						
					Iterable<Entity> profileEntities = datastore.prepare(entityQuery).asIterable(DEFAULT_FETCH_OPTIONS);
					
					for (Entity profileEntity : profileEntities) {
						
						if (!profileEntity.getParent().toString().equals("Device(\"" + regId + "\")"))
							result.add(EntityConverter.fromEntitytoProfile(profileEntity));
					}
				}
			}
			
		}
		
		return result;
		
	}
	
	public static void deleteProfile(String regId, String entryId) {
		
		// Getting the key
		Key k = KeyFactory.createKey(getRegDeviceKey(regId), ENTITY_KIND_PROFILE, entryId);
		
		// Call datastore.delete
		datastore.delete(k);

	}
	
	// save Workouts
	public static void saveWorkouts(String regId, String workouts) throws JSONException { 
		
		logger.log(Level.INFO, "saveWorkouts");
		
		JSONArray workoutEntryList = null;
		Transaction txn = datastore.beginTransaction();
		
		logger.log(Level.INFO, workouts);
		
		try {
			
			// Get the JSONArraydata
			workoutEntryList = new JSONArray(workouts);
			
			Entity parentEntity = findDeviceByRegId(regId);
			
			// Check that device is registered properly
			if (parentEntity == null)
				return;
			
			// Clear History of workouts for profile
			List<Workout> oldRecord = getWorkouts(regId, "check");
			
			for (Workout entry : oldRecord) {
				deleteWorkout(regId, entry.getRegId() + ":" + entry.getName() + "," + entry.getId());
			}
			
			// Add the history entries to the datastore
			for (int i = 0; i < workoutEntryList.length(); i++) {
				Workout entry = new Workout();
				entry.fromJSONObject(workoutEntryList.getJSONObject(i));
				
				Entity historyEntity = EntityConverter.toEntityfromWorkout((Workout) entry, ENTITY_KIND_WORKOUT, parentEntity.getKey());
				
				datastore.put(historyEntity);
			}
			
			txn.commit();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			
			if (txn.isActive())
				txn.rollback();
		}
		

	}
	
	public static List<Workout> getWorkouts(String regId, String check) {
		
		logger.log(Level.INFO, "getWorkouts");
		
		
		// Get existing data from datastore
		
		// Create new arraylist
		List<Workout> result = new ArrayList<Workout>();
		
		// Set up query 
		if (check != null) {
			
			Query regQuery = new Query(ENTITY_KIND_WORKOUT);
			regQuery.setAncestor(getRegDeviceKey(regId));

			Iterable<Entity> regEntities = datastore.prepare(regQuery).asIterable(DEFAULT_FETCH_OPTIONS);
			
			for (Entity regEntity : regEntities)
				result.add(EntityConverter.fromEntitytoWorkout(regEntity));
			
		}
		
		else if (check == null) {
			
			Query query = new Query(ENTITY_KIND_DEVICE);
			query.setKeysOnly();
			
			Iterable<Entity> entities = datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
			
			for (Entity entity : entities) {
				
				if (entity.getKey().toString().equals("Device(\"" + regId + "\")")) {
				
					Query entityQuery = new Query(ENTITY_KIND_WORKOUT);
						
					query.setAncestor(entity.getKey());
						
					Iterable<Entity> workoutEntities = datastore.prepare(entityQuery).asIterable(DEFAULT_FETCH_OPTIONS);
					
					for (Entity workoutEntity : workoutEntities) {
						
						// Should only return workouts of the same regId
						if (workoutEntity.getParent().toString().equals("Device(\"" + regId + "\")"))
							result.add(EntityConverter.fromEntitytoWorkout(workoutEntity));
					}
				}
			}
			
		}
		
		return result;
		
	}
	
	public static void deleteWorkout(String regId, String entryId) {
		
		// Getting the key
		Key k = KeyFactory.createKey(getRegDeviceKey(regId), ENTITY_KIND_WORKOUT, entryId);
		
		// Call datastore.delete
		datastore.delete(k);

	}
	
}
