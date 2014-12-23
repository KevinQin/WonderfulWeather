package com.wonderfulWeather.app.model;

/**
 * Created by kevin on 2014/12/23.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public void setId(int id)
    {
        this.id=id;
    }

    public int getId()
    {
        return id;
    }

    public void  setCityName(String cityName)
    {
        this.cityName=cityName;
    }

    public String getCityName()
    {
        return  this.cityName;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode=cityCode;
    }

    public String getCityCode()
    {
        return  this.cityCode;
    }

    public  void setProvinceId(int provinceId)
    {
        this.provinceId=provinceId;
    }

    public int getProvinceId()
    {
        return  provinceId;
    }
}
