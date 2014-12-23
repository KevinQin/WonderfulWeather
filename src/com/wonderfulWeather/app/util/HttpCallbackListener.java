package com.wonderfulWeather.app.util;

/**
 * Created by kevin on 2014/12/23.
 *
 * 网络操作监听回调接口
 */
public interface HttpCallbackListener {
    /**
     * 当网络调用正常完成时
     * */
    void onFinish(String response);

    /**
     * 当网络调用出错时执行
     * */
    void onError(Exception e);
}
