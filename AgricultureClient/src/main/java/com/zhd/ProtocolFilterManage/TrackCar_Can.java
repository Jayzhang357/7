package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class TrackCar_Can {

	public byte Percentage_of_actual_engine_Torque=(byte) 0xFF; // ������ʵ��Ť�ذٷֱ�
	public short Engine_speed=(byte) 0xFFFF; ; // ������ת��
	public byte Accelerator_Pedal_position=(byte) 0xFF; // ����̤��λ��
	public byte Fuel_pressure=(byte) 0xFF; //ȼ��ѹ��
	public byte Oil_level=(byte) 0xFF; //����Һλ
	public byte Oil_Pressure=(byte) 0xFF; // ����ѹ��
	public byte Cooling_water_temperature=(byte) 0xFF; //��ȴˮ�¶�
	public byte Fuel_temperature=(byte) 0xFF; // ȼ���¶�
	public short Fuel_consumption_rate=(byte) 0xFFFF; ; // ȼ��������
	public int Cumulative_fuel_consumption=(byte) 0xFFFFFFFF; // �ۼ��ͺ�
	public int Cumulative_engine_time=(byte) 0xFFFFFFFF; ; // �������ۼ�����ʱ��
	
	
	public byte Fuel_quantity=(byte) 0xFF; // ȼ����
	public short Travelling_speed=(byte) 0xFFFF; //��ʻ�ٶ�
	public int Accumulated_Mileage=(byte) 0xFFFFFFFF; ; //�ۼ����
	public byte Main_Clutch_State=(byte) 0xFF; // �����״̬
	public short PTO_speed=(byte) 0xFFFF; ; // PTOת��
	public byte Hydraulic_pressure=(byte) 0xFF; // Һѹϵͳѹ��
	public byte Atmospheric_pressure=(byte) 0xFF; // ����ѹ��
	public short Transmission_oil_temperature=(byte) 0xFFFF; ; // ����������
	public int Single_fuel_consumption=(byte) 0xFFFFFFFF; ; // �����ͺ�
	public short Ambient_temperature=(byte) 0xFFFF; ; //�����¶�
	public short System_voltage=(byte) 0xFFFF; ; //ϵͳ��ѹ
	public byte Percent_Load_at_current_speed=(byte) 0xFF; // ��ǰת���¸��ɰٷֱ�
	public byte Percentage_of_friction_torque=(byte) 0xFF; // Ħ��Ť�ذٷֱ�
	public byte Gearbox_pressure_condition=(byte) 0xFF; // ������ѹ��״̬
	public byte Elevator_left_lift=(byte) 0xFF; // ��������������
	public byte Elevator_right_lift=(byte) 0xFF; // ��������������
	public byte Oil_temperature_of_hydraulic_oil_tank=(byte) 0xFF; //Һѹ��������
	public short Forward_High_Clutch_pressure=(byte) 0xFFFF; ; // ǰ���������ѹ��
	public short Forward_low_clutch_pressure=(byte) 0xFFFF; ; // ǰ���������ѹ��
	public short Reverse_clutch_pressure=(byte) 0xFFFF; ; //���������ѹ��
	public short Transmission_output_speed=(byte) 0xFFFF; ; // ���������ת��
	public short Transmission_input_speed=(byte) 0xFFFF; ; // ����������ת��
	public byte Shift_correlation=(byte) 0xFF; // ��λ���
	public byte Lubrication_pressure=(byte) 0xFF; // ��ѹ��
	public short Rear_power_output_speed=(byte) 0xFFFF; ; // �������ת��
	public byte Working_Light_Correlation=(byte) 0xFF; //���������
	public byte Dpf_Dust_Accumulation=(byte) 0xFF; // DPF������
	public byte Percentage_of_urea_level=(byte) 0xFF; // ����Һλ�ٷֱ� 
	public byte  Light_Status=(byte) 0xFF; //ָʾ��״̬
	public short Light_Status_2=(byte) 0xFFFF; ; // ָʾ��״̬2
	public short Instantaneous_fuel_consumption=(byte) 0xFFFF; ; //˲ʱ�ͺ�
	public short Oil_Temperature=(byte) 0xFFFF; ; // �����¶�
	
	

	public byte[] ToByte() {
		Exchange ex = new Exchange();
		ex.AddByte((byte) 0x0F); 
		ex.AddByte((byte) 0x01);
		
		
		ex.AddBytes(shortToByte((short) 87), false);
	
	

		
		
	
		
		
		ex.AddByte((byte) Percentage_of_actual_engine_Torque); 
		ex.AddBytes(shortToByte(Engine_speed), false); 
		 
		 
		 ex.AddByte((byte) Accelerator_Pedal_position);
		
		 ex.AddByte((byte) Fuel_pressure);
		 ex.AddByte((byte) Oil_level);
		 ex.AddByte((byte) Oil_Pressure);
		 ex.AddByte((byte) Cooling_water_temperature);
		 ex.AddByte((byte) Fuel_temperature);
		 ex.AddBytes(shortToByte(Fuel_consumption_rate), false); 
		 ex.AddBytes(intToByteArray(Cumulative_fuel_consumption), false); // ������ʻ���
		 ex.AddBytes(intToByteArray(Cumulative_engine_time), false); // ������ʻ���
			
	
		
		 ex.AddByte((byte) Fuel_quantity);
		 ex.AddBytes(shortToByte(Travelling_speed), false); 
		 ex.AddBytes(intToByteArray(Accumulated_Mileage), false); // ������ʻ���
			
			
	
		 ex.AddByte((byte) Main_Clutch_State);
		 ex.AddBytes(shortToByte(PTO_speed), false); 
		
		 ex.AddByte((byte) Hydraulic_pressure);
		 ex.AddByte((byte) Atmospheric_pressure);
		 ex.AddBytes(shortToByte(Transmission_oil_temperature), false); 
		 ex.AddBytes(intToByteArray(Single_fuel_consumption), false); // ������ʻ���
			
		
		 ex.AddBytes(shortToByte(Ambient_temperature), false);
		 ex.AddBytes(shortToByte(System_voltage), false);
		
		 ex.AddByte((byte) Percent_Load_at_current_speed);
		 ex.AddByte((byte) Percentage_of_friction_torque);
		 ex.AddByte((byte) Gearbox_pressure_condition);
		 ex.AddByte((byte) Elevator_left_lift);
		 ex.AddByte((byte) Elevator_right_lift);
		 ex.AddByte((byte) Oil_temperature_of_hydraulic_oil_tank);
		 ex.AddBytes(shortToByte(Forward_High_Clutch_pressure), false);
		 ex.AddBytes(shortToByte(Forward_low_clutch_pressure), false);
		 ex.AddBytes(shortToByte(Reverse_clutch_pressure), false);
		 ex.AddBytes(shortToByte(Transmission_output_speed), false);
		 ex.AddBytes(shortToByte(Transmission_input_speed), false);
	
		 
		 
		 ex.AddByte((byte) Shift_correlation);
		 ex.AddByte((byte) Lubrication_pressure);
		 ex.AddBytes(shortToByte(Rear_power_output_speed), false);
		
		 ex.AddByte((byte) Working_Light_Correlation);
		 ex.AddByte((byte) Dpf_Dust_Accumulation);
		 ex.AddByte((byte) Percentage_of_urea_level);
		 ex.AddByte((byte) Light_Status);
		 ex.AddBytes(shortToByte(Light_Status_2), false);
		 ex.AddBytes(shortToByte(Instantaneous_fuel_consumption), false);
		 ex.AddBytes(shortToByte(Oil_Temperature), false);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
		 ex.AddByte((byte) 0);
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
