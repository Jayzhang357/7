package com.zhd.gnssmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public interface ListItem {
	public int getLayout();  
    public boolean isClickable();
    public String getTitle();
	public View getView(Context context, View convertView, LayoutInflater inflater);  
}
