package com.penandfree.test.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CalibActivity extends Activity
{
	ImageView calibgImageView;

	Button leftDeviceBtn;
	Button rightDeviceBtn;
	Button reSetBtn;
	Button exitBtn;

	CalibView calibView;
	boolean isDisconnect;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.calibration);

		calibView = (CalibView) findViewById(R.id.calibView);

		calibgImageView = (ImageView) findViewById(R.id.calibgImageView);
		leftDeviceBtn = (Button) findViewById(R.id.leftDeviceBtn);
		rightDeviceBtn = (Button) findViewById(R.id.rightDeviceBtn);
		reSetBtn = (Button) findViewById(R.id.reSetBtn);
		exitBtn = (Button) findViewById(R.id.exitBtn);

		setDataListener();
	}

	void setDataListener(){
		calibView.setOnDataListener(new CalibView.OnDataListener() {
			@Override
			public void onResultFinish(Context m_Context ,PointF[] posPhysicalPen ,PointF[] posScreen) {
				Define.mPNFUsbLib.setDevicePosition(posPhysicalPen ,posScreen);
				setResult(RESULT_OK,null);
				finish();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Define.mPNFUsbLib.ConnectUsb(mCoordiHandler);
	}


	@Override
	protected void onPause() 
	{
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();		
	}

	public void leftDeviceBtnClicked(View v){
		calibView.setLeftDevice();
	}

	public void rightDeviceBtnClicked(View v){
		calibView.setRightDevice();
	}

	public void reSetBtnClicked(View v){
		calibView.setReSetCali();
	}

	public void exitBtnClicked(View v){
		finish();
	}

	Handler mCoordiHandler = new Handler() 
	{        
		@Override       
		public void handleMessage(Message msg) 
		{
			calibView.onPnfEventEx(msg.what ,msg.arg1 ,msg.arg2 ,msg.obj);
		}
	};
}
