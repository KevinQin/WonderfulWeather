package com.wonderfulWeather.app.util;

import android.text.TextUtils;
import com.wonderfulWeather.app.model.City;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.Province;
import com.wonderfulWeather.app.model.WeatherDB;

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

}
