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
		ex.AddBytes(shortToByte((short) Supply_Voltage), false); // 电源电压
		ex.AddByte((byte) Engine_cooling_water_temperature); // 发动机冷却水温度

		ex.AddBytes(shortToByte((short) Engine_speed), false);// 发动机转速
		ex.AddBytes(shortToByte((short) Target_engine_speed), false);// 发动机目标转速
		ex.AddByte((byte) Percentage_of_actual_engine_Torque);// 发动机实际扭矩百分比

		ex.AddByte((byte) Fuel_temperature);// 燃油温度
		ex.AddBytes(shortToByte((short) Oil_Temperature), false);// 机油温度
		ex.AddByte((byte) Atmospheric_pressure);// 大气压力
		ex.AddBytes(
				shortToByte((short) Internal_temperature_of_engine_compartment),
				false);// 发动机舱内部温度
		ex.AddBytes(shortToByte((short) Atmospheric_temperature), false);// 大气温度
		ex.AddByte((byte) Intake_temperature);// 进气温度
		ex.AddBytes(intToByteArray(Cumulative_engine_time), false);// 发动机累计运行时间
		ex.AddBytes(shortToByte((short) Travelling_speed), false);// 行驶速度
		ex.AddBytes(intToByteArray(Single_trip_mileage), false); // 单次行驶里程
		ex.AddBytes(intToByteArray(Total_mileage), false);// 总行驶里程
		ex.AddBytes(intToByteArray(Single_fuel_consumption), false);// 单次油耗

		ex.AddBytes(intToByteArray(Cumulative_fuel_consumption), false);// 累计油耗
		ex.AddByte((byte) Relative_oil_pressure);// 相对机油压力
		ex.AddByte((byte) Absolute_oil_pressure);// 绝对机油压力
		ex.AddByte((byte) Relative_pressurization_pressure);// 相对增压压力
		ex.AddByte((byte) Absolute_pressurization_pressure);// 绝对增压压力
		ex.AddBytes(shortToByte((short) Fuel_level), false);// 燃油位
		ex.AddByte((byte) Fuel_percentage);// 燃油百分比
		ex.AddByte((byte) Oil_level);// 机油液位
		ex.AddBytes(shortToByte((short) CRANKCASE_pressure), false);// 曲轴箱压力
		ex.AddByte((byte) Cooling_hydraulic_pressure);// 冷却液压力
		ex.AddByte((byte) Coolant_level);// 冷却液液位
		ex.AddByte((byte) Lock_condition);// 锁车状态
		ex.AddByte((byte) GPS_monitoring_function);// GPS监控功能
		ex.AddByte((byte) Key_captcha);// Key验证码
		ex.AddByte((byte) GPSID_match_status);// GPSID匹配状态
		ex.AddByte((byte) Current_job_status);// 当前作业状态
		ex.AddByte((byte) Vehicle_Alert_Status_1);// 车辆报警状态1
		ex.AddByte((byte) Vehicle_Alert_Status_2);// 车辆报警状态2
		ex.AddByte((byte) Full_grain_alert);// 粮满报警
		ex.AddByte((byte) Main_Clutch_State);// 主离合状态
		ex.AddBytes(shortToByte((short) Speed_of_stripping_machine), false);// 剥皮机转速
		ex.AddBytes(shortToByte((short) Elevator_speed), false);// 升运器转速
		ex.AddBytes(shortToByte((short) Height_of_header), false);// 割台高度
		ex.AddBytes(shortToByte((short) Throw_wheel_speed), false);// 抛送轮转速
		ex.AddBytes(shortToByte((short) Vehicle_working_hours), false);// 整车工作时长
		ex.AddBytes(shortToByte((short) Agitator_speed), false);// 搅龙转速
		ex.AddBytes(shortToByte((short) Guide_wheel), false);// 疏导轮
		ex.AddByte((byte) Tank_pressure);// 气罐压力
		ex.AddByte((byte) Hydraulic_oil_temperature);// 液压油温度
		ex.AddBytes(shortToByte((short) Fuel_consumption_per_hour), false);// 小时油耗

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
