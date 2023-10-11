package com.zhd.gnssmanager;

public class GnssEnum {

	/**
	 * 主板类型
	 * @author Administrator
	 *
	 */
	public enum MotherBoardEnum{
		ZDT810(0),ZDN810(1),ZDU810(2),ZDU820(3),ZDT820(4),ZDTM820(5),ZC200MU(6);

		MotherBoardEnum(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 差分类型
	 * @author Administrator
	 *
	 */
	public enum DiffTypeEnum{
		内置电台(0),外置电台(1),外置数据(2);

		DiffTypeEnum(int value) {
			this.value = value;
		}

		private int value;

		public int getValue() {
			return this.value;
		}
	}

}
