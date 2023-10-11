package com.zhd.gnssmanager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zhd.gps.manage.models.SatelliteEntity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 显示信噪比控件
 *
 * @author Administrator
 *
 */
public class SatellitesSNRView extends View {

	private Paint paint = new Paint();
	private Paint painttext = new Paint();
	private HashMap<Integer, SatelliteEntity> mGsvMap = new HashMap<Integer, SatelliteEntity>();
	private NumberFormat INT_FORMAT = new DecimalFormat("#,####");
	private int snrPaddingBottom = 50; // 信噪比柱状底部距离控件底部的距离
	private PathEffect effects = new DashPathEffect(new float[] { 10, 10, 10,
			10 }, 10);
	private float scale = 6; // 将信噪比值放大scale倍绘制出来，避免柱状高度太低

	public SatellitesSNRView(Context context) {
		super(context);
	}

	public SatellitesSNRView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}
	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		setMeasuredDimension(widthSpec, heightSpec);
		super.onMeasure(widthSpec, heightSpec);

	}
	public void setGSVMap(HashMap<Integer, SatelliteEntity> mGSVmap) {
		if (mGSVmap == null)
			return;
		this.mGsvMap = mGSVmap;
		this.invalidate();
	}

	public void clearSatellites() {
		this.mGsvMap.clear();
		//	this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.v("卫星总数234","作图");
		drawSatelliteSnrViewBack(canvas);
		drawSatelliteSnr(canvas);
	}

	/**
	 * 绘制卫星背景图
	 *
	 * @param canvas
	 */
	private void drawSatelliteSnrViewBack(Canvas canvas) {
		int width = this.getWidth();
		int height = this.getHeight();
		scale = (float) (100 / ((height - snrPaddingBottom) * 1.00000)); // 当信噪比为100时占据整个高度，依此推算出绘制比例

		paint.setStyle(Style.STROKE);
		int color = Color.rgb(139, 131, 134);
		paint.setColor(color);

		Path path = new Path();
		float y = height - snrPaddingBottom;
		path.moveTo(5, y);
		path.lineTo(width - 5, y);
		canvas.drawPath(path, paint);

		path = new Path();
		y = height - snrPaddingBottom - 20 / scale;
		path.moveTo(5, y);
		path.lineTo(width - 5, y);
		paint.setPathEffect(effects);
		canvas.drawPath(path, paint);

		path = new Path();
		y = height - snrPaddingBottom - 40 / scale;
		path.moveTo(5, y);
		path.lineTo(width - 5, y);
		canvas.drawPath(path, paint);

		path = new Path();
		y = height - snrPaddingBottom - 60 / scale;
		path.moveTo(5, y);
		path.lineTo(width - 5, y);
		canvas.drawPath(path, paint);

		path = new Path();
		y = height - snrPaddingBottom - 80 / scale;
		path.moveTo(5, y);
		path.lineTo(width - 5, y);
		canvas.drawPath(path, paint);
		paint.setPathEffect(null);
	}

	private void drawSatelliteSnr(Canvas canvas) {
		// TODO Auto-generated method stub
		int sumNum = mGsvMap.size();
		if (sumNum <= 0)
			return;
		int width = this.getWidth();
		int height = this.getHeight();
		int snrWidth = (width - 20 - (sumNum - 1) * 5) / sumNum;// 5为各柱状间的间隔
		if (snrWidth > 50) {
			snrWidth = 50;
		}
		int index = 0; // 标记已经绘制的柱状数
		painttext.setStyle(Style.FILL);
		painttext.setAntiAlias(true);
		painttext.setTextSize(15);
		painttext.setColor(getResources().getColor(R.color.black));
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setTextSize(15);
		try
		{
			List<Map.Entry<Integer, SatelliteEntity>> infoIds = new ArrayList<Map.Entry<Integer, SatelliteEntity>>(
					mGsvMap.entrySet());
			Collections.sort(infoIds,
					new Comparator<Map.Entry<Integer, SatelliteEntity>>() {
						public int compare(Map.Entry<Integer, SatelliteEntity> o1,
										   Map.Entry<Integer, SatelliteEntity> o2) {
							// return (o2.getValue() - o1.getValue());
							return (o1.getKey() - o2.getKey());
						}
					});
			for (int i = 0; i < infoIds.size(); i++) {
				SatelliteEntity Satellite = infoIds.get(i).getValue();
				int x = 10 + (snrWidth + 5) * index;// x横坐标
				float snrL1 = Satellite.getSnrL1();
				float snrHeight = snrL1 / scale; // 将信噪比值放大scale倍绘制出来，避免柱状高度太低
				float y = height - snrHeight - snrPaddingBottom; // y横坐标
				float snrY = y - 5; // 信噪比值绘制处y坐标
				float snrX = x + snrWidth / 2; // 信噪比值绘制处x坐标

				paint.setColor(getSnrColor(snrL1));
				canvas.drawRect(x, y, x + snrWidth, y + snrHeight, paint); // 绘制柱状图

				String prn = INT_FORMAT.format(Satellite.getSatPrn());
				float prnX = snrX;
				float prnY = y + snrHeight + 20;

				painttext.setTextSize(12);
				painttext.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(prn, prnX, prnY, painttext); // 卫星号
				canvas.drawText(INT_FORMAT.format(snrL1), snrX, snrY, painttext); // 信噪比

				index++;
			}
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
