package com.zhd.ProtocolFilterManage;

public class Add_Project {
	private byte project_name_lenght;// 作业名称长度
	private String project_name;// 作业名称
	private byte field_name_lenght;// 田块名称长度
	private String field_name;// 田块名称
	private String terminal_equipment_num;// 终端设备号
	private byte project_style_num;// 作业类型号
	private byte project_style_content;// 作业内容

	private int implement_width;// 农具宽度
	private int implement_lateral;// 农具侧向偏移
	private int implement_b_a;// 农具前后偏移
	private int project_area;// 作业面积
	private byte project_state;// 作业状态
	private byte ab_count;// AB线点数
	private int mLatitude_a; // 纬度
	private int mLongitude_a; // 经度
	private int mLatitude_b; // 纬度
	private int mLongitude_b; // 经度

	public byte[] ToByte() {
		Exchange ex = new Exchange();

		ex.AddStringAsBytes_1(project_name);

		ex.AddStringAsBytes_1(field_name);
		ex.AddStringAsBytes_2(terminal_equipment_num);
		ex.AddByte(project_style_num);
		ex.AddByte(project_style_content);
		ex.InttoBytes(implement_width);
		ex.InttoBytes(implement_lateral);
		ex.InttoBytes(implement_b_a);
		ex.InttoBytes(project_area);
		ex.AddByte(project_state);
		ex.AddByte(ab_count);
		ex.InttoBytes(mLatitude_a);
		ex.InttoBytes(mLongitude_a);
		ex.InttoBytes(mLatitude_b);
		ex.InttoBytes(mLongitude_b);
		/*
		 * for(int i=0;i<(int)ab_count;i++) {
		 * ex.AddIntAsBytes(ab_list[i].getLongitude());
		 * ex.AddIntAsBytes(ab_list[i].getLatitude()); }
		 */
		return ex.GetAllBytes();
	}

	public void Project_name_lenght_set(byte value) {

		project_name_lenght = value;
	}

	public byte Project_name_lenght_get() {

		return project_name_lenght;
	}

	public void Project_name_set(String value) {

		project_name = value;
	}

	public String Project_name_get() {

		return project_name;
	}

	public void Field_name_lenght_set(byte value) {

		field_name_lenght = value;
	}

	public byte Field_name_lenght_get() {

		return field_name_lenght;
	}

	public void Field_name_set(String value) {

		field_name = value;
	}

	public String Field_name_get() {

		return field_name;
	}

	public void Terminal_equipment_num_set(String value) {

		terminal_equipment_num = value;
	}

	public String Terminal_equipment_num_get() {

		return terminal_equipment_num;
	}

	public void Project_style_num_set(byte value) {

		project_style_num = value;
	}

	public byte Project_style_num_get() {

		return project_style_num;
	}

	public void Project_style_content_set(byte value) {

		project_style_content = value;
	}

	public byte Project_style_content_get() {

		return project_style_content;
	}

	public void Implement_width_set(int value) {

		implement_width = value;
	}

	public int Implement_width_get() {

		return implement_width;
	}

	public void Implement_lateral_set(int value) {

		implement_lateral = value;
	}

	public int Implement_lateral_get() {

		return implement_lateral;
	}

	public void Implement_b_a_set(int value) {

		implement_b_a = value;
	}

	public double Implement_b_a_get() {

		return implement_b_a;
	}

	public void Project_area_set(int value) {

		project_area = value;
	}

	public int Project_area_get() {

		return project_area;
	}

	public void Project_state_set(byte value) {

		project_state = value;
	}

	public byte Project_state_get() {

		return project_state;
	}

	public void Ab_count_set(byte value) {

		ab_count = value;
	}

	public byte Ab_count_get() {

		return ab_count;
	}

	public void mLatitude_a_set(int value) {

		mLatitude_a = value;
	}

	public int mLatitude_a_get() {

		return mLatitude_a;
	}

	public void mLongitude_a_set(int value) {

		mLongitude_a = value;
	}

	public int mLongitude_a_get() {

		return mLongitude_a;
	}

	public void mLatitude_b_set(int value) {

		mLatitude_b = value;
	}

	public int mLatitude_b_get() {

		return mLatitude_b;
	}

	public void mLongitude_b_set(int value) {

		mLongitude_b = value;
	}

	public int mLongitude_b_get() {

		return mLongitude_b;
	}

}
