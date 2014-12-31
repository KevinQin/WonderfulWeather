package com.wonderfulWeather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import com.wonderfulWeather.app.receiver.AutoUpdateReceiver;
import com.wonderfulWeather.app.util.CommonUtility;
import com.wonderfulWeather.app.util.HttpCallbackListener;
import com.wonderfulWeather.app.util.HttpUtil;
import com.wonderfulWeather.app.util.Utility;

/**
 * Created by kevin on 2014/12/23.
 * 后台自动更新天气服务
 *
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        });

        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=30 * 60 * 1000; //半小时
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather()
    {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("weather_code","");
        String address= CommonUtility.getWeatherUrl(weatherCode);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        address = CommonUtility.getWeatherUrl2(weatherCode);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherMoreResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
