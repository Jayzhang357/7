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
import com.zhd.shj.dal.Job;
import com.zhd.shj.entity.JobRecordInfo;
import com.zhd.shj.entity.RectangleArrayCalculator;
import com.zhd.shj.entity.RectangleCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DrawCoverView extends View {
    private Bitmap backgroundBitmap;
    public DrawCoverView(Context context,Bitmap background) {
        super(context);
        backgroundBitmap=background;
    }

    public double[] getDrawXYONE(double[] line) {
        double xa = line[0];
        double ya = line[1];
        xa = xa - mOriginPointX;
        ya = ya - mOriginPointY;
        double x = xa * AppHelper.RATIO_VALUE;
        double y = ya * AppHelper.RATIO_VALUE;
        double angle = Math.toRadians(-90); // 翻转角度，这里是30度
        double newX = x * Math.cos(angle) - y * Math.sin(angle); // 翻转后的x坐标
        double newY = x * Math.sin(angle) + y * Math.cos(angle); // 翻转后的y坐标
        return new double[]{newX + AppHelper.zhuanX, (newY + getHeight() - AppHelper.zhuanY)};
    }
    private double A=0;  private double B=0;  private double C=0;  private double D=0;
    private boolean isPD=false;
    public  void  setPD(double x1,double y1,double z1,double x2,double y2,double z2 ,double x3,double y3,double z3)
    {

        double[] a = getDrawXYONE(new double[]{x1, y1});
        double[] b = getDrawXYONE(new double[]{x2, y2});
        double[] c = getDrawXYONE(new double[]{x3, y3});
        A = (b[1] -  a[1]) * (z3 - z1) - (z2 - z1) * (c[1] -  a[1]);

        B = (z2 - z1) * (c[0] - a[0]) - (b[0] - a[0]) * (z3 - z1);

        C = (b[0] - a[0]) * (c[1] - a[1]) - (b[1] -  a[1]) * (c[0] - a[0]);


        D = - (A * a[0] + B *  a[1] + C * z1);



    }
    public  void  setPD(Boolean ispd)
    {
        isPD=ispd;
    }
    public void setJob(Job job) {
        mJob = job;
        int kk = 2000000;
        Log.e("获取作业", ";");
        synchronized (mCurrentRecordDrawOffsetMap) {
            if (mCurrentRecordDrawOffsetMap != null)
                mCurrentRecordDrawOffsetMap.clear();
            mCurrentRecordDrawOffsetMap = mJob.getRecordList(mOriginPointX - kk, mOriginPointY - kk, mOriginPointX + kk, mOriginPointY + kk);
            Log.e("获取作业", mCurrentRecordDrawOffsetMap.size() + "；");


        }

        mCurrentRecordDrawOffsetMapTemp = new ArrayList<JobRecordInfo>();
        for (int i = 0; i < mCurrentRecordDrawOffsetMap.size(); i++) {


            double[] abcde = getDrawXYONE(new double[]{mCurrentRecordDrawOffsetMap.get(i).B, mCurrentRecordDrawOffsetMap.get(i).L});
            //     Log.e("abc", abc[0] + ";" + abc[1]);
            double[] xy = abcde;
            //    Log.e("xy", xy[0] + ";" + xy[1]);
            JobRecordInfo abcd = new JobRecordInfo();
            abcd.B = xy[0];
            abcd.L = xy[1];
            abcd.SegmentIndex = mCurrentRecordDrawOffsetMap.get(i).SegmentIndex;
            abcd.H = mCurrentRecordDrawOffsetMap.get(i).H;
            mCurrentRecordDrawOffsetMapTemp.add(abcd);
        }
        Log.e("获取作业数量", mCurrentRecordDrawOffsetMapTemp.size() + "；");
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
        double[] abcde = getDrawXYONE(new double[]{this.mCurrentRecordX, this.mCurrentRecordY});
        double[] xy = abcde;
        JobRecordInfo abcd = new JobRecordInfo();
        abcd.B = xy[0];
        abcd.L = xy[1];
        abcd.SegmentIndex =JobRecordId;
        abcd.H = h;
        mCurrentRecordDrawOffsetMapTemp.add(abcd);
    }


    public boolean mSetGB = false;
    public int count_t = 0;
    public List<JobRecordInfo> mCurrentRecordDrawOffsetMapTemp = new ArrayList<JobRecordInfo>();

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // 获取背景图的宽高
        int bitmapWidth = backgroundBitmap.getWidth();
        int bitmapHeight = backgroundBitmap.getHeight();

        // 计算缩放比例
        float scaleX = (float) viewWidth / bitmapWidth * AppHelper.RATIO_VALUE;
        float scaleY = (float) viewHeight / bitmapHeight *AppHelper. RATIO_VALUE;
        canvas.save(); // 保存当前的画布状态
        Matrix matrix = new Matrix();
        // 设置Matrix对象的缩放比例
        matrix.setScale(scaleX, scaleY);

        // 使用Matrix进行缩放，使背景图填充整个界面
        canvas.setMatrix(matrix);

        // 绘制背景图
        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        canvas.restore(); // 恢复画布状态
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
       ;
        Log.e("作图", AppHelper.RATIO_VALUE + ";");

        float mYaw = 90;
        synchronized (mCurrentRecordDrawOffsetMap) {
            Log.e("获取作业", mCurrentRecordDrawOffsetMap.size() + ";" + mOriginPointX + ";" + mOriginPointY);
            if (mCurrentRecordDrawOffsetMap != null && mCurrentRecordDrawOffsetMap.size() >= 2) {

                if (!mSetGB)
                {

                }
                else {
                    mCurrentRecordDrawOffsetMapTemp = new ArrayList<JobRecordInfo>();
                    for (int i = 0; i < mCurrentRecordDrawOffsetMap.size(); i++) {


                        double[] abc = getDrawXYONE(new double[]{mCurrentRecordDrawOffsetMap.get(i).B, mCurrentRecordDrawOffsetMap.get(i).L});
                        //     Log.e("abc", abc[0] + ";" + abc[1]);
                        double[] xy = abc;
                        //    Log.e("xy", xy[0] + ";" + xy[1]);
                        JobRecordInfo abcd = new JobRecordInfo();
                        abcd.B = xy[0];
                        abcd.L = xy[1];
                        abcd.SegmentIndex = mCurrentRecordDrawOffsetMap.get(i).SegmentIndex;
                        abcd.H = mCurrentRecordDrawOffsetMap.get(i).H;
                        mCurrentRecordDrawOffsetMapTemp.add(abcd);
                    }
                    mSetGB=false;
                }

                paint.setStyle(Paint.Style.STROKE);
                Log.e("获取数量", mCurrentRecordDrawOffsetMapTemp.size() + ";" + mOriginPointX + ";" + mOriginPointY);

                for (int i = 1; i < mCurrentRecordDrawOffsetMapTemp.size(); i++) {

                    if (mCurrentRecordDrawOffsetMapTemp.get(i).SegmentIndex == mCurrentRecordDrawOffsetMapTemp.get(i - 1).SegmentIndex) {
                        {
                            if(isPD)
                                setH= - (A * mCurrentRecordDrawOffsetMapTemp.get(i).B + B * mCurrentRecordDrawOffsetMapTemp.get(i).L + D) / C;

                            Log.e("坡度",i+";"+setH+";"+setHsum);
                            if ((mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 > 10) {
                                paint.setColor(Color.MAGENTA);
                            } else if ((mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 >2.5&&(mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 < 10) {
                                paint.setColor(Color.YELLOW);
                            }
                            else if ((mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 >-10&&(mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 < -2.5) {
                                paint.setColor(Color.CYAN);
                            }
                            else if ((mCurrentRecordDrawOffsetMapTemp.get(i).H - setH) * 100 < -10) {
                                paint.setColor(Color.BLUE);
                            }else {
                                paint.setColor(Color.GREEN);
                            }
                            //     Log.e("作图颜色",(mCurrentRecordDrawOffsetMapTemp.get(i).H-setH)+";"+i+";"+mCurrentRecordDrawOffsetMapTemp.get(i).SegmentIndex );
                            paint.setStrokeWidth(AppHelper.RATIO_VALUE*AppHelper.width);
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            paint.setDither(true);
                            canvas.drawLine((float) mCurrentRecordDrawOffsetMapTemp.get(i).B, (float) mCurrentRecordDrawOffsetMapTemp.get(i).L, (float) mCurrentRecordDrawOffsetMapTemp.get(i - 1).B, (float) mCurrentRecordDrawOffsetMapTemp.get(i - 1).L, paint);
                        }
                    } else {
                    }
                }
            }
        }
        count_t++;
    }
    public static double calculateNonOverlapArea(List<RectangleCalculator> rectangleList, RectangleCalculator newRectangle) {
        // Step 1: Compute vertices for all existing rectangles
        // 计算所有长方形的最小外接矩形
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (RectangleCalculator rect : rectangleList) {
            double[][] vertices = rect.calculateVertices();
            for (int i = 0; i < vertices.length; i++) {
                double x = vertices[i][0];
                double y = vertices[i][1];
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        // 计算新长方形的最小外接矩形
        double[][] newVertices = newRectangle.calculateVertices();
        double newMinX = Double.MAX_VALUE;
        double newMaxX = Double.MIN_VALUE;
        double newMinY = Double.MAX_VALUE;
        double newMaxY = Double.MIN_VALUE;
        for (int i = 0; i < newVertices.length; i++) {
            double x = newVertices[i][0];
            double y = newVertices[i][1];
            newMinX = Math.min(newMinX, x);
            newMaxX = Math.max(newMaxX, x);
            newMinY = Math.min(newMinY, y);
            newMaxY = Math.max(newMaxY, y);
        }

        // 计算交集面积
        double overlapWidth = Math.max(0, Math.min(maxX, newMaxX) - Math.max(minX, newMinX));
        double overlapHeight = Math.max(0, Math.min(maxY, newMaxY) - Math.max(minY, newMinY));
        double overlapArea = overlapWidth * overlapHeight;

        // 计算不重叠面积
        double newArea = (newMaxX - newMinX) * (newMaxY - newMinY);
        return Math.max(0, newArea - overlapArea);

    }

    public float angle = 0;

    // 每条AB线的初始变量

    public float IMPLEMENT_WIDTH = 3.00f; // 默认的农具宽度 米
    public double setH = 0; // 默认的农具宽度 米
    public double setHsum = 0; // 默认的农具宽度 米
    public float Yaw = 0.00f; // 角度
    public boolean setA = false;
    public boolean setB = false;
    public boolean setC = false;


    public double mOriginPointX = 0;
    public double mOriginPointY = 0;


    protected ArrayList<JobRecordInfo> mCurrentRecordDrawOffsetMap = new ArrayList<JobRecordInfo>();

    private Job mJob;

    public void seth(double h) {
        this.setH = h;
        this.  setHsum=h;

    }

    public void setYaw(float yaw) {
        this.Yaw = yaw;


    }


    public boolean setAB = false;

    protected HashMap<Integer, double[]> mAbLineMap = null; // 生成的AB附属线的延长后的xy坐标


    public void endDraw() {
        setAB = false;

        setA = false;
        setC = false;

        mOriginPointX = 0;
        mOriginPointY = 0;
        setB = false;
        if (mCurrentRecordDrawOffsetMap != null)
            mCurrentRecordDrawOffsetMap.clear();
        if (mCurrentRecordDrawOffsetMapTemp != null)
            mCurrentRecordDrawOffsetMapTemp.clear();

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


}