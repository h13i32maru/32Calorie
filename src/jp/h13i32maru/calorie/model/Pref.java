package jp.h13i32maru.calorie.model;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    
    private static final String PREF_NAME = "pref";
    
    private static Pref instance = null;
    
    private SharedPreferences pref;
    
    public static Pref getInstance(Context c){
        if(instance == null){
            instance = new Pref(c);
        }
        return instance;
    }
    
    private Pref(Context c){
        pref = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public boolean remove(String key){
        return pref.edit().remove(key).commit();
    }

    public String getString(String key, String _default){
        return pref.getString(key, _default);
    }
    
    public boolean putString(String key, String value){
        return pref.edit().putString(key, value).commit();
    }
        
    public boolean getBoolean(String key, Boolean _default){
        return pref.getBoolean(key, _default);
    }
    
    public boolean putBoolean(String key, Boolean value){
        return pref.edit().putBoolean(key, value).commit();
    }
    
    public int getInt(String key, int defValue){
    	return pref.getInt(key, defValue);
    }
    
    public boolean putInt(String key, int value){
    	return pref.edit().putInt(key, value).commit();
    }
    
    public JSONArray getJSONArray(String key, JSONArray defValue){
    	if(defValue == null){
    		defValue = new JSONArray();
    	}
    	
    	String json = pref.getString(key, defValue.toString());
    	
    	try {
			return new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return defValue;
    }
    
    public boolean putJSONArray(String key, JSONArray array){
    	String json = array.toString();
    	return pref.edit().putString(key, json).commit();
    }
}