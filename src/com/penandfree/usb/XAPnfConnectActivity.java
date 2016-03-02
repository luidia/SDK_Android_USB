package com.penandfree.usb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.penandfree.test.app.MainActivity;
import com.pnf.usb.lib.PNFUsbLibData;


public class XAPnfConnectActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		OnInit();
	}

	void OnInit()
	{
		if(PNFUsbLibData.isLaunch()){
			finish();
		}else{
			finish();
			startActivity(new Intent(XAPnfConnectActivity.this, MainActivity.class));
		}
	}
}

