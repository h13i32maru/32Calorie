package jp.h13i32maru.calorie.activity;

import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.chart.LineChart;
import jp.h13i32maru.calorie.db.CalorieDAO;
import jp.h13i32maru.calorie.db.CalorieInfo;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.util._Log;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class LineChartActivity extends Activity {

    public static int COUNT = 7;
    
    private LineChart mLineChart;
    private CalorieDAO mDAO;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.line_chart);
        
        mLineChart = (LineChart)findViewById(R.id.line_chart);

        mDAO = CalorieDAO.getInstance(this);

        setChart(COUNT);
        
        SeekBar seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _Log.d("start = " + seekBar.getProgress());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                _Log.d("progress = " + progress);
                if(progress < COUNT){
                    seekBar.setProgress(COUNT);
                }
                
                setChart(seekBar.getProgress());
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _Log.d("stop = " + seekBar.getProgress());
            }
        });

    }
    
    protected void setChart(int count){
        mLineChart.clear();
        
        List<List<CalorieInfo>> history = mDAO.getHistory(count);
        
        Pref pref = Pref.getInstance(this);
        
        int num = Math.min(count, history.size());
        if(num <= 0){
            Toast.makeText(this, getString(R.string.chart_no_data), Toast.LENGTH_SHORT).show();
            finish();
        }
        int target = pref.getInt(C.config.target, C.config.target_def_value);
        int min = Integer.MAX_VALUE;
        int max = target;
        float sum = 0;
        int average;
        
        for(int i = 0; i < num; i++){
            List<CalorieInfo> calorieInfoList = history.get(i);
            int total = getTotal(calorieInfoList);
            min = Math.min(min, total);
            max = Math.max(max, total);
            sum += total;
            
            mLineChart.addPoint("" + total, num - i - 1, total);
        }
        min -= 100;
        min = (min / 100) * 100;
        if(min < 0){
            min = 0;
        }
        
        max += 100;
        max = (max / 100) * 100;
        
        average = (int)(sum / num);
        
        mLineChart.setXAxis(0, count - 1, 1);
        mLineChart.setyAxis(min, max, 100);
        mLineChart.addLine(getString(R.string.config_general_target) + target, 0, target, count - 1, target, null);
        mLineChart.addLine(getString(R.string.summary_average) + average, 0, average, count - 1, average, null);
    }
    
    public int getTotal(List<CalorieInfo> calorieInfoList){
        int total = 0;
        for(CalorieInfo calorieInfo: calorieInfoList){
            total += calorieInfo.getValue();
        }
        return total;
    }
}
