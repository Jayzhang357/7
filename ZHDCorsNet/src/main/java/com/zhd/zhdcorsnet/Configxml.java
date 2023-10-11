package com.zhd.zhdcorsnet;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Configxml implements Serializable {
	private Context mContext;
	private static final long SerialVersionUID = 8512527438264657265L;
	private static Configxml CONFIG_XML;
	private String PATH = Environment.getExternalStorageDirectory().getPath()
			+ File.separator + "CFB" + File.separator + "config.xml";
	private String APNPATh = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "CFB";
	private static Boolean IsChanged = true;

	public Configxml(Context context) {
		mContext = context;
		getConfigFromXml(context, PATH);
	}

	public static Configxml getInstantce(Context mcontext) {
		if (CONFIG_XML == null || IsChanged)
			CONFIG_XML = new Configxml(mcontext);

		IsChanged = false;
		return CONFIG_XML;
	}

	public String Compath = "/dev/ttyS5";//


	public String Baudrate = "115200";//
	public String ip = "";//
	public String port = "";//
	public String username = "";//
	public String password = "";//
	public int Choise = 0;//

	public String getpassword() {
		return password;
	}

	public void setpassword(String password) {
		this.password = password;
	}

	public String getusername() {
		return username;
	}

	public void setusername(String username) {
		this.username = username;
	}

	public String getport() {
		return port;
	}

	public void setport(String port) {
		this.port = port;
	}

	public String getip() {
		return ip;
	}

	public void setip(String ip) {
		this.ip = ip;
	}

	public int getChoise() {
		return Choise;
	}

	public void setChoise(int Choise) {
		this.Choise = Choise;
	}

	public String getBaudrate() {
		return Baudrate;
	}

	public void setBaudrate(String Baudrate) {
		this.Baudrate = Baudrate;
	}

	public String getCompath() {
		return Compath;
	}

	public void setCompath(String Compath) {
		this.Compath = Compath;
	}

	/**
	 * @param context
	 * @param fileName
	 * @return
	 */
	private void getConfigFromXml(Context context, String fileName) {
		try {
			File file = new File(fileName);
			FileInputStream fis;
			fis = new FileInputStream(file);
			XmlPullParser parse = Xml.newPullParser();
			parse.setInput(fis, "UTF-8");
			for (int type = parse.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parse
					.next()) {
				String value = "";
				if (type == XmlPullParser.START_TAG) {
					String tagName = parse.getName();
					if ("Compath".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setCompath("/dev/ttyS5");
							return;
						}
						setCompath(value);
					} else if ("Baudrate".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setBaudrate("15200");
							return;
						}
						setBaudrate(value);
					} else if ("Choise".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setChoise(0);
							return;
						}
						setChoise(Integer.parseInt(value));
					}
					else if ("ip".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setip("");
							return;
						}
						setip(value);
					}
					else if ("port".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setport("");
							return;
						}
						setport(value);
					}
					else if ("username".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setusername("");
							return;
						}
						setusername(value);
					}
					else if ("password".equals(tagName)) {
						value = parse.nextText();
						if (value == null) {
							setpassword("");
							return;
						}
						setpassword(value);
					}
				}


			}

			fis.close();
			IsChanged = false;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存xml
	 *
	 * @return
	 */
	public boolean saveConfigXml() {
		boolean flag = false;

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);

			// <?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
			serializer.startDocument("UTF-8", true);

			// <Config>
			serializer.startTag("", "Config");


			// Compath
			serializer.startTag("", "Compath");
			serializer.text(String.valueOf(getCompath()));
			serializer.endTag("", "Compath");

			// Baudrate
			serializer.startTag("", "Baudrate");
			serializer.text(String.valueOf(getBaudrate()));
			serializer.endTag("", "Baudrate");


			// Choice
			serializer.startTag("", "Choise");
			serializer.text(String.valueOf(getChoise()));
			serializer.endTag("", "Choise");


			// ip
			serializer.startTag("", "ip");
			serializer.text(String.valueOf(getip()));
			serializer.endTag("", "ip");


			// port
			serializer.startTag("", "port");
			serializer.text(String.valueOf(getport()));
			serializer.endTag("", "port");

			// username
			serializer.startTag("", "username");
			serializer.text(String.valueOf(getusername()));
			serializer.endTag("", "username");


			// password
			serializer.startTag("", "password");
			serializer.text(String.valueOf(getpassword()));
			serializer.endTag("", "password");

			// </Config>
			serializer.endTag("", "Config");
			serializer.endDocument();

			File destDir = new File(APNPATh);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			if (writer.toString().getBytes().length > 100) {
				File xml = new File(APNPATh, "config.xml");

				if (!xml.exists()) {
					xml.createNewFile();
				}

				FileOutputStream outStream = new FileOutputStream(xml);

				outStream.write(writer.toString().getBytes());
				outStream.flush();
				outStream.close();

				IsChanged = true;
				flag = true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return flag;
	}
}