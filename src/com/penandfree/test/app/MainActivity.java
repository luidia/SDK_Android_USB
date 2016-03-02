package com.penandfree.test.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pnf.usb.lib.PNFPenEvent;
import com.pnf.usb.lib.PNFUsbLib;
import com.pnf.usb.lib.PenDataClass;

public class MainActivity extends Activity {
	LinearLayout modelCodeLay;
	
	final int REQUEST_USB_COORDINATE = 0;
	
	final int ALERTVIEW_CHANGE_LEFT_DEVICE_TAG    = 10;
	final int ALERTVIEW_CHANGE_RIGHT_DEVICE_TAG   = 11;
	final int ALERTVIEW_EXIT_TAG   = 12;
	
	boolean m_penConntectedStatus;
    int temperatureCnt;
    int penErrorCnt;
	
    Button[] penDataBtn = new Button[7];
	enum UIControlIndex {
		ModelCode,
		Status,
	    Temperature,
	    RawX,
	    RawY,
	    ConvX,
	    ConvY
	};
	
	UIControlIndex ControlIndex = UIControlIndex.ModelCode;
	Button leftDeviceBtn;
	Button rightDeviceBtn;
	TextView debugTextView;
	
	boolean isStopPen = false;
	boolean isHasFocus = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Point LCDSize = new Point();
		((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(LCDSize);
		Define.iDisGetWidth = LCDSize.x;
		Define.iDisGetHeight = LCDSize.y;
		
        setContentView(R.layout.main);
        
        modelCodeLay = (LinearLayout) findViewById(R.id.modelCodeLay);
        
        leftDeviceBtn = (Button) findViewById(R.id.leftDeviceBtn);
        rightDeviceBtn = (Button) findViewById(R.id.rightDeviceBtn);
        debugTextView = (TextView) findViewById(R.id.debugTextView);
        
        for(int i=0;i<penDataBtn.length;i++){
			String imgStr = "penData"+(i+1)+"Btn";
			penDataBtn[i] = (Button) findViewById(ResourcesIdNameToId(this, imgStr));
		}
        

        Define.mPNFUsbLib = new PNFUsbLib(this ,getApplicationContext());
        
        changeDevicePosition(true ,true);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		Define.mPNFUsbLib.ConnectUsb(mUSBHandle);
		temperatureCnt = 0;
		if(Define.mPNFUsbLib.isPenMode()){
			m_penConntectedStatus = true;
		}else{
			m_penConntectedStatus = false;
			addDebugText("PenController is not set");
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop(){
    	super.onStop();
    }
    
    @Override
	public void onDestroy() {
		finish();
		super.onDestroy();
	}
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
		isHasFocus = hasFocus;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
    
    @Override
	public void onBackPressed() 
	{
    	MessageBox("TestApp Close?", "YES", "NO" ,ALERTVIEW_EXIT_TAG);
		return;
	}
    
    void changeDevicePosition(boolean left ,boolean _init){
        leftDeviceBtn.setSelected(left);
        rightDeviceBtn.setSelected(!left);
        
        if (_init) {
        	PointF[] calScreen = new PointF[4];
            PointF[] calResultPoint = new PointF[4];
            if (leftDeviceBtn.isSelected()) {
            	calScreen[0] = new PointF(0,0);
            	calScreen[1] = new PointF(Define.iDisGetWidth,0); 
            	calScreen[2] = new PointF(Define.iDisGetWidth,Define.iDisGetHeight);
            	calScreen[3] = new PointF(0,Define.iDisGetHeight);
            	
            	calResultPoint[0] = new PointF(3630 ,7);
    			calResultPoint[1] = new PointF(6022 ,2233);
    			calResultPoint[2] = new PointF(2727 ,5768);
    			calResultPoint[3] = new PointF(335	,3542);
            }
            else {
            	calScreen[0] = new PointF(Define.iDisGetWidth,0);
            	calScreen[1] = new PointF(0,0); 
            	calScreen[2] = new PointF(0,Define.iDisGetHeight);
            	calScreen[3] = new PointF(Define.iDisGetWidth,Define.iDisGetHeight);
            	
            	calResultPoint[0] = new PointF(3478	,-5);
                calResultPoint[1] = new PointF(1291	,2492);
                calResultPoint[2] = new PointF(4917 ,5656);
                calResultPoint[3] = new PointF(7104 ,3159);
            }
            
            Define.mPNFUsbLib.setDevicePosition(calResultPoint, calScreen);
        }
    }

    void addDebugText(String text) {
    	String t = debugTextView.getText() + "\n" + text;
        debugTextView.setText(t);
    }

    void lazyCheckCalibration() {
    	if(!Define.mPNFUsbLib.existCalibrationInfo())
        {
    		addDebugText("lollol pen");
            addDebugText("calibration 데이터 없음");
        }
        else {
        	addDebugText("lollol pen");
            addDebugText("calibration 데이터 있음");
        }
        penDataBtn[0].setText("lollol pen");
    }
    
    public void calibrationClicked(View v) {
    	Intent intent = new Intent(MainActivity.this, CalibActivity.class);
		startActivityForResult(intent, REQUEST_USB_COORDINATE);
    }
    
    void closeCalibViewController(boolean left) {
        changeDevicePosition(left ,false);
    }
    
    public void leftDeviceClicked(View v) {
    	MessageBox("왼쪽 Default 값으로 변경됩니다" ,"확인" ,null ,ALERTVIEW_CHANGE_LEFT_DEVICE_TAG);
    	
        changeDevicePosition(true ,true);
    }

    public void rightDeviceClicked(View v) {
    	MessageBox("오른쪽 Default 값으로 변경됩니다" ,"확인" ,null ,ALERTVIEW_CHANGE_RIGHT_DEVICE_TAG);
        changeDevicePosition(false ,true);
    }

    public void drawingClicked(View v) {
    	startActivity(new Intent(MainActivity.this, DrawActivity.class));
    }

    public void stopPenClicked(View v) {
    	isStopPen = true;
    }

    public void reStartPenClicked(View v) {
    	isStopPen = false;
    }
    
    
    
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == REQUEST_USB_COORDINATE){
			if(resultCode == Activity.RESULT_OK){
//				if(data.getBooleanExtra("disconnect", false)){//disconnect
//				}else{
//					closeCalibViewController(data.getBooleanExtra("left", false));
//				}
				
				closeCalibViewController(Define.mPNFUsbLib.isDeviceLeft());
			}
		}
	}
    
    Handler mUSBHandle = new Handler() 
	{        
		@Override       
		public void handleMessage(Message msg) 
		{
			if(isStopPen){
				return;
			}
			onPenEvent(msg.what ,msg.arg1 ,msg.arg2 ,msg.obj);
		}
	};
	
	void onPenEvent(int what, int RawX, int RawY ,Object obj)
	{
		PenDataClass penData = (PenDataClass)obj;
		int Temperature = penData.Pen_Temperature;
		
		PointF pos = Define.mPNFUsbLib.GetCoordinatePostionXY4Cal(RawX,RawY);
		float x =  pos.x;
		float y = pos.y;
		
		PointF RawPoint = new PointF(RawX ,RawY);
		PointF ConvPoint = new PointF(x ,y);
		
		int penState = 0;
		
		switch(what)
		{
			case PNFPenEvent.PNF_USB_EVENT_DOWN:
				penState = 1;
				break;
			case PNFPenEvent.PNF_USB_EVENT_MOVE:
				penState = 2;
				break;
			case PNFPenEvent.PNF_USB_EVENT_UP:
				penState = 3;
				break;
			case PNFPenEvent.PNF_USB_EVENT_HOVERING:
				penState = 4;
				break;
			case PNFPenEvent.PNF_USB_EVENT_CONNECTED:
				penState = 0;
				
				addDebugText("CONNECTED");
				break;
			case PNFPenEvent.PNF_USB_EVENT_DISCONNECTED:
				penState = 0;
				
				addDebugText("FAIL_LISTENING");
				Toast.makeText(MainActivity.this ,"abnormal connect. please reconnect device", Toast.LENGTH_SHORT).show();
				break;
			case PNFPenEvent.PNF_USB_EVENT_ERROR:
				penErrorCnt++;
	            if (penErrorCnt > 5) {
	            	Toast.makeText(getApplicationContext(),"PEN_RMD_ERROR", Toast.LENGTH_SHORT).show();
	            	addDebugText("PEN_RMD_ERROR");
	                penErrorCnt = 0;
	            }
				break;
		}
		
		penDataBtn[1].setText("  "+penState);
		penDataBtn[2].setText("  "+Temperature);
		penDataBtn[3].setText("  "+RawPoint.x);
		penDataBtn[4].setText("  "+RawPoint.y);
		penDataBtn[5].setText("  "+ConvPoint.x);
		penDataBtn[6].setText("  "+ConvPoint.y);
	}
	
	int ResourcesIdNameToId(Context context ,String imgName){
		return context.getResources().getIdentifier(imgName, "id", context.getPackageName());
	}
	
	boolean IsEmpty(final String _sStr)
	{
		if(_sStr == null || _sStr.trim().length()==0 || _sStr.trim()=="")
			return true;
		
		return false;
	}
	
	void MessageBox(final String _sContent, final String _sPositiveBtText, final String _sNegativeBtText ,final int alertTag)
	{
		if(!isHasFocus) return;

		AlertDialog.Builder mAlertBox =  new AlertDialog.Builder(this);;
		mAlertBox.setMessage(_sContent);	

		if(!IsEmpty(_sPositiveBtText))
		{
			mAlertBox.setPositiveButton(_sPositiveBtText, new OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					if(alertTag == ALERTVIEW_EXIT_TAG){
						System.exit(0);
					}
				}
			});
		}

		if(!IsEmpty(_sNegativeBtText))
		{
			mAlertBox.setNegativeButton(_sNegativeBtText, new OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{													
				}
			});
		}

		mAlertBox.show();		
	}
}
