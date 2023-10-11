package com.zhd.ProtocolFilterManage;

public class Update_Project {
	private byte project_name_lenght;// 作业名称长度
	private String project_name;// 作业名称



	private int implement_width;// 农具宽度
	private int implement_lateral;// 农具侧向偏移
	private int implement_b_a;// 农具前后偏移
	private int project_area;// 作业面积
	private byte project_state;// 作业状态

	public byte[] ToByte() {
		Exchange ex = new Exchange();

		ex.AddStringAsBytes_1(project_name);


		ex.InttoBytes(implement_width);
		ex.InttoBytes(implement_lateral);
		ex.InttoBytes(implement_b_a);
		ex.InttoBytes(project_area);
		ex.AddByte(project_state);

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


}
