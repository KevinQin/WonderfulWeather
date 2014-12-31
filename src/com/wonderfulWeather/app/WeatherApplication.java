package com.wonderfulWeather.app;

import android.app.Application;
import android.content.Context;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.util.LocationUtility;

/**
 * Created by kevin on 2014/12/26.
 */
public class WeatherApplication extends Application {
    private static Context context;
    private static County currentCounty;

    public void onCreate()
    {
        context=getApplicationContext();
    }

    public static Context getContext()
    {
        return  context;
    }

    public static void setCurrentCounty(County c)
    {
        currentCounty=c;
    }

    public static County getCurrentCounty()
    {
        return currentCounty;
    }

}
