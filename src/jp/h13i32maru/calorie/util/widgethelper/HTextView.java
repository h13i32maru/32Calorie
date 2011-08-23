package jp.h13i32maru.calorie.util.widgethelper;

import android.app.Activity;
import android.widget.TextView;

public class HTextView {

	public static void set(Activity activity, int id, int value){
		TextView t = (TextView)activity.findViewById(id);
		t.setText("" + value);
	}
	
	public static int get(Activity activity, int id, int defValue){
		TextView t = (TextView)activity.findViewById(id);
		String value = t.getText().toString();
		if(value == null || value.length() == 0){
			return defValue;
		}
		
		return Integer.parseInt(value);
	
	}
}
