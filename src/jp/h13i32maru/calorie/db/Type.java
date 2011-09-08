package jp.h13i32maru.calorie.db;

import jp.h13i32maru.calorie.R;
import android.graphics.Color;

public class Type {
    
    public static final int[] mTypeArray = {0, 1, 2, 3, 4};

    private static final int[] mNameArray = {
        R.string.calorie_name_breakfast,
        R.string.calorie_name_lunch,
        R.string.calorie_name_dinner,
        R.string.calorie_name_snack,
        R.string.calorie_name_other,
    };
    
    public static final int[] mColorArray = {
        Color.parseColor("#c87a4b"),
        Color.parseColor("#d1a058"),
        Color.parseColor("#669b6a"),
        Color.parseColor("#bcb037"),
        Color.parseColor("#829246"),
    };
    
    private static final int[] mOrderArray = {
        0,
        1,
        2,
        3,
        4,
    };

    public final int name;
    public final int color;
    public final int order;
    
    public Type(int type){
        //TODO:範囲チェックすべき
        name = mNameArray[type];
        color = mColorArray[type];
        order = mOrderArray[type];
    }
}
