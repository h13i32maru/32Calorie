package jp.h13i32maru.calorie.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CalorieInfo {
	private static final String JSON_KEY_NAME = "name";
	private static final String JSON_KEY_COLOR = "color";
	private static final String JSON_KEY_VALUE = "value";
	
	private String mName;
	private int mColor;
	private int mValue;

	public static String encodeJSON(CalorieInfo c){
		JSONObject obj = new JSONObject();
		try {
			obj.put(JSON_KEY_NAME, c.getName());
			obj.put(JSON_KEY_VALUE, c.getValue());
			obj.put(JSON_KEY_COLOR, c.getColor());
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static CalorieInfo decodeJSON(String json){
		try {
			JSONObject obj = new JSONObject(json);
			String name = obj.getString(JSON_KEY_NAME);
			int value = obj.getInt(JSON_KEY_VALUE);
			int color = obj.getInt(JSON_KEY_COLOR);
			return new CalorieInfo(name, value, color);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public CalorieInfo(String name, int value, int color){
		mName = name;
		mValue = value;
		mColor = color;
	}
	
	public String getName(){
		return mName;
	}
	
	public int getValue(){
		return mValue;
	}
	
	public int getColor(){
		return mColor;
	}
	
	public void setName(String name){
	    mName = name;
	}
	
	public void setValue(int value){
		mValue = value;
	}
}
