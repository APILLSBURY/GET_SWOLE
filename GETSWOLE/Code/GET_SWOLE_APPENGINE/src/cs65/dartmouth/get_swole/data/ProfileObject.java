package cs65.dartmouth.get_swole.data;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;



public class ProfileObject {
	private String regId;
	
	private String pictureString1;
	private String pictureString2;
	
	private String firstName;
	private String lastName;
	private String hometown;
	private String sport;
	
	private int gender;
	private double height; // stored in inches
	private double weight; // stored in lb
	private String bio; 
	private String email;
	private String phone;

	public ProfileObject() {
		regId = "";
		
		pictureString1 = "";
		pictureString2 = "";
		
		firstName = "";
		lastName = "";
		hometown = "";
		sport = "";
		
		gender = -1;
		height = 0;
		weight = 0;
		bio = "";
		email = "";
		phone = "";
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
	
	public void setGender(int ge) {
		gender = ge;
	}
	
	public void setHeight(double feet, double in) {
		height = (double) (feet * 12 + in);
	}
	
	public void setWeight(double we) {
		weight = we;
	}
	
	public void setBio(String info) {
		bio = info;
	}
	
	public void setEmail(String em) {
		email = em;
	}
	
	public void setPhone(String ph) {
		phone = ph;
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
	
	public int getGender() {
		return gender;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public String getBio() {
		return bio;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
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
			
			gender = obj.getInt("gender");
			height = obj.getDouble("height");
			weight = obj.getDouble("weight");
			bio = obj.getString("bio");
			email = obj.getString("email");
			phone = obj.getString("phone");

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
			
			obj.put("gender", gender);
			obj.put("height", height);
			obj.put("weight", weight);
			obj.put("bio", bio);
			obj.put("email", email);
			obj.put("phone", phone);
			
			return obj;
		}
		catch (JSONException e) {
			return null;
		}
	}
}