package jp.h13i32maru.calorie.chart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class LineChart extends View {

    private float mXMin = 0;
    private float mXMax = 0;
    private float mXDistance = 0;
    
    private float mYMin = 0;
    private float mYMax = 0;
    private float mYDistance = 0;
    
    private Paint mTextPaint;
    
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;
    
    private float mPointRadius = 6;

    private List<Line> mLineList = new ArrayList<Line>();
    
    private List<Point> mPointList = new ArrayList<Point>();
    
    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        
        float density = metrics.density;
        
        mTextPaint = new Paint();
        mTextPaint.setTextSize(11 * density);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.rgb(0xaa, 0xaa, 0xaa));
    }
    
    public void addPoint(String label, float x, float y){
        Point point = new Point(label, x, y);
        mPointList.add(point);
    }
    
    public void setXAxis(float min, float max, float distance){
        mXMin = min;
        mXMax = max;
        mXDistance = distance;
    }
    
    public void setyAxis(float min, float max, float distance){
        mYMin = min;
        mYMax = max;
        mYDistance = distance;
    }
    
    protected void calcPadding(){
        mPaddingLeft = (int)mTextPaint.measureText("  " + mYMax);
        mPaddingRight = mPaddingLeft / 2;
        mPaddingTop = (int)((-mTextPaint.ascent() + mTextPaint.descent()) * 2);
        mPaddingBottom = mPaddingTop;
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
        calcPadding();
        
        int right = getMeasuredWidth();
        int bottom = getMeasuredHeight();
        
        Rect rect;
        Paint paint;
        
        //背景
        rect = new Rect(0, 0, right, bottom);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
        
        //X軸
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(0xaa, 0xaa, 0xaa));
        for(float x = mXMin; x <= mXMax; x += mXDistance){
            canvas.drawLine(x(x), y(mYMin), x(x), y(mYMax), paint);
            
            String label = formatNum(x);
            float tx = x(x) - mTextPaint.measureText(label) / 2;
            float ty = y(mYMin) - mTextPaint.ascent();
            canvas.drawText(label, tx, ty, mTextPaint);
        }
        
        //Y軸
        for(float y = mYMin; y <= mYMax; y += mYDistance){
            canvas.drawLine(x(mXMin), y(y), x(mXMax), y(y), paint);
            
            String label = formatNum(y) + " ";
            float tx = x(mXMin) - mTextPaint.measureText(label);
            float ty = y(y) - (mTextPaint.ascent() / 2) - mTextPaint.descent() / 2;
            canvas.drawText(label, tx, ty, mTextPaint);
        }
        
        //座標を折れ線で接続
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(0xff, 0x44, 0x44));
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        for(int i = 0; i < mPointList.size(); i++){
            if(i + 1 < mPointList.size()){
                Point p1 = mPointList.get(i);
                Point p2 = mPointList.get(i + 1);
                float startX = x(p1.x);
                float startY = y(p1.y);
                float stopX = x(p2.x);
                float stopY = y(p2.y);
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
        
        //各座標を描画
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0xff, 0x44, 0x44));
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setTextSize(mTextPaint.getTextSize());
        paint2.setColor(Color.rgb(0x00, 0x00, 0x00));
        for(Point point : mPointList){
            float x = x(point.x);
            float y = y(point.y);
            canvas.drawCircle(x, y, mPointRadius, paint);
            canvas.drawText(point.label, x - paint2.measureText(point.label) / 2, y - mPointRadius - paint2.descent(), paint2);
        }
        
        //外部から指定されて線を描画
        for(Line line: mLineList){
            paint = line.getPaint();
            if(paint == null){
                paint = new Paint();
                paint.setColor(Color.rgb(0xff, 0x44, 0x44));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
            }
            
            float startX = x(line.startX);
            float startY = y(line.startY);
            float stopX = x(line.stopX);
            float stopY = y(line.stopY);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            
            paint2 = new Paint();
            paint2.setColor(Color.rgb(0x00, 0x00, 0x00));
            paint2.setTextSize(mTextPaint.getTextSize());
            paint2.setAntiAlias(true);
            float tx = stopX - paint2.measureText(line.label);
            float ty = stopY + paint2.ascent() / 2;
            canvas.drawText(line.label, tx, ty, paint2);
        }
    }
    
    public void addLine(String label, float startX, float startY, float stopX, float stopY, Paint paint){
        Line line = new Line(label, startX, startY, stopX, stopY);
        line.setPaint(paint);
        mLineList.add(line);
        
        postInvalidate();
    }
    
    protected String formatNum(float num){
        return String.format("%d", (int)num);
    }
    
    /**
     * X座標をキャンバス上の座標に変換する
     * @param xx
     * @return
     */
    protected float x(float x){
        int width = getMeasuredWidth() - mPaddingLeft - mPaddingRight;
        float scale = width / (mXMax - mXMin);

        return (x - mXMin) * scale + mPaddingLeft;
    }
    
    protected float y(float y){
        int height = getMeasuredHeight() - mPaddingTop - mPaddingBottom;
        float scale = height / (mYMax - mYMin);
        
        return height - ((y - mYMin) * scale) + mPaddingTop;
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
    
    public static class Point{
        public final String label;
        public final float x;
        public final float y;
        public Point(String label, float x, float y){
            this.label = label;
            this.x = x;
            this.y = y;
        }
    }
    
    public static class Line{
        public final String label;
        public final float startX;
        public final float startY;
        public final float stopX;
        public final float stopY;
        public Paint mPaint = null;
        public Line(String label, float startX, float startY, float stopX, float stopY){
            this.label = label;
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }
        
        public void setPaint(Paint paint){
            mPaint = paint;
        }
        
        public Paint getPaint(){
            return mPaint;
        }
    }
}
