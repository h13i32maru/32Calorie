package jp.h13i32maru.calorie.db;

public class CalorieInfo {

    private Calorie mCalorie;
    private Type mType;
    
    public CalorieInfo(Calorie calorie){
        mCalorie = calorie;
        mType = new Type((int)calorie.getType());
    }
    
    public Calorie toCalorie(){
        long id = mCalorie.getId();
        long group = mCalorie.getGroup();
        long type = mCalorie.getType();
        long value = mCalorie.getValue();
        return new Calorie(id, group, type, value);
    }
    
    public int getName(){
        return mType.name;
    }
    
    public int getColor(){
        return mType.color;
    }
    
    public int getOrder(){
        return mType.order;
    }
    
    public int getValue(){
        return (int)mCalorie.getValue();
    }
    
    public int getType(){
        return mType.type;
    }
    
    public void setValue(int value){
        mCalorie.setValue(value);
    }
}
