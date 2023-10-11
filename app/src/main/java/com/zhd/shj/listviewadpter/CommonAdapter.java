package com.zhd.shj.listviewadpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhd.shj.R;

import java.util.ArrayList;
import java.util.List;

public class CommonAdapter extends BaseAdapter {

	private int mCurrentPosition = -1;
	private List<String> mData = null;
	private ArrayList<String> list = new ArrayList<String>();
	private Activity activity = null;
	private Handler handler;
	private boolean mHasEdittxt = false;

	public final class ViewHolder {
		public TextView contentTv;
	}

	private LayoutInflater mInflater;

	public CommonAdapter(Context context, List<String> data) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mData = data;
	}

	public CommonAdapter(Activity activity, Handler handler, List<String> mData) {
		this.activity = activity;
		this.handler = handler;
		this.mData = mData;
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

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getCurrentPosition() {
		return mCurrentPosition;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = mInflater.inflate(R.layout.controltype_list, null, false);

		ViewHolder holder = new ViewHolder();
		holder.contentTv = (TextView) view.findViewById(R.id.txtContent);
		view.setTag(holder);

		holder.contentTv.setText((String) mData.get(position));

		int[] colors = { Color.rgb(255, 255, 255), Color.rgb(219, 243, 243) };// RGB颜色
		view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

		if (position == mCurrentPosition) // 如果当前的行就是ListView中选中的一行，就更改显示样式
			view.setBackgroundColor(Color.rgb(01, 128, 128));// 更改整行的背景色

		return view;
	}

}
