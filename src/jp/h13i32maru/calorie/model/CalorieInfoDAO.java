package jp.h13i32maru.calorie.model;

import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.R;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;

public class CalorieInfoDAO {
	private String[] mNameArray = new String[5];
	private static final int[] mColorArray = {
	    Color.argb(0xff, 0xf0, 0x51, 0x51),
	    Color.argb(0xff, 0xff, 0xff, 0x5e),
	    Color.argb(0xff, 0x83, 0xf1, 0x52),
	    Color.argb(0xff, 0x52, 0x94, 0xf1),
	    Color.argb(0xff, 0xe2, 0x74, 0xf4)
	};
	private static final String PREF_KEY = "calorie_info";
	
	private Pref mPref;
	
	protected CalorieInfoList getInitialList(){
		CalorieInfoList calorieInfoList = new CalorieInfoList();
		for(int i = 0; i < mNameArray.length; i++){
			calorieInfoList.add(new CalorieInfo(mNameArray[i], 0, mColorArray[i]));
		}
		return calorieInfoList;
	}
	
	public CalorieInfoDAO(Context context){
	    mNameArray[0] = context.getString(R.string.calorie_name_breakfast);
	    mNameArray[1] = context.getString(R.string.calorie_name_lunch);
	    mNameArray[2] = context.getString(R.string.calorie_name_dinner);
	    mNameArray[3] = context.getString(R.string.calorie_name_snack);
	    mNameArray[4] = context.getString(R.string.calorie_name_other);
	    
		mPref = Pref.getInstance(context);

		//今後アプリのバージョンが上がったときに、保存されている値をアップデートするのに使う
		mPref.setVersionCode();
	}
	
	public CalorieInfoList getList(){
		JSONArray array = mPref.getJSONArray(PREF_KEY, null);
		if(array == null || array.length() == 0){
			return getInitialList();
		}
		
		CalorieInfoList list = new CalorieInfoList();
		for(int i = 0; i < array.length(); i++){
			try {
				CalorieInfo c = CalorieInfo.decodeJSON(array.getString(i));
				c.setName(mNameArray[i]);
				c.setColor(mColorArray[i]);
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
