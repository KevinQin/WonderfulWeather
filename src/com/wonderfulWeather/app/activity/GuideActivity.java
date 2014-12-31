package com.wonderfulWeather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import com.wonderfulWeather.app.adapter.ViewPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.model.WeatherDB;
import com.wonderfulWeather.app.util.CommonUtility;

public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	
	private ImageView[] dots;
	private int currentIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);

		initViews();
		
		initDots();
	}



	private void initData()
	{
		//初始化数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				//初始化数据库
				WeatherDB.getInstance(GuideActivity.this);
			}
		}).start();
	}

	private void initDots() {
		LinearLayout ll=(LinearLayout)findViewById(R.id.ll);
		dots=new ImageView[views.size()];
		for(int i=0;i<views.size();i++)
		{
			dots[i]=(ImageView)ll.getChildAt(i);
			dots[i].setImageResource(R.drawable.dot_g1);
		}
		
		currentIndex=0;
		dots[currentIndex].setImageResource(R.drawable.dot_g2);
	}



	private void initViews() {
		LayoutInflater inflater=LayoutInflater.from(this);
		
		views=new ArrayList<View>();
		views.add(inflater.inflate(R.layout.what_new_one, null));
		views.add(inflater.inflate(R.layout.what_new_two, null));
		views.add(inflater.inflate(R.layout.what_new_three, null));
		views.add(inflater.inflate(R.layout.what_new_four, null));
		
		vpAdapter=new ViewPagerAdapter(views,this);
		vp=(ViewPager)findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
	}

	
	private void setCurrentDot(int position)
	{
		if(position<0 || position>views.size()-1 || currentIndex==position)
		{
			return;
		}
		
		dots[position].setImageResource(R.drawable.dot_g2);
		dots[currentIndex].setImageResource(R.drawable.dot_g1);
		
		currentIndex=position;
	}
	
	


	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentDot(arg0);		
	}
	
}
