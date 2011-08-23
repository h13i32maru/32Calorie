package jp.h13i32maru.calorie.util.widgethelper;

import android.app.Activity;
import android.widget.CheckBox;

public class HCheckBox {
    public static void set(Activity activity, int id, boolean on){
        CheckBox view = (CheckBox)activity.findViewById(id);
        view.setChecked(on);
    }
    
    public static boolean get(Activity activity, int id){
        CheckBox view = (CheckBox)activity.findViewById(id);
        return view.isChecked();
    }
}
