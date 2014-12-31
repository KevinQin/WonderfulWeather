package com.wonderfulWeather.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.wonderfulWeather.app.R;
import com.wonderfulWeather.app.model.County;

import java.util.List;

/**
 * Created by kevin on 2014/12/30.
 *
 * Weather county list adapter
 *
 */
public class CountyAdapter extends ArrayAdapter<County> {

    private int resourceId;

    public CountyAdapter(Context context, int resource, List<County> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        County c=getItem(position);
        View v;
        ViewHolder vh;
        if(convertView==null)
        {
            v=LayoutInflater.from(getContext()).inflate(resourceId,null);
            vh=new ViewHolder();
            vh.countyName=(TextView)v.findViewById(R.id.list_item_text);
            vh.locationFlag=(ImageView)v.findViewById(R.id.list_item_icon);
            v.setTag(vh);
        }
        else
        {
            v=convertView;
            vh=(ViewHolder) convertView.getTag();
        }
        vh.countyName.setText(c.getCounty());
        if(c.getLocation()){
            vh.locationFlag.setVisibility(View.VISIBLE);
        }
        return  v;
    }

    class ViewHolder{
        TextView countyName;
        ImageView locationFlag;
    }
}
