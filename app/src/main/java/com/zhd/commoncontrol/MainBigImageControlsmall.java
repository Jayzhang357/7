package com.zhd.commoncontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhd.AppHelper;
import com.zhd.shj.R;


/**
 * 自定义ImageButton，模拟ImageButton，并在其下方显示文字
 * @author Administrator
 *
 */
@SuppressLint("ResourceAsColor") public class MainBigImageControlsmall extends LinearLayout {

	private ImageView mImgView = null;
	private Context mContext;
	private int mImgResourceId = 0;
	public TextView t1;
	public MainBigImageControlsmall(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.contorl_big_imgbuttonsmall, this, true);
		mContext = context;
		mImgView = (ImageView)findViewById(R.id.img);
		t1= (TextView)findViewById(R.id.t1);



		t1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// 获取 TextView 的高度
				int textViewHeight = t1.getHeight();
				// 根据高度设置文本大小
				int textSize = textViewHeight/2+5; // 自定义计算文本大小的方法
				if(AppHelper.Language==1&&textSize>25) textSize= textViewHeight/4+2;
				t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

				// 移除 OnGlobalLayoutListener 避免重复执行
				t1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
		mImgView.setSelected(true);
		android.view.ViewGroup.LayoutParams para;
		para = mImgView.getLayoutParams();
		mImgView.setLayoutParams(para);
	}
	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		setMeasuredDimension(widthSpec, heightSpec);
		super.onMeasure(widthSpec, heightSpec);

	}
	/*设置图片接口*/
	public void setImageResource(int resId,String txt){
		mImgResourceId = resId;
		mImgView.setImageResource(resId);
		mImgView.getAdjustViewBounds();

		t1.setText(txt);
	}
	public void setText(String txt){

		t1.setText(txt);
	}
	public void setImageBitmap(Bitmap resId){

		mImgView.setImageBitmap(resId);

	}

	public int getResourseImageId(){
		return mImgResourceId;
	}

	//	     /*设置触摸接口*/
	public void setOnTouch(OnTouchListener listen){
		mImgView.setOnTouchListener(listen);
		//mTextView.setOnTouchListener(listen);
	}
}