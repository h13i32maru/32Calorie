package jp.h13i32maru.calorie.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;

public class CalorieInfoDAO {
	private static final String[] mNameArray = {"朝食", "昼食", "夕食", "夜食", "間食"};
	private static final int[] mColorArray = {
			Color.argb(0xaa, 0xff, 0x00, 0x00),
			Color.argb(0xaa, 0x00, 0xff, 0x00),
			Color.argb(0xaa, 0x00, 0x00, 0xff),
			Color.argb(0xaa, 0x88, 0x88, 0x00),
			Color.argb(0xaa, 0x55, 0x55, 0x55)
	};
	private static final String PREF_KEY = "calorie_info";
	
	private Pref mPref;
	
	protected List<CalorieInfo> getInitialList(){
		List<CalorieInfo> calorieInfoList = new ArrayList<CalorieInfo>();
		for(int i = 0; i < mNameArray.length; i++){
			calorieInfoList.add(new CalorieInfo(mNameArray[i], 0, mColorArray[i]));
		}
		return calorieInfoList;
	}
	
	public CalorieInfoDAO(Context context){
		mPref = Pref.getInstance(context);
	}
	
	public List<CalorieInfo> getList(){
		JSONArray array = mPref.getJSONArray(PREF_KEY, null);
		if(array == null || array.length() == 0){
			return getInitialList();
		}
		
		List<CalorieInfo> list = new ArrayList<CalorieInfo>();
		for(int i = 0; i < array.length(); i++){
			try {
				CalorieInfo c = CalorieInfo.decodeJSON(array.getString(i));
				list.add(c);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return list;
	}
	
	public void save(List<CalorieInfo> calorieInfoList){
		JSONArray array = new JSONArray();
		for(CalorieInfo c: calorieInfoList){
			array.put(CalorieInfo.encodeJSON(c));
		}
		
		mPref.putJSONArray(PREF_KEY, array);
	}
	
	public void clear(){
		mPref.remove(PREF_KEY);
	}
}
