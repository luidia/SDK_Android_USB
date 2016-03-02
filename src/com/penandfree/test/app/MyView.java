package com.penandfree.test.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View{
	Context mContext;
	
	Paint mPaint;
	Bitmap mBitmap;
	Canvas mCanvas;
	Path mPath;
	Path rectPath;
	
	public Paint EraserPaint;
	
	int iDrawCnt = 0;
	
	public MyView(Context c)
	{
		super(c);
		initMyview(c);
	}
	
	public MyView(Context c, AttributeSet attrs) 
	{
		super(c, attrs);
		initMyview(c);
		
	}
	
	void initMyview(Context c)
	{
		mContext = c;
		
		mPath = new Path();
		rectPath = new Path();
		
		if(mPaint == null){
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setColor(Color.rgb(100, 100, 100));
			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(1);
			mPaint.setAlpha(255);
		}

		if(EraserPaint == null){
			EraserPaint = new Paint();
			EraserPaint.setStyle(Paint.Style.FILL);
			EraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		}
		
		
		if(mBitmap == null){
			mBitmap = Bitmap.createBitmap(Define.iDisGetWidth, Define.iDisGetHeight, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
		}
	}
	
	@Override
	protected void onDraw(Canvas c) {
		c.drawBitmap(mBitmap, 0, 0, null);
	}
	
	
	PointF	previousPoint1 = new PointF();
	PointF	previousPoint2 = new PointF();
	PointF	currentPoint = new PointF();
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			DoMouseDown(x ,y);
			break;
		case MotionEvent.ACTION_MOVE:
			DoMouseDragged(x ,y);
			break;
		case MotionEvent.ACTION_UP:
			DoMouseUp(x ,y);
			break;
		}
		return true;
	}
	
	void DoMouseDown(float x ,float y){
		iDrawCnt = 0;
		mPath.reset();
		
		previousPoint1 = new PointF(x ,y);
		previousPoint2 = new PointF(x ,y);
		currentPoint = new PointF(x ,y);
	}
	
	void DoMouseDragged(float x ,float y){
		if(iDrawCnt == 0 ){
			previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
			currentPoint = new PointF(x ,y);
		}else{
			previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
			previousPoint1 = new PointF(currentPoint.x ,currentPoint.y);
			currentPoint = new PointF(x ,y);
		}
		
		PointF mid1 = BizMidPoint(previousPoint1, previousPoint2);
		PointF mid2 = BizMidPoint(currentPoint, previousPoint1);
		
		mPath.moveTo(mid1.x, mid1.y);
		mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		mCanvas.drawPath(mPath, mPaint);
		iDrawCnt++;
		
		rectPath.reset();
		rectPath.moveTo(mid1.x, mid1.y);
		rectPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		
		invalidate();
	}

	void DoMouseUp(float x ,float y){
		previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
		previousPoint1 = new PointF(currentPoint.x ,currentPoint.y);
		currentPoint = new PointF(x ,y);
		
		PointF mid1 = BizMidPoint(previousPoint1, previousPoint2);
		PointF mid2 = BizMidPoint(currentPoint, previousPoint1);
		
		mPath.moveTo(mid1.x, mid1.y);
		mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		mCanvas.drawPath(mPath, mPaint);
		
		invalidate();
	}
	
	void btnEraser()
	{
		mCanvas.drawPaint(EraserPaint);
		invalidate();
	}
	
	PointF BizMidPoint(PointF pt,PointF pt2)
	{
		return new PointF((pt.x + pt2.x)/2, (pt.y + pt2.y)/2);
	}
	
	Rect RectFtoRect(RectF rectf,float thick)
	{
		Rect rect = new Rect((int)(rectf.left-thick) ,(int)(rectf.top-thick) ,(int)(rectf.right+thick) ,(int)(rectf.bottom+thick));
		return rect;
	}
}