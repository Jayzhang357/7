package com.zhd.gnssmanager;

/**
 * 卫星详细信息控件条目信息（用于显示卫星号、高度角、方位角、L1、L2）
 * @author Administrator
 *
 */
public class SatellitesListViewItemInfo {

	/**
	 * 可用的屏幕宽度
	 */
	private int width = 0;
	private int maxValue = 0;
	private int value1 = 0;
	private int value2 = 0;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;
	private int resid;
	private int color;
	public int getValue1() {
		return value1;
	}
	public void setValue1(int value) {
		this.value1 = value;
	}
	public int getValue2() {
		return value2;
	}
	public void setValue2(int value2) {
		this.value2 = value2;
	}
	public String getInfo1() {
		return info1;
	}
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	public String getInfo2() {
		return info2;
	}
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	public String getInfo3() {
		return info3;
	}
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	public String getInfo4() {
		return info4;
	}
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	public String getInfo5() {
		return info5;
	}
	public void setInfo5(String info5) {
		this.info5 = info5;
	}
	public int getResid() {
		return resid;
	}
	public void setResid(int resid) {
		this.resid = resid;
	}

	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}

}
