package com.wonderfulWeather.app.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.activity.WeatherActivity;
import com.wonderfulWeather.app.util.CommonUtility;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private Activity activity;
	
	
	public ViewPagerAdapter(List<View> views,Activity activity)
	{
		this.views=views;
		this.activity=activity;
	}
	
	
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}



	@Override
	public void finishUpdate(View container) {}



	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position),0);
		if(position+1==views.size())
		{
			Button mStartButton=(Button)container.findViewById(R.id.btnStart);
			mStartButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					setGuideed();
					goHome();
				}
				
			});
		}
		return views.get(position);
	}
	
	private void goHome()
	{
		Intent intent=new Intent(activity, WeatherActivity.class);
		intent.putExtra("county_code","000000000");
		activity.startActivity(intent);
		activity.finish();
	}
	
	private void setGuideed()
	{
		SharedPreferences preferences=activity.getSharedPreferences(CommonUtility.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor editor=preferences.edit();
		editor.putBoolean(CommonUtility.IS_FIRST_IN , false);
		editor.commit();
	}



	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		
	}



	@Override
	public Parcelable saveState() {
		return null;
	}



	@Override
	public void startUpdate(View container) {
	}



	@Override
	public int getCount() {
		if(views!=null)
		{
			return views.size();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0==arg1);
	}

}
