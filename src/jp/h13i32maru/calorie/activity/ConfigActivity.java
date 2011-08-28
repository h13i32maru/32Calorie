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
		int target = mPref.getInt(C.config.target, C.config.target_def_value);
	    HTextView.set(this, R.id.target, target);

		int max = mPref.getInt(C.config.max, C.config.max_def_value);
		HTextView.set(this, R.id.max, max);
		
		boolean widgetBG = mPref.getBoolean(C.config.widget_background, C.config.widget_background_def_value);
		HCheckBox.set(this, R.id.widget_background, widgetBG);
		
		boolean widgetOneColor = mPref.getBoolean(C.config.widget_one_color, C.config.widget_one_color_def_value);
		HCheckBox.set(this, R.id.widget_one_color, widgetOneColor);
	}
	
	/**
	 * 設定を書き込みます
	 */
	protected void saveConfig(){
		int target = HTextView.get(this, R.id.target, C.config.target_def_value);
        mPref.putInt(C.config.target, target);

		int max = HTextView.get(this, R.id.max, C.config.max_def_value);		
		mPref.putInt(C.config.max, max);
		
		boolean widgetBG = HCheckBox.get(this, R.id.widget_background);
		mPref.putBoolean(C.config.widget_background, widgetBG);
	
		boolean widgetOneColor = HCheckBox.get(this, R.id.widget_one_color);
        mPref.putBoolean(C.config.widget_one_color, widgetOneColor);
	}
}
