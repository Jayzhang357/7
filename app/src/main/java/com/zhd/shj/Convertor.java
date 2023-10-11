package com.zhd.shj;

import android.os.Environment;

import com.zhd.commonhelper.CommonUtil;
import com.zhd.core.data.RefObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ZHD.Coordlib.struct.Coord;
import ZHD.Coordlib.struct.ZHDDatumPar;
import ZHD.Coordlib.struct.ZHDEllipser;
import ZHD.Coordlib.struct.ZHDTempPar;


public class Convertor {

	public static ZHDTempPar mTemppar;
	public static ZHDDatumPar mDatumPar;
	public static ZHDEllipser[] ellipses;
	public static String mGeopath = "";
	private static double FN = 10000000.0;// （北偏移）
	private static double FE = 500000.0;// （东偏移）
	private static double f = 298.2572236;
	private static double a = 6378137;

	public static final String mEllipseFile = "/ellipse.csv";
	public static final String mDefaultFileName = "/zhd.dam";

	// static {
	// try {
	// Coord.init();
	// mTemppar = new ZHDTempPar();
	// mDatumPar = new ZHDDatumPar();
	// if(!LoadInitialParams()) {
	// // Log.e("Convertor", "初始化坐标参数失败!");
	// }
	// } catch (Exception ex) {
	// // Log.e("Convertor", ex.getMessage());
	// }
	// }


	/**
	 * 把字符串转换为角度（弧度单位）
	 *
	 * @param str
	 * @return 北方向或者 东方向为正
	 */
	public static double parseAngle(String str) {
		String[] parts = str.split("\\:");
		double part1 = Double.parseDouble(parts[0]);
		double part2 = Double.parseDouble(parts[1]);
		boolean plus = true;
		char last_part2 = parts[2].charAt(parts[2].length() - 1);
		if (last_part2 == 'S' || last_part2 == 'W') {
			plus = false;
		} else {
			plus = true;
		}

		double part3 = Double.parseDouble(parts[2].substring(0, 8));
		part1 = part1 + part2 / 60 + part3 / 3600;
		if (plus) {
			return part1 * Math.PI / 180;
		} else {
			return -part1 * Math.PI / 180;
		}

	}

	/**
	 * 经纬度转度分秒字串
	 * */
	public static String longlatToTimeStr(double longlatD) {
		String oriStr = longlatD + "";
		String[] times = new String[3];
		try {

			times[0] = oriStr.split(".")[0];
			double timeM = Double.parseDouble("0." + oriStr.split(".")[1]);
			timeM = timeM * 60;
			String minuteStr = timeM + "";
			times[1] = minuteStr.split(".")[0];
			double timeS = Double.parseDouble("0." + minuteStr.split(".")[1]);
			timeS = timeS * 60;
			times[2] = timeS + "";

		} catch (Exception ex) {

		}
		return times[0] + ":" + times[1] + ":" + times[2];

	}

	/**
	 * WGS84默卡托坐标转换为当地平面坐标
	 * */
	public static double[] BLHtoxyh(double B, double L, double H) {
		double[] doubleRet = null;
		try {
			double x = 0, y = 0, h = 0;
			RefObject<Double> tx = new RefObject<Double>(x);
			RefObject<Double> ty = new RefObject<Double>(y);
			RefObject<Double> th = new RefObject<Double>(h);
			Coord.BLHtoxyh(mDatumPar, mTemppar, B, L, H, tx, ty, th);
			x = tx.argvalue;
			y = ty.argvalue;
			h = th.argvalue;
			doubleRet = new double[3];
			doubleRet[0] = x;
			doubleRet[1] = y;
			doubleRet[2] = h;
		} catch (Exception ex) {
		}

		return doubleRet;

	}

