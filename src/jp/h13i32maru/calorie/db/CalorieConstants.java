package jp.h13i32maru.calorie.db;

public interface CalorieConstants {

    public static final String table_name = "calorie";
    
    public static class column{
        public static final String id = "_id";
        public static final String group_id = "group_id";
        public static final String type = "type";
        public static final String value = "value";
    }
    
    public static class type{
        public static final String id = column.id + " integer primary key autoincrement not null";
        public static final String group_id = column.group_id + " integer key not null";
        public static final String type = column.type + " integer not null";
        public static final String value = column.value + " integer";
    }
}
