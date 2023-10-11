package com.zhd.gnssmanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListViewAdapter extends BaseAdapter {

	private Context context;	  
	 protected ArrayList<ListItem> resultList;	  
	 private LayoutInflater layoutInflater;  
	 
	 public ListViewAdapter(Context context, ArrayList<ListItem> list) {         
         this.layoutInflater = LayoutInflater.from(context);
         this.resultList = list; 
         this.context = context;
     } 
	 
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Object getItem(int position) {
		return resultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return resultList.get(position).isClickable();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		return resultList.get(position).getView(context, convertView, layoutInflater);
	}
}

