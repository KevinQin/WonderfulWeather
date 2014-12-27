package com.wonderfulWeather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.WeatherApplication;
import com.wonderfulWeather.app.model.City;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.Province;
import com.wonderfulWeather.app.model.WeatherDB;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by kevin on 2014/12/23.
 *
 * 网络返回结果处理
 */
public class Utility {


    public static int getDrawableResourceId(String key) {
        R.drawable drawables=new R.drawable();
        //默认的id
        int resId=R.drawable.logo;
        try {
            //根据字符串字段名，取字段
            Field field=R.drawable.class.getField(key);
            //取值
            resId=(Integer)field.get(drawables);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resId;
    }

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


    public  static boolean handleWeatherMoreResponse(Context context,String response)
    {
        try
        {
            if(!response.startsWith("{") ){return false;}
            HashMap<String,String> dict=new HashMap<String,String>();
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityid=weatherInfo.getString("cityid");
            dict.put("cityen_"+cityid,weatherInfo.getString("city_en"));
            dict.put("date",weatherInfo.getString("date"));
            dict.put("date_y",weatherInfo.getString("date_y"));
            dict.put("week",weatherInfo.getString("week"));
            dict.put("temp_2",weatherInfo.getString("temp2"));
            dict.put("temp_3",weatherInfo.getString("temp3"));
            dict.put("temp_4",weatherInfo.getString("temp4"));
            dict.put("temp_5",weatherInfo.getString("temp5"));
            dict.put("temp_6",weatherInfo.getString("temp6"));
            dict.put("weather_2",weatherInfo.getString("weather2"));
            dict.put("weather_3",weatherInfo.getString("weather3"));
            dict.put("weather_4",weatherInfo.getString("weather4"));
            dict.put("weather_5",weatherInfo.getString("weather5"));
            dict.put("weather_6",weatherInfo.getString("weather6"));

            dict.put("img_3",weatherInfo.getString("img3"));
            dict.put("img_4",weatherInfo.getString("img4"));
            dict.put("img_5",weatherInfo.getString("img5"));
            dict.put("img_6",weatherInfo.getString("img6"));
            dict.put("img_7",weatherInfo.getString("img7"));
            dict.put("img_8",weatherInfo.getString("img8"));
            dict.put("img_9",weatherInfo.getString("img9"));
            dict.put("img_10",weatherInfo.getString("img10"));
            dict.put("img_11",weatherInfo.getString("img11"));
            dict.put("img_12",weatherInfo.getString("img12"));

            dict.put("index",weatherInfo.getString("index"));
            dict.put("index_d",weatherInfo.getString("index_d"));

            dict.put("time_2",weatherInfo.getString("fchh")+":00");

            SaveWeatherMapInfo(context, dict);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
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
            String temp=weatherInfo.getString("temp");
            String wind=weatherInfo.getString("WD")+" "+weatherInfo.getString("WS");
            String pm25=" 湿度："+weatherInfo.getString("SD");//weatherInfo.getString("pm25");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("time")+":00";
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,temp,weatherDesp,publishTime,wind,pm25);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void SaveWeatherMapInfo(Context context,HashMap<String,String> map)
    {
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        Set<String> keys=map.keySet();
        for(Iterator it= keys.iterator();it.hasNext();)
        {
            String key=(String)it.next();
            String value= map.get(key);
            editor.putString(key,value);
        }
        editor.commit();
    }

    /**
     * 将服务器返回的天气信息存储到SharedPreferences文件中
     *
     * */
    public static  void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                        String temp1,String temp2, String realtemp,String weatherDesp,String publishTime,
                                        String wind,String pm25)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("temp",realtemp);
        editor.putString("wind",wind);
        editor.putString("pm25",pm25);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }


    public static int getScreenWidthForPx()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) WeatherApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Log.d("tag","width-px="+width+",height-px="+height+",density="+density+",densityDpi="+densityDpi);
        return  width; //(int)(width/density);
    }

    public static String formatDateForWeather(String dateStr,int addDay)
    {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            //SimpleDateFormat dd=new SimpleDateFormat("yyyy-MM-dd EE hh:mm:ss");
            SimpleDateFormat mf = new SimpleDateFormat("MM.dd");
            java.util.Calendar calstart = java.util.Calendar.getInstance();
            calstart.setTime(df.parse(dateStr));
            calstart.add(java.util.Calendar.DAY_OF_WEEK, addDay);
            return mf.format(calstart.getTime());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return dateStr;
        }

    }
}
