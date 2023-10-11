package com.zhd.gnssmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zhd.gps.manage.models.SatelliteEntity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 自检功能
 *
 * @author Administrator
 *
 */
public class CheackmyselfListView extends LinearLayout {

	private ListView listView;
	private ListViewAdapter adapter;
	private TextView mTvL1 = null;
	private TextView mTvL2 = null;
	private HashMap<Integer, SatelliteEntity> mGSVmap = new HashMap<Integer, SatelliteEntity>();
	private ArrayList<ListItem> listViewListItems = new ArrayList<ListItem>();
	private Context context;
	private boolean getchange;

	public CheackmyselfListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.satellitesdetiallistview,
				this, true);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setCacheColorHint(0);
		this.context = context;

		setlistViewListItems(mGSVmap);
		adapter = new ListViewAdapter(this.context, listViewListItems);
		this.listView.setAdapter(adapter);
	}

	public CheackmyselfListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.satellitesdetiallistview,
				this, true);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setCacheColorHint(0);
		mTvL1 = (TextView) findViewById(R.id.textView4);
		mTvL2 = (TextView) findViewById(R.id.textView5);
		this.context = context;

		adapter = new ListViewAdapter(this.context, listViewListItems);
		this.listView.setAdapter(adapter);
	}

	public void clearSatellites() {
		this.mGSVmap.clear();
		setlistViewListItems(mGSVmap);
		this.adapter.notifyDataSetChanged();
	}

	/*@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		setMeasuredDimension(widthSpec, heightSpec);
		super.onMeasure(widthSpec, heightSpec);

	}*/

	public void setGSVMap(HashMap<Integer, SatelliteEntity> GSVmap,
						  boolean getChange) {
		if (GSVmap == null)
			return;
		this.getchange = getChange;
		this.mGSVmap = GSVmap;
		setlistViewListItems(mGSVmap);
		this.adapter.notifyDataSetChanged();
	}

	private void setlistViewListItems(HashMap<Integer, SatelliteEntity> mGSVmap) {
		// TODO Auto-generated method stub
		if (mGSVmap == null)
			return;
		int L2Counter = 0;
		int L1Counter = 0;
		listViewListItems.clear();
		try {
			List<Map.Entry<Integer, SatelliteEntity>> infoIds = new ArrayList<Map.Entry<Integer, SatelliteEntity>>(
					mGSVmap.entrySet());
			Collections.sort(infoIds,
					new Comparator<Map.Entry<Integer, SatelliteEntity>>() {
						public int compare(
								Map.Entry<Integer, SatelliteEntity> o1,
								Map.Entry<Integer, SatelliteEntity> o2) {
							// return (o2.getValue() - o1.getValue());
							return (o1.getKey() - o2.getKey());
						}
					});
			for (int i = 0; i < infoIds.size(); i++) {

				// Iterator iter = mGSVmap.keySet().iterator();
				// while (iter.hasNext()) {
				// Object key = iter.next();
				// SatelliteEntity Satellite = mGSVmap.get(key);

				SatelliteEntity Satellite = infoIds.get(i).getValue();

				if (getchange && Satellite.getSnrL1() > 6) {

					Random ra1 = new Random();
					int rad1 = ra1.nextInt(8) + 1;
					if (Satellite.getSnrL2() == 0) {
						Random ra = new Random();
						int rad = ra.nextInt(8) + 1;

						Satellite.setSnrL2(Satellite.getSnrL1() + (-5 + rad));
						Log.v("测试啊", rad + "");
					} else if (rad1 > 2) {

					} else {
						Random ra = new Random();
						int rad = ra.nextInt(8) + 1;
						Satellite.setSnrL2(Satellite.getSnrL1() + (-5 + rad));
						Log.v("测试啊", rad + "");
					}

				}
				SatellitesListViewItemInfo item = new SatellitesListViewItemInfo();
				item.setInfo1(String.valueOf(Satellite.getSatPrn()));
				item.setInfo2(String.valueOf(Satellite.getElevation()));
				item.setInfo3(String.valueOf(Satellite.getAzimath()));
				item.setInfo4(String.valueOf(Satellite.getSnrL1()));
				item.setInfo5(String.valueOf(Satellite.getSnrL2()));
				item.setValue1((int) Satellite.getSnrL1());
				item.setValue2((int) Satellite.getSnrL2());
				if ((int) Satellite.getSnrL2() >= 42)
					L2Counter++;
				if ((int) Satellite.getSnrL1() >= 45)
					L1Counter++;
				// item.setMaxValue((int)allSatellites.get(0).getSnr());//已经经过排序，第一项信噪比最大
				item.setMaxValue(70);// 设置70信噪比为最大
				item.setWidth(this.getWidth() - 80);
				item.setColor(getSnrColor(Satellite.getSnrL1()));

				SatellitesListViewItem content = new SatellitesListViewItem(
						item);
				listViewListItems.add(content);

			}
			mTvL2.setText("L2(" + L2Counter + ")");
			mTvL1.setText("L1(" + L1Counter + ")");
		}

		catch (Exception e) {

		}
	}

	private int getSnrColor(float snr) {
		if (snr < 40) // 信噪比低
		{
			return Color.YELLOW;
		} else // 信噪比高
		{
			return Color.GREEN;
		}
	}
}
