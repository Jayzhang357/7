package com.zhd.gps.manage.models;

public class GpsEnum {
	public static enum GpsPositionType {

		None("未定位", 0), Single("单点", 1), RTD("差分定位", 2), SBAS("SBAS", 3), RTK_FIXED(
				"固定解", 4), RTK_FLOAT("浮点解", 5), Fix_POS("RTK_FLOAT", 7), WASS(
				"WASS", 9);

		// 成员变量
		private String name;
		private int index;

		// 构造方法
		private GpsPositionType(String name, int index) {
			this.name = name;
			this.index = index;
		}

		/**
		 * 根据编号获取类型
		 *
		 * @param i
		 *            编号
		 * @return
		 */
		public static GpsPositionType fromInt(int i) {
			switch (i) {
				case 0:
					return None;
				case 1:
					return Single;
				case 2:
					return SBAS;
				case 3:
					return RTD;
				case 4:
					return RTK_FIXED;
				case 5:
					return RTK_FLOAT;
				case 6:
					return RTD;
				case 7:
					return Fix_POS;
				default:
					return None;
			}
		}

		// 普通方法
		public static String getName(int index) {
			for (GpsPositionType type : GpsPositionType.values()) {
				if (type.getIndex() == index) {
					return type.name;
				}
			}
			return null;
		}

		// get set 方法
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public static int getCount() {
			return GpsPositionType.values().length;
		}

	};

	public static enum GpsType {
		GPS(0), GLONASS(1), BD(2), SBAS(3);

		GpsType(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}
}
