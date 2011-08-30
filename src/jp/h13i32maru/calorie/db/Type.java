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
    
    private static final int[] mColorArray = {
        Color.rgb(0x73, 0x59, 0x41),
        Color.rgb(0xFC, 0xEB, 0xB6),
        Color.rgb(0xF0, 0x78, 0x18),
        Color.rgb(0xF0, 0xA8, 0x30),
        Color.rgb(0x78, 0xC0, 0xA8),
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
