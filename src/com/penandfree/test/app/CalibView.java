package com.penandfree.test.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pnf.usb.lib.PNFPenEvent;

public class CalibView extends View{
	Context mContext;
	
	Paint mPaint;
	
	PointF	m_posCoordinate[] = null;
	PointF	m_posRestultPoint[] = null;
	PointF	m_posDrawCoordinate[] = null;
	int		m_posMode[] = null;
	
	int m_nCoordinateCounter;
	int m_nCoordinateMaxCounter;
	
	public CalibView(Context c)
	{
		super(c);
		initMyview(c);
	}
	
	public CalibView(Context c, AttributeSet attrs) 
	{
		super(c, attrs);
		initMyview(c);
		
	}
	
	void initMyview(Context c)
	{
		mContext = c;
		
		if(mPaint == null){
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.rgb(255, 255, 255));
			mPaint.setStrokeWidth(2);
		}
		
		InitData();
	}
	
	void InitData(){
		int isx = 60;
		int iex = Define.iDisGetWidth-60;
		
		int isy = 60;
		int iey = (Define.iDisGetHeight-48)-60;
		
		m_nCoordinateCounter = 1;
		m_nCoordinateMaxCounter = 4;

		if(Define.mPNFUsbLib.isDeviceLeft()){
			m_posDrawCoordinate = new PointF[]{
					new PointF(isx,isy), new PointF(iex,isy), 
					new PointF(iex,iey), new PointF(isx,iey)
			};
			m_posCoordinate = new PointF[]{
					new PointF(0,0)            , new PointF(Define.iDisGetWidth,0), 
					new PointF(Define.iDisGetWidth,Define.iDisGetHeight), new PointF(0,Define.iDisGetHeight) 
			};
		}else{
			m_posDrawCoordinate = new PointF[]{
					new PointF(iex,isy), new PointF(isx,isy), 
					new PointF(isx,iey), new PointF(iex,iey)
			};
			m_posCoordinate = new PointF[]{
					new PointF(Define.iDisGetWidth,0), new PointF(0,0), 
					new PointF(0,Define.iDisGetHeight), new PointF(Define.iDisGetWidth,Define.iDisGetHeight) 
			};
		}

		m_posRestultPoint = new PointF[]{
				new PointF(), new PointF(), 
				new PointF(), new PointF()
		};
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(m_nCoordinateCounter >=0 && m_nCoordinateCounter <m_nCoordinateMaxCounter)			
		{
			canvas.drawLine(m_posDrawCoordinate[m_nCoordinateCounter].x - 40, m_posDrawCoordinate[m_nCoordinateCounter].y, 
					m_posDrawCoordinate[m_nCoordinateCounter].x + 40, m_posDrawCoordinate[m_nCoordinateCounter].y, mPaint);
			
			canvas.drawLine(m_posDrawCoordinate[m_nCoordinateCounter].x, m_posDrawCoordinate[m_nCoordinateCounter].y - 40, 
					m_posDrawCoordinate[m_nCoordinateCounter].x, m_posDrawCoordinate[m_nCoordinateCounter].y + 40, mPaint);

			canvas.drawCircle(m_posDrawCoordinate[m_nCoordinateCounter].x, m_posDrawCoordinate[m_nCoordinateCounter].y, 6, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return true;
	}
	
	void setLeftDevice(){
		Define.mPNFUsbLib.setDeviceLeft(true);
		
		InitData();
		invalidate();
	}
	
	void setRightDevice(){
		Define.mPNFUsbLib.setDeviceLeft(false);
		
		InitData();
		invalidate();
	}
	
	void setReSetCali(){
		InitData();
		invalidate();
	}
	
	void onPnfEventEx(int what, int RawX, int RawY ,Object obj)
	{
		if(what == PNFPenEvent.PNF_USB_EVENT_UP)
		{
			if(m_nCoordinateCounter < m_nCoordinateMaxCounter)							
			{
				m_posRestultPoint[m_nCoordinateCounter] = new PointF(RawX ,RawY);
				
				if(++m_nCoordinateCounter == m_nCoordinateMaxCounter){
					float distanceW = 0;
					float distanceH = 0;
					if(Define.mPNFUsbLib.isDeviceLeft()){
						distanceW = m_posRestultPoint[2].x-m_posRestultPoint[1].x;
						distanceH = m_posRestultPoint[3].y-m_posRestultPoint[2].y;
						m_posRestultPoint[0] = new PointF(m_posRestultPoint[3].x-distanceW,m_posRestultPoint[1].y+distanceH);
					}else{
						distanceW = m_posRestultPoint[1].x-m_posRestultPoint[2].x;
						distanceH = m_posRestultPoint[3].y-m_posRestultPoint[2].y;
						m_posRestultPoint[0] = new PointF(m_posRestultPoint[3].x+distanceW,m_posRestultPoint[1].y+distanceH);
					}
				}
			}
			
			if(m_nCoordinateCounter >= m_nCoordinateMaxCounter)			
			{
				if(mDataListener != null) {
					mDataListener.onResultFinish(mContext ,m_posRestultPoint ,m_posCoordinate);
				}
			}else{
				invalidate();
			}
		}
	}
	
	private OnDataListener mDataListener = null;
	public interface OnDataListener {
		public abstract void onResultFinish(Context m_Context ,PointF[] posPhysicalPen ,PointF[] posScreen);
	}
	public void setOnDataListener(OnDataListener listener) {
		mDataListener = listener;
	}
}