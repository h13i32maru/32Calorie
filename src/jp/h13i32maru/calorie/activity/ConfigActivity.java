package jp.h13i32maru.calorie.activity;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.util.Text;
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
		int limit = mPref.getInt(C.config.limit, C.config.limit_def_value);
		int max = mPref.getInt(C.config.max, C.config.max_def_value);
		
		Text.set(this, R.id.limit, limit);
		Text.set(this, R.id.max, max);
	}
	
	/**
	 * 設定を書き込みます
	 */
	protected void saveConfig(){
		int limit = Text.get(this, R.id.limit, C.config.limit_def_value);
		int max = Text.get(this, R.id.max, C.config.max_def_value);
		
		mPref.putInt(C.config.limit, limit);
		mPref.putInt(C.config.max, max);
	}
}
