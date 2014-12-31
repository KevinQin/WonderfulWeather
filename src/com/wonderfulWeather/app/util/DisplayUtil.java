package com.wonderfulWeather.app.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.wonderfulWeather.app.WeatherApplication;

/**
 * Created by kevin on 2014/12/31.
 * Android大小单位转换工具类
 *
 */
public class DisplayUtil {


    public static Size getScreenWidthForPx()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) WeatherApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        // float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        //int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240） 每英寸点数
        //Log.d("getScreenWidthForPx", "width-px=" + width + ",height-px=" + height + ",density=" + density + ",densityDpi=" + densityDpi);
        return new Size(width,height);
    }


    public static Size getScreenSizeForDpi()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) WeatherApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        //int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        //Log.d("getScreenWidthForDpi","width-px="+width+",height-px="+height+",density="+density+",densityDpi="+densityDpi);
        return new Size((int)(width/density),(int)(height/density));
    }


    public static float getScreenScaledDensity()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) WeatherApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        //int width = metric.widthPixels;     // 屏幕宽度（像素）
        //int height = metric.heightPixels;   // 屏幕高度（像素）
        //float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        //int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        //Log.d("getScreenDensity","width-px="+width+",height-px="+height+",density="+density+",densityDpi="+densityDpi);
        return  metric.scaledDensity;
    }

    public static float getScreenDensity()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) WeatherApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        //Log.d("getScreenDensity","width-px="+width+",height-px="+height+",density="+density+",densityDpi="+densityDpi);
        return  density;
    }


    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param scale（DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(float pxValue, float scale) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param scale（DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(float dipValue, float scale) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /*
    * 尺寸
    * */
    public static class Size
    {
        static int width;
        static int height;

        public Size(int _width,int _height)
        {
            width=_width;
            height=_height;
        }

        public static int getWidth()
        {
            return width;
        }

        public static int getHeight()
        {
            return height;
        }
    }
}
