package com.wonderfulWeather.app.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.wonderfulWeather.app.WeatherApplication;
import com.wonderfulWeather.app.util.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kevin on 2014/12/23.
 *
 * 数据库操作类
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {


    final static String hotCity="'北京','上海','广州','深圳','天津','杭州','南京','济南','重庆','青岛','大连','宁波','厦门','成都','武汉','沈阳','哈尔滨','沈阳','西安','长春','长沙','福州','郑州','石家庄','太原','贵阳','昆明','合肥','南宁','海口','兰州'";


    /**
     * 创建省的表 province
     */
    public static final String CREATE_PROVINCE="create table province (" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     * 创建城市表 city
     * */
    public static final String CREATE_CITY="create table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";

    /**
     * 创建县级表 county
     * */
    public static final String CREATE_COUNTY="create table county(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";


    /**
     * 创建县级表 county
     * */
    public static final String CREATE_COUNTY_V2="create table county(" +
            "id integer primary key autoincrement," +
            "code text," +
            "county text," +
            "county_en text," +
            "city text," +
            "city_en text," +
            "province text," +
            "province_en text," +
            "isHot text)";


    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        */
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion)
        {
            case 1:
                db.execSQL("drop table city;");
                db.execSQL("drop table province;");
                db.execSQL("drop table county;");
                db.execSQL(CREATE_COUNTY_V2);
                init(db);
        }
    }

    private void init(SQLiteDatabase db)
    {
        try {
            InputStream ins = WeatherApplication.getContext().getResources().getAssets().open("data");
            LogUtil.d("database","Init data Begin");
            BufferedReader br=new BufferedReader(new InputStreamReader(ins));
            StringBuilder sb=new StringBuilder();
            String line="";
            while((line=br.readLine())!=null)
            {
                sb.append(line);
            }
            String [] cArray=sb.toString().split(",");
            int n=0;
            LogUtil.d("databae","beginInser data["+ cArray.length +"]");
            String sql="insert into county (code,county,county_en,city,city_en,province,province_en,isHot) values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s')";
            int len=cArray.length;
            LogUtil.d("database","count="+len);
            db.beginTransaction();
            db.execSQL(CREATE_COUNTY_V2);
            LogUtil.d("database",CREATE_COUNTY_V2);
            for (int i=0;i<len;i++)
            {
                String [] a=cArray[i].split("\\|");
                String s_sql=String.format(sql,a[0],a[1],a[2],a[3],a[4],a[5],a[6],"0");
                db.execSQL(s_sql);
                LogUtil.d("databae",s_sql);
            }
            //更新热门城市
            db.execSQL("update county set isHot=1 where county in ("+ hotCity +")");
            //因为退出来的时候，还有部分没有执行
            db.setTransactionSuccessful();
            LogUtil.d("databae","init data over");
        }
        catch (Exception ex)
        {
            LogUtil.d("WeatehrOpenHelper",ex.getMessage());
            LogUtil.d("databae","init data Error");
        }
        finally {
            db.endTransaction();
        }
    }
}
