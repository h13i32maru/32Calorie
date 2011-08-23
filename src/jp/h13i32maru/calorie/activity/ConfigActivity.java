package jp.h13i32maru.calorie.activity;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.util.widgethelper.HCheckBox;
import jp.h13i32maru.calorie.util.widgethelper.HTextView;
import android.app.Activity;
import android.os.Bundle;

public class ConfigActivity extends Activity {
	
	private Pref mPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        
        mPref = Pref.getInstance(this);
        
        loadConfig();
	}
	
	@Override
	public void finish(){
		saveConfig();
		
		super.finish();
	}
	
	/**
	 * 設定を読み込みます
	 */
	protected void loadConfig(){
		int goal = mPref.getInt(C.config.goal, C.config.goal_def_value);
	    HTextView.set(this, R.id.goal, goal);

		int max = mPref.getInt(C.config.max, C.config.max_def_value);
		HTextView.set(this, R.id.max, max);
		
		boolean widgetBG = mPref.getBoolean(C.config.widget_background, C.config.widget_background_def_value);
		HCheckBox.set(this, R.id.widget_background, widgetBG);
	}
	
	/**
	 * 設定を書き込みます
	 */
	protected void saveConfig(){
		int goal = HTextView.get(this, R.id.goal, C.config.goal_def_value);
        mPref.putInt(C.config.goal, goal);

		int max = HTextView.get(this, R.id.max, C.config.max_def_value);		
		mPref.putInt(C.config.max, max);
		
		boolean widgetBG = HCheckBox.get(this, R.id.widget_background);
		mPref.putBoolean(C.config.widget_background, widgetBG);
	}
}
