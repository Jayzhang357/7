package com.zhd.shj.DrawView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;


import com.zhd.AppHelper;
import com.zhd.shj.JobHelper;
import com.zhd.shj.dal.Job;
import com.zhd.shj.entity.JobRecordInfo;

import java.util.ArrayList;

import java.util.HashMap;


public class DrawView extends View {
    Bitmap mbitmap;
    Bitmap mbitmap1;

    public DrawView(Context context, Bitmap bitmap, Bitmap bitmap1) {
        super(context);
        mbitmap = bitmap;
        mbitmap1 = bitmap1;

        Log.e("获取作业", "重置;");
    }

    public double[] getDrawXYONE(double[] line) {
        double xa = line[0];
        double ya = line[1];


        xa = xa - mDrawPointX;
        ya = ya - mDrawPointY;


        double x = xa * AppHelper.RATIO_VALUE;
        double y = ya * AppHelper.RATIO_VALUE;
        double angle = Math.toRadians(-90); // 翻转角度，这里是30度

        double newX = x * Math.cos(angle) - y * Math.sin(angle); // 翻转后的x坐标
        double newY = x * Math.sin(angle) + y * Math.cos(angle); // 翻转后的y坐标

        return new double[]{newX + AppHelper.zhuanX, (newY + getHeight() - AppHelper.zhuanY)};

    }

    public void setJob(Job job) {
        mJob = job;
        int kk = 20;
        Log.e("获取", ";");
        synchronized (mCurrentRecordDrawOffsetMap) {
            if (mCurrentRecordDrawOffsetMap != null)
                mCurrentRecordDrawOffsetMap.clear();
            mCurrentRecordDrawOffsetMap = mJob.getRecordList(mOriginPointX - kk, mOriginPointY - kk, mOriginPointX + kk, mOriginPointY + kk);
            Log.e("获取", mCurrentRecordDrawOffsetMap.size() + "；");

        }
    }

    public void setCurrentRecordXY(double mCurrentRecordX, double mCurrentRecordY, double h, int JobRecordId) {

        this.mCurrentRecordX = mCurrentRecordX;
        this.mCurrentRecordY = mCurrentRecordY;
        JobRecordInfo abc = new JobRecordInfo();
        abc.B = this.mCurrentRecordX;
        abc.L = this.mCurrentRecordY;
        abc.H = h;

        abc.SegmentIndex = JobRecordId;
        mCurrentRecordDrawOffsetMap.add(abc);

    }
    public int count_t=0;
    public   float centerX; // 计算中心点的 x 坐标
    public  float centerY; // 计算中心点的 y 坐标

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        mDrawPointX = mOriginPointX;
        mDrawPointY = mOriginPointY;



        if (setA && setB) {
            paint = new Paint();
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            double[] a = getDrawXYONE(new double[]{mOriginApointX, mOriginApointY});
            double[] b = getDrawXYONE(new double[]{mOriginBpointX, mOriginBpointY});
            double abcd = JobHelper.lineSpace(mOriginApointX, mOriginApointY, mOriginBpointX, mOriginBpointY);

            double abcd1 = JobHelper.lineSpace( a[0],  a[1], b[0], b[1]);
            Log.e("AB距離", abcd + ";"+abcd1);
            Log.e("AB距離方向", JobHelper.jiaoduAB(a[0], a[1], b[0], b[1]) + "");
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            canvas.drawLine((float) a[0], (float) a[1], (float) b[0], (float) b[1], paint);
            paint = new Paint();
            paint.setFakeBoldText(true);
            paint.setTextSize(25);
            paint.setColor(Color.RED);
            canvas.drawText("A", (float) a[0], (float) a[1], paint);
            canvas.drawText("B", (float) b[0], (float) b[1], paint);
            paint.setTextSize(10);
            canvas.drawText((int) abcd + "m", (float) (a[0] + b[0]) / 2, (float) (a[1] + b[1]) / 2, paint);
        }
        if (setC && setB) {
            paint = new Paint();
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            double[] c = getDrawXYONE(new double[]{mOriginCpointX, mOriginCpointY});
            double[] b = getDrawXYONE(new double[]{mOriginBpointX, mOriginBpointY});
            double abcd = JobHelper.lineSpace(mOriginCpointX, mOriginCpointY, mOriginBpointX, mOriginBpointY);
            double abcd1 = JobHelper.lineSpace( c[0],  c[1], b[0], b[1]);

            Log.e("BC距離", abcd + ";"+abcd1);
            Log.e("BC距離方向", JobHelper.jiaoduAB(b[0], b[1], c[0], c[1]) + "");
            paint.setStyle(Paint.Style.STROKE);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setAntiAlias(true);
            canvas.drawLine((float) c[0], (float) c[1], (float) b[0], (float) b[1], paint);
            paint = new Paint();
            paint.setFakeBoldText(true);
            paint.setTextSize(25);
            paint.setColor(Color.RED);
            canvas.drawText("C", (float) c[0], (float) c[1], paint);
            paint.setTextSize(10);
            canvas.drawText((int) abcd + "m", (float) (c[0] + b[0]) / 2, (float) (c[1] + b[1]) / 2, paint);

        }
        count_t++;





