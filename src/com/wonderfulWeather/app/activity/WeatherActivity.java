package com.wonderfulWeather.app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.WeatherApplication;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.WeatherDB;
import com.wonderfulWeather.app.service.AutoUpdateService;
import com.wonderfulWeather.app.util.*;

import java.util.Date;
import java.util.List;

/**
 * Created by kevin on 2014/12/23.
 *
 * Weather活动
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener() ;
    private com.wonderfulWeather.app.util.LocationListener locationListener=null;

    private LinearLayout weatherInfoLayout;

    private LinearLayout weatherMorLayout;

    private TextView cityNameText;

    private TextView publishText;

    private TextView weatherDespText;

    private TextView windText;

    private TextView pm25Text;

    private TextView currtemp;

    private Button switchCity;

    private boolean isNeedQuery=false;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== CommonUtility.NETWORK_ERRPR) {
               Toast.makeText(WeatherApplication.getContext(),"为了更快更精准的获取天气信息，建议打开WIFI网络",Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //开始定位
        initLocation();

        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
        weatherMorLayout=(LinearLayout)findViewById(R.id.weather_more_info);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        windText=(TextView)findViewById(R.id.wind);
        pm25Text=(TextView)findViewById(R.id.pm25);
        currtemp = (TextView)findViewById(R.id.currtemp);
        switchCity=(Button)findViewById(R.id.switch_city);
        String countyCode=getIntent().getStringExtra("county_code");

        if(!TextUtils.isEmpty(countyCode))
        {
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            weatherMorLayout.setVisibility(View.INVISIBLE);
            publishText.setText("同步中...");
            isNeedQuery = true;
           if("000000000".equals(countyCode)) {
               if(WeatherApplication.getCurrentCounty()!=null)
               {
                   queryWeatherInfo(WeatherApplication.getCurrentCounty().getCode());
               }
               else
               {
                   Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                   startActivity(intent);
                   finish();
               }
           }else{
                queryWeatherInfo(countyCode);
           }
        }
        else
        {
            try {
                showWeather();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        switchCity.setOnClickListener(this);
    }


    private void initLocation()
    {
            mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
            mLocationClient.registerLocationListener( myListener );    //注册监听函数
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
            option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
            option.setScanSpan(30*60*1000);//设置发起定位请求的间隔时间为5000ms
            option.setIsNeedAddress(true);//返回的定位结果包含地址信息
            option.setAddrType("all");
            option.setProdName(WeatherApplication.getContext().getResources().getString(R.string.app_name));
            option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
            mLocationClient.setLocOption(option);
    }


    public void BeginRequestLocation(com.wonderfulWeather.app.util.LocationListener listener)
    {
        locationListener=listener;
        mLocationClient.start();
        LogUtil.d("location","Start get Location");
        if (mLocationClient != null && mLocationClient.isStarted()) {
            int code= mLocationClient.requestLocation();
            LogUtil.d("location","response code:"+code);
        }
        else {
            locationListener.getLocationError("LocSDK5:locClient is null or not started");
        }
        mLocationClient.stop();
    }

    /**
     * 查询天气代码所对应的天气信息
     * */
    private void queryWeatherInfo(String weatherCode)
    {
        queryFromServer(CommonUtility.getWeatherUrl(weatherCode),"weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     * */
    private void queryFromServer(final String address,final String type)
    {
        LogUtil.d("HTTP",address);

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                LogUtil.d(type, response);

                if ("weatherCode".equals(type)) {
                    if (Utility.handleWeatherResponse(WeatherActivity.this, response)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                } else if ("weatherMore".equals(type)) {
                    if (Utility.handleWeatherMoreResponse(WeatherActivity.this, response)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeatherMoreDay();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeatherMoreDay()
    {

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String cityCode=prefs.getString("weather_code","");
        if(TextUtils.isEmpty(prefs.getString("cityen_"+cityCode,"'"))){
            findViewById(R.id.weather_more_info).setVisibility(View.INVISIBLE);
            return;
        }


        LinearLayout list_layout = (LinearLayout) findViewById(R.id.weather_more_info);
        list_layout.removeAllViews();
        LayoutInflater inflater=LayoutInflater.from(this);
        for(int i=2;i<=6;i++)
        {

            String [] tempArray=prefs.getString("temp_"+i,"").replace("℃","").split("~");
            int temp1=Integer.parseInt(tempArray[0]);
            int temp2 = Integer.parseInt(tempArray[1]);
            if(temp1>temp2)
            {
                int _temp=temp1;
                temp1=temp2;
                temp2=_temp;
            }

            String image_b="d";
            String imageKey="d00";
            if(new Date().getHours()>18 || new Date().getHours()<7)
            {
               image_b="n";
            }
            imageKey="img_" + (i*2-1);
            String img_num=prefs.getString(imageKey, "");
            if(img_num.length()==1){
                imageKey = image_b+"0"+img_num;
            }
            else
            {
                imageKey = image_b+img_num;
            }

            LinearLayout list_item=(LinearLayout)inflater.inflate(R.layout.day_weather, null);
            ImageView img=(ImageView)list_item.findViewById(R.id.weather_small_day_icon);
            img.setImageResource(Utility.getDrawableResourceId(imageKey));
            ((TextView)list_item.findViewById(R.id.weather_small_day_temp1)).setText(temp2+"℃");
            ((TextView)list_item.findViewById(R.id.weather_small_day_temp2)).setText(temp1+"℃");

            TextView days=(TextView)list_item.findViewById(R.id.weather_small_day_text);

            days.setText(Utility.formatDateForWeather(prefs.getString("date_y",""),i-1));
            list_layout.addView(list_item);
            if(i<6) {
                LinearLayout view = new LinearLayout(this);
                RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
                para.addRule(RelativeLayout.CENTER_HORIZONTAL);
                view.setBackgroundColor(Color.argb(200, 255, 255, 255));
                list_layout.addView(view, para);
            }
        }
        weatherMorLayout.setVisibility(View.VISIBLE);

    }

    private void showWeather()
    {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);

        String cityCode=prefs.getString("weather_code","");
        cityNameText.setText(prefs.getString("city_name",""));
        currtemp.setText(prefs.getString("temp",""));

        int temp1=Integer.parseInt(prefs.getString("temp1","").replace("℃",""));
        int temp2=Integer.parseInt(prefs.getString("temp2", "").replace("℃", ""));
        if(temp1>temp2)
        {
            int _temp=temp1;
            temp1=temp2;
            temp2=_temp;
        }
        weatherDespText.setText(prefs.getString("weather_desp", "") + " " + temp1 + "/" + temp2 +"℃");
        String [] date=prefs.getString("current_date", "").split("-");
        String [] time=prefs.getString("publish_time", "").split(":");
        publishText.setText(date[1]+"月"+date[2]+"日 "+time[0]+":"+time[1]);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        currtemp.setText(prefs.getString("temp",""));
        windText.setText(prefs.getString("wind",""));
        pm25Text.setText(prefs.getString("pm25",""));
        //更多信息
        LogUtil.d("weather","isNeedRequery="+isNeedQuery+"|city_en="+prefs.getString("city_en_"+cityCode,""));
        if (TextUtils.isEmpty(prefs.getString("city_en_"+cityCode,"")) || isNeedQuery)
        {
            queryFromServer(CommonUtility.getWeatherUrl2(cityCode), "weatherMore");
            isNeedQuery=false;
        }
        else
        {
            showWeatherMoreDay();
        }
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
           case 10001:
                publishText.setText("同步中...");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode))
                {
                    queryWeatherInfo(weatherCode);
                }
                break;
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            if (WeatherApplication.getCurrentCounty() == null) {
                BeginRequestLocation(new LocationListener() {
                    @Override
                    public void getLocationSuccessed(County c) {
                        WeatherApplication.setCurrentCounty(c);
                    }

                    @Override
                    public void getLocationError(String ErrorCode) {

                    }
                });
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.d("location","No Listener Method");
            if (location == null) return ;
            LogUtil.d("location","get Location over,type:"+location.getLocType());
            String city="",district="";

            //离线定位
            if(location.getLocType()==BDLocation.TypeOffLineLocationNetworkFail)
            {
                mLocationClient.requestOfflineLocation();
            }

            //离线
            if (location.getLocType()== BDLocation.TypeOffLineLocation)
            {
                city=location.getCity();
                district=location.getDistrict();
            }

            //缓存
            if(location.getLocType()==BDLocation.TypeCacheLocation)
            {
                city=location.getCity();
                district=location.getDistrict();
            }

            //在线定位
            if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                city=location.getCity();
                district=location.getDistrict();
            }
            //城市判断并回调
            if(!TextUtils.isEmpty(city) || !TextUtils.isEmpty(city))
            {
                //是否有对应的区县
                List<County> clist = WeatherDB.getInstance(WeatherApplication.getContext()).QueryCounty(district.substring(0, district.length() - 1));
                if (clist.size() == 0) { //是否有对应的城市
                    clist=WeatherDB.getInstance(WeatherApplication.getContext()).QueryCounty(city.substring(0,city.length()-1));
                }

                if(clist.size()>0) {
                    locationListener.getLocationSuccessed(clist.get(0));
                }
            }
            else
            {
               handler.sendEmptyMessage(CommonUtility.NETWORK_ERRPR);
            }
        }
    }
}



