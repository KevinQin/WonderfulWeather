package com.wonderfulWeather.app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.WeatherApplication;
import com.wonderfulWeather.app.util.CommonUtility;
import com.wonderfulWeather.app.util.LocationUtlity;
import com.wonderfulWeather.app.view.Dialog;

public class SplashActivity extends Activity {
	boolean isFirstIn=false;
	
	private static final int GO_HOME=1000;
	private static final int GO_GUIDE=1001;
	
	private static final long SPLASH_DELAY_MILLIS=2000;

	
    private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			Intent intent=null;
			switch(msg.what)
			{
				case GO_HOME:
					intent =new Intent(SplashActivity.this,WeatherActivity.class);
					startActivity(intent);
					finish();
					break;
				case GO_GUIDE:
					intent=new Intent(SplashActivity.this,GuideActivity.class);
					startActivity(intent);
					finish();
					break;
				/*case CommonUtility.GPS_ERROR:
					final Dialog dig2=new Dialog(SplashActivity.this);
					dig2.Init("提示", "请打开GPS设备以进行定位", false, "是", "否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LocationUtlity.openGPS(WeatherApplication.getContext());
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dig2.dismiss();
						}
					});
					dig2.show();
					break;
				case CommonUtility.NETWORK_ERRPR:
					final Dialog dig=new Dialog(SplashActivity.this);
					dig.Init("提示", "请打开网络服务，否则无法获取天气信息", false, "是", "否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LocationUtlity.openNet(WeatherApplication.getContext());
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dig.dismiss();
							finish();
						}
					});
					dig.show();
					break;*/
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		init();
	}
	
	private void init()
	{
		SharedPreferences preferences=getSharedPreferences(CommonUtility.SHARED_PREF_NAME,MODE_PRIVATE);
		isFirstIn=preferences.getBoolean(CommonUtility.IS_FIRST_IN, true);
		if(!isFirstIn)
		{
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}
		else
		{
			mHandler.sendEmptyMessageDelayed(GO_GUIDE,SPLASH_DELAY_MILLIS);
		}
	}
}
