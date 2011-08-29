package jp.h13i32maru.calorie.model;

public class C {
	
	public static class menu{
		public static final int clear = 0;
		public static final int settings = 1;
		public static final int help = 2;
		public static final int line_chart = 3;
	}

	public static class req{
		public static final int config = 0;
	}
	
	public static class config{
		public static final String target = "target";
		public static final int target_def_value = 1800;
		
		public static final String max = "max";
		public static final int max_def_value = 2500;
		
		public static final String widget_background = "widget_background";
		public static final boolean widget_background_def_value = true;
		
		public static final String widget_one_color = "widget_one_color";
		public static final boolean widget_one_color_def_value = false;
	}
}
