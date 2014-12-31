package com.wonderfulWeather.app.util;

import com.wonderfulWeather.app.model.County;

/**
 * Created by kevin on 2014/12/30.
 *
 * 获取位置服务回调
 *
 */
public interface LocationListener {

    void getLocationSuccessed(County c);
    void getLocationError(String ErrorCode);
}

/*
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nProvince:"+location.getProvince());
                sb.append("\nCity:"+location.getCity());
                sb.append("\nDistrict:"+location.getDistrict());
            }*/
