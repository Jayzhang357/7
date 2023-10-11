package com.zhd.shj.update;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoUpdateServiceAgent {

	private static final String NAMESPACE = "http://tempuri.org/";

	/**
	 * 根据SN号获取作业
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	public static String GetDownloadJob(String webserviceUrl,
										String equipmentname) throws IOException, XmlPullParserException {
		URL url = new URL(webserviceUrl + "/GetDownloadJob");
		HttpURLConnection connection = null;

		connection=(HttpURLConnection)url.openConnection();

		//请求方式为POST
		connection.setRequestMethod("POST");
		//设置请求超时时间
		connection.setConnectTimeout(5000);
		//定义输出流
		OutputStream out=connection.getOutputStream();
		//你需要输出的数据
		String data="equipmentname="+equipmentname;
		//输出数据，并转化为字节型式
		out.write(data.getBytes());
		//读取服务器的响应，通过UTF-8格式读取，因为服务器的中文格式为UTF-8
		BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
		String str="";
		//对服务器的响应做出不同的处理方式，由于android中不能在子线程改变UI，所以通过handler进行转换
		while ((str=reader.readLine())!=null){

			return str;
		}




		return str;

	}
	/**
	 * 根据SN号获取作业
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	public static String GetDownloadJob_1(String webserviceUrl,
										  double  b,double l) throws IOException, XmlPullParserException {
		URL url = new URL(webserviceUrl + "/GetDownloadJob_1");
		HttpURLConnection connection = null;

		connection=(HttpURLConnection)url.openConnection();

		//请求方式为POST
		connection.setRequestMethod("POST");
		//设置请求超时时间
		connection.setConnectTimeout(5000);
		//定义输出流
		OutputStream out=connection.getOutputStream();
		//你需要输出的数据
		String data="b="+b+"&l="+l;
		//输出数据，并转化为字节型式
		out.write(data.getBytes());
		//读取服务器的响应，通过UTF-8格式读取，因为服务器的中文格式为UTF-8
		BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
		String str="";
		//对服务器的响应做出不同的处理方式，由于android中不能在子线程改变UI，所以通过handler进行转换
		while ((str=reader.readLine())!=null){

			return str;
		}




		return str;

	}
	/**
	 * 检测更新
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	public static Object UpdateJob(String webserviceUrl, String SN_num,
								   String Fieldname, String Jobname, double APointB, double APointL,
								   double BPointB, double BPointL, double OriginPointB, double OriginPointL, double APointX, double APointY,
								   double BPointX, double BPointY, double OriginPointX, double OriginPointY) throws IOException,
			XmlPullParserException {

		String method = "UpdateJob";

		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		rpc.addProperty("SN_num", SN_num);
		rpc.addProperty("Fieldname", Fieldname);
		rpc.addProperty("Jobname", Jobname);
		rpc.addProperty("APointB", APointB + "");
		rpc.addProperty("APointL", APointL + "");
		rpc.addProperty("BPointB", BPointB + "");
		rpc.addProperty("BPointL", BPointL + "");
		rpc.addProperty("OriginPointB", OriginPointB + "");
		rpc.addProperty("OriginPointL", OriginPointL + "");
		rpc.addProperty("APointX", APointX + "");
		rpc.addProperty("APointY", APointY + "");
		rpc.addProperty("BPointX", BPointX + "");
		rpc.addProperty("BPointY", BPointY + "");
		rpc.addProperty("OriginPointX", OriginPointX + "");
		rpc.addProperty("OriginPointY", OriginPointY + "");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();
		return object;

	}

	/**
	 * 检测更新
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkUpdate(String webserviceUrl, String apkName,
									 String version) throws IOException, XmlPullParserException {
		String method = "checkUpdateS";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		rpc.addProperty("apkname", "Hi-Farm_V" + version + ".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 检测王长
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object GetWebUrl(String webserviceUrl) throws IOException,
			XmlPullParserException {
		String method = "GetWebUrl";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取UM版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkmcuversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkmcu";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取NW版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkNWversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkNW";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取RD版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkRDversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkRD";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取一体机版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkoneversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkone";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	public static Object checksumversion(String webserviceUrl, String checkname)
			throws IOException, XmlPullParserException {
		String method = checkname;
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取UM版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkumversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkum";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取UM版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkSBGversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkSBG";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 * 获取UM版本名称
	 *
	 * @param
	 * @param
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object checkMDUversion(String webserviceUrl)
			throws IOException, XmlPullParserException {
		String method = "checkMDU";
		SoapObject rpc = new SoapObject(NAMESPACE, method);

		/*
		 * PropertyInfo pi = new PropertyInfo(); pi.setName("requestFileName");
		 * pi.setValue(apkName); pi.setType(apkName.getClass());
		 * rpc.addProperty(pi);
		 */
		// rpc.addProperty("apkname", "Hi-Farm_V"+version+".apk");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 5000);
		ht.debug = true;
		ht.call(NAMESPACE + method, envelope);
		Object object = envelope.getResponse();

		return object;
	}

	/**
	 *
	 * @param webserviceUrl
	 * @param apkName
	 * @param index
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Object getUpdateInfo(String webserviceUrl, String apkName,
									   int index, String method) throws IOException,
			XmlPullParserException {

		SoapObject rpc = new SoapObject(NAMESPACE, method);

		rpc.addProperty("index", index);

		PropertyInfo pi = new PropertyInfo();
		pi.setName("requestFileName");
		pi.setValue(apkName);
		pi.setType(apkName.getClass());
		rpc.addProperty(pi);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE ht = new HttpTransportSE(webserviceUrl, 12000);
		;
		ht.debug = true;

		ht.call(NAMESPACE + method, envelope);

		Object object = envelope.getResponse();

		return object;
	}

}
