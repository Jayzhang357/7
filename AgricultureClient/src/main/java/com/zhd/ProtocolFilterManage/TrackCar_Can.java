package com.zhd.ProtocolFilterManage;

import java.io.IOException;

public class TrackCar_Can {

	public byte Percentage_of_actual_engine_Torque=(byte) 0xFF; // 发动机实际扭矩百分比
	public short Engine_speed=(byte) 0xFFFF; ; // 发动机转速
	public byte Accelerator_Pedal_position=(byte) 0xFF; // 油门踏板位置
	public byte Fuel_pressure=(byte) 0xFF; //燃油压力
	public byte Oil_level=(byte) 0xFF; //机油液位
	public byte Oil_Pressure=(byte) 0xFF; // 机油压力
	public byte Cooling_water_temperature=(byte) 0xFF; //冷却水温度
	public byte Fuel_temperature=(byte) 0xFF; // 燃油温度
	public short Fuel_consumption_rate=(byte) 0xFFFF; ; // 燃油消耗率
	public int Cumulative_fuel_consumption=(byte) 0xFFFFFFFF; // 累计油耗
	public int Cumulative_engine_time=(byte) 0xFFFFFFFF; ; // 发动机累计运行时间
	
	
	public byte Fuel_quantity=(byte) 0xFF; // 燃油量
	public short Travelling_speed=(byte) 0xFFFF; //行驶速度
	public int Accumulated_Mileage=(byte) 0xFFFFFFFF; ; //累计里程
	public byte Main_Clutch_State=(byte) 0xFF; // 主离合状态
	public short PTO_speed=(byte) 0xFFFF; ; // PTO转速
	public byte Hydraulic_pressure=(byte) 0xFF; // 液压系统压力
	public byte Atmospheric_pressure=(byte) 0xFF; // 大气压力
	public short Transmission_oil_temperature=(byte) 0xFFFF; ; // 变速箱油温
	public int Single_fuel_consumption=(byte) 0xFFFFFFFF; ; // 单次油耗
	public short Ambient_temperature=(byte) 0xFFFF; ; //环境温度
	public short System_voltage=(byte) 0xFFFF; ; //系统电压
	public byte Percent_Load_at_current_speed=(byte) 0xFF; // 当前转速下负荷百分比
	public byte Percentage_of_friction_torque=(byte) 0xFF; // 摩擦扭矩百分比
	public byte Gearbox_pressure_condition=(byte) 0xFF; // 变速箱压力状态
	public byte Elevator_left_lift=(byte) 0xFF; // 提升器左提升力
	public byte Elevator_right_lift=(byte) 0xFF; // 提升器右提升力
	public byte Oil_temperature_of_hydraulic_oil_tank=(byte) 0xFF; //液压油箱油温
	public short Forward_High_Clutch_pressure=(byte) 0xFFFF; ; // 前进高离合器压力
	public short Forward_low_clutch_pressure=(byte) 0xFFFF; ; // 前进低离合器压力
	public short Reverse_clutch_pressure=(byte) 0xFFFF; ; //倒挡离合器压力
	public short Transmission_output_speed=(byte) 0xFFFF; ; // 变速箱输出转速
	public short Transmission_input_speed=(byte) 0xFFFF; ; // 变速箱输入转速
	public byte Shift_correlation=(byte) 0xFF; // 档位相关
	public byte Lubrication_pressure=(byte) 0xFF; // 润滑压力
	public short Rear_power_output_speed=(byte) 0xFFFF; ; // 后动力输出转速
	public byte Working_Light_Correlation=(byte) 0xFF; //工作灯相关
	public byte Dpf_Dust_Accumulation=(byte) 0xFF; // DPF积尘量
	public byte Percentage_of_urea_level=(byte) 0xFF; // 尿素液位百分比 
	public byte  Light_Status=(byte) 0xFF; //指示灯状态
	public short Light_Status_2=(byte) 0xFFFF; ; // 指示灯状态2
	public short Instantaneous_fuel_consumption=(byte) 0xFFFF; ; //瞬时油耗
	public short Oil_Temperature=(byte) 0xFFFF; ; // 机油温度
	
	

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
		 ex.AddBytes(intToByteArray(Cumulative_fuel_consumption), false); // 单次行驶里程
		 ex.AddBytes(intToByteArray(Cumulative_engine_time), false); // 单次行驶里程
			
	
		
		 ex.AddByte((byte) Fuel_quantity);
		 ex.AddBytes(shortToByte(Travelling_speed), false); 
		 ex.AddBytes(intToByteArray(Accumulated_Mileage), false); // 单次行驶里程
			
			
	
		 ex.AddByte((byte) Main_Clutch_State);
		 ex.AddBytes(shortToByte(PTO_speed), false); 
		
		 ex.AddByte((byte) Hydraulic_pressure);
		 ex.AddByte((byte) Atmospheric_pressure);
		 ex.AddBytes(shortToByte(Transmission_oil_temperature), false); 
		 ex.AddBytes(intToByteArray(Single_fuel_consumption), false); // 单次行驶里程
			
		
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

			temp = temp >> 8; // 向右移8位
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
