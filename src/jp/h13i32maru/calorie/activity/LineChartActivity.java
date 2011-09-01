package jp.h13i32maru.calorie.activity;

import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.chart.LineChart;
import jp.h13i32maru.calorie.db.CalorieDAO;
import jp.h13i32maru.calorie.db.CalorieInfo;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

public class LineChartActivity extends Activity {

    public static final int MIN_COUNT = 7;
    public static final int MAX_COUNT = 30;
    //グラフのX軸を増やしすぎると文字が重なって見えにくくなるので、
    //文字表示を切り替えるX軸の数の敷地
    public static final int CRUSH_LABEL = 20;
    
    private Pref mPref;
    private LineChart mLineChart;
    private CalorieDAO mDAO;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.line_chart);
        
        mPref = Pref.getInstance(this);
        
        mLineChart = (LineChart)findViewById(R.id.line_chart);

        mDAO = CalorieDAO.getInstance(this);

        setChart(MIN_COUNT);
        
        SeekBar seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                int count = progress + MIN_COUNT;
                setChart(count);
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //int count = seekBar.getProgress() + MIN_COUNT;
                //Toast.makeText(LineChartActivity.this, "" + count, Toast.LENGTH_SHORT).show();
            }
        });

    }
    
    protected void setChart(int count){
        mLineChart.clear();

        if(count >= CRUSH_LABEL){
            mLineChart.setXAixsLabeler(new LineChart.AxisLabeler() {
                @Override
                public String getLabel(float axisNum) {
                    if(axisNum % 2 == 0){
                        return "" + (int)axisNum;
                    }
                    return "";
                }
            });
        }
        
        //最近のデータがindex = 0に入っている
        List<List<CalorieInfo>> history = mDAO.getHistory(count);
        
        mPref = Pref.getInstance(this);
        
        int num = Math.min(count, history.size());
        if(num <= 0){
            Toast.makeText(this, getString(R.string.chart_no_data), Toast.LENGTH_SHORT).show();
            finish();
        }
        int target = mPref.getInt(C.config.target, C.config.target_def_value);
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
            
            if(count < CRUSH_LABEL){
                mLineChart.addPoint("" + total, count - i - 1, total);
            }
            else{
                mLineChart.addPoint("", count - i - 1, total);
            }
        }
        //余白を持たせるために-100 & 10の桁未満は切り捨てる
        min -= 100;
        min = (min / 100) * 100;
        if(min < 0){
            min = 0;
        }
        
        //余白を持たせるために+100 & 10の桁未満は切り捨てる
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
