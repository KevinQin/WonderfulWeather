package com.wonderfulWeather.app.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by kevin on 2014/12/23.
 *
 * http网络操作工具
 */
public class HttpUtil {

    /**
     * Get方式获取服务器信息
     * */
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    String response="";
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity=httpResponse.getEntity();
                        response= EntityUtils.toString(entity);
                    }

                    if(listener!=null)
                    {
                        listener.onFinish(response);
                    }
                }
                catch (Exception ex)
                {
                    if(listener!=null) {
                        listener.onError(ex);
                    }
                }
                finally {
                    httpClient.getConnectionManager().shutdown();
                }
            }
        }).start();
    }

}
