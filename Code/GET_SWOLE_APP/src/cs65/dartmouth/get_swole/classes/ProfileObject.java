package cs65.dartmouth.get_swole.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileObject {
	private String regId;
	
	private String pictureString1;
	private String pictureString2;
	
	private String firstName;
	private String lastName;
	private String hometown;
	private String sport;
	
	private String bio;

	public ProfileObject() {
		regId = "";
		
		pictureString1 = "";
		pictureString2 = "";
		
		firstName = "";
		lastName = "";
		hometown = "";
		sport = "";
		bio = "";
	}
	
	// Setting methods
	
	public void setId(String regID) {
		regId = regID;
	}
	
	public void setPictureString(String string) {
		if (string.equals("null"))
			setPictureString1(string);
		else {
			setPictureString1(string.substring(0, string.length() / 2));
			setPictureString2(string.substring(string.length() / 2));
		}
	}
	
	public void setName(String first, String last) {
		firstName = first;
		lastName = last;
	}
	
	public void setPictureString1(String name) {
		pictureString1 =  name;
	}
	
	public void setPictureString2(String name) {
		pictureString2 = name;
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
	
	public String getPictureString() {
		return pictureString1 + pictureString2;
	}
	
	public String getPictureString1() {
		return pictureString1;
	}
	
	public String getPictureString2() {
		return pictureString2;
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
			
			pictureString1 = obj.getString("pictureString1");
			pictureString2 = obj.getString("pictureString2");
			
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
			
			obj.put("pictureString1", pictureString1);
			obj.put("pictureString2", pictureString2);
			
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