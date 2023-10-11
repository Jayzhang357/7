package com.zhd.gnssmanager;

import com.zhd.gps.manage.models.GpsEnum;
import com.zhd.gps.manage.models.GpsEnum.GpsType;
import com.zhd.gps.manage.models.SatelliteEntity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 星空图控件
 *
 * @author Administrator
 *
 */
public class SatellitesLocationView extends View {

	private Paint paint = new Paint();
	private Paint paintPic = new Paint();
	private HashMap<Integer, SatelliteEntity> mGsvMap = new HashMap<Integer, SatelliteEntity>();
	private NumberFormat INT_FORMAT = new DecimalFormat("#,####");
	private float centerX = 0;
	private float centerY = 0;
	private float maxRadius = 0;
	private PathEffect effects = new DashPathEffect(new float[] { 10, 10, 10,
			10 }, 10);

	public SatellitesLocationView(Context context) {
		super(context);

		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		setMeasuredDimension(widthSpec, heightSpec);
		super.onMeasure(widthSpec, heightSpec);

	}
	public SatellitesLocationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);
	}

	public void setGSVMap(HashMap<Integer, SatelliteEntity> GsvMap) {

		if (GsvMap == null)
			return;
		this.mGsvMap = GsvMap;
		this.invalidate();
	}

	public void clearSatellites() {
		this.mGsvMap.clear();
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawsatelliteViewBack(canvas);
		drawSatelliteLocation(canvas);
	}

	/**
	 * 绘制卫星背景图
	 *
	 * @param canvas
	 */
	private void drawsatelliteViewBack(Canvas canvas) {
		centerX = this.getWidth() / 2;
		centerY = this.getHeight() / 2;
		maxRadius = (centerX > centerY ? centerY : centerX) - 2;

		drawCircle(canvas);
		drawDeshLine(canvas);
		drawNorth(canvas);
	}

	/**
	 * 绘制三个圆圈
	 *
	 * @param canvas
	 */
	private void drawCircle(Canvas canvas) {
		paint.setAntiAlias(true);
		int color = Color.rgb(139, 131, 134);
		paint.setColor(color);
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(effects);

		float padding = maxRadius / 3;
		canvas.drawCircle(centerX, centerY, padding * 2, paint); // 中圈
		canvas.drawCircle(centerX, centerY, padding, paint); // 内圈
		paint.setPathEffect(null);
		paint.setStrokeWidth(3);
		canvas.drawCircle(centerX, centerY, maxRadius, paint); // 外圈
	}

	/**
	 * 绘制以30度为间隔的虚线
	 *
	 * @param canvas
	 */
	private void drawDeshLine(Canvas canvas) {
		int color = Color.rgb(139, 131, 134);
		paint.setColor(color);
		paint.setStrokeWidth(1);
		paint.setTextSize(20);

		float radius = maxRadius - 30;

		for (int i = 0; i < 360; i += 30) {
			double sin = Math.sin(i * Math.PI / 180);
			double cos = Math.cos(i * Math.PI / 180);
			float x = (float) (centerX + sin * maxRadius);
			float y = (float) (centerY - cos * maxRadius);

			Path path = new Path();
			path.moveTo(centerX, centerY);
			path.lineTo(x, y);
			paint.setStyle(Style.STROKE);
			paint.setPathEffect(effects);
			canvas.drawPath(path, paint);

			paint.setPathEffect(null);
			paint.setStyle(Style.FILL);
			paint.setTextSize(10);

			String degree = String.valueOf(i) + "°";
			float degreeX = (float) (centerX + sin * radius) - 15;
			float degreeY = (float) (centerY - cos * radius);
			canvas.drawText(degree, degreeX, degreeY, paint);
		}
	}

	/**
	 * 绘制指北方向
	 *
	 * @param canvas
	 */
	private void drawNorth(Canvas canvas) {
		paint.setAntiAlias(true);
		int color = Color.rgb(139, 131, 134);
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		paint.setTextSize(10);

		float x = centerX - 15;
		float y = centerY - maxRadius + 60;
		canvas.drawText("N", x, y, paint);
	}

	private void drawSatelliteLocation(Canvas canvas) {
		int sumNum = mGsvMap.size();
		if (sumNum <= 0)
			return;

		paint.setAntiAlias(true);
		paint.setTextSize(15);
		paint.setStyle(Style.FILL);
		try {
			List<Map.Entry<Integer, SatelliteEntity>> infoIds = new ArrayList<Map.Entry<Integer, SatelliteEntity>>(
					mGsvMap.entrySet());

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
				SatelliteEntity Satellite = infoIds.get(i).getValue();
				float azimuth = Satellite.getAzimath();
				float elevation = Satellite.getElevation();
				GpsType gpstype = Satellite.getGpsType();
				float x = (float) (centerX + maxRadius * (1 - elevation / 90.0)
						* Math.cos((90.0 - azimuth) * Math.PI / 180));
				float y = (float) (centerY - maxRadius * (1 - elevation / 90.0)
						* Math.sin((90.0 - azimuth) * Math.PI / 180));
				float snr = Satellite.getSnrL1(); // 信噪比
				String prn = INT_FORMAT.format(Satellite.getSatPrn()); // 卫星号

				float r = 13;
				RectF targetRect = new RectF((x - r), (y - r), (x + r), (y + r));
				RectF targetLRect = new RectF((x - 1.3f * r), (y - r),
						(x + 1.3f * r), (y + r));
				paintPic = paint;
				paintPic.setAlpha(127);

				if (gpstype == null)
					gpstype = GpsType.BD;

				switch (gpstype.getValue()) {
					case 0:
						paint.setColor(Color.GREEN);
						break;

					case 1:
						paint.setColor(Color.YELLOW);
						break;

					case 2:
						paint.setColor(Color.RED);
						break;

					case 3:
						paint.setColor(Color.BLUE);
						break;

					default:
						break;
				}
				// paint.setAlpha(200);
				canvas.drawCircle(x, y, r, paintPic); // 绘制表示卫星的圆形

				float prnX = x - 5;
				float prnY = y + 30;
				Paint paintprn = new Paint();
				paintprn.setColor(Color.BLACK);
				paintprn.setAntiAlias(true);
				paintprn.setStyle(Style.FILL);
				paintprn.setStrokeWidth(1);
				paintprn.setTextSize(15);
				// canvas.drawText(prn, prnX, prnY, paint); // 绘制卫星号
				FontMetricsInt fontMetrics = paintprn.getFontMetricsInt();
				int baseline = (int) (targetRect.top
						+ (targetRect.bottom - targetRect.top
						- fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);
				// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
				paintprn.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(prn, targetRect.centerX(), baseline, paintprn);
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
