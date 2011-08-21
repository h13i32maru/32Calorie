package jp.h13i32maru.calorie.widget;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.activity.MainActivity;
import jp.h13i32maru.calorie.multibar.MultiBar;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RemoteViews;

public class CalorieWidget extends AppWidgetProvider {
    
    public static void update(Context context){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, CalorieWidget.class));
        
        Intent intent = new Intent(context, CalorieWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        
        context.sendBroadcast(intent);
    }

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
            int height = (int)(info.minHeight / 2.25);
            
            MultiBar bar = new MultiBar(context);
            if(metrics.density >= 2){
                bar.setTextSize(18);
            }
            else{
                bar.setTextSize(16);
            }
            bar.setSize(width, height);
            bar.layout(0, 0, width, height);
            bar.setDrawingCacheEnabled(true);
            MainActivity.loadConfig(bar);
            MainActivity.restoreCalorieInfoList(bar);
            Bitmap bitmap = bar.getDrawingCache();

            int total = bar.getTotalBarValue();
            int remain = bar.getGoal() - total;
            
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setImageViewBitmap(R.id.bar_image, bitmap);
            
            remoteViews.setCharSequence(R.id.total_text, "setText", "合計 " + total);
            remoteViews.setFloat(R.id.total_text, "setTextSize", 12);
            
            remoteViews.setCharSequence(R.id.remain_text, "setText", "残り " + remain);
            remoteViews.setFloat(R.id.remain_text, "setTextSize", 12);
            remoteViews.setInt(R.id.remain_text, "setTextColor", MainActivity.getRemainColor(remain));
            
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
            
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
