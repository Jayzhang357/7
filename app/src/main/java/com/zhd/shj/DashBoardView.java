package com.zhd.shj;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.core.content.ContextCompat;


/**
 * Created by mou on 2017/8/28.
 */

public class DashBoardView extends View {
    private int mRadius; // 扇形半径
    private int mStartAngle = 150; // 起始角度
    private int mSweepAngle = 240; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = 180; // 最大值
    private int mSection = 9; // 值域（mMax-mMin）等分份数
    private int mPortion = 5; // 一个mSection等分份数
    private String mHeaderText = "km/h"; // 表头
    private int mVelocity = 0; // 实时速度
    private int mStrokeWidth; // 画笔宽度
    private int mLength1; // 长刻度的相对圆弧的长度
    private int mLength2; // 刻度读数顶部的相对圆弧的长度


    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint mTextNumPaint;

    //画外边缘线的画笔
    private Paint linePaint;
    //画指针
    private Paint mPaintPointer;
    private Path path;


    private RectF mRectFArc;
    private RectF mRectFInnerArc;
    private Rect mRectText;
    private String[] mTexts;
    private int[] mColors;
    private int mPotinterRaduis;

    public DashBoardView(Context context) {
        this(context, null);
    }

    public DashBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mStrokeWidth = dp2px(3);

