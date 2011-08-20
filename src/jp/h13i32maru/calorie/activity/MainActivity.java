package jp.h13i32maru.calorie.activity;

import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.CalorieInfo;
import jp.h13i32maru.calorie.model.CalorieInfoDAO;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.multibar.MultiBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
    private List<CalorieInfo> mCalorieInfoList;
    private List<TableRow> mTableRowCalorieInfoList = new ArrayList<TableRow>();
    private int mSelectedCalorie = -1;
    private MultiBar mMultiBar;
    private CalorieInfoDAO mCalorieInfoDAO;
    
    public static void loadConfig(MultiBar multiBar){
        Pref pref = Pref.getInstance(multiBar.getContext());
        int limit = pref.getInt(C.config.limit, C.config.limit_def_value);
        int max = pref.getInt(C.config.max, C.config.max_def_value);
        multiBar.setLimit(limit);
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
    		public void progress(int index, int value, int totalValue) {
    			CalorieInfo c = mCalorieInfoList.get(index);
    			c.setValue(value);
    			
    			TextView t = (TextView)mTableRowCalorieInfoList.get(index).findViewById(R.id.calorie_value);
    			t.setText("" + value);
    			
    			t = (TextView)findViewById(R.id.total_calorie_value);
    			t.setText("" + totalValue);
    		}
    	});
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	mCalorieInfoDAO.save(mCalorieInfoList);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode){
    	case C.req.config:
    		MainActivity.loadConfig(mMultiBar);
    		break;
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem item;
        
        item = menu.add(Menu.NONE , C.menu.clear, Menu.NONE , "クリア");
        item.setIcon(android.R.drawable.ic_menu_more);
        
        item = menu.add(Menu.NONE, C.menu.config, Menu.NONE, "設定");
        item.setIcon(android.R.drawable.ic_menu_more);
        
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
        case C.menu.config:
        {
        	Intent intent = new Intent(this, ConfigActivity.class);
        	startActivityForResult(intent, C.req.config);
        }
        	break;
        default:
        	return false;
        }
        
        return true;
    }
    
    /*
    protected void restoreCalorieInfoList(){
    	mMultiBar.clearAllBar();
        mCalorieInfoList = mCalorieInfoDAO.getList();
        for(CalorieInfo c: mCalorieInfoList){
        	mMultiBar.addBar(c.getName(), c.getValue(), c.getColor());
        }
    }
    */

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
        	view.setBackgroundColor(c.getColor());
        	
        	TextView nameText = (TextView)tableRow.findViewById(R.id.calorie_name);
        	nameText.setText(c.getName());
        	
        	TextView valueText = (TextView)tableRow.findViewById(R.id.calorie_value);
        	valueText.setText("" + c.getValue());

        	tableLayout.addView(tableRow);
        	mTableRowCalorieInfoList.add(tableRow);
        }
        
        TableRow tableRow = (TableRow)inflater.inflate(R.layout.table_row_total, null);
        TextView t = (TextView)tableRow.findViewById(R.id.total_calorie_value);
        t.setText("" + mMultiBar.getTotalBarValue());
        tableLayout.addView(tableRow);
    }
    
    protected void selectCalorie(int index){
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
    		mTableRowCalorieInfoList.get(index).setBackgroundColor(Color.rgb(0x44, 0x44, 0x44));
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
    			v.setBackgroundColor(Color.rgb(0xff, 0x88, 0x44));
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