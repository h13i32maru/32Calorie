package jp.h13i32maru.calorie.activity;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.chart.LineChart;
import android.app.Activity;
import android.os.Bundle;

public class LineChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.line_chart);
        
        LineChart lineChart = (LineChart)findViewById(R.id.line_chart);
        
        lineChart.setXAxis(0, 6, 1);
        lineChart.setyAxis(1000, 2200, 100);
        lineChart.addLine("目標1800", 0, 1800, 6, 1800, null);
        lineChart.addLine("平均1500", 0, 1500, 6, 1500, null);
        lineChart.addPoint("1000", 0, 1000);
        lineChart.addPoint("1400", 1, 1400);
        lineChart.addPoint("1340", 2, 1340);
        lineChart.addPoint("1800", 3, 1800);
        lineChart.addPoint("1770", 4, 1770);
        lineChart.addPoint("2000", 5, 2000);
        lineChart.addPoint("1900", 6, 1900);
    }
}
