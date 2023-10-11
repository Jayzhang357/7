package com.zhd.shj;

import android.text.InputType;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class CommonHelper {
    public static DecimalFormat decimalFormat = new DecimalFormat(
            "0.00");
    public static boolean createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            try {
                // 按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return true;
    }
    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }
    /*
     * 文件是否存在
     */
    public static boolean fileIsExist(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return false;
        }
        return true;
    }
    public  static void disableShowSoftInput(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 文件转换为byte
    public static byte[] bigfile2BetyArray(String path) {
        File file = new File(path);
        // init array with file length
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);

            fis.read(bytesArray); // read file into bytes[]
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytesArray;
    }
    // string转换为byte
    public static byte[] StringtoBytes(String value) {
        try {
            byte[] bs = value.getBytes("UTF-8");

            return bs;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static double GetDistance(double long1, double lat1, double long2,
                                     double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
    private static final double EARTH_RADIUS = 6378.137;
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    // 文件转换为byte
    public static byte[] file2BetyArray(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        FileInputStream stream = null;
        ByteArrayOutputStream out = null;
        try {
            stream = new FileInputStream(file);
            out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            return out.toByteArray();// 此方法大文件OutOfMemory
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {

                stream.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
    public static byte[] reYmodemContent_l(byte[] bytes, int num) {

        byte[] YmodemHead = new byte[1029];

        System.arraycopy(bytes, 0, YmodemHead, 3, bytes.length);

        YmodemHead[0] = 0x02;
        YmodemHead[1] = (byte) num;
        YmodemHead[2] = (byte) (255 - num);

        if (bytes.length != 1024) {
            for (int i = 3 + bytes.length; i < 1027; i++) {
                YmodemHead[i] = 0x1A;
            }
        }
        byte[] crc = crc_16_CCITT_False(YmodemHead, YmodemHead.length);
        System.arraycopy(crc, 0, YmodemHead, 1027, crc.length);
        return YmodemHead;
    }

    public static byte[] reYmodemContent_s(byte[] bytes, int num) {

        byte[] YmodemHead = new byte[133];

        System.arraycopy(bytes, 0, YmodemHead, 3, bytes.length);

        YmodemHead[0] = 0x01;
        YmodemHead[1] = (byte) num;
        YmodemHead[2] = (byte) (255 - num);

        if (bytes.length != 128) {
            for (int i = 3 + bytes.length; i < 131; i++) {
                YmodemHead[i] = 0x1A;
            }
        }
        byte[] crc = crc_16_CCITT_False(YmodemHead, YmodemHead.length);
        System.arraycopy(crc, 0, YmodemHead, 131, crc.length);
        return YmodemHead;
    }

    public static byte[] reYmodemEnd() {
        byte[] YmodemEnd = new byte[133];

        YmodemEnd[0] = 0x01;
        YmodemEnd[1] = 0x00;
        YmodemEnd[2] = (byte) (0xFF);

        return YmodemEnd;
    }

    public static byte[] reYmodemHead(byte[] bytes, int length) {
        byte[] YmodemHead = new byte[133];

        System.arraycopy(bytes, 0, YmodemHead, 3, bytes.length);
        String strlenght = length + "";
        byte[] lenghtb = StringtoBytes(strlenght);
        System.arraycopy(lenghtb, 0, YmodemHead, 4 + bytes.length,
                lenghtb.length);

        YmodemHead[0] = 0x01;
        YmodemHead[1] = 0x00;
        YmodemHead[2] = (byte) 0xFF;
        byte[] crc1 = crc_16_CCITT_False(YmodemHead, YmodemHead.length);
        System.arraycopy(crc1, 0, YmodemHead, 131, crc1.length);
        return YmodemHead;
    }

    public static byte[] intToByteArray(int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer
                : integer)) / 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer >>> (n * 8));

        return byteArray;
    }

    // crc
    public static byte[] crc_16_CCITT_False(byte[] bytes, int len) {
        int crc = 0;

        for (int i = 3; i < len - 2; i++) {
            crc = crc ^ bytes[i] << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & ((int) 0x8000)) != 0)
                    crc = crc << 1 ^ 0x1021;
                else
                    crc = crc << 1;
            }
        }
        crc &= 0xffff;

        // 输出String字样的16进制
        String strCrc = Integer.toHexString(crc).toUpperCase();
        System.out.println(strCrc);
        byte[] crb = new byte[2];
        crb[0] = (byte) ((int) crc / 256);
        crb[1] = (byte) ((int) crc % 256);
        return crb;
    }


}
