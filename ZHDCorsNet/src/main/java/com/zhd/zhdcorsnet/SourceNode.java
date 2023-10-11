package com.zhd.zhdcorsnet;

public class SourceNode {

	/**
	 * 源节点号或者节点名称
	 */
	public String Mountpoint = null;
	/**
	 * 标识
	 */
	public String Identifier = null;
	/**
	 * 电文格式
	 */
	public String Format = null;
	/**
	 * 详细信息
	 */
	public String RTCMDetail = null;
	/**
	 * 载波相位 0:No(e.g DGPS);1:Yes L1(e.g RTK);2:Yes L1+L2(e.g RTK)
	 */
	public String Carrier = null;
	/**
	 * 全球导航卫星系统名称
	 */
	public String GNSS = null;
	/**
	 * 网络名称
	 */
	public String NetWork = null;
	/**
	 * 3个字符的国家代码
	 */
	public String Country = null; // 3个字符
	/**
	 * 纬度位置， 保留小数点后两位
	 */
	public String Latitude = null; // two digits after decimal point
	/**
	 * 经度位置，小数点后两位
	 */
	public String Longitude = null;
	/**
	 * 是否发送NEMA数据
	 * <p>
	 * 0:User Must Not Send Nmea to caster;1:User must Send nmea To Caster
	 */
	public String NMEASent = null;
	/**
	 * 模型
	 * <p>
	 * 0:用户获取到的是单基站数据
	 * </p>
	 * <p>
	 * 1:用户获取到的是VRS网络差分数据，即虚拟基准站
	 * </p>
	 */
	public String Solution = null;
	/**
	 * 生成器
	 */
	public String Generator = null;
	public String Compression = null;
	/**
	 * 认证，
	 * <p>N:None(无);B:Basic(基础);D:Digest(摘要)
	 */
	public String Authentication = null;
	/**
	 * 是否需要付费
	 * <p>
	 * N:No User Fee;Y:Usage is charged
	 */
	public String Fee = null;
	/**
	 * 波特率
	 */
	public String BitRate = null;
}
