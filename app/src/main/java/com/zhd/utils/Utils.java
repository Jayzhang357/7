package com.zhd.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {


    public static byte[] fzcontent(byte[] be, int zs)
    {
        int datasize=0;
        if(be.length/512==zs)
        {
            datasize=(int)be.length%512+2;
            Log.e("长度",datasize+"");
        }
        else
            datasize=514;

        byte[] datasizebyte= Inttobyte(datasize);
        byte[] zsbyte= Inttobyte(zs);
        byte[] fzcontentbs=new byte[3+datasize];
        fzcontentbs[0]=(byte)(0x0C);
        fzcontentbs[1]=datasizebyte[0];
        fzcontentbs[2]=datasizebyte[1];
        fzcontentbs[3]=zsbyte[0];
        fzcontentbs[4]=zsbyte[1];
        for(int i=0;i<datasize-2;i++)
        {
            fzcontentbs[i+5]=be[zs*512+i];
        }
        return  fzcontentbs;
    }


    public static  byte[] getCRC(ArrayList<Byte> abc ,int star,int end) {
        int checksum = 0;

// 遍历 ArrayList 中的每个 Byte 对象
        for (int i=star;i<end;i++) {
            int byteValue = abc.get(i) & 0xFF; // 获取 Byte 对象的无符号值
            checksum += byteValue; // 累加每个字节的无符号值
            // 检查溢出并回绕
        /*    if ((checksum & 0xFFFF0000) != 0) {
                checksum &= 0xFFFF; // 回绕
                checksum++; // 回绕位加1
            }*/
        }

// 将校验和分成两个字节并存储在 byte[2] 数组中
        byte[] checksumBytes = new byte[2];
        checksumBytes[0] = (byte) (checksum & 0xFF); // 低位字节
        checksumBytes[1] = (byte) ((checksum >> 8) & 0xFF);

        if( checksumBytes[0]==(byte)(0x89))
        {
            Log.e("返回字符串","收到88");
        }
        return checksumBytes;
    }

    public static  byte[] getCRC(byte[]  abc ,int star,int end) {
        int checksum = 0;

// 遍历 ArrayList 中的每个 Byte 对象
        for (int i=star;i<end;i++) {
            int byteValue = abc[i] & 0xFF; // 获取 Byte 对象的无符号值
            checksum += byteValue; // 累加每个字节的无符号值
            // 检查溢出并回绕
        /*    if ((checksum & 0xFFFF0000) != 0) {
                checksum &= 0xFFFF; // 回绕
                checksum++; // 回绕位加1
            }*/
        }

// 将校验和分成两个字节并存储在 byte[2] 数组中
        byte[] checksumBytes = new byte[2];
        checksumBytes[0] = (byte) (checksum & 0xFF); // 低位字节
        checksumBytes[1] = (byte) ((checksum >> 8) & 0xFF);

        return checksumBytes;
    }


    public static  byte[] getCRC(String filePath) {
        File file = new File(filePath);

        int checksum = 0; // 初始化校验和

        try {
            InputStream inputStream = new FileInputStream(file);

            int data;
            while ((data = inputStream.read()) != -1) {
                checksum += data; // 累加每个字节的值
                // 或者使用 checksum ^= data; 来执行异或操作
            }

            inputStream.close(); // 关闭文件输入流

        } catch (IOException e) {
            e.printStackTrace();
        }

// 将校验和分成两个字节并存储在 byte[2] 数组中
        byte[] checksumBytes = new byte[2];
        checksumBytes[0] = (byte) (checksum & 0xFF); // 低位字节
        checksumBytes[1] = (byte) ((checksum >> 8) & 0xFF);

        return checksumBytes;
    }


   public static  byte[] Inttobyte(int abc){
        // 将整数转化为两个字节数组
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) (abc & 0xFF); // 低位字节
        byteArray[1] = (byte) ((abc >> 8) & 0xFF); // 高位字节
       return byteArray;
    }

    public static  byte[] Longtobyte(int abc){
        // 将整数转化为两个字节数组
        byte[] byteArray = new byte[4];

// 将int的每个字节提取并存储在byte数组中
        byteArray[0] = (byte) (abc & 0xFF);          // 最低位字节
        byteArray[1] = (byte) ((abc >> 8) & 0xFF);   // 第二个字节
        byteArray[2] = (byte) ((abc >> 16) & 0xFF);  // 第三个字节
        byteArray[3] = (byte) ((abc >> 24) & 0xFF);  // 最高位字节
        return byteArray;
    }
    public static int bytetoint(byte[] src, int srcIndex, int length) {
        byte[] dest = new byte[length];
        for (int i = 0; i < length; i++) {
            dest[i] = src[srcIndex + i];
        }
        int result = little_bytesToInt(dest);
        return result;
    }

    private static int big_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr = (addr << 8) | (bytes[1] & 0xff);
        } else {
            addr = bytes[0] & 0xFF;
            addr = (addr << 8) | (bytes[1] & 0xff);
            addr = (addr << 8) | (bytes[2] & 0xff);
            addr = (addr << 8) | (bytes[3] & 0xff);
        }
        return addr;
    }

    private static int little_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
        } else {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
            addr |= (((int) bytes[2] << 16) & 0xFF0000);
            addr |= (((int) bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }

    private static int getBits(byte b, int start, int length) {
        int bit = (int) ((b >> start) & (0xFF >> (8 - length)));
        return bit;
    }

    private static int getBit(byte b, int i) {
        int bit = (int) ((b >> i) & 0x1);
        return bit;
    }

    private static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    //bits转int
    public static int getByteBits(byte b, int start, int length) {
        byte[] res = getBooleanArray(b);
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += res[start + i] * Math.pow(2, length - 1 - i);
        }
        return result;
    }

    public static int bitArrayToInt(byte[] data) {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            result += data[i] * Math.pow(2, data.length - 1 - i);
        }
        return result;
    }
}