       if (mDrawTrawX != 0 && mDrawTrawY != 0) {
            Log.e("作图坐标1", mDrawTrawX + ";" + mDrawTrawY);
            Log.e("作图坐标2", mDrawPointX + ";" + mDrawPointY);
            double[] abc = getDrawXYONE(new double[]{mDrawTrawX, mDrawTrawY});
            Log.e("abc", abc[0] + ";" + abc[1]);
            double[] xy = abc;
           Matrix     matrix = new Matrix();
            matrix.setRotate(Yaw);
            Log.e("vtg方向QQQ",Yaw+"");
            matrix.postScale(0.3f, 0.3f);

            Bitmap scaledBitmap = Bitmap.createBitmap(mbitmap, 0, 0, mbitmap.getWidth(), mbitmap.getHeight(), matrix, true);
             centerX = (float) (xy[0]) - scaledBitmap.getWidth() / 2; // 计算中心点的 x 坐标
             centerY = (float) (xy[1]) - scaledBitmap.getHeight() / 2; // 计算中心点的 y 坐标




            canvas.save(); // 保存当前的画布状态
            canvas.translate(centerX, centerY); // 将画布的坐标轴移到中心点
            canvas.drawBitmap(scaledBitmap, 0, 0, null); // 绘制 Bitmap
            canvas.restore(); // 恢复画布状态


            //   canvas.drawBitmap(scaledBitmap, (float) (xy[0] ) , (float) (xy[1] ) , null);
        }
      if (drawCompassView) {
          Matrix    matrix = new Matrix();

            matrix.postScale(0.5f, 0.5f);

            Bitmap scaledBitmap = Bitmap.createBitmap(mbitmap1, 0, 0, mbitmap1.getWidth(), mbitmap1.getHeight(), matrix, true);
            float centerX = 150- scaledBitmap.getWidth() / 2; // 计算中心点的 x 坐标
            float centerY = 80 - scaledBitmap.getHeight() / 2; // 计算中心点的 y 坐标
            canvas.save(); // 保存当前的画布状态
            canvas.translate(centerX, centerY); // 将画布的坐标轴移到中心点
            canvas.drawBitmap(scaledBitmap, 0, 0, null); // 绘制 Bitmap
            canvas.restore(); // 恢复画布状态

        }


        paint.setStrokeWidth(30);
        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawLine(280, getHeight()-20, 380, getHeight()-20, paint);
        paint = new Paint();
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(">10",315, getHeight()-12, paint);


        paint.setStrokeWidth(30);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawLine(380, getHeight()-20, 480, getHeight()-20, paint);
        paint = new Paint();
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("2.5~10",400, getHeight()-12, paint);


