package jp.h13i32maru.calorie.activity;

import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.common.CalorieBarBuilder;
import jp.h13i32maru.calorie.db.CalorieDAO;
import jp.h13i32maru.calorie.db.CalorieInfo;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.multibar.MultiBar;
import jp.h13i32maru.calorie.widget.CalorieWidget;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String FIRST_LAUNDH = "first_launch";
    
    private MultiBar mMultiBar;
    private CalorieDAO mCalorieDAO;
    private List<CalorieInfo> mCalorieInfoList;
    private List<View> mTypeAreaViewList = new ArrayList<View>();
    private int mSelectedCalorie = -1;
    private int mDelta;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        
        mMultiBar = (MultiBar)findViewById(R.id.multi_bar);
        mCalorieDAO = CalorieDAO.getInstance(this);
        mCalorieInfoList = mCalorieDAO.getLastCalorieInfoList();
        
        CalorieBarBuilder.loadConfig(mMultiBar);
        CalorieBarBuilder.loadData(mMultiBar, mCalorieInfoList);
        
        initCategoryArea();
        setSummary();

        mMultiBar.setOnProgressListener(new MultiBar.OnProgressListener() {
    		@Override
    		public void progress(int index, int value, int delta, int totalValue) {
    			CalorieInfo c = mCalorieInfoList.get(index);
    			c.setValue(value);
    			
    			TextView t = (TextView)mTypeAreaViewList.get(index).findViewById(R.id.type_value);
    			t.setText("" + value + " cal");
    			
    			mDelta += delta;
    			/*
    			t = (TextView)mTableRowCalorieInfoList.get(index).findViewById(R.id.calorie_delta);
                String sign = (mDelta > 0 ? "+" : "");
    			t.setText("" + sign + mDelta);
    			*/
    			setSummary();
    		}
    	});
        
        findViewById(R.id.summary).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                v.setBackgroundResource(R.drawable.summary_bg_selected);
                v.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        v.setBackgroundResource(R.drawable.summary_bg);
                    }
                }, 100);
                
                Intent intent = new Intent(MainActivity.this, LineChartActivity.class);
                startActivity(intent);
            }
        });
        
        Pref pref = Pref.getInstance(this);
        if(pref.getBoolean(FIRST_LAUNDH, true)){
            pref.putBoolean(FIRST_LAUNDH, false);
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        
        mCalorieDAO.update(mCalorieInfoList);
        CalorieWidget.update(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode){
    	case C.req.config:
    		CalorieBarBuilder.loadConfig(mMultiBar);
    		initCategoryArea();
    		break;
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem item;
        
        item = menu.add(Menu.NONE , C.menu.clear, Menu.NONE , getString(R.string.menu_clear));
        item.setIcon(android.R.drawable.ic_menu_delete);
        
        item = menu.add(Menu.NONE, C.menu.settings, Menu.NONE, getString(R.string.menu_settings));
        item.setIcon(android.R.drawable.ic_menu_preferences);
        
        item = menu.add(Menu.NONE, C.menu.line_chart, Menu.NONE, getString(R.string.menu_line_chart));
        item.setIcon(android.R.drawable.ic_menu_more);
        
        item = menu.add(Menu.NONE, C.menu.help, Menu.NONE, getString(R.string.menu_help));
        item.setIcon(android.R.drawable.ic_menu_help);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	int id = item.getItemId();
        switch(id){
        case C.menu.clear:
            mCalorieDAO.update(mCalorieInfoList);
            mCalorieInfoList = mCalorieDAO.createNew();
        	CalorieBarBuilder.loadData(mMultiBar, mCalorieInfoList);
        	initCategoryArea();
            break;
        case C.menu.settings:
        {
        	Intent intent = new Intent(this, ConfigActivity.class);
        	startActivityForResult(intent, C.req.config);
        }
        	break;
        case C.menu.help:
        {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
            break;
        case C.menu.line_chart:{
            Intent intent = new Intent(this, LineChartActivity.class);
            startActivity(intent);
        }
            break;
        default:
        	return false;
        }
        
        return true;
    }

    protected void initCategoryArea(){
        mTypeAreaViewList.clear();
        
        ViewGroup parent = (ViewGroup)findViewById(R.id.category_area);
        parent.removeAllViews();
        
        LayoutInflater inflater = getLayoutInflater();

        for(int i = 0; i < mCalorieInfoList.size(); i++){
            CalorieInfo ci = mCalorieInfoList.get(i);
            View view = inflater.inflate(R.layout.category, null);
            GradientDrawable d = (GradientDrawable)getResources().getDrawable(R.drawable.category_band_bg);
            d.setColor(ci.getColor());
            view.findViewById(R.id.category_band_area).setBackgroundDrawable(d);
            
            TextView t = (TextView)view.findViewById(R.id.type_name);
            t.setText(getString(ci.getName()));
            
            t = (TextView)view.findViewById(R.id.type_value);
            t.setText(ci.getValue() + " cal");
            
            View decButton = view.findViewById(R.id.dec_button);
            decButton.setOnTouchListener(new OnTouchListener(i, -10, 30));
            
            View incButton = view.findViewById(R.id.inc_button);
            incButton.setOnTouchListener(new OnTouchListener(i, 10, 30));
            parent.addView(view);
            
            mTypeAreaViewList.add(view);
        }
    }
    
    protected void setSummary(){
        int totalValue = mMultiBar.getTotalBarValue();
        TextView t;
        
        t = (TextView)findViewById(R.id.total_text);
        t.setText(getString(R.string.summary_total) + " " + totalValue + " cal");
        
        int remain = mMultiBar.getTarget() - totalValue;
        t = (TextView)findViewById(R.id.remain_text);
        t.setText(getString(R.string.summary_remain) + " " + remain + " cal");
        
        t.setTextColor(CalorieBarBuilder.getRemainColor(remain, this));
    }
    
    protected void selectCalorie(int index){
        if(index == -1){
            mDelta = 0;
    		mSelectedCalorie = -1;
    	}
    	else{
    	    if(mSelectedCalorie != index){
    	        mDelta = 0;
    	    }
    		mMultiBar.setBarSelected(index);
    		mSelectedCalorie = index;
    	}
    }
    
    /**
     * バーの値を増減させるためのボタンにセットするリスナー
     * @author h13i32maru
     */
    private class OnTouchListener implements View.OnTouchListener{
    	int mId;
        int mDelta;
    	int mInterval;
    	Toast mToast;
    	public OnTouchListener(int id, int delta, int interval){
    		mId = id;
    	    mDelta = delta;
    		mInterval = interval;
    		mToast = Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.unselected_calorie), Toast.LENGTH_SHORT);
    	}
    	
    	@Override
    	public boolean onTouch(View v, MotionEvent event) {
    	    selectCalorie(mId);
    	    
    		switch(event.getAction()){
    		case MotionEvent.ACTION_DOWN:
	          if(mSelectedCalorie == -1){
	                mToast.show();
	                return true;
	            }
    			
    			mMultiBar.start(mSelectedCalorie, mDelta, mInterval);
    			return true;
    		case MotionEvent.ACTION_UP:
    			mMultiBar.stop();
    			return true;
    		}
    		return false;
    	}
    	
    }
}