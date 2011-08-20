package jp.h13i32maru.calorie.widget;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.activity.MainActivity;
import jp.h13i32maru.calorie.multibar.MultiBar;
import jp.h13i32maru.calorie.util._Log;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class CalorieWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        
        int padding = 4 * (int)metrics.density;

	
        for (int appWidgetId : appWidgetIds) {
            AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
            
            int width = info.minWidth - padding;
            int height = info.minHeight / 2;
            
            MultiBar bar = new MultiBar(context);
            bar.setTextSize(16);
            bar.setSize(width, height);
            bar.layout(0, 0, width, height);
            bar.setDrawingCacheEnabled(true);
            MainActivity.loadConfig(bar);
            MainActivity.restoreCalorieInfoList(bar);
            Bitmap bitmap = bar.getDrawingCache();
            
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteView.setImageViewBitmap(R.id.bar_image, bitmap);
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }
}
