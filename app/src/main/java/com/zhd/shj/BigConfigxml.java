package com.zhd.shj;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class BigConfigxml implements Serializable {
    private Context mContext;
    private static final long SerialVersionUID = 8512527438264657265L;
    private static BigConfigxml CONFIG_XML;
    private String PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "SHJ" + File.separator + "Beconfig.xml";
    private String APNPATh = Environment.getExternalStorageDirectory()
            .getPath() + File.separator + "SHJ";
    private static Boolean IsChanged = true;

    public BigConfigxml(Context context) {
        mContext = context;
        getConfigFromXml(context, PATH);
    }

    public static BigConfigxml getInstantce(Context mcontext) {
        if (CONFIG_XML == null || IsChanged)
            CONFIG_XML = new BigConfigxml(mcontext);

        IsChanged = false;
        return CONFIG_XML;
    }
    public String getCompath() {
        return Compath;
    }

    public void setCompath(String Compath) {
        this.Compath = Compath;
    }
    public String sik = "G4882o32ndv0";// 请填写您申请的sik
    public String sis = "52d7937dc63ffba8";// 请填写您申请的sis
    public String Compath = "/dev/ttyS3";//
    public String ip = "203.107.45.154";//
    public int kzzd=20;
    public int kzlmd=5;
    public int wdcs=100;
    public int ycxz=5;
    public int sdwsx=10;
    public int port = 8002;//
    public  int Language=0;
    public int differenstyle=0;
    public String Sourece = "";//
    public String username = "";//
    public String password = "";//
    public String getSourece() {
        return Sourece;
    }

    public void setSourece(String Sourece) {
        this.Sourece = Sourece;
    }
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
                    }
                     else if  ("Language".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             Language=0;
                             return;
                         }
                         Language=Integer.parseInt(value);
                     }
                     else if  ("differenstyle".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             differenstyle=0;
                             return;
                         }
                         differenstyle=Integer.parseInt(value);
                     }
                     else if ("sik".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             sik = "G48c2lp2197d";
                             return;
                         }
                         sik=value;
                     }
                     else if ("sis".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             sis = "G48c2lp2197d";
                             return;
                         }
                         sis=value;
                     }
                     else if ("ip".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             ip="47.97.195.13";
                             return;
                         }
                         ip=value;
                     } else if ("port".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             port=6000;
                             return;
                         }
                         port=Integer.parseInt(value);
                     }
                     else if ("kzzd".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             kzzd=6000;
                             return;
                         }
                         kzzd=Integer.parseInt(value);
                     }
                     else if ("kzlmd".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             kzlmd=6000;
                             return;
                         }
                         kzlmd=Integer.parseInt(value);
                     }
                     else if ("wdcs".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             wdcs=6000;
                             return;
                         }
                         wdcs=Integer.parseInt(value);
                     }
                     else if ("ycxz".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             ycxz=6000;
                             return;
                         }
                         ycxz=Integer.parseInt(value);
                     }
                     else if ("sdwsx".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             sdwsx=6000;
                             return;
                         }
                         sdwsx=Integer.parseInt(value);
                     }
                     else if ("Sourece".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             setSourece("");
                             return;
                         }
                         setSourece(value);
                     } else if ("username".equals(tagName)) {
                         value = parse.nextText();
                         if (value == null) {
                             setusername("");
                             return;
                         }
                         setusername(value);
                     } else if ("password".equals(tagName)) {
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
    public int getport() {
        return port;
    }

    public void setport(int port) {
        this.port = port;
    }

    public String getip() {
        return ip;
    }

    public void setip(String ip) {
        this.ip = ip;
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
            // sik
            serializer.startTag("", "sik");
            serializer.text(sik);
            serializer.endTag("", "sik");
            // sis
            serializer.startTag("", "sis");
            serializer.text(sis);
            serializer.endTag("", "sis");
            // ip
            serializer.startTag("", "ip");
            serializer.text(String.valueOf(getip()));
            serializer.endTag("", "ip");

            // port
            serializer.startTag("", "port");
            serializer.text(String.valueOf(getport()));
            serializer.endTag("", "port");
            // kzzd
            serializer.startTag("", "kzzd");
            serializer.text(String.valueOf(kzzd));
            serializer.endTag("", "kzzd");

            // kzlmd
            serializer.startTag("", "kzlmd");
            serializer.text(String.valueOf(kzlmd));
            serializer.endTag("", "kzlmd");
            // wdcs
            serializer.startTag("", "wdcs");
            serializer.text(String.valueOf(wdcs));
            serializer.endTag("", "wdcs");
            // ycxz
            serializer.startTag("", "ycxz");
            serializer.text(String.valueOf(ycxz));
            serializer.endTag("", "ycxz");
            // sdwsx
            serializer.startTag("", "sdwsx");
            serializer.text(String.valueOf(sdwsx));
            serializer.endTag("", "sdwsx");
            // Language
            serializer.startTag("", "Language");
            serializer.text(Language+"");
            serializer.endTag("", "Language");
            // differenstyle
            serializer.startTag("", "differenstyle");
            serializer.text(differenstyle+"");
            serializer.endTag("", "differenstyle");
            // Sourece
            serializer.startTag("", "Sourece");
            serializer.text(String.valueOf(getSourece()));
            serializer.endTag("", "Sourece");
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
                File xml = new File(APNPATh, "Beconfig.xml");

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