        paint.setStrokeWidth(30);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawLine(480, getHeight()-20, 580, getHeight()-20, paint);
        paint = new Paint();
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("-2.5~2.5",492, getHeight()-12, paint);


        paint.setStrokeWidth(30);
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawLine(580, getHeight()-20, 680, getHeight()-20, paint);
        paint = new Paint();
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("-10~-2.5",592, getHeight()-12, paint);


        paint.setStrokeWidth(30);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawLine(680, getHeight()-20, 780, getHeight()-20, paint);
        paint = new Paint();
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("<-10",705, getHeight()-12, paint);
// 计算像素总数和匹配像素数


    }

    public float angle = 0;
    public boolean drawCompassView = true;
    // 每条AB线的初始变量

    public float IMPLEMENT_WIDTH =3.00f; // 默认的农具宽度 米

    public float Yaw = 0.00f; // 角度
    public boolean setA = false;
    public boolean setB = false;
    public boolean setC = false;
    public boolean setbegin = false;
    public double mOriginApointX = 0;
    public double mOriginApointY = 0;
    public double mOriginBpointX = 0;
    public double mOriginBpointY = 0;
    public double mOriginCpointX = 0;
    public double mOriginCpointY = 0;
    public double mOriginPointX = 0;
    public double mOriginPointY = 0;


    public double mDrawTrawX = 0;
    public double mDrawTrawY = 0;


    public double mDrawPointX = 0;
    public double mDrawPointY = 0;

    protected ArrayList<JobRecordInfo> mCurrentRecordDrawOffsetMap = new ArrayList<JobRecordInfo>();

    private Job mJob;

    public void setYaw(float yaw) {
        this.Yaw = yaw;


    }


    public void setApointXY(double apointX, double apointY) {
        this.mOriginApointX = apointX;
        this.mOriginApointY = apointY;
        setA = true;

    }

    public void setBpointXY(double apointX, double apointY) {
        this.mOriginBpointX = apointX;
        this.mOriginBpointY = apointY;
        setB = true;

    }

    public void setCpointXY(double apointX, double apointY) {
        this.mOriginCpointX = apointX;
        this.mOriginCpointY = apointY;
        setC = true;

    }


    public boolean setAB = false;

    protected HashMap<Integer, double[]> mAbLineMap = null; // 生成的AB附属线的延长后的xy坐标





    public void endDraw() {
        setAB = false;

        setA = false;
        setC = false;
        mOriginApointX = 0;
        mOriginApointY = 0;
        mOriginBpointX = 0;
        mOriginBpointY = 0;
        mOriginPointX = 0;
        mOriginPointY = 0;
        setB = false;
        if (mCurrentRecordDrawOffsetMap != null)
            mCurrentRecordDrawOffsetMap.clear();
        if (mAbLineMap != null) {
            mAbLineMap.clear();
        }
        invalidate();
    }


    private double[] calcNewPoint(double x, double y, float angle) {
        // calc arc

        angle = 360 - angle;
        //  angle += (360 - mDegree);
        angle -= 90;

        double R = Math.sqrt(x * x + y * y);
        double xLength = x;
        double yLength = y;

        // 计算两个坐标点的角度
        float mDegree = angle;

        double l = (float) ((mDegree * Math.PI) / 180);
        double cosv = (float) Math.cos(l);
        double sinv = (float) Math.sin(l);

        // calc new point
        double newX = (float) ((x) * cosv - (y) * sinv);
        double newY = (float) ((x) * sinv + (y) * cosv);
        return new double[]{newX, newY};

    }

    public double mCurrentRecordX = 0;
    public double mCurrentRecordY = 0;

    public void CurrentImpXY(double CurrentImpX, double CurrentImpY) {
        this.mOriginPointX = CurrentImpX;
        this.mOriginPointY = CurrentImpY;

    }

    public void CurrentTraceXY(double CurrentImpX, double CurrentImpY) {
        this.mDrawTrawX = CurrentImpX;
        this.mDrawTrawY = CurrentImpY;

    }




}