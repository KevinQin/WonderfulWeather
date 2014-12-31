package com.wonderfulWeather.app.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.wonderfulWeather.app.db.WeatherOpenHelper;
import com.wonderfulWeather.app.util.LogUtil;
import com.wonderfulWeather.app.util.Utility;

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
    public static final int VERSION=2;

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
     * 把县区County实例存储到数据库中
     * */
    public void saveCounty(County county)
    {
        if(county!=null)
        {
            db.execSQL("insert into county(code,county,countyEn,city,cityEn,province,provinceEn,isHot) values (?,?,?,?,?,?,?,?)",new String[]{
                    county.getCode(),county.getCounty(),county.getCountyEn(),county.getCity(),county.getCityEn(),
                    county.getProvince(),county.getProvinceEn(),county.getHot()?"1":"0"
            });
        }
    }

    public List<County> QueryCounty( String keyWord)
    {
        List<County> countyList=new ArrayList<County>();

        keyWord=keyWord.toLowerCase();

        String sql=String.format("select * from county where county like '%1$s%%' or county_en like '%1$s%%' order by ishot desc,id asc",keyWord);
        Cursor cursor=db.rawQuery(sql,null);
        LogUtil.d("database",sql);

        if(cursor.moveToFirst())
        {
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCode(cursor.getString(cursor.getColumnIndex("code")));
                county.setCounty(cursor.getString(cursor.getColumnIndex("county")));
                county.setCountyEn(cursor.getString(cursor.getColumnIndex("county_en")));
                county.setCity(cursor.getString(cursor.getColumnIndex("city")));
                county.setCityEn(cursor.getString(cursor.getColumnIndex("city_en")));
                county.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                county.setProvinceEn(cursor.getString(cursor.getColumnIndex("province_en")));
                county.setHot(cursor.getInt(cursor.getColumnIndex("isHot"))==1);
                countyList.add(county);
                boolean isLocation=Utility.IsCurrentLocation(county);
                county.setLocation(isLocation);
                if(isLocation) {
                    LogUtil.d("isLocation", county.getCounty() + ":" + isLocation);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return countyList;
    }

    /**
     * 载入指定城市的所有县区
     * */
    public List<County> loadHotCounty()
    {
        List<County> countyList=new ArrayList<County>();

        Cursor cursor=db.rawQuery("select * from county where isHot=?", new String[]{"1"});


        if(cursor.moveToFirst())
        {
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCode(cursor.getString(cursor.getColumnIndex("code")));
                county.setCounty(cursor.getString(cursor.getColumnIndex("county")));
                county.setCountyEn(cursor.getString(cursor.getColumnIndex("county_en")));
                county.setCity(cursor.getString(cursor.getColumnIndex("city")));
                county.setCityEn(cursor.getString(cursor.getColumnIndex("city_en")));
                county.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                county.setProvinceEn(cursor.getString(cursor.getColumnIndex("province_en")));
                county.setHot(true);
                boolean isLocation=Utility.IsCurrentLocation(county);
                county.setLocation(isLocation);
                if(isLocation) {
                    LogUtil.d("isLocation", county.getCounty() + ":" + isLocation);
                }
                countyList.add(county);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return countyList;
    }
}
