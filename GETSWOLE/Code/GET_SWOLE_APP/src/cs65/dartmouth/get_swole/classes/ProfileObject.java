package cs65.dartmouth.get_swole.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileObject {
	public String regId;
	
	public String firstName;
	public String lastName;
	public String hometown;
	public String sport;
	
	public String bio;

	public ProfileObject() {
		regId = "";
		firstName = "";
		lastName = "";
		hometown = "";
		sport = "";
	}
	
	// Setting methods
	
	public void setId(String regID) {
		regId = regID;
	}
	
	public void setName(String first, String last) {
		firstName = first;
		lastName = last;
	}
	
	public void setHometown(String town) {
		hometown = town;
	}
	
	public void setSport(String sp) {
		sport = sp;
	}
	
	
	public void setBio(String info) {
		bio = info;
	}
	
	// Getting methods
	
	public String getId() {
		return regId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getHometown() {
		return hometown;
	}
	
	public String getSport() {
		return sport;
	}
	
	
	public String getBio() {
		return bio;
	}
	
	// GCM JSONOBJECT HANDLING
	
	public JSONObject fromJSONObject(JSONObject obj) {	
		try {
			
			regId = obj.getString("regId");
			
			firstName = obj.getString("firstName");
			lastName = obj.getString("lastName");
			hometown = obj.getString("hometown");
			sport = obj.getString("sport");

		}
		catch (JSONException e) {
			return null;
		}
		return obj;
	}
				
	public JSONObject toJSONObject() {
		
		try {
			
			JSONObject obj = new JSONObject();
			
			obj.put("regId", regId);
			
			obj.put("firstName", firstName);
			obj.put("lastName", lastName);
			obj.put("hometown", hometown);
			obj.put("sport", sport);
			
			return obj;
		}
		catch (JSONException e) {
			return null;
		}
	}
}