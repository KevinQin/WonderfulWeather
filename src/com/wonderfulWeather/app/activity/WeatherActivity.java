package com.wonderfulWeather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.service.AutoUpdateService;
import com.wonderfulWeather.app.util.*;

import java.util.Date;

/**
 * Created by kevin on 2014/12/23.
 *
 * Weather活动
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;

    private TextView cityNameText;

    private TextView publishText;

    private TextView weatherDespText;

    private TextView windText;

    private TextView pm25Text;

    private TextView currtemp;

    private Button switchCity;

    private boolean isNeedQuery=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
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
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            isNeedQuery=true;
            queryWeatherInfo(countyCode);
        }
        else
        {
            showWeather();
        }
        switchCity.setOnClickListener(this);

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

                LogUtil.d(type,response);

                if("weatherCode".equals(type))
                {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }

                else if("weatherMore".equals(type))
                {
                    if(!TextUtils.isEmpty(response) && response.length()>10) {
                        if (Utility.handleWeatherMoreResponse(WeatherActivity.this, response)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   showWeatherMoreDay();
                                   findViewById(R.id.weather_more_info).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                    else
                    {
                       LogUtil.d("Http","No data");
                       findViewById(R.id.weather_more_info).setVisibility(View.INVISIBLE);
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
        if (TextUtils.isEmpty(prefs.getString("city_en","")) || isNeedQuery)
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
            /*  case 10001:
                publishText.setText("同步中...");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode))
                {
                    queryWeatherInfo(weatherCode);
                }
                break;*/
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
}
