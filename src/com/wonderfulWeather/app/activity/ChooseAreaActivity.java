package com.wonderfulWeather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.adapter.CountyAdapter;
import com.wonderfulWeather.app.model.County;
import com.wonderfulWeather.app.model.WeatherDB;
import com.wonderfulWeather.app.util.LogUtil;

import java.util.*;

/**
 * Created by kevin on 2014/12/23.
 *
 * 区县选择活动
 */
public class ChooseAreaActivity extends Activity {

    private ProgressDialog progressDialog;
    private ListView listView;
    private CountyAdapter adapter;
    private List<County> countyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView=(ListView)findViewById(R.id.list_view);
        loadHotCities();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String countyCode = countyList.get(position).getCode();
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
            intent.putExtra("county_code", countyCode);
            startActivity(intent);
            finish();
            }
        });

        final ImageView ivDeleteText=(ImageView)findViewById(R.id.ivDeleteText);
        final EditText etSearch = (EditText) findViewById(R.id.etSearch);
        ivDeleteText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        final Button btnCancelSearch=(Button)findViewById(R.id.btnCancelSearch);
        btnCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

             public void onTextChanged(CharSequence s, int start, int before, int count) {
                countyList.clear();
                adapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.INVISIBLE);
                    //载入热门城市
                    loadHotCities();
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                    //查询城市
                    if(s.length()>=2) {
                        queryCounties(s.toString());
                    }
                }
            }
        });
    }

    private void loadHotCities()
    {
        countyList=WeatherDB.getInstance(this).loadHotCounty();
        adapter=new CountyAdapter(this,R.layout.list_item,countyList);
        listView.setAdapter(adapter);
        listView.setSelection(0);
    }

    /**
     * 查询当前选定城市的所有区县，优先数据库查询，如果没有去服务器检索
     * */
    private void queryCounties(String keyword)
    {
        showProgressDialog();
        countyList=WeatherDB.getInstance(this).QueryCounty(keyword);
        LogUtil.d("database","querty ["+ keyword +"] size:"+countyList.size());
        adapter=new CountyAdapter(this,R.layout.list_item,countyList);
        listView.setAdapter(adapter);
        listView.setSelection(0);
        closeProgressDialog();
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
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
    }
}
