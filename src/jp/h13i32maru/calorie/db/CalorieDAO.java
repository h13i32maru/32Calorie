package jp.h13i32maru.calorie.db;

import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.util._Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class CalorieDAO implements CalorieConstants{
    
    private static CalorieDAO instance;
    
    private SQLiteDatabase mDB;
    
    public static CalorieDAO getInstance(Context context){
        if(instance == null){
            instance = new CalorieDAO(context);
        }
        
        return instance;
    }
    
    public static String getCreateTableSQL(){
        String sql = String.format("create table %s (%s, %s, %s, %s)",
                table_name,
                type.id,
                type.group_id,
                type.type,
                type.value);
        
        return sql;
    }
    
    public static ContentValues toContentValues(Calorie calorie){
        ContentValues values = new ContentValues();
        values.put(column.group_id, calorie.getGroup());
        values.put(column.type, calorie.getType());
        values.put(column.value, calorie.getValue());
        return values;
    }
    
    public static Calorie toCalorie(Cursor c){
        long id = c.getLong(c.getColumnIndex(column.id));
        long group = c.getLong(c.getColumnIndex(column.group_id));
        long type = c.getLong(c.getColumnIndex(column.type));
        long value = c.getLong(c.getColumnIndex(column.value));
        return new Calorie(id, group, type, value);
    }
    
    private static List<Calorie> toCalorieList(Cursor c){
        List<Calorie> calorieList = new ArrayList<Calorie>();
        if(c.moveToFirst()){
            do{
               calorieList.add(toCalorie(c));
            }while(c.moveToNext());
        }
        return calorieList;
    }
    
    private static List<CalorieInfo> toCalorieInfoList(List<Calorie> calorieList){
        List<CalorieInfo> ciList = new ArrayList<CalorieInfo>();
        for(Calorie calorie: calorieList){
            ciList.add(new CalorieInfo(calorie));
        }
        return ciList;
    }
    
    private static List<Calorie> toCalorieList(List<CalorieInfo> calorieInfoList){
        List<Calorie> calorieList = new ArrayList<Calorie>();
        for(CalorieInfo calorieInfo: calorieInfoList){
            calorieList.add(calorieInfo.toCalorie());
        }
        return calorieList;
    }
    
    private CalorieDAO(Context c) throws SQLiteException{
        mDB = new DBOpenHelper(c).getWritableDatabase();
    }
    
    public int groupCount(){
        String sql = String.format("select count(1) as count from (select %s from %s group by %s)", column.group_id, table_name, column.group_id);
        Cursor c = mDB.rawQuery(sql, null);
        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex("count"));
        c.close();
        return count;
    }
    
    public long getFirstGroup(){
        String sql = String.format("select min(%s) as min_group from %s", column.group_id, table_name);
        Cursor c = mDB.rawQuery(sql, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex("min_group");
        if(c.isNull(columnIndex)){
            return -1;
        }
        long group = c.getLong(columnIndex);
        c.close();
        return group;
    }
    
    public long getLastGroup(){
        String sql = String.format("select max(%s) as max_group from %s", column.group_id, table_name);
        Cursor c = mDB.rawQuery(sql, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex("max_group");
        if(c.isNull(columnIndex)){
            c.close();
            return -1;
        }
        long group = c.getLong(columnIndex);
        c.close();
        return group;
    }

    public List<CalorieInfo> getLastCalorieInfoList(){
        long group = getLastGroup();
        //データがまだない場合はgroupは-1となるので、新規で作成する
        _Log.d("group = " + group);
        if(group < 0){
            return createNew();
        }
        String sql = String.format("select * from %s where %s = ?", table_name, column.group_id);
        Cursor c = mDB.rawQuery(sql, new String[]{"" + group});
        List<Calorie> calorieList = toCalorieList(c);
        c.close();
        return toCalorieInfoList(calorieList);
    }
    
    public void update(List<CalorieInfo> calorieInfoList){
        List<Calorie> calorieList = toCalorieList(calorieInfoList);
        try{
            mDB.beginTransaction();
            for(Calorie calorie: calorieList){
                ContentValues values = toContentValues(calorie);
                mDB.update(table_name, values, "_id = ?", new String[]{"" + calorie.getId()});
            }
            mDB.setTransactionSuccessful();
        }
        catch(Exception e){
            _Log.e("fail create table", e);
        }
        finally{
            mDB.endTransaction();
        }
    }
    
    public List<CalorieInfo> createNew(){
        long group = getLastGroup() + 1;
        List<Calorie> calorieList = new ArrayList<Calorie>(); 
        
        try{
            mDB.beginTransaction();
            for(int type: Type.mTypeArray){
                Calorie calorie = new Calorie(group, type, 0);
                calorieList.add(calorie);
                
                ContentValues values = toContentValues(calorie);
                long id = mDB.insert(table_name, null, values);
                calorie.setId(id);
            }
            
            //DBの残していく数は一年分(365日)とする
            if(groupCount() > 365){
                long firstGroup = getFirstGroup();
                mDB.delete(table_name, column.group_id + " = ?", new String[]{"" + firstGroup});
            }
            
            mDB.setTransactionSuccessful();
        }catch(Exception e){
            _Log.e("", e);
        }finally{
            mDB.endTransaction();
        }
        
        return toCalorieInfoList(calorieList);
    }
    
    public List<List<CalorieInfo>> getHistory(int num){
        List<List<CalorieInfo>> data = new ArrayList<List<CalorieInfo>>();
        long group = getLastGroup();
        String sql = String.format("select * from %s where %s = ?", table_name, column.group_id);
        for(int i = 0; i < num; i++){
            Cursor c = mDB.rawQuery(sql, new String[]{"" + group});
            boolean result = c.moveToFirst();
            if(result == false){
                break;
            }
            List<CalorieInfo> list = toCalorieInfoList(toCalorieList(c));
            data.add(list);
            group--;
        }
        
        return data;
    }
}
