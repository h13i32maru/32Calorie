package jp.h13i32maru.calorie.util;

import java.util.Set;

import android.content.Intent;
import android.util.Log;

public class _Log {

    public static final String TAG = "_Log";
    
    public static String format(String msg){
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[4];
        String _thread = Thread.currentThread().getName();
        String _file = element.getFileName();
        String _line = Integer.toString(element.getLineNumber());
        String _method = element.getMethodName();
        
        return String.format("[%s,%s,%s,%s] %s", _thread, _file, _line, _method, msg);
    }
    
    //Debug
    public static void d(String msg){
        Log.d(TAG, format(msg));
    }

    public static void d(String msg, Throwable tr){
        Log.d(TAG, format(msg), tr);
    }
    
    //Info
    public static void i(String msg){
        Log.i(TAG, format(msg));
    }
    
    public static void i(String msg, Throwable tr){
        Log.i(TAG, format(msg), tr);
    }
    
    //Error
    public static void e(String msg){
        Log.e(TAG, format(msg));
    }
    
    public static void e(String msg, Throwable tr){
        Log.e(TAG, format(msg), tr);
    }
    
    //Warning
    public static void w(String msg){
        Log.w(TAG, format(msg));
    }
    
    public static void w(String msg, Throwable tr){
        Log.w(TAG, format(msg), tr);
    }
    
    //Verbose
    public static void v(String msg){
        Log.v(TAG, format(msg));
    }
    
    public static void v(String msg, Throwable tr){
        Log.v(TAG, format(msg), tr);
    }
    
    //Intent
    public static String extraToString(Intent intent){
        Set<String> keys = intent.getExtras().keySet();
        String str = "";
        for(String key: keys){
            str += (key + "=" + intent.getStringExtra(key));
        }
        return str;
    }
    
    public static void intent(Intent intent){
        Log.v(TAG, format(extraToString(intent)));
    }
    
    public static void intent(Intent intent, Throwable tr){
        Log.v(TAG, format(extraToString(intent)), tr);
    }
}
