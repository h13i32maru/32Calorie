package jp.h13i32maru.calorie.multibar;
import java.util.ArrayList;
import java.util.List;

import jp.h13i32maru.calorie.R;
import jp.h13i32maru.calorie.util._Log;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class MultiBar extends View {
	
	private static final int BAR_RADIUS_X = 10;
	private static final int BAR_RADIUS_Y = 10;
	
	private OnProgressListener mOnProgress;
	private List<Bar> mBarList = new ArrayList<Bar>();
	private volatile boolean mStartFlag = false;
	private int mTargetValue = 1800;
	private int mMaxValue = 2500;
	private int mTextBarSpace = 0;
	private Paint mPaintText;
	private int mBarWidth;
	private int mBarHeight;
	private int mOneColor = -1;
	private int mBorderWidth = 4;
	private int mDelta = 0;
	private int mSelectedBar = -1;
	
	public MultiBar(Context context){
	    super(context);
	}
	
	public MultiBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initAttribute(context, attrs);
	}
	
	public void addValue(int index, int delta){
	    if(mSelectedBar != index){
	        setBarSelected(index);
	        mDelta = 0;
	    }
	    mDelta += delta;
	    mBarList.get(index).addValue(delta);
	    int value = getBarVaue(index);
	    int total = getTotalBarValue();
	    mOnProgress.progress(index, value, delta, total);
	    postInvalidate();
	}
	
	public void setBorderWidth(int width){
	    mBorderWidth = width;
	}
	
	/**
	 * 各バーを同じ色で描画する
	 * @param color
	 */
	public void setOneColor(int color){
	    mOneColor = color;
	}
	
	public int getTarget(){
	    return mTargetValue;
	}
	
	public void setTarget(int target){
		mTargetValue = target;
		postInvalidate();
	}
	
	public void setMax(int max){
		mMaxValue = max;
		postInvalidate();
	}

	public void setOnProgressListener(OnProgressListener p){
		mOnProgress = p;
	}
	
	public void addBar(String name, int value, int color){
		mBarList.add(new Bar(name, value, color));
		postInvalidate();
	}
	
	public void clearAllBar(){
		mBarList.clear();
		postInvalidate();
	}
	
	public int getBarColor(int index){
		return mBarList.get(index).getColor();
	}
	
	public String getBarName(int index){
		return mBarList.get(index).getName();
	}
	
	public int getBarVaue(int index){
		return mBarList.get(index).getValue();
	}
	
	public int getTotalBarValue(){
		int total = 0;
		for(Bar bar: mBarList){
			total += bar.getValue();
		}
		return total;
	}

	public void setBarSelected(int index){
		clearBarSelected();
        mSelectedBar = index;
		Bar bar = mBarList.get(index);
		bar.setSelected(true);
		postInvalidate();
	}
	
	public void clearBarSelected(){
	    mSelectedBar = -1;
		for(Bar bar: mBarList){
			bar.setSelected(false);
		}
		postInvalidate();
	}
	
	public void start(final int index, final int delta, final int interval){
		if(index < 0 || mBarList.size() <= index){
			_Log.e("index is out of range mBarList.size(): index = " + index + " mBarList.size() = " + mBarList.size());
			return;
		}
		
		final Bar bar = mBarList.get(index);
		if(mSelectedBar != index){
		    mDelta = 0;
		}
		setBarSelected(index);
		mStartFlag = true;
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(mStartFlag){
					
					int value = bar.addValue(delta);
					int totalValue = getTotalBarValue();
					mDelta += delta;
					
					if(totalValue > mMaxValue){
						value = value - (totalValue - mMaxValue);
						totalValue = mMaxValue;
						bar.setValue(value);
						mStartFlag = false;
					}
					else if(value == 0){
						mStartFlag = false;
					}
					postInvalidate();
					if(mOnProgress != null){
						final int v = value;
						final int t = totalValue;
						post(new Runnable(){
							@Override
							public void run() {
								mOnProgress.progress(index, v, delta, t);
							}
						});
					}
					
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
					}

				}
			}
		}).start();
	}
	
	public void stop(){
		mStartFlag = false;
	}
	
	public void setTextSize(int textSize){
	    if(textSize <= 0){
	        mPaintText = null;
	        return;
	    }
	    
	    mPaintText = new Paint();
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.parseColor("#fff3b1"));
        mPaintText.setFakeBoldText(true);
	}
	
	protected void initAttribute(Context context, AttributeSet attrs){
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiBar);
		
		int textSize = typedArray.getDimensionPixelSize(R.styleable.MultiBar_textSize, 16);
		setTextSize(textSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		//ボーダーの描画
		RectF rectF = new RectF(0, getBarTop() - mBorderWidth, getWidth(), getHeight());
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#f6edb6"));
		paint.setStrokeWidth(mBorderWidth);
	    canvas.drawRoundRect(rectF, BAR_RADIUS_X, BAR_RADIUS_Y, paint);
		
		//背景バーの描画
		Rect rect = new Rect(getBarLeft(), getBarTop(), getBarLeft() + mBarWidth, getBarTop() + mBarHeight);
		rectF = new RectF(rect);
		/*影をつける実装をしたけど、あとから各バーを描くと影が消えています。何か方法はないか？
		int[] colors = new int[2];
		colors[0] = Color.parseColor("#b3ab73");
		colors[1] = Color.parseColor("#00000000");
		Shader shaderA = new LinearGradient(0, rect.top, 0, rect.top + 20, colors, null, Shader.TileMode.CLAMP);
		Shader shaderB = new LinearGradient(0, rect.bottom, 0, rect.bottom - 20, colors, null, Shader.TileMode.CLAMP);
	    Shader shaderC = new LinearGradient(rect.left, 0, rect.left + 20, 0, colors, null, Shader.TileMode.CLAMP);
        Shader shaderD = new LinearGradient(rect.right, rect.bottom - 20, rect.right - 20, rect.bottom - 20, colors, null, Shader.TileMode.CLAMP);
	    Shader shaderAB = new ComposeShader(shaderA, shaderB, PorterDuff.Mode.DARKEN);
	    Shader shaderCD = new ComposeShader(shaderC, shaderD, PorterDuff.Mode.DARKEN);
	    Shader shader = new ComposeShader(shaderAB, shaderCD, PorterDuff.Mode.DARKEN);
        */
        paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		//paint.setShader(shader);
		paint.setColor(Color.parseColor("#ddd6ae"));
		canvas.drawRoundRect(rectF, BAR_RADIUS_X, BAR_RADIUS_Y, paint);
	
		//各バーの描画
		drawAllBar(canvas);
	        
		//目標ラインの描画
		int targetLeft = mBarWidth * mTargetValue / mMaxValue; 
		rect = new Rect(targetLeft - 2, getBarTop(), targetLeft + 2, getBarTop() + mBarHeight);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.parseColor("#503e34"));
		paint.setAntiAlias(true);
		canvas.drawRect(rect, paint);
				
		//テキストの描画
		if(mPaintText != null){
		    int textTop = (int)(getPaddingTop() - mPaintText.ascent());
            canvas.drawText("0", 0, textTop, mPaintText);
            canvas.drawText("" + mMaxValue, getMeasuredWidth() - mPaintText.measureText("" + mMaxValue), textTop, mPaintText);
		    canvas.drawText("" + mTargetValue, targetLeft - mPaintText.measureText("" + mTargetValue) / 2, textTop, mPaintText);
		}
	}
	
	protected int getBarLeft(){
	    return mBorderWidth;
	}

	protected int getBarTop(){
	    if(mPaintText == null){
	        return 0;
	    }
	    
		FontMetrics fontMetrics = mPaintText.getFontMetrics();
		return (int)(-fontMetrics.top + fontMetrics.bottom + mTextBarSpace + mBorderWidth);
	}
	
	protected void drawAllBar(Canvas canvas){
		int left = getBarLeft();
		int top = getBarTop();
		int right = getBarLeft();
		int bottom = top + mBarHeight;
		
		//最初と最後のバーを探して保存しておく
		int first = -1;
		int last = 0;
		for(int i = 0; i < mBarList.size(); i++){
			if(mBarList.get(i).getValue() > 0){
				if(first == -1){
					first = i;
				}
				last = i;
			}
		}
		
		for(int i = 0; i < mBarList.size(); i++){
			Bar bar = mBarList.get(i);
			if(bar.getValue() <= 0){
			    continue;
			}
			left = right;
			right = left + (mBarWidth * bar.getValue() / mMaxValue);

			float[] outer = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
			//最初のバーの場合は左側を角丸にする
			if(i == first){
				outer[0] = BAR_RADIUS_X;
				outer[1] = BAR_RADIUS_Y;
				outer[6] = BAR_RADIUS_X;
				outer[7] = BAR_RADIUS_Y;
			}
			//最後のバーの場合は右側を角丸にする
			if(i == last && getTotalBarValue() == mMaxValue){
				outer[2] = BAR_RADIUS_X;
				outer[3] = BAR_RADIUS_Y;
				outer[4] = BAR_RADIUS_X;
				outer[5] = BAR_RADIUS_Y;
				
				//最後のバーの場合、整数の計算で切り捨てが発生して少し隙間ができる場合があるのでぴったりになるように調整する
				right = getBarLeft() + mBarWidth;
			}
			RoundRectShape round = new RoundRectShape(outer, null, null);
			ShapeDrawable shape = new ShapeDrawable(round);
			shape.setBounds(left, top, right, bottom);
			
			int color = bar.getColor();
			if(mOneColor != -1){
			    color = mOneColor;
			}
			/*
			int[] colors = new int[2];
			colors[0] = color;
			colors[1] = Color.argb(0xff, (int)(Color.red(color) / 1.2), (int)(Color.green(color) / 1.2), (int)(Color.blue(color) / 1.2));
			float[] positions = new float[2];
			positions[0] = 0;
			positions[1] = 1F;
            */
			int[] colors = new int[3];
            colors[0] = color;
            colors[1] = Color.argb(0xff, Math.min(255, (int)(Color.red(color) * 1.2)), Math.min(255, (int)(Color.green(color) * 1.2)), Math.min(255, (int)(Color.blue(color) * 1.2)));
            colors[2] = colors[0];
            float[] positions = new float[3];
            positions[0] = 0;
            positions[1] = 0.5F;
            positions[2] = 1F;
            
			LinearGradient shader = new LinearGradient(0, 0, 0, bottom - top, colors, positions, Shader.TileMode.CLAMP);
			
			Paint paint = shape.getPaint();
			paint.setStyle(Paint.Style.FILL);
			paint.setShader(shader);
			paint.setAntiAlias(true);

			shape.draw(canvas);
			
			/*
			if(bar.getSelected()){
				int halfWidth = 3;
				paint = new Paint();
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(halfWidth * 2);
				paint.setColor(Color.rgb(0xff, 0x88, 0x44));
				paint.setAntiAlias(true);
				canvas.drawRect(left + halfWidth, top + halfWidth, right - halfWidth, bottom - halfWidth, paint);
			}
			*/
			
			if(bar.getSelected()){
		         int textTop = (int)(getPaddingTop() - mPaintText.ascent());
		         String text = (mDelta > 0 ? "+" : "") + mDelta;
		         canvas.drawText(text, right - mPaintText.measureText(text) / 2, textTop, mPaintText);
			}
		}
	}
	
	public void setSize(int width, int height){
	    mBarWidth = width;
	    mBarHeight = height - getBarTop();
	    
	    setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    
		mBarWidth = MeasureSpec.getSize(widthMeasureSpec) - (2 * mBorderWidth);
		mBarHeight = MeasureSpec.getSize(heightMeasureSpec) * 4 / 32;
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = getBarTop() + mBarHeight + mBorderWidth;
		setMeasuredDimension(width, height);
	}
	
	/**
	 * 各バーを示すクラス
	 * @author h13i32maru
	 */
	private static class Bar{
		private int mValue;
		private String mName;
		private int mColor;
		private boolean mSelected;
		public Bar(String name, int value, int color){
			mValue = value;
			mName = name;
			mColor = color;
			mSelected = false;
		}
		
		public int getValue(){
			return mValue;
		}
		
		public String getName(){
			return mName;
		}
		
		public int getColor(){
				return mColor;
		}
		
		public boolean getSelected(){
			return mSelected;
		}
		
		public void setValue(int value){
			mValue = value;
		}
		
		public void setSelected(boolean selected){
			mSelected = selected;
		}
		
		public int addValue(int value){
			mValue += value;
			if(mValue < 0){
				mValue = 0;
			}
			return mValue;
		}
	}
	
	public static interface OnProgressListener{
		void progress(int index, int value, int delta, int totalValue);
	}
}
