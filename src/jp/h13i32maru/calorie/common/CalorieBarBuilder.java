package jp.h13i32maru.calorie.common;

import java.util.List;

import jp.h13i32maru.calorie.db.CalorieInfo;
import jp.h13i32maru.calorie.model.C;
import jp.h13i32maru.calorie.model.Pref;
import jp.h13i32maru.calorie.multibar.MultiBar;
import android.content.Context;
import android.graphics.Color;

public class CalorieBarBuilder {
    
    public static void loadConfig(MultiBar multiBar){
        Pref pref = Pref.getInstance(multiBar.getContext());
        int target = pref.getInt(C.config.target, C.config.target_def_value);
        int max = pref.getInt(C.config.max, C.config.max_def_value);
        multiBar.setTarget(target);
        multiBar.setMax(max);
    }
    
    public static void loadData(MultiBar multiBar, List<CalorieInfo> calorieInfoList){
        multiBar.clearAllBar();
        Context context = multiBar.getContext();
        for(CalorieInfo c: calorieInfoList){
            multiBar.addBar(context.getString(c.getName()), c.getValue(), c.getColor());
        }
    }
    
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
}
