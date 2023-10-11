package com.zhd.zhdcorsnet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StateBroadcast {
	public static final int STATE_NONE = 0;
	public static final int STATE_RECEIVE = 1;
	public static final String ACTION_DIFF_NET_STATE = "com.zhd.zhdcorsnet.action.state";

	private Context context;
	private int cur_state;

	public StateBroadcast(Context ctx){
		this.context = ctx;
	}

	/**
	 * 接收差分
	 */
	public void receiveDiff(){
		sendState(STATE_RECEIVE);
	}

	/**
	 * 断开差分
	 */
	public void breakDiff(){
		sendState(STATE_NONE);
	}

	/**
	 * 发送状态
	 * @param state
	 */
	public void sendState(int state){
		if(cur_state == state){
			return;
		}
		cur_state = state;
		sendBoradcast(state);
		recordContent(state);
	}

	private void sendBoradcast(int state){
		Intent intent = new Intent(ACTION_DIFF_NET_STATE);
		intent.putExtra("diff_state", state);
		context.sendBroadcast(intent);
	}

	private void recordContent(int state){
		SharedPreferences pre = context.getSharedPreferences("diff_net", Context.MODE_PRIVATE);
		pre.edit().putInt("diff_state", state).commit();
	}
}
