package com.wonderfulWeather.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by kevin on 2014/12/26.
 */
public class WeatherApplication extends Application {
    private static Context context;

    public void onCreate()
    {
        context=getApplicationContext();
    }

    public  static Context getContext()
    {
        return  context;
    }
}
