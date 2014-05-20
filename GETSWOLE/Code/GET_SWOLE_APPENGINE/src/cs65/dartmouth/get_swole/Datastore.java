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
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.api.datastore.Transaction;


// Simple implementation of datastore
// Not persistent (will lose data when app is restarted) nor threadsafe
public final class Datastore {
	
	private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder.withPrefetchSize(1000).chunkSize(1000);
	private static final Logger logger = Logger.getLogger(Datastore.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	private static final String ENTITY_KIND_DEVICE = "Device";
	private static final String ENTITY_KIND_PROFILE = "Profile";
	private static final String ENTITY_KIND_FRIENDS = "Friends";
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
	
//	public static void deleteHistoryEntry(String regId, String entryId) {
//		
//		// Getting the key
//		Key k = KeyFactory.createKey(getRegDeviceKey(regId), ENTITY_KIND_HISTORY_ENTRY, entryId);
//		
//		// Call datastore.delete
//		datastore.delete(k);
//
//	}
	
	// save Data
//	public static void saveData(String regId, String data) throws JSONException { 
//		
//		logger.log(Level.INFO, "saveData");
//		
//		JSONArray historyEntryList = null;
//		Transaction txn = datastore.beginTransaction();
//		
//		logger.log(Level.INFO, data);
//		
//		try {
//			
//			// Get the JSONArraydata
//			historyEntryList = new JSONArray(data);
//			
//			Entity parentEntity = findDeviceByRegId(regId);
//			
//			// Check that device is registered properly
//			if (parentEntity == null)
//				return;
//			
//			// Clear History entry
//			List<HistoryEntry> oldRecord = getHistoryEntry(regId);
//			
//			for (HistoryEntry entry : oldRecord) {
//				deleteHistoryEntry(regId, Long.toString(entry.id));
//			}
//			
//			// Add the history entries to the datastore
//			for (int i = 0; i < historyEntryList.length(); i++) {
//				HistoryEntry entry = new HistoryEntry();
//				entry.fromJSONObject(historyEntryList.getJSONObject(i));
//				
//				logger.log(Level.INFO, "id: "+ entry.id + " datTime: " + entry.dateTime);
//				
//				Entity historyEntity = HistoryEntryEntityConverter.toEntity((HistoryEntry) entry, ENTITY_KIND_HISTORY_ENTRY, parentEntity.getKey());
//				
//				datastore.put(historyEntity);
//			}
//			
//			txn.commit();
//			
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		finally {
//			
//			if (txn.isActive())
//				txn.rollback();
//		}
//		
//
//	}
	
	// getHistoryEntry
//	public static ArrayList<HistoryEntry> getHistoryEntry(String regId) {
//		
//		logger.log(Level.INFO, "getHistoryEntry");
//		// Get existing data from datastore
//		
//		// Create new arraylist
//		ArrayList<HistoryEntry> result = new ArrayList<HistoryEntry>();
//		
//		// Set up query and return history
//		if (regId != null) {
//			
//			logger.log(Level.INFO, "regId is not null in getHistoryEntry");
//			
//			Query regQuery = new Query(ENTITY_KIND_HISTORY_ENTRY);
//			regQuery.setAncestor(getRegDeviceKey(regId));
//			
//			logger.log(Level.INFO, "Set the ancestor");
//			
//			Iterable<Entity> regEntities = datastore.prepare(regQuery).asIterable(DEFAULT_FETCH_OPTIONS);
//			
//			logger.log(Level.INFO, "now got the entities");
//			
//			for (Entity regEntity : regEntities)
//				result.add(HistoryEntryEntityConverter.fromEntity(regEntity));
//			
//			logger.log(Level.INFO, "should have gotten the result");
//			
//		}
//		else if (regId == null) {
//			
//			logger.log(Level.INFO, "regId is null in getHistoryEntry");
//			
//			Query query = new Query(ENTITY_KIND_DEVICE);
//			query.setKeysOnly();
//			
//			Iterable<Entity> entities = datastore.prepare(query).asIterable(DEFAULT_FETCH_OPTIONS);
//			
//			for (Entity entity : entities) {
//				
//				logger.log(Level.INFO, "entity");
//				
//				Query entityQuery = new Query(ENTITY_KIND_HISTORY_ENTRY);
//				
//				query.setAncestor(entity.getKey());
//				
//				Iterable<Entity> historyEntities = datastore.prepare(entityQuery).asIterable(DEFAULT_FETCH_OPTIONS);
//				
//				for (Entity historyEntity : historyEntities) {
//					
//					logger.log(Level.INFO, "Entry");
//					
//					result.add(HistoryEntryEntityConverter.fromEntity(historyEntity));
//				}
//				
//			}
//			
//		}
//		
//		return result;
//		
//	}
	
}
