package com.wonderfulWeather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.model.City;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.Province;
import com.wonderfulWeather.app.model.WeatherDB;
import com.wonderfulWeather.app.util.HttpCallbackListener;
import com.wonderfulWeather.app.util.HttpUtil;
import com.wonderfulWeather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2014/12/23.
 *
 * 区县选择活动
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVICE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList=new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);

        listView.setAdapter(adapter);

        weatherDB=WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVICE)
                {
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY)
                {
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果再去服务器上检索
     * */
    private void queryProvinces()
    {
        provinceList=weatherDB.loadProvince();
        if(provinceList.size()>0)
        {
            dataList.clear();
            for(Province province:provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVICE;
        }
        else
        {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询当前选定省分的所有城市，优先数据库查询，如果没有去服务器检索
     * */
    private void queryCities()
    {
        cityList=weatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0)
        {
            dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }
        else
        {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询当前选定城市的所有区县，优先数据库查询，如果没有去服务器检索
     * */
    private void queryCounties()
    {
        countyList=weatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0)
        {
            dataList.clear();
            for (County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }
        else
        {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上加载省市县数据
     * */
    private void queryFromServer(final String code,final String type)
    {
        String address;

        if(!TextUtils.isEmpty(code))
        {
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        else
        {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type))
                {
                    result= Utility.handlerProvinceResponse(weatherDB,response);
                }
                else if("city".equals(type))
                {
                    result = Utility.handlerCityResponse(weatherDB,response,selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result = Utility.handlerCountyResponse(weatherDB,response,selectedCity.getId());
                }

                if(result)
                {
                    //在主线程上执行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载数据失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog()
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表，省列表还是直接退出。
    * */
    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY)
        {
            queryCities();
        }
        else if(currentLevel==LEVEL_CITY)
        {
            queryProvinces();
        }
        else
        {
            finish();
        }
    }
}
