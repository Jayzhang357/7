package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class Corn_Can {

	public short Supply_Voltage=(byte) 0xFFFF;
	public byte Engine_cooling_water_temperature=(byte) 0xFF;

	public short Engine_speed=(byte) 0xFFFF;
	public short Target_engine_speed=(byte) 0xFFFF;
	public byte Percentage_of_actual_engine_Torque=(byte) 0xFF;

	public byte Fuel_temperature=(byte) 0xFF;
	public short Oil_Temperature=(byte) 0xFFFF;
	public byte Atmospheric_pressure=(byte) 0xFF;
	public short Internal_temperature_of_engine_compartment=(byte) 0xFFFF;
	public short Atmospheric_temperature=(byte) 0xFFFF;
	public byte Intake_temperature=(byte) 0xFF;
	public int Cumulative_engine_time=(byte) 0xFFFFFFFF;
	public short Travelling_speed=(byte) 0xFFFF;
	public int Single_trip_mileage=(byte) 0xFFFFFFFF;
	public int Total_mileage=(byte) 0xFFFFFFFF; ;
	public int Single_fuel_consumption=(byte) 0xFFFFFFFF;
	public int Cumulative_fuel_consumption=(byte) 0xFFFFFFFF;
	public byte Relative_oil_pressure=(byte) 0xFF;
	public byte Absolute_oil_pressure=(byte) 0xFF;
	public byte Relative_pressurization_pressure=(byte) 0xFF;
	public byte Absolute_pressurization_pressure=(byte) 0xFF;
	public short Fuel_level=(byte) 0xFFFF;
	public byte Fuel_percentage=(byte) 0xFF;
	public byte Oil_level=(byte) 0xFF;
	public short CRANKCASE_pressure=(byte) 0xFFFF;
	public byte Cooling_hydraulic_pressure=(byte) 0xFF;
	public byte Coolant_level=(byte) 0xFF;
	public byte Lock_condition=(byte) 0xFF;
	public byte GPS_monitoring_function=(byte) 0xFF;
	public byte Key_captcha=(byte) 0xFF;
	public byte GPSID_match_status=(byte) 0xFF;
	public byte Current_job_status=(byte) 0xFF;
	public byte Vehicle_Alert_Status_1=(byte) 0xFF;
	public byte Vehicle_Alert_Status_2=(byte) 0xFF;
	public byte Full_grain_alert=(byte) 0xFF;
	public byte Main_Clutch_State=(byte) 0xFF;
	public short Speed_of_stripping_machine=(byte) 0xFFFF;
	public short Elevator_speed=(byte) 0xFFFF;
	public short Height_of_header=(byte) 0xFFFF;
	public short Throw_wheel_speed=(byte) 0xFFFF;
	public short Vehicle_working_hours=(byte) 0xFFFF;
	public short Agitator_speed=(byte) 0xFFFF;
	public short Guide_wheel=(byte) 0xFFFF;
	public byte Tank_pressure=(byte) 0xFF;
	public byte Hydraulic_oil_temperature=(byte) 0xFF;
	public short Fuel_consumption_per_hour=(byte) 0xFFFF;

	public byte[] ToByte() {
		Exchange ex = new Exchange();
		ex.AddByte((byte) 0x0C);
		ex.AddByte((byte) 0x00);
		ex.AddBytes(shortToByte((short) 78), false);
		ex.AddBytes(shortToByte((short) Supply_Voltage), false); // ��Դ��ѹ
		ex.AddByte((byte) Engine_cooling_water_temperature); // ��������ȴˮ�¶�

		ex.AddBytes(shortToByte((short) Engine_speed), false);// ������ת��
		ex.AddBytes(shortToByte((short) Target_engine_speed), false);// ������Ŀ��ת��
		ex.AddByte((byte) Percentage_of_actual_engine_Torque);// ������ʵ��Ť�ذٷֱ�

		ex.AddByte((byte) Fuel_temperature);// ȼ���¶�
		ex.AddBytes(shortToByte((short) Oil_Temperature), false);// �����¶�
		ex.AddByte((byte) Atmospheric_pressure);// ����ѹ��
		ex.AddBytes(
				shortToByte((short) Internal_temperature_of_engine_compartment),
				false);// ���������ڲ��¶�
		ex.AddBytes(shortToByte((short) Atmospheric_temperature), false);// �����¶�
		ex.AddByte((byte) Intake_temperature);// �����¶�
		ex.AddBytes(intToByteArray(Cumulative_engine_time), false);// �������ۼ�����ʱ��
		ex.AddBytes(shortToByte((short) Travelling_speed), false);// ��ʻ�ٶ�
		ex.AddBytes(intToByteArray(Single_trip_mileage), false); // ������ʻ���
		ex.AddBytes(intToByteArray(Total_mileage), false);// ����ʻ���
		ex.AddBytes(intToByteArray(Single_fuel_consumption), false);// �����ͺ�

		ex.AddBytes(intToByteArray(Cumulative_fuel_consumption), false);// �ۼ��ͺ�
		ex.AddByte((byte) Relative_oil_pressure);// ��Ի���ѹ��
		ex.AddByte((byte) Absolute_oil_pressure);// ���Ի���ѹ��
		ex.AddByte((byte) Relative_pressurization_pressure);// �����ѹѹ��
		ex.AddByte((byte) Absolute_pressurization_pressure);// ������ѹѹ��
		ex.AddBytes(shortToByte((short) Fuel_level), false);// ȼ��λ
		ex.AddByte((byte) Fuel_percentage);// ȼ�Ͱٷֱ�
		ex.AddByte((byte) Oil_level);// ����Һλ
		ex.AddBytes(shortToByte((short) CRANKCASE_pressure), false);// ������ѹ��
		ex.AddByte((byte) Cooling_hydraulic_pressure);// ��ȴҺѹ��
		ex.AddByte((byte) Coolant_level);// ��ȴҺҺλ
		ex.AddByte((byte) Lock_condition);// ����״̬
		ex.AddByte((byte) GPS_monitoring_function);// GPS��ع���
		ex.AddByte((byte) Key_captcha);// Key��֤��
		ex.AddByte((byte) GPSID_match_status);// GPSIDƥ��״̬
		ex.AddByte((byte) Current_job_status);// ��ǰ��ҵ״̬
		ex.AddByte((byte) Vehicle_Alert_Status_1);// ��������״̬1
		ex.AddByte((byte) Vehicle_Alert_Status_2);// ��������״̬2
		ex.AddByte((byte) Full_grain_alert);// ��������
		ex.AddByte((byte) Main_Clutch_State);// �����״̬
		ex.AddBytes(shortToByte((short) Speed_of_stripping_machine), false);// ��Ƥ��ת��
		ex.AddBytes(shortToByte((short) Elevator_speed), false);// ������ת��
		ex.AddBytes(shortToByte((short) Height_of_header), false);// ��̨�߶�
		ex.AddBytes(shortToByte((short) Throw_wheel_speed), false);// ������ת��
		ex.AddBytes(shortToByte((short) Vehicle_working_hours), false);// ��������ʱ��
		ex.AddBytes(shortToByte((short) Agitator_speed), false);// ����ת��
		ex.AddBytes(shortToByte((short) Guide_wheel), false);// �赼��
		ex.AddByte((byte) Tank_pressure);// ����ѹ��
		ex.AddByte((byte) Hydraulic_oil_temperature);// Һѹ���¶�
		ex.AddBytes(shortToByte((short) Fuel_consumption_per_hour), false);// Сʱ�ͺ�

		return ex.GetAllBytes();
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();//

			temp = temp >> 8; // ������8λ
		}
		byte get = b[0];

		return b;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

}
