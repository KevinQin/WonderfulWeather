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
import com.wonderfulWeather.app.model.County;
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
                    county.setCode(array[0]);
                    county.setCounty(array[1]);
                    county.setCountyEn(array[2]);
                    county.setCity(array[3]);
                    county.setCityEn(array[4]);
                    county.setProvince(array[5]);
                    county.setProvinceEn(array[6]);
                    county.setHot(false);
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
    public  static boolean handleWeatherMoreResponse(Context context,String response)
    {
        try
        {
            if(!response.startsWith("{") ){return false;}
            HashMap<String,String> dict=new HashMap<String,String>();
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityid=weatherInfo.getString("cityid");
            dict.put("city_en_"+cityid,weatherInfo.getString("city_en"));
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
    public static boolean handleWeatherResponse(Context context,String response)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(response);
            HashMap<String,String> dict=new HashMap<String,String>();
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
            dict.put("city_selected","true");
            dict.put("city_name",weatherInfo.getString("city"));
            dict.put("weather_code", weatherInfo.getString("cityid"));
            dict.put("temp1",weatherInfo.getString("temp1"));
            dict.put("temp2",weatherInfo.getString("temp2"));
            dict.put("temp",weatherInfo.getString("temp"));
            dict.put("wind",weatherInfo.getString("WD")+" "+weatherInfo.getString("WS"));
            dict.put("pm25"," 湿度："+weatherInfo.getString("SD"));
            dict.put("weather_desp",weatherInfo.getString("weather"));
            dict.put("publish_time",weatherInfo.getString("time")+":00");
            dict.put("current_date",sdf.format(new Date()));
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
     * 将服务器返回的天气信息存储到SharedPreferences文件中
     *
     * */
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

    public static String formatDateForWeek(String dateStr,int addDay)
    {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            //SimpleDateFormat dd=new SimpleDateFormat("yyyy-MM-dd EE hh:mm:ss");
            //SimpleDateFormat mf = new SimpleDateFormat("MM.dd");
            java.util.Calendar calstart = java.util.Calendar.getInstance();
            calstart.setTime(df.parse(dateStr));
            calstart.add(java.util.Calendar.DAY_OF_WEEK, addDay);
            int n=calstart.get(Calendar.DAY_OF_WEEK);
            String result="";
            switch (n)
            {
                case 1:
                    result="星期日";
                    break;
                case 2:
                    result="星期一";
                    break;
                case 3:
                    result="星期二";
                    break;
                case 4:
                    result="星期三";
                    break;
                case 5:
                    result="星期四";
                    break;
                case 6:
                    result="星期五";
                    break;
                case 7:
                    result="星期六";
                    break;
            }
            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return dateStr;
        }
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

    public static boolean IsCurrentLocation( County c)
    {
        try {
            return c.getCode().equals(WeatherApplication.getCurrentCounty().getCode());
        }
        catch (Exception ex)
        {
            LogUtil.d("is Location,can't to get location information",ex.toString());
            return  false;
        }
    }
}
