package cs65.dartmouth.get_swole.data;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;

import android.util.Base64;
import android.util.Log;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;


public class EntityConverter {

	public static ProfileObject fromEntitytoProfile(Entity entity) {
		
		ProfileObject profile = new ProfileObject();
		
		profile.setId((String) entity.getProperty("regId"));
		
		profile.setPictureString1( ((Text) entity.getProperty("pictureString1")).getValue().toString());
		profile.setPictureString2( ((Text) entity.getProperty("pictureString2")).getValue().toString());
		
		profile.setName((String) entity.getProperty("firstName"), (String) entity.getProperty("lastName"));
		profile.setHometown((String) entity.getProperty("hometown"));
		profile.setSport((String) entity.getProperty("sport"));
	
		return profile;
	}
	
	public static Entity toEntityfromProfile(ProfileObject entry, String kind, Key parentKey) {
		
		Entity entity = new Entity(kind, entry.getId(), parentKey);
		
		entity.setProperty("regId", entry.getId());
		
		entity.setProperty("pictureString1", new Text(entry.getPictureString1()));
		entity.setProperty("pictureString2", new Text(entry.getPictureString2()));

		entity.setProperty("firstName", entry.getFirstName());
		entity.setProperty("lastName", entry.getLastName());
		entity.setProperty("hometown", entry.getHometown());
		entity.setProperty("sport", entry.getSport());
		
		return entity;
		
	}
	
	public static Workout fromEntitytoWorkout(Entity entity) {
		
		Workout workout = new Workout();
		
		workout.setRegId((String) entity.getProperty("regId"));
		workout.setId((long) entity.getProperty("id"));
		workout.setName((String) entity.getProperty("name"));
		workout.setNotes((String) entity.getProperty("notes"));
		workout.setExerciseList( (String) ((Text) entity.getProperty("exerciseList")).getValue().toString() );
	
		return workout;
	}
	
	public static Entity toEntityfromWorkout(Workout entry, String kind, Key parentKey) {
		
		Entity entity = new Entity(kind, entry.getRegId() + ":" + entry.getName() + "," + entry.getId(), parentKey);
		
		entity.setProperty("regId", entry.getRegId());
		entity.setProperty("id", entry.getId());
		entity.setProperty("name", entry.getName());
		entity.setProperty("notes", entry.getNotes());
		entity.setProperty("exerciseList", new Text(entry.getExerciseListJSONString()));
		
		return entity;
		
	}

	
}
