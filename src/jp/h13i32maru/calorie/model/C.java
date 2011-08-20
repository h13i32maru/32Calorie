package jp.h13i32maru.calorie.model;

public class C {
	
	public static class menu{
		public static final int clear = 0;
		public static final int config = 1;
	}

	public static class req{
		public static final int config = 0;
	}
	
	public static class config{
		public static final String goal = "goal";
		public static final int goal_def_value = 2000;
		
		public static final String max = "max";
		public static final int max_def_value = 2500;
	}
}
