package com.penandfree.test.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pnf.usb.lib.PNFPenEvent;
import com.pnf.usb.lib.XAUsbFreeCore;

public class DrawActivity extends Activity {
	public final int DLG_SELECT_MODE = 1;
	public final int REQUEST_USB_COORDINATE = 4;
	
	MyView myview;
	//=======================USB=======================================    
	public static XAUsbFreeCore g_UsbFreeCore = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.draw);
		myview = (MyView) findViewById(R.id.myView);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {		 		
		super.onResume();
		Define.mPNFUsbLib.ConnectUsb(mUSBHandle);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() 
	{
		finish();
		return;
	}
	
	public void ClearAllBtn(View v)
    {
		myview.btnEraser();
    }
	
	public void CloseBtn(View v)
    {
		finish();
    }
	
	Handler mUSBHandle = new Handler() 
	{        
		@Override       
		public void handleMessage(Message msg) 
		{
			onPenEvent(msg.what ,msg.arg1 ,msg.arg2 ,msg.obj);
		}
	};
	
	void onPenEvent(int what, int RawX, int RawY ,Object obj)
	{
		PointF pos = Define.mPNFUsbLib.GetCoordinatePostionXY4Cal(RawX,RawY);
		float x =  pos.x;
		float y = pos.y;
		
		switch(what)
		{
			case PNFPenEvent.PNF_USB_EVENT_DOWN:
				myview.DoMouseDown(x,y);
				break;
			case PNFPenEvent.PNF_USB_EVENT_MOVE:
				myview.DoMouseDragged(x,y);
				break;
			case PNFPenEvent.PNF_USB_EVENT_UP:
				myview.DoMouseUp(x,y);
				break;
			case PNFPenEvent.PNF_USB_EVENT_CONNECTED:
				break;
			case PNFPenEvent.PNF_USB_EVENT_DISCONNECTED:
				break;
		}
	}
}