        mLength1 = dp2px(8) + mStrokeWidth;
        mLength2 = mLength1 + dp2px(4);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(sp2px(14));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.BLACK);


        mTextNumPaint = new Paint();
        mTextNumPaint.setAntiAlias(true);
        mTextNumPaint.setColor(Color.RED);
        mTextNumPaint.setTextSize(sp2px(24));
        mTextNumPaint.setStyle(Paint.Style.FILL);
        mTextNumPaint.setTextAlign(Paint.Align.CENTER);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mTextNumPaint.setTypeface(font);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setDither(true);

        mPaintPointer = new Paint();
        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setStrokeWidth(3);

        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setColor(Color.BLACK);
        path = new Path();

        mRectFArc = new RectF();
        mRectFInnerArc = new RectF();
        mRectText = new Rect();

        mTexts = new String[mSection + 1]; // 需要显示mSection + 1个刻度读数
        for (int i = 0; i < mTexts.length; i++) {
            int n = (mMax - mMin) / mSection;
            mTexts[i] = String.valueOf(mMin + i * n);
        }

        mColors = new int[]{ContextCompat.getColor(getContext(), R.color.green),
                ContextCompat.getColor(getContext(), R.color.red)};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

        int width = resolveSize(dp2px(260), widthMeasureSpec);
        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;

        // 由起始角度确定的高度
        float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
        // 由结束角度确定的高度
        float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
        int height = (int) Math.max(point1[1] + mStrokeWidth * 2, point2[1] + mStrokeWidth * 2);

        //设置宽高
        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom() + dp2px(10));

        //调用了setMeasuredDimension既可以获得 中心位置
        mCenterX = mCenterY = getMeasuredWidth() / 2f;

        mRectFArc.set(
                getPaddingLeft() + mStrokeWidth,
                getPaddingTop() + mStrokeWidth,
                getMeasuredWidth() - getPaddingRight() - mStrokeWidth,
                getMeasuredWidth() - getPaddingBottom() - mStrokeWidth
        );

        mPaint.setTextSize(sp2px(16));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);

        mRectFInnerArc.set(
                getPaddingLeft() + mLength2 + mRectText.height() + dp2px(25),
                getPaddingTop() + mLength2 + mRectText.height() + dp2px(25),
                getMeasuredWidth() - getPaddingRight() - mLength2 - mRectText.height() - dp2px(25),
                getMeasuredWidth() - getPaddingBottom() - mLength2 - mRectText.height() - dp2px(25)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画外边缘圆弧
         */
        drawCircleOut(canvas);
        /**
         * 画刻度线
         */
        drawLine(canvas);
        /**
         * 画长刻度读数
         */
        drawTextCircle(canvas);

        //画内部的圆弧
        drawInnerCircle(canvas);
        /**
         * 画指针
         */
        drawPointer(canvas);
        /**
         * 画表头
         * 没有表头就不画
         */
        drawTextInner(canvas);
        /**
         * 画实时度数值
         */
//        drawNumber(canvas);
        drawTextNumber(canvas);

    }

    private void drawTextNumber(Canvas canvas) {
        canvas.drawText(String.valueOf(mVelocity), mCenterX, getMeasuredHeight() - mRectText.height(), mTextNumPaint);
    }

    /**
     * 绘制指针
     */
    private void drawPointer(Canvas canvas) {
        mPotinterRaduis = mRadius / 8;
        float initAngle = getAngleFromResult(mVelocity);
        path.reset();
        float[] point1 = getCoordinatePoint(mPotinterRaduis / 2, initAngle + 90);
        path.moveTo(point1[0], point1[1]);
        float[] point2 = getCoordinatePoint(mPotinterRaduis / 2, initAngle - 90);
        path.lineTo(point2[0], point2[1]);
        float[] point3 = getCoordinatePoint(mRadius / 3, initAngle);
        path.lineTo(point3[0], point3[1]);
        path.close();
        canvas.drawPath(path, mPaintPointer);
        // 绘制三角形指针底部的圆弧效果
        canvas.drawCircle((point1[0] + point2[0]) / 2, (point1[1] + point2[1]) / 2,
                mPotinterRaduis, mPaintPointer);
    }


    /**
     * 通过数值得到角度位置
     */
    private float getAngleFromResult(float result) {
        if (result > mMax){
            return mVelocity;
        }
        return mSweepAngle * (result - mMin) / (mMax - mMin) + mStartAngle;
    }


    private void drawNumber(Canvas canvas) {
        mPaint.setColor(ContextCompat.getColor(getContext(),  R.color.alphaYellow));
        mPaint.setStrokeWidth(dp2px(2));
        int xOffset = dp2px(22);
        if (mVelocity >= 100) {
            drawDigitalTube(canvas, mVelocity / 100, -xOffset);
            drawDigitalTube(canvas, (mVelocity - 100) / 10, 0);
            drawDigitalTube(canvas, mVelocity % 100 % 10, xOffset);
        } else if (mVelocity >= 10) {
            drawDigitalTube(canvas, -1, -xOffset);
            drawDigitalTube(canvas, mVelocity / 10, 0);
            drawDigitalTube(canvas, mVelocity % 10, xOffset);
        } else {
            drawDigitalTube(canvas, -1, -xOffset);
            drawDigitalTube(canvas, -1, 0);
            drawDigitalTube(canvas, mVelocity, xOffset);
        }
    }


    /**
     * 实现画刻度线的功能
     */
    private void drawLine(final Canvas canvas) {
        // 保存之前的画布状态
        canvas.save();
        // 移动画布，实际上是改变坐标系的位置
        canvas.translate(mCenterX, mCenterX);
        canvas.rotate(60);
        // 设置画笔的宽度（线的粗细）
        linePaint.setStrokeWidth(2);
        // 累计叠加的角度
        float c = 0;
        float sweepAngle = 240;

        float a = sweepAngle / 45;

        for (int i = 0; i <= 45; i++) {
//            if (c <= sweepAngle) {// 如果累计画过的角度，小于当前有效刻度
            if (i == 0 || i == 45) {
                double p = c / (double) sweepAngle;
                int red = 255 - (int) (p * 255);
                int green = (int) (p * 255);
                linePaint.setARGB(255, green, red, 50);
                canvas.drawLine(0, mRadius - dp2px(14), 0, mRadius - dp2px(5), linePaint);
            } else {
                if (i % mPortion == 0) {
                    double p = c / (double) sweepAngle;
                    int red = 255 - (int) (p * 255);
                    int green = (int) (p * 255);
                    linePaint.setARGB(255, green, red, 50);
                    canvas.drawLine(0, mRadius - dp2px(14), 0, mRadius - dp2px(5), linePaint);
                } else {
                    double p = c / (double) sweepAngle;
                    int red = 255 - (int) (p * 255);
                    int green = (int) (p * 255);
                    linePaint.setARGB(255, green, red, 50);
                    canvas.drawLine(0, mRadius - dp2px(10), 0, mRadius - dp2px(6), linePaint);
                }
            }
            // 画过的角度进行叠加
            c += a;
//            } else {
//                linePaint.setColor(Color.GREEN);
//                canvas.drawLine(0, mRadius - dp2px(10), 0, mRadius - dp2px(5), linePaint);
//            }

            canvas.rotate(a);
        }
        // 恢复画布状态。
        canvas.restore();
    }


    private void drawTextInner(Canvas canvas) {
        if (!TextUtils.isEmpty(mHeaderText)) {
            mTextPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mHeaderText, mCenterX, (float) (mCenterY - mPotinterRaduis - mRectText.height()), mTextPaint);
        }
    }

    private void drawInnerCircle(Canvas canvas) {
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(5));
        mPaint.setShader(generateSweepGradient());
        canvas.drawArc(mRectFInnerArc, mStartAngle + 1, mSweepAngle - 2, false, mPaint);
    }

    private void drawTextCircle(Canvas canvas) {
        float α;
        float[] p;
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i <= mSection; i++) {
            α = mStartAngle + angle * i;
            p = getCoordinatePoint(mRadius - mLength2, α);
            if (α % 360 > 135 && α % 360 < 225) {
                mTextPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 < 45) || (α % 360 > 315 && α % 360 <= 360)) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
            }
            mTextPaint.getTextBounds(mHeaderText, 0, mTexts[i].length(), mRectText);
            int txtH = mRectText.height();
            if (i <= 1 || i >= mSection - 1) {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH / 2, mTextPaint);
            } else if (i == 3) {
                canvas.drawText(mTexts[i], p[0] + txtH / 2, p[1] + txtH, mTextPaint);
            } else if (i == mSection - 3) {
                canvas.drawText(mTexts[i], p[0] - txtH / 2, p[1] + txtH, mTextPaint);
            } else {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH, mTextPaint);
            }
        }
    }

    private void drawCircleOut(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue_normal));
        mPaint.setShader(generateSweepGradient());
        canvas.drawArc(mRectFArc, mStartAngle, mSweepAngle, false, mPaint);
    }


    ValueAnimator valueAnimator;
    private int animatorDuration;

    private void setAnimator(final int percent) {
        //根据变化的幅度来调整动画时长
        animatorDuration = (int) Math.abs(percent - mVelocity) * 40;
        //   在这里我们用到了ValueAnimator，ValuAnimator本质上就是通过设置一个起始值和结束值，来取到一个从起始值到结束值的一个逐渐增长的Animation值。在draw方法中使用这个值并且不断的重绘，就能达到一种动画效果。
        valueAnimator = ValueAnimator.ofInt(mVelocity, percent).setDuration(animatorDuration);
        valueAnimator.setInterpolator(new SpringInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //把获取到的
                mVelocity = (int) animation.getAnimatedValue();
                invalidate();

            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mVelocity = (int) percent;
            }
        });

        valueAnimator.start();

    }

    /**
     * 数码管样式
     */
    //      1
    //      ——
    //   2 |  | 3
    //      —— 4
    //   5 |  | 6
    //      ——
    //       7
    private void drawDigitalTube(Canvas canvas, int num, int xOffset) {
        float x = mCenterX + xOffset;
        float y = mCenterY + dp2px(40);
        int lx = dp2px(5);
        int ly = dp2px(10);
        int gap = dp2px(2);

        // 1
        mPaint.setAlpha(num == -1 || num == 1 || num == 4 ? 25 : 255);
        canvas.drawLine(x - lx, y, x + lx, y, mPaint);
        // 2
        mPaint.setAlpha(num == -1 || num == 1 || num == 2 || num == 3 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx - gap, y + gap, x - lx - gap, y + gap + ly, mPaint);
        // 3
        mPaint.setAlpha(num == -1 || num == 5 || num == 6 ? 25 : 255);
        canvas.drawLine(x + lx + gap, y + gap, x + lx + gap, y + gap + ly, mPaint);
        // 4
        mPaint.setAlpha(num == -1 || num == 0 || num == 1 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx, y + gap * 2 + ly, x + lx, y + gap * 2 + ly, mPaint);
        // 5
        mPaint.setAlpha(num == -1 || num == 1 || num == 3 || num == 4 || num == 5 || num == 7
                || num == 9 ? 25 : 255);
        canvas.drawLine(x - lx - gap, y + gap * 3 + ly,
                x - lx - gap, y + gap * 3 + ly * 2, mPaint);
        // 6
        mPaint.setAlpha(num == -1 || num == 2 ? 25 : 255);
        canvas.drawLine(x + lx + gap, y + gap * 3 + ly,
                x + lx + gap, y + gap * 3 + ly * 2, mPaint);
        // 7
        mPaint.setAlpha(num == -1 || num == 1 || num == 4 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx, y + gap * 4 + ly * 2, x + lx, y + gap * 4 + ly * 2, mPaint);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }


    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     */
    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    private SweepGradient generateSweepGradient() {
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                mColors,
                new float[]{0, mSweepAngle / 360f}
        );

        Matrix matrix = new Matrix();
        matrix.setRotate(mStartAngle - 10, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        return sweepGradient;
    }

    public int getVelocity() {
        return mVelocity;
    }

    public void setVelocity(int velocity) {
        if (mVelocity == velocity || velocity < mMin || velocity > mMax) {
            return;
        }
        setAnimator(velocity);
//        mVelocity = velocity;
    }


    private class SpringInterpolator implements Interpolator {
        private final float mTension;

        public SpringInterpolator() {
            mTension = 0.4f;
        }

        public SpringInterpolator(float tension) {
            mTension = tension;
        }

        @Override
        public float getInterpolation(float input) {
            float result = (float) (Math.pow(2, -10 * input) *
                    Math.sin((input - mTension / 4) * (2 * Math.PI) / mTension) + 1);
            return result;
        }
    }
}