	/**
	 * WGS84坐标转换为当地平面坐标,高斯三度带
	 * */
	public static double[] BLHtoxyhGS3(double B, double L, double H, double L0) {
		double e2, l, t, m0, n2, N;
		double A0, A2, A4, A6, A8, X0;
		double x = 0, y = 0, h = 0;

		// L0 = ConvertToRadian(L0);
		B = CommonUtil.ConvertToRadian(B);
		L = CommonUtil.ConvertToRadian(L);

		e2 = 1 - Math.pow((1 - (1.0 / f)), 2);
		// [椭球变形]

		double da = 0;
		double df = 0;
		// 对H不进行修改,H用参数进行改正[20090205]
		// H += EllipsoidExpand.GetDeltaH(B, L);//高程修改
		A0 = 1 + 3 / 4.0 * e2 + 45 / 64.0 * Math.pow(e2, 2) + 350 / 512.0
				* Math.pow(e2, 3) + 11025 / 16384.0 * Math.pow(e2, 4);
		A2 = (-1 / 2.0)
				* (3 / 4.0 * e2 + 60 / 64.0 * Math.pow(e2, 2) + 525 / 512.0
				* Math.pow(e2, 3) + 17640 / 16384.0 * Math.pow(e2, 4));
		A4 = 1 / 4.0 * (15 / 64.0 * Math.pow(e2, 2) + 210 / 512.0
				* Math.pow(e2, 3) + 8820 / 16384.0 * Math.pow(e2, 4));
		A6 = (-1 / 6.0)
				* (35 / 512.0 * Math.pow(e2, 3) + 2520 / 16384.0 * Math.pow(e2,
				4));
		A8 = (1 / 8.0) * 315 / 16384.0 * Math.pow(e2, 4);
		t = Math.tan(B);
		l = L - L0;
		m0 = l * Math.cos(B);
		n2 = e2 / (1 - e2) * Math.pow(Math.cos(B), 2);
		N = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(B), 2));
		X0 = a
				* (1 - e2)
				* (A0 * B + A2 * Math.sin(2 * B) + A4 * Math.sin(4 * B) + A6
				* Math.sin(6 * B) + A8 * Math.sin(8 * B));

		x = X0 + 0.5 * N * t * Math.pow(m0, 2) + 1 / 24.0
				* (5 - Math.pow(t, 2) + 9 * n2 + 4 * Math.pow(n2, 2)) * N * t
				* Math.pow(m0, 4) + 1 / 720.0
				* (61 - 58 * Math.pow(t, 2) + Math.pow(t, 4)) * N * t
				* Math.pow(m0, 6);
		y = N
				* m0
				+ 1
				/ 6.0
				* (1 - Math.pow(t, 2) + n2)
				* N
				* Math.pow(m0, 3)
				+ 1
				/ 120.0
				* (5 - 18 * Math.pow(t, 2) + Math.pow(t, 4) + 14 * n2 - 58 * n2
				* Math.pow(t, 2)) * N * Math.pow(m0, 5);
		y += 500000;
		double[] doubleRet = null;
		try {
			doubleRet = new double[3];
			doubleRet[0] = x;
			doubleRet[1] = y;
			doubleRet[2] = h;
		} catch (Exception ex) {

		}

		return doubleRet;

	}

	/**
	 * 当地平面坐标转换为B L H坐标
	 * */
	public static double[] xyhtoBLHGS3(double x, double y, double h, double L0) {
		double e2, l, n2, t, N, V;
		double A0, B0, Bf, K0, K2, K4, K6;

		double B = 0, L = 0, H = 0;

		e2 = 1 - Math.pow((1 - (1.0 / f)), 2);
		y -= 500000;
		// 1.若有椭球变形,则先变形后再反投影]
		double da = 0;
		double df = 0;
		//

		K0 = 0.5 * (3 / 4.0 * e2 + 45 / 64.0 * Math.pow(e2, 2) + 350 / 512.0
				* Math.pow(e2, 3) + 11025 / 16384.0 * Math.pow(e2, 4));
		K2 = (-1 / 3.0)
				* (63 / 64.0 * Math.pow(e2, 2) + 1108 / 512.0 * Math.pow(e2, 3) + 58239 / 16384.0 * Math
				.pow(e2, 4));
		K4 = (1 / 3.0)
				* (604 / 512.0 * Math.pow(e2, 3) + 68484 / 16384.0 * Math.pow(
				e2, 4));
		K6 = (-1 / 3.0) * (26328 / 16384.0) * Math.pow(e2, 4);

		A0 = 1 + 3 / 4.0 * e2 + 45 / 64.0 * Math.pow(e2, 2) + 350 / 512.0
				* Math.pow(e2, 3) + 11025 / 16384.0 * Math.pow(e2, 4);
		B0 = x / (A0 * a * (1 - e2));
		Bf = B0
				+ Math.sin(2 * B0)
				* (K0 + Math.pow(Math.sin(B0), 2)
				* (K2 + Math.pow(Math.sin(B0), 2)
				* (K4 + K6 * Math.pow(Math.sin(B0), 2))));
		t = Math.tan(Bf);
		n2 = e2 / (1 - e2) * Math.pow(Math.cos(Bf), 2);
		N = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(Bf), 2));
		V = Math.sqrt(1 + n2);
		B = Bf - 0.5 * Math.pow(V, 2) * t * Math.pow(y / N, 2) + 1 / 24.0
				* (5 + 3 * Math.pow(t, 2) + n2 - 9 * Math.pow(n2, 2))
				* Math.pow(V, 2) * t * Math.pow(y / N, 4) - 1 / 720.0
				* (61 + 90 * Math.pow(t, 2) + 45 * Math.pow(n2, 2))
				* Math.pow(V, 2) * t * Math.pow(y / N, 6);
		l = 1
				/ Math.cos(Bf)
				* y
				/ N
				- 1
				/ 6.0
				* (1 + 2 * Math.pow(t, 2) + n2)
				* (1 / Math.cos(Bf))
				* Math.pow(y / N, 3)
				+ 1
				/ 120.0
				* (5 + 28 * Math.pow(t, 2) + 24 * Math.pow(t, 4) + 6 * n2 + 8
				* n2 * Math.pow(t, 2)) * (1 / Math.cos(Bf))
				* Math.pow(y / N, 5);
		L = l + L0;

		//
		// m_B -= EllipsoidExpand.GetDeltaB(m_B, m_L, h);

		// 经纬度改正
		// if (H != 0)
		// {
		// //
		// double dB = GetDeltaB(B, L, H);
		// double dL = GetDeltaL(B, L, H);
		// double dH = GetDeltaH(B, L, H);
		// //
		// B -= dB;
		// L -= dL;
		// H = h - dH;
		// }
		double[] doubleRet = null;
		try {
			doubleRet = new double[2];
			doubleRet[0] = B * 180 / Math.PI;
			doubleRet[1] = L * 180 / Math.PI;
		} catch (Exception ex) {

		}

		return doubleRet;
	}

	/**
	 * 当地平面坐标转换为WGS84默卡托坐标
	 * */
	public static double[] xyhtoBLH(double x, double y, double h) {

		double[] doubleRet = null;
		try {
			double x1 = 0, y1 = 0, h1 = 0;
			RefObject<Double> tx = new RefObject<Double>(x1);
			RefObject<Double> ty = new RefObject<Double>(y1);
			RefObject<Double> th = new RefObject<Double>(h1);
			Coord.xyhtoBLH(mDatumPar, mTemppar, x, y, h, tx, ty, th);
			x1 = tx.argvalue;
			y1 = ty.argvalue;
			h1 = th.argvalue;
			doubleRet = new double[2];
			doubleRet[0] = x1 * 180 / Math.PI;
			doubleRet[1] = y1 * 180 / Math.PI;
		} catch (Exception ex) {
			// Log.e("Convertor", ex.getMessage());
		}

		return doubleRet;
	}

	public final static int W_3 = 0;
	public final static int W_6 = 1;
	public final static int W_None = 2;

	/**
	 * 返回弧度值中央经线
	 *
	 * @param curLongitude
	 *            以度为单位的经度
	 * @param mode
	 *            模式
	 * @return
	 */
	public static double getMeridian(double curLongitude, int mode) {
		double temp;
		switch (mode) {
			case W_3:
				if (curLongitude > 0)
					temp = ((int) (curLongitude + 1.5) / 3) * 3;
				else {
					temp = ((int) (curLongitude - 1.5) / 3) * 3;
				}
				break;
			case W_6:
				temp = ((int) (curLongitude / 6) + 1) * 6 - 3;
				break;
			case W_None:
				temp = curLongitude;
				break;
			default:
				temp = 0;
				break;
		}
		// Log.i("Meridian", ""+temp);
		temp = (temp * Math.PI / 180);
		return temp;
	}

	private static final String csv_splitter = ",";

	/**
	 * 加载椭球（包括内置预定义和用户自定义）
	 */
	public static void load(String csvPath) {

		// 软件内置支持的全球通用椭球
		File csv = new File(csvPath);
		if (!csv.exists()) {
			return;
		}

		try {
			FileInputStream in;
			in = new FileInputStream(csv);
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(reader);
			ellipses = readEllipses(buffer, true);
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 读取的椭球信息
	 *
	 * @param reader
	 *            椭球文件读取对象
	 * @param isChinese
	 *            是否为中文系统
	 * @return
	 */
	public static ZHDEllipser[] readEllipses(BufferedReader reader,
											 boolean isChinese) {
		try {
			// 存到list中再赋值给数组
			ArrayList<ZHDEllipser> ellipsoidsList = new ArrayList<ZHDEllipser>();
			String temp = reader.readLine(); // 跳过第一行
			while ((temp = reader.readLine()) != null) {
				String[] szArray = temp.split(csv_splitter);
				ZHDEllipser tempellpsoid = new ZHDEllipser();
				if (isChinese) {
					tempellpsoid.setName(szArray[0].trim()); // 椭球中文名
				} else {
					tempellpsoid.setName(szArray[1].trim()); // 椭球英文名
				}
				tempellpsoid.setA(toDouble(szArray[2]));
				tempellpsoid.setF(toDouble(szArray[3]));
				ellipsoidsList.add(tempellpsoid); // 添加到列表
			}

			// 赋值给数组
			ZHDEllipser[] pEllipser = new ZHDEllipser[ellipsoidsList.size()];
			pEllipser = new ZHDEllipser[ellipsoidsList.size()];
			for (int i = 0; i < pEllipser.length; i++) {
				pEllipser[i] = new ZHDEllipser();
				pEllipser[i].setName(ellipsoidsList.get(i).getName());
				pEllipser[i].setA(ellipsoidsList.get(i).getA());
				pEllipser[i].setF(ellipsoidsList.get(i).getF());
			}
			return pEllipser;

		} catch (Exception e) {
			return new ZHDEllipser[0];
		}
	}

	/**
	 * 返回转换后的XY值
	 *
	 * @param b
	 * @param l
	 * @return
	 */
	public static double[] getXY(double b, double l) {
		double B = CommonUtil.ConvertToRadian(b);
		double L = CommonUtil.ConvertToRadian(l);

		double[] xyh = Convertor.BLHtoxyh(B, L, 0);
		return xyh;
	}

	private static double toDouble(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public static String WORK_FOLDER_PATH = "";

	/**
	 * 获取系统工作文件夹路径（存放系统各个配置文件及数据库文件的文件夹路径）
	 *
	 * @return
	 */
	public static String getGeoSDPath() {
		if (WORK_FOLDER_PATH == "") {
			Boolean sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
				String sdPath = sdDir.toString();
				WORK_FOLDER_PATH = sdPath + "/HiFarm/Geopath";
				CommonHelper.createFolder(WORK_FOLDER_PATH);
			}
			// File sdDir = Environment.getDataDirectory();// 获取根目录
			// String sdPath = sdDir.toString();
			// WORK_FOLDER_PATH = sdPath + "/HiFarm";
			// Helper.createFolder(WORK_FOLDER_PATH);
		}
		return WORK_FOLDER_PATH;
	}



}
