package com.wonderfulWeather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.wonderfulWeather.app.model.City;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.Province;
import com.wonderfulWeather.app.model.WeatherDB;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevin on 2014/12/23.
 *
 * 网络返回结果处理
 */
public class Utility {

    /**
     * 解析及处理服务器返回的省级数据
     * */
    public synchronized static boolean handlerProvinceResponse(WeatherDB weatherDB,String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allProvinces=response.split(",");
            if( allProvinces.length>0)
            {
                for(String p:allProvinces)
                {
                    String [] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析及处理城市信息
     * */
    public synchronized static boolean handlerCityResponse(WeatherDB weatherDB, String response,int provinceId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String [] allCities=response.split(",");
            if( allCities.length>0)
            {
                for(String c:allCities)
                {
                    String [] array=c.split("\\|");
                    City city=new City();

                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);

                    weatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /*
    * 分析处理县区数据
    * */
    public synchronized static boolean handlerCountyResponse(WeatherDB weatherDB,String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String [] allCounties=response.split(",");
            if(allCounties.length>0)
            {
                for (String c:allCounties)
                {
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    county.setCityId(cityId);

                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出来的数据存储到本地
     * */
    public static void handleWeatherResponse(Context context,String response)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的天气信息存储到SharedPreferences文件中
     *
     * */
    public static  void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                        String temp1,String temp2,String weatherDesp,String publishTime)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }

}
