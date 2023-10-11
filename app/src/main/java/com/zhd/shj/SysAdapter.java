package com.zhd.shj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhd.shj.R;


import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class SysAdapter extends BaseAdapter {

    private List<String> mDataList;
    private HashMap<String, Boolean> mStates = new HashMap<String, Boolean>();// 用于记录每个RadioButton的状态，并保证只可选一个
    private int mCurrentPosition = -1;
    private boolean mIsNew = true;

    public int getCheckedIndex() {

        return mCurrentPosition;
    }

    private LayoutInflater mInflater;
    private Context mContext;

    public SysAdapter(Context context, List<String> data) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDataList = data;

        mContext = context;
    }

    public void setTextView(int currentPosition) {
    }

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public String getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder = null;

        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.button_option_item, null);

            holder.tvItem = (TextView) view.findViewById(R.id.tvItem);
            holder.rbtnIsChosen = (RadioButton) view
                    .findViewById(R.id.rBtnChoose);

            holder.tvIsChosen= (TextView) view.findViewById(R.id.tvIsChoosen);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final RadioButton radio = (RadioButton) view
                .findViewById(R.id.rBtnChoose);
        holder.tvItem.setText(mDataList.get(position));
//		holder.tvIsChosen.setText(" ");
        holder.rbtnIsChosen.setChecked(false);
        holder.rbtnIsChosen.setFocusable(false);
        holder.rbtnIsChosen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // 重置，确保最多只有一项被选中

                for (String key : mStates.keySet()) {
                    mStates.put(key, false);
                }

                mStates.put(String.valueOf(position), radio.isChecked());
                mCurrentPosition = position;
                SysAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (mStates.get(String.valueOf(position)) == null
                || mStates.get(String.valueOf(position)) == false) {
            res = false;
            mStates.put(String.valueOf(position), false);
        } else
            res = true;

        holder.rbtnIsChosen.setChecked(res);

        int[] colors = { Color.rgb(255, 255, 255), Color.rgb(219, 243, 243) };// RGB颜色
        view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

        if (position == mCurrentPosition) // 如果当前的行就是ListView中选中的一行，就更改显示样式
        {
            view.setBackgroundColor(Color.rgb(49, 106, 197));// 更改整行的背景色
        }

        if(mCurrentPosition == -1)
        {
            if (mIsNew)
                view.setBackgroundColor(Color.rgb(49, 106, 197));// 更改整行的背景色
            mIsNew = false;
        }

        return view;

    }

    public final class ViewHolder {
        public RadioButton rbtnIsChosen;
        public TextView tvItem;
        public TextView tvIsChosen;
    }
}