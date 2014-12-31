package com.wonderfulWeather.app.util;

/**
 * Created by kevin on 2014/12/25.
 */
public class CommonUtility {

    public final static String SHARED_PREF_NAME="comm_pref";

    /*
    * 定位完成后发送的消息
    * */
    public final static int LOCATION_GET_MESSAGE=0x8000;
    public final static int NETWORK_ERRPR=0x8001;
    public final static int GPS_ERROR=0x8002;

    /*
    * 是否首次进入APP
    * */
    public final static String IS_FIRST_IN="isFirstIn";

    /**
     * 服务器地址
     * */
    final static String SERVER_URL="http://edmp.cc:9090/HttpService.ashx";

    /*
    * 省参数
    * */
    public final static String PROVICE_URL=SERVER_URL + "?fn=1";

    /*
    * 城市参数
    * */
    public final static String CITY_URL=SERVER_URL + "?fn=2&code=";

    /*
   * 县区参数
   * */
    public final static String COUNTY_URL=SERVER_URL + "?fn=3&code=";

    /*
    * 查询热门城市
    * */
    public  final static  String HOT_CITY_URL=SERVER_URL + "?fn=4";

    /*
   * 查询天气简要信息
   * */
    final static  String WEATHER_URL=SERVER_URL + "?fn=11&code=";

    /*
    * 查询天气全部信息
    * */
    final static  String WEATHER_URL_2=SERVER_URL + "?fn=12&code=";

    /*
    * 得到天气简要信息URL
    * */
    public static String getWeatherUrl(String code)
    {
        return WEATHER_URL+code;
    }

    /*
    * 得到天气详细信息URL
    * */
    public static String getWeatherUrl2(String code)
    {
        return WEATHER_URL_2+code;
    }
}
