package jp.h13i32maru.calorie.activity;

import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.CalorieInfo;
import jp.h13i32maru.calorie.model.CalorieInfoDAO;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.multibar.MultiBar;
import jp.h13i32maru.calorie.util.widgethelper.HTextView;
import jp.h13i32maru.calorie.widget.CalorieWidget;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String FIRST_LAUNDH = "first_launch";
    
    private List<CalorieInfo> mCalorieInfoList;
    private List<TableRow> mTableRowCalorieInfoList = new ArrayList<TableRow>();
    private int mSelectedCalorie = -1;
    private MultiBar mMultiBar;
    private CalorieInfoDAO mCalorieInfoDAO;
    private int mDelta;
    
    public static int getRemainColor(int remain){
        if(remain >= 500){
            return Color.WHITE;
        }
        else if(remain >= 1){
            return Color.rgb(0xff, 0x88, 0x88);
        }
        else{
            return Color.RED;
        }
    }
    
    public static void loadConfig(MultiBar multiBar){
        Pref pref = Pref.getInstance(multiBar.getContext());
        int target = pref.getInt(C.config.target, C.config.target_def_value);
        int max = pref.getInt(C.config.max, C.config.max_def_value);
        multiBar.setTarget(target);
        multiBar.setMax(max);
    }
    
   public static List<CalorieInfo> restoreCalorieInfoList(MultiBar multiBar){
       CalorieInfoDAO dao = new CalorieInfoDAO(multiBar.getContext());
       
        multiBar.clearAllBar();
        List<CalorieInfo> calorieInfoList = dao.getList();
        for(CalorieInfo c: calorieInfoList){
            multiBar.addBar(c.getName(), c.getValue(), c.getColor());
        }
        
        return calorieInfoList;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mMultiBar = (MultiBar)findViewById(R.id.multi_bar);
        mCalorieInfoDAO = new CalorieInfoDAO(this);
        
        MainActivity.loadConfig(mMultiBar);
        
        mCalorieInfoList = MainActivity.restoreCalorieInfoList(mMultiBar);
        
        initTableCalorieInfo();
        
        initButton();
        
        mMultiBar.setOnProgressListener(new MultiBar.OnProgressListener() {
    		@Override
    		public void progress(int index, int value, int delta, int totalValue) {
    			CalorieInfo c = mCalorieInfoList.get(index);
    			c.setValue(value);
    			
    			TextView t = (TextView)mTableRowCalorieInfoList.get(index).findViewById(R.id.calorie_value);
    			t.setText("" + value);
    			
    			mDelta += delta;
    			t = (TextView)mTableRowCalorieInfoList.get(index).findViewById(R.id.calorie_delta);
                String sign = (mDelta > 0 ? "+" : "");
    			t.setText("" + sign + mDelta);
    			
    			setSummary();
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
        mCalorieInfoDAO.save(mCalorieInfoList);
        CalorieWidget.update(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode){
    	case C.req.config:
    		MainActivity.loadConfig(mMultiBar);
    		initTableCalorieInfo();
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
        	mCalorieInfoDAO.clear();
        	mCalorieInfoList = MainActivity.restoreCalorieInfoList(mMultiBar);
        	initTableCalorieInfo();
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
        default:
        	return false;
        }
        
        return true;
    }

    protected void initButton(){
    	View incButton = findViewById(R.id.inc_button);
    	incButton.setOnTouchListener(new OnTouchListener(10, 30));
    	
        View incSlowButton = findViewById(R.id.inc_slow_button);
        incSlowButton.setOnTouchListener(new OnTouchListener(10, 100));
        
    	View decButton = findViewById(R.id.dec_button);
    	decButton.setOnTouchListener(new OnTouchListener(-10, 30));
    	
    	View decSlowButton = findViewById(R.id.dec_slow_button);
    	decSlowButton.setOnTouchListener(new OnTouchListener(-10, 100));
    }
    
    protected void initTableCalorieInfo(){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.table_calorie_info);
        tableLayout.removeAllViews();
        
        mTableRowCalorieInfoList.clear();
        mSelectedCalorie = -1;
        
        LayoutInflater inflater = getLayoutInflater();
        for(int i = 0; i < mCalorieInfoList.size(); i++){
        	TableRow tableRow = (TableRow)inflater.inflate(R.layout.table_row_calorie_info, null);
        	tableRow.setTag(i);
        	tableRow.setOnClickListener(new View.OnClickListener() {
        		@Override
    			public void onClick(View v) {
        			int index = (Integer)v.getTag();
        			selectCalorie(index);
        		}
    		});

        	CalorieInfo c = mCalorieInfoList.get(i);
        	
        	View view = tableRow.findViewById(R.id.caloire_icon);
        	Drawable d = getResources().getDrawable(R.drawable.round_corner_icon);
        	d.setColorFilter(c.getColor(), PorterDuff.Mode.SRC);
        	
        	view.setBackgroundDrawable(d);
        	
        	TextView nameText = (TextView)tableRow.findViewById(R.id.calorie_name);
        	nameText.setText(c.getName());
        	
        	TextView valueText = (TextView)tableRow.findViewById(R.id.calorie_value);
        	valueText.setText("" + c.getValue());

        	tableLayout.addView(tableRow);
        	mTableRowCalorieInfoList.add(tableRow);
        }
        
        setSummary();
    }
    
    protected void setSummary(){
        int totalValue = mMultiBar.getTotalBarValue();
        TextView t;
        
        t = (TextView)findViewById(R.id.total_text);
        t.setText(getString(R.string.summary_total) + " " + totalValue);
        
        int remain = mMultiBar.getTarget() - totalValue;
        t = (TextView)findViewById(R.id.remain_text);
        t.setText(getString(R.string.summary_remain) + " " + remain);
        
        t.setTextColor(MainActivity.getRemainColor(remain));
    }
    
    protected void selectCalorie(int index){
        mDelta = 0;
        for(View v: mTableRowCalorieInfoList){
            TextView t = (TextView)v.findViewById(R.id.calorie_delta);
            t.setText("");
        }
        
        if(index == -1){
    		for(TableRow t: mTableRowCalorieInfoList){
    			t.setBackgroundDrawable(null);
    		}
    		mSelectedCalorie = -1;
    	}
    	else if(mSelectedCalorie == index){
    		mMultiBar.clearBarSelected();
    		mTableRowCalorieInfoList.get(index).setBackgroundDrawable(null);
    		mSelectedCalorie = -1;
    	}
    	else{
    		if(mSelectedCalorie != -1){
    			mTableRowCalorieInfoList.get(mSelectedCalorie).setBackgroundDrawable(null);
    		}
    		mMultiBar.setBarSelected(index);
    		mTableRowCalorieInfoList.get(index).setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner_calorie_info));
    		//mTableRowCalorieInfoList.get(index).setBackgroundColor(Color.rgb(0x44, 0x44, 0x44));
    		mSelectedCalorie = index;
    	}
    }
    
    /**
     * バーの値を増減させるためのボタンにセットするリスナー
     * @author h13i32maru
     */
    private class OnTouchListener implements View.OnTouchListener{
    	int mDelta;
    	int mInterval;
    	public OnTouchListener(int delta, int interval){
    		mDelta = delta;
    		mInterval = interval;
    	}
    	
    	@Override
    	public boolean onTouch(View v, MotionEvent event) {
    		switch(event.getAction()){
    		case MotionEvent.ACTION_DOWN:
    		    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_corner_arrow));
    			mMultiBar.start(mSelectedCalorie, mDelta, mInterval);
    			return true;
    		case MotionEvent.ACTION_UP:
    			v.setBackgroundDrawable(null);
    			mMultiBar.stop();
    			return true;
    		}
    		return false;
    	}
    	
    }
}