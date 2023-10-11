package com.zhd.shj.DrawView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.zhd.AppHelper;
import com.zhd.shj.dal.Job;
import com.zhd.shj.entity.JobRecordInfo;
import com.zhd.shj.entity.RectangleCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CoverCoutView extends View {

    public CoverCoutView(Context context) {
        super(context);

    }

    public int RATIO_VALUE = 10;

    public double[] getDrawXYONE(double[] line) {
        double xa = line[0];
        double ya = line[1];
        xa = xa - mOriginPointX;
        ya = ya - mOriginPointY;
        double x = xa * RATIO_VALUE;
        double y = ya * RATIO_VALUE;
        double angle = Math.toRadians(-90); // 翻转角度，这里是30度
        double newX = x * Math.cos(angle) - y * Math.sin(angle); // 翻转后的x坐标
        double newY = x * Math.sin(angle) + y * Math.cos(angle); // 翻转后的y坐标
        return new double[]{newX + AppHelper.zhuanX, (newY + getHeight() - AppHelper.zhuanY)};
    }

    public void setJob(Job job) {
        mJob = job;
        int kk = 10;
        Log.e("view2获取作业", ";");
        synchronized (mCurrentRecordDrawOffsetMap) {
            if (mCurrentRecordDrawOffsetMap != null)
                mCurrentRecordDrawOffsetMap.clear();
            mCurrentRecordDrawOffsetMap = mJob.getRecordList_1(mOriginPointX - kk, mOriginPointY - kk, mOriginPointX + kk, mOriginPointY + kk);
            Log.e("view2获取作业", mCurrentRecordDrawOffsetMap.size() + "；");

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
        Log.e("view2获取作业数量", mCurrentRecordDrawOffsetMapTemp.size() + "；");
    }

    int iiii = 1;

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
        abcd.SegmentIndex = JobRecordId;
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
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        ;
        Log.e("view2获取作业作图", AppHelper.RATIO_VALUE + ";");
        synchronized (mCurrentRecordDrawOffsetMap) {
            Log.e("获取作业", mCurrentRecordDrawOffsetMap.size() + ";" + mOriginPointX + ";" + mOriginPointY);
            if (mCurrentRecordDrawOffsetMap != null && mCurrentRecordDrawOffsetMap.size() >= 2) {


                mCurrentRecordDrawOffsetMapTemp = new ArrayList<JobRecordInfo>();
                for (int i = 0; i < mCurrentRecordDrawOffsetMap.size(); i++) {

                    if (i != mCurrentRecordDrawOffsetMap.size() - 1 && mCurrentRecordDrawOffsetMap.get(i + 1).ID == mCurrentRecordDrawOffsetMap.get(i).ID + 1) {
                        double[] abc = getDrawXYONE(new double[]{mCurrentRecordDrawOffsetMap.get(i).B, mCurrentRecordDrawOffsetMap.get(i).L});
                        //     Log.e("abc", abc[0] + ";" + abc[1]);
                        double[] xy = abc;
                        //    Log.e("xy", xy[0] + ";" + xy[1]);
                        JobRecordInfo abcd = new JobRecordInfo();
                        abcd.B = xy[0];
                        abcd.L = xy[1];
                        abcd.ID = mCurrentRecordDrawOffsetMap.get(i).ID;
                        abcd.SegmentIndex = mCurrentRecordDrawOffsetMap.get(i).SegmentIndex;
                        abcd.H = mCurrentRecordDrawOffsetMap.get(i).H;
                        mCurrentRecordDrawOffsetMapTemp.add(abcd);
                    }
                    if (i == mCurrentRecordDrawOffsetMap.size() - 1 && mCurrentRecordDrawOffsetMap.get(i).ID == mCurrentRecordDrawOffsetMap.get(i - 1).ID + 1) {
                        double[] abc = getDrawXYONE(new double[]{mCurrentRecordDrawOffsetMap.get(i).B, mCurrentRecordDrawOffsetMap.get(i).L});
                        //     Log.e("abc", abc[0] + ";" + abc[1]);
                        double[] xy = abc;
                        //    Log.e("xy", xy[0] + ";" + xy[1]);
                        JobRecordInfo abcd = new JobRecordInfo();
                        abcd.B = xy[0];
                        abcd.L = xy[1];
                        abcd.ID = mCurrentRecordDrawOffsetMap.get(i).ID;
                        abcd.SegmentIndex = mCurrentRecordDrawOffsetMap.get(i).SegmentIndex;
                        abcd.H = mCurrentRecordDrawOffsetMap.get(i).H;
                        mCurrentRecordDrawOffsetMapTemp.add(abcd);
                    }
                }


                paint.setStyle(Paint.Style.STROKE);
                Log.e("view2获取作业获取数量", mCurrentRecordDrawOffsetMapTemp.size() + ";" + mOriginPointX + ";" + mOriginPointY);

                for (int i = 1; i < mCurrentRecordDrawOffsetMapTemp.size(); i++) {

                    if (mCurrentRecordDrawOffsetMapTemp.get(i).SegmentIndex == mCurrentRecordDrawOffsetMapTemp.get(i - 1).SegmentIndex && mCurrentRecordDrawOffsetMapTemp.get(i).ID - 1 == mCurrentRecordDrawOffsetMapTemp.get(i - 1).ID) {
                        {

                            paint.setColor(Color.RED);
                            //     Log.e("作图颜色",(mCurrentRecordDrawOffsetMapTemp.get(i).H-setH)+";"+i+";"+mCurrentRecordDrawOffsetMapTemp.get(i).SegmentIndex );
                            paint.setStrokeWidth(RATIO_VALUE * AppHelper.width);
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            paint.setDither(true);
                            canvas.drawLine((float) mCurrentRecordDrawOffsetMapTemp.get(i).B, (float) mCurrentRecordDrawOffsetMapTemp.get(i).L, (float) mCurrentRecordDrawOffsetMapTemp.get(i - 1).B, (float) mCurrentRecordDrawOffsetMapTemp.get(i - 1).L, paint);


                        }
                    } else {

                    }


                }

            }
        }


    }

    public double getArea() {
        if (mCurrentRecordDrawOffsetMap != null && mCurrentRecordDrawOffsetMap.size() >= 2) {
            double[] abc = getDrawXYONE(new double[]{mCurrentRecordX, mCurrentRecordY});

            double[] abc1 = getDrawXYONE(new double[]{mOriginPointX, mOriginPointY});

            double X1 = abc[0];
            double Y1 = abc[1];
            double X2 = abc1[0];
            double Y2 = abc1[1];
            double w =AppHelper.width * RATIO_VALUE;

            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            draw(bitmapCanvas);

            int targetColor = Color.RED; // 目标颜色

            double count = 0; // 相同颜色像素的计数器
            double count_sum = 0; // 相同颜色像素的计数器
            double x1 = Math.min(X1, X2);
            double y1 = Math.min(Y1, Y2);
            double x2 = Math.max(X1, X2);
            double y2 = Math.max(Y1, Y2);


            if (x1 == x2) { // 如果线段垂直于y轴
                for (int y = (int) y1; y <= y2; y++) {
                    for (int x = (int) (x1 - w / 2); x <= x1 + w / 2; x++) {
                        count_sum++;

                        int pixel = bitmap.getPixel(x, y);
                        if (pixel == targetColor) {
                            count++;
                        }
                    }
                }
            } else if (y1 == y2) { // 如果线段垂直于x轴
                for (int x = (int) x1; x <= x2; x++) {
                    for (int y = (int) (y1 - w / 2); y <= y1 + w / 2; y++) {
                        count_sum++;

                        int pixel = bitmap.getPixel(x, y);
                        if (pixel == targetColor) {
                            count++;
                        }
                    }
                }
            } else { // 如果线段不垂直于x轴也不垂直于y轴
                //    double k = (Y2 - Y1) / (double)(X2 - X1); // 计算斜率
                double angle = Math.atan2(Y2 - Y1, X2 - X1);
                double cosAngle = Math.cos(angle);
                double sinAngle = Math.sin(angle);

                double xOffset = w / 2 * sinAngle;
                double yOffset = w / 2 * cosAngle;
                double[] vertices = new double[8];
                vertices[0] = X1 + xOffset;
                vertices[1] = Y1 - yOffset;
                vertices[2] = X2 + xOffset;
                vertices[3] = Y2 - yOffset;
                vertices[4] = X2 - xOffset;
                vertices[5] = Y2 + yOffset;
                vertices[6] = X1 - xOffset;
                vertices[7] = Y1 + yOffset;
                x1 = Math.min(Math.min(vertices[0], vertices[2]), Math.min(vertices[4], vertices[6]));
                y1 = Math.min(Math.min(vertices[1], vertices[3]), Math.min(vertices[5], vertices[7]));
                x2 = Math.max(Math.max(vertices[0], vertices[2]), Math.max(vertices[4], vertices[6]));
                y2 = Math.max(Math.max(vertices[1], vertices[3]), Math.max(vertices[5], vertices[7]));

/*          canvas.drawPoint( (int)vertices[0],  (int) vertices[1], paint);

            canvas.drawPoint( (int)vertices[2],  (int) vertices[3], paint);

            paint.setColor(Color.BLUE);
            canvas.drawPoint( (int)vertices[4],  (int) vertices[5], paint);

            canvas.drawPoint( (int)vertices[6],  (int) vertices[7], paint);*/
                //     double theta = Math.atan(k); // 计算线段的倾角

                for (int x = (int) x1; x <= x2; x++) {
                    for (int y = (int) y1; y <= y2; y++) {
                        //double distance = Math.abs((y - Y1) *1 Math.cos(theta) - (x - X1) * Math.sin(theta)); // 计算当前点到中轴线的距离
                        double distance = distance((double) X1, (double) Y1, (double) X2, (double) Y2, (double) x, (double) y);
                        if (distance <= w / 2.0 && x >= x1 && x <= x2 && y >= y1 && y <= y2) { // 如果点在长方形内部，则输出
                            count_sum++;

                            int pixel = bitmap.getPixel(x, y);
                            if (pixel == targetColor) {
                                count++;
                            }
                        }
                    }
                }
            }

            if (count / count_sum > 1)
                return 0;
            else
                return 1-(count / count_sum);


        } else {
            return 0;
        }


    }

    // 判断某个点是否在长方形中
    public static double distance(double x1, double y1, double x2, double y2, double x, double y) {
        double distance = 0;
        double a = y2 - y1;
        double b = x1 - x2;
        double c = x2 * y1 - x1 * y2;
        if (a == 0 && b == 0) {
            // P1P2 两点重合
            distance = Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        } else if (a == 0) {
            // P1P2 为水平线
            if (y == y1) {
                distance = 0;
            } else {
                distance = 999999;
            }
        } else if (b == 0) {
            // P1P2 为竖直线
            if (x == x1) {
                distance = 0;
            } else {
                distance = 999999;
            }
        } else {
            // P1P2 为斜线
            double k = -a / b;
            double b1 = y1 - k * x1;
            double d = Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
            double xp = (x + k * y - k * b1) / (1 + k * k);
            double yp = k * xp + b1;
            if (xp < Math.min(x1, x2) || xp > Math.max(x1, x2) || yp < Math.min(y1, y2) || yp > Math.max(y1, y2)) {
                // 点不在 P1P2 上
                distance = 999999;
            } else {
                distance = d;
            }
        }
        return distance;
    }

    public float angle = 0;

    // 每条AB线的初始变量

    public float IMPLEMENT_WIDTH = 3.00f; // 默认的农具宽度 米
    public double setH = 0; // 默认的农具宽度 米
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

    public void CurrentRecord(double mCurrentRecordX, double mCurrentRecordY) {
        this.mCurrentRecordX = mCurrentRecordX;
        this.mCurrentRecordY = mCurrentRecordY;

    }

}