package com.zhd.shj.entity;

public class CommonEnum {
	
	public static String getQxSDK(int Sdktype)
	{
		switch(Sdktype)
		{
			case 101:return "启动成功";	
			case 201:return "鉴权成功";	
			case -203:return "启动失败,建议重试";	
			case -101:return "网络连接不可用,建议重试";	
			case -204:return "获取能力信息失败,请联系业务";	
			case -207:return "需要手动激活,请联系业务";	
			case -208:return "需要在设备端激活,请联系业务";	
			case -211:return "系统错误,请联系业务";	
			case -212:return "当前账号未包含该功能,请联系业务";	
			case -301:return "鉴权失败,建议重试,多次鉴权失败请请联系业务";	
			case -302:return "无可用账号,请联系业务";	
				
			case -303:return "需要手动绑定账号,请联系业务";	
			case -304:return "账号正在绑定中,无需处理";	
			case -305:return "device id与dsk不匹配";	
			case -307:return "账号尚未绑定,请联系业务";		
			case -308:return "账号已过期,请联系业务";	
			case -309:return "账号数不够,请联系业务";	
			case -310:return "当前账号不允许此操作,请联系业务";	
			case -311:return "账号或秘钥错误,请联系业务";	
			case -401:return "调用OPENAPI失败 ,请联系业务";	
			case -402:return "OPENAPI响应报⽂错误,请联系业务";	
			default:
				break;
		}
		return "";
	}
	
	
	
	
	public enum DbBoolean {
		True(0), False(1);

		DbBoolean(int value) {
			this.value = value;

		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 控制类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum ControlType {
		液压阀(0), MDU(1);

		ControlType(int value) {
			this.value = value;

		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 作业类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum JobType {
		AB直线(0), 曲线(1), 圆形(2);

		JobType(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}

		public static String valueOf(int value) {
			switch (value) {
			case 0:
				return "AB直线";
			case 1:
				return "曲线";
			case 2:
				return "圆形";

			default:
				return null;
			}
		}
	}

	/**
	 * 差分类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum DiffType {
		电台模式(0), 网络模式(1), 电台网络模式(2),  网络基站(3), 千寻(4), 千寻SDK(5), 平板千寻SDK(6);

		DiffType(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}

	}

	/**
	 * 网络类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum NetStatus {
		// NO_ERROR 0 正在连接
		// LINK_SUCCESS 9 连接成功
		// ACM_ERROR 1 未检测3g模块
		// AT_ERROR 2 3g模块无响应
		// SIMCARD_ERROR 3 未检测到SIM卡
		// SIGNAL_ERROR 4 3g信号异常
		// LOGIN_ERROR 5 网络注册异常
		// PPP_ERROR 6 连接失败
		// PING_ERROR 7 网络掉线

		// NO_ERROR(0), ACM_ERROR(1), SIMCARD_ERROR(3), AT_ERROR(2),
		// SIGNAL_ERROR(
		// 4), LOGIN_ERROR(5), PPP_ERROR(6), PING_ERROR(7), LINK_SUCESS(9);
		正在连接(0), 无3g模块(1), 模块无响应(2), 无SIM卡(3), 信号异常(4), 注册异常(5), 连接失败(6), 网络掉线(
				7), 已连接(9);

		NetStatus(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}

		public static NetStatus valueOf(int value) { // 手写的从int到enum的转换函数
			switch (value) {
			case 0:
				return 正在连接;
			case 1:
				return 无3g模块;
			case 2:
				return 模块无响应;
			case 3:
				return 无SIM卡;
			case 4:
				return 信号异常;
			case 5:
				return 注册异常;
			case 6:
				return 连接失败;
			case 7:
				return 网络掉线;
			case 9:
				return 已连接;
			default:
				return null;
			}
		}

		// public static NetStatus valueOf(int value) { // 手写的从int到enum的转换函数
		// switch (value) {
		// case 0:
		// return NO_ERROR;
		// case 1:
		// return ACM_ERROR;
		// case 2:
		// return AT_ERROR;
		// case 3:
		// return SIMCARD_ERROR;
		// case 4:
		// return SIGNAL_ERROR;
		// case 5:
		// return LOGIN_ERROR;
		// case 6:
		// return PPP_ERROR;
		// case 7:
		// return PING_ERROR;
		// case 9:
		// return LINK_SUCESS;
		// default:
		// return null;
		// }
		// }

	}

	/**
	 * 单位类型
	 * 
	 * @author Administrator
	 * 
	 */
	public enum Unit {
		公制(0), 市制(1), 英制(2);

		Unit(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 语言
	 * 
	 * @author Administrator
	 * 
	 */
	public enum Language {
		中文(0), 英语(1);

		Language(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * ecu gps mode
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EcuGpsMode {
		RTK_Float(1), RTK_Fixed(2), FlexMode(3), SBAS(4);

		EcuGpsMode(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * ecu steer status
	 * 
	 * @author Administrator
	 * 
	 */
	public enum EcuGpsStatus {
		NO_HEADING(4), RTK_INIT(6), GPS_OK(7);

		EcuGpsStatus(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 驾驶状态
	 * 
	 * @author Administrator
	 * 
	 */
	public enum SteeringStatus {
		Steer_Bad_Signal(0), Steer_No_ECU(1), Steering(3), No_Job(4), No_Heading(
				5);

		SteeringStatus(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}
	/**
	 * 驾驶状态
	 * 
	 * @author Administrator
	 * 
	 */
	public enum JobTyepe {
		AB_Straight(0), Curve_A(1), Curve_B(2);

		JobTyepe(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}
	public enum SteeringGpsStatus {
		NO_COMM_TO_RM(1), NO_RADIO_MODEM(2), NO_BASE_DETECTED(3), NO_HEADING(4), ATT_IN_PROGRESS(
				5), RTK_INIT(6), GPS_OK(7), FLEX_TIME_EXCEEDED(8), WAAS_NOT_CONVERGED(
				9), WAAS_NOT_DETECTED(10), OMNISTAR_NOT_CONVERGED(11), OMNISTAR_NOT_DETECTED(
				12), NMEA_QUALITY_INVALID(13), NMEA_DOP_TOO_HIGH(14), ;

		SteeringGpsStatus(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	public enum StyleType {
		HiFarm(1), Navigation(0);

		StyleType(int value) {
			this.value = value;

		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

}
