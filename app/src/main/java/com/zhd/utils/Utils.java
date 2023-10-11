package com.zhd.utils;

public class Utils {

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
