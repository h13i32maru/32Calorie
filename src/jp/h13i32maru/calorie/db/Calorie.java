package jp.h13i32maru.calorie.db;

public class Calorie {
    
    private long mId;
    private long mGroup;
    private long mType;
    private long mValue;
    
    public Calorie(long id, long group, long type, long value){
        mId = id;
        mGroup = group;
        mType = type;
        mValue = value;
    }
    
    public Calorie(long group, long type, long value){
        this(-1, group, type, value);
    }

    public long getId(){
        return mId;
    }
    
    public long getGroup(){
        return mGroup;
    }
    
    public long getType(){
        return mType;
    }
    
    public long getValue(){
        return mValue;
    }
    
    public void setId(long id){
        mId = id;
    }
    
    public void setGroup(long group){
        mGroup = group;
    }
    
    public void setType(long type){
        mType = type;
    }
    
    public void setValue(long value){
        mValue = value;
    }
    
}
