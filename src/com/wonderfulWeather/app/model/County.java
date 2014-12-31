package com.wonderfulWeather.app.model;

/**
 * Created by kevin on 2014/12/23.
 * county Model
 */
public class County {
    private int id;
    private String county;
    private String code;
    private String countyEn;
    private String city;
    private String cityEn;
    private String province;
    private String provinceEn;
    private boolean isHot;
    private boolean isLocation;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id=id;
    }

    public String getCode()
    {
        return  code;
    }

    public void setCode(String countyCode)
    {
        this.code=countyCode;
    }

    public String getCounty()
    {
        return county;
    }

    public void setCounty(String countyName)
    {
        this.county=countyName;
    }

    public void setCountyEn(String countyEn){this.countyEn=countyEn;}

    public String getCountyEn(){return this.countyEn;}

    public String getCity(){return this.city;}

    public void setCity(String city){this.city=city;}

    public String getCityEn(){return this.cityEn;}

    public void setCityEn(String cityEn){this.cityEn=cityEn;}

    public void setProvince(String province){this.province=province;}

    public String getProvince(){return  this.province;}

    public void setProvinceEn(String provinceEn){this.provinceEn=provinceEn;}

    public String getProvinceEn(){return this.provinceEn;}

    public boolean getHot(){return this.isHot;}

    public void setHot(boolean hot){this.isHot=isHot;}

    public boolean getLocation(){return this.isLocation;}

    public void setLocation(boolean location){this.isLocation=location;}
}
