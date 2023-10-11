package com.zhd.gnssmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OptionsAdapter extends BaseAdapter {

	private int mCurrentPosition = -1;
	private List<String> mlist = new ArrayList<String>();
	private Activity activity = null;
	private Handler handler;
	private boolean mHasEdittxt = false;
	private int mTvHeight=0;

	private LayoutInflater mInflater;

	public OptionsAdapter(Context context, List<String> data) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mlist = data;
	}

	public OptionsAdapter(Activity activity, Handler handler, List<String> mData) {
		this.activity = activity;
		this.handler = handler;
		this.mlist = mData;
	}

	public void setTextView(int currentPosition) {
		this.mCurrentPosition = currentPosition;
	}

	public void setHasEdittxt(boolean hasEdittxt) {
		this.mHasEdittxt = hasEdittxt;
	}

	public void setCurrentPosition(int currentPosition) {
		this.mCurrentPosition = currentPosition;
	}
	public int getHeight() {
		
		return mTvHeight;
	}
	public int getCount() {
		return mlist.size();
	}

	public Object getItem(int position) {
		return mlist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {

		TextView textView = null;
		if (view == null) {
			view = LayoutInflater.from(activity).inflate(R.layout.option_item,
					null);
			textView = (TextView) view.findViewById(R.id.item_text);
			mTvHeight=textView.getHeight();
			view.setTag(textView);
		} else {
			textView = (TextView) view.getTag();
		}

		textView.setText(mlist.get(position));

		textView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("selIndex", position);
				msg.setData(data);
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});

		return view;
	}

}

class ViewHolder {
	TextView textView;
}
