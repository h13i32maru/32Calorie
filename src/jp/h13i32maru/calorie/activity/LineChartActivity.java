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
import android.widget.Toast;

public class LineChartActivity extends Activity {

    public static int COUNT = 7;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.line_chart);
        
        LineChart lineChart = (LineChart)findViewById(R.id.line_chart);

        CalorieDAO dao = CalorieDAO.getInstance(this);

        List<List<CalorieInfo>> history = dao.getHistory(7);
        
        Pref pref = Pref.getInstance(this);
        
        int num = Math.min(COUNT, history.size());
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
            
            lineChart.addPoint("" + total, num - i - 1, total);
        }
        min -= 100;
        min = (min / 100) * 100;
        if(min < 0){
            min = 0;
        }
        
        max += 100;
        max = (max / 100) * 100;
        
        average = (int)(sum / num);
        
        lineChart.setXAxis(0, COUNT - 1, 1);
        lineChart.setyAxis(min, max, 100);
        lineChart.addLine(getString(R.string.config_general_target) + target, 0, target, COUNT - 1, target, null);
        lineChart.addLine(getString(R.string.summary_average) + average, 0, average, COUNT - 1, average, null);
    }
    
    public int getTotal(List<CalorieInfo> calorieInfoList){
        int total = 0;
        for(CalorieInfo calorieInfo: calorieInfoList){
            total += calorieInfo.getValue();
        }
        return total;
    }
}
