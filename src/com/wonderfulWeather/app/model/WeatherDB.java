package com.wonderfulWeather.app.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.wonderfulWeather.app.db.WeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2014/12/23.
 *
 * 数据库操作实体
 */
public class WeatherDB {

    /**
     * 数据库名
     * */
    public static String DB_NAME="wonderful_weather";

    /**
     * 数据库版本号
     * */
    public static final int VERSION=1;

    /**
     * 单体实例
     * */
    private static WeatherDB weatherDB;

    private SQLiteDatabase db;

    /**
     * 构造方法私有化
     * */
    private WeatherDB(Context context)
    {
        WeatherOpenHelper dbHelper=new WeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 获取WeatherDB的实例
     * */
    public synchronized static WeatherDB getInstance(Context context)
    {
        if(weatherDB==null)
        {
            weatherDB=new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * 将Province实例存储到数据库
     * */
    public void saveProvince(Province province)
    {
        if(province!=null)
        {
            db.execSQL("insert into province (province_name,province_code) values (?,?)",new String[]{province.getProvinceName(),province.getProvinceCode()});
        }
    }

    /**
     * 从数据库中读取全国所有的省份信息
     * */
    public List<Province> loadProvince()
    {
        List<Province> provinceList=new ArrayList<Province>();

        Cursor cursor=db.rawQuery("select * from province",null);
        if(cursor.moveToFirst())
        {
            do
            {
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return provinceList;
    }

    /**
     * 将城市City实例存储到数据库中
     * */
    public void saveCity(City city)
    {
        if(city!=null)
        {
            db.execSQL("insert into city(city_name,city_code,province_id) values (?,?,?)",new String[]{city.getCityName(),city.getCityCode(),String.valueOf(city.getProvinceId())});
        }
    }

    /**
     * 获取指定省份的所有城市
     * */
    public List<City> loadCity(int provinceId)
    {
        List<City> cityList=new ArrayList<City>();
        Cursor cursor=db.rawQuery("select * from city where province_id=?",new String[]{String.valueOf(provinceId)});
        if(cursor.moveToFirst())
        {
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return  cityList;
    }

    /**
     * 把县区County实例存储到数据库中
     * */
    public void saveCounty(County county)
    {
        if(county!=null)
        {
            db.execSQL("insert into county(county_name,county_code,city_id) values (?,?,?)",new String[]{county.getCountyName(),county.getCountyCode(),String.valueOf(county.getCityId())});
        }
    }

    /**
     * 载入指定城市的所有县区
     * */
    public List<County> loadCounty(int cityId)
    {
        List<County> countyList=new ArrayList<County>();

        Cursor cursor=db.rawQuery("select * from county where city_id=?",new String[]{String.valueOf(cityId)});

        if(cursor.moveToFirst())
        {
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                countyList.add(county);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return countyList;
    }
}
