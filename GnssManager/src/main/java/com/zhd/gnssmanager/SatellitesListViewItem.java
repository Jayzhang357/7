package com.zhd.gnssmanager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 卫星详细信息控件条目（用于显示卫星号、高度角、方位角、L1、L2）
 * @author Administrator
 *
 */
public class SatellitesListViewItem implements ListItem {

	private SatellitesListViewItemInfo mItem;

	public SatellitesListViewItem(SatellitesListViewItemInfo item) {
		mItem = item;
	}

	@Override
	public int getLayout() {
		return R.layout.satellitesdetial_list_item;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public String getTitle() {
		return mItem.getInfo1();
	}

	@Override
	public View getView(Context context, View convertView,
						LayoutInflater inflater) {
		if (convertView == null) {
			convertView = inflater.inflate(getLayout(), null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.textView1);
		tv.setText(mItem.getInfo1());
		tv = (TextView) convertView.findViewById(R.id.textView2);
		tv.setText(mItem.getInfo2());
		tv = (TextView) convertView.findViewById(R.id.textView3);
		tv.setText(mItem.getInfo3());
		tv = (TextView) convertView.findViewById(R.id.textView4);
		tv.setText(mItem.getInfo4());
		tv = (TextView) convertView.findViewById(R.id.textView5);
		tv.setText(mItem.getInfo5());

		TextView tv4 = (TextView) convertView.findViewById(R.id.progressBar1);
		int width = 0;
		if (mItem.getMaxValue() != 0) {
			width = mItem.getWidth() * mItem.getValue1() / mItem.getMaxValue();
		}
		if (width == 0)
			width = 2;
		tv4.setWidth(width);
		tv4.setBackgroundColor(mItem.getColor());

//		TextView tv5 = (TextView) convertView.findViewById(R.id.progressBar2); //该处为双通道L2预留
//		if (mItem.getValue2() == 0)
//			tv5.setVisibility(View.GONE);
		return convertView;
	}
}
