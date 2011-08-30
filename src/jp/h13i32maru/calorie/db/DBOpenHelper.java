package jp.h13i32maru.calorie.db;

import jp.h13i32maru.calorie.util._Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "calorie";
    private static final int VERSION = 1;
    
    public DBOpenHelper(Context c){
        super(c, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.beginTransaction();
            
            db.execSQL(CalorieDAO.getCreateTableSQL());
            
            db.setTransactionSuccessful();
        }
        catch(Exception e){
            _Log.e("fail create table", e);
        }
        finally{
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
