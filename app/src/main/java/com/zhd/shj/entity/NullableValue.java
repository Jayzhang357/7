package com.zhd.shj.entity;

import java.util.Date;

public class NullableValue {

	/**
	 * JAVA以1970/1/1为起点开始计时
	 */
	public static long NullDataTime = Date.parse("1970/1/1");
	/**
	 * DotNet以0001/1/1为起点开始计时
	 */
	public static long DotNetNullDataTime = Date.parse("0001/1/1");
}
