package com.zhd.commonhelper;

/**
 * 字节转换类
 * @author
 *
 */
public class BitConverter {
    /**
     * 数组反转
     *
     * @param array
     * @param start
     *            起始位置
     * @param length
     *            长度
     */
    public static void reverse(int[] array, int start, int length) {
        for (int i = start; i < length / 2 + start; i++) {
            int temp = array[i];
            array[i] = array[2 * start + length - 1 - i];
            array[2 * start + length - 1 - i] = temp;
        }
    }

    /**
     * 数组反转
     *
     * @param array
     * @param start
     *            起始位置
     * @param length
     *            长度
     */
    public static void reverse(char[] array, int start, int length) {
        for (int i = start; i < length / 2 + start; i++) {
            char temp = array[i];
            array[i] = array[2 * start + length - 1 - i];
            array[2 * start + length - 1 - i] = temp;
        }
    }

    public static double GetDouble(byte[] buf, int index) {
        byte[] tmp = new byte[8];
        System.arraycopy(buf, index, tmp, 0, 8);
        reverse(tmp, 0, tmp.length);
        return byteArrayToDouble(tmp, 0);
    }

    /**
     * 数组反转
     *
     * @param array
     * @param start
     *            起始位置
     * @param length
     *            长度
     */
    public static void reverse(double[] array, int start, int length) {
        for (int i = start; i < length / 2 + start; i++) {
            double temp = array[i];
            array[i] = array[2 * start + length - 1 - i];
            array[2 * start + length - 1 - i] = temp;
        }
    }

    /**
     * 数组反转
     *
     * @param array
     * @param start
     *            起始位置
     * @param length
     *            长度
     */
    public static void reverse(byte[] array, int start, int length) {
        for (int i = start; i < length / 2 + start; i++) {
            byte temp = array[i];
            array[i] = array[2 * start + length - 1 - i];
            array[2 * start + length - 1 - i] = temp;
        }
    }

    /**
     * short绫诲瀷杞崲涓哄熀鏈暟鎹被鍨嬪瓧鑺傛暟缁�
     *
     * @param value
     *            鍙屽瓧鑺傛暣鍨�
     * @return 鍩烘湰鏁版嵁绫诲瀷瀛楄妭鏁扮粍byte[]
     */
    public static byte[] shortTobyteArray(short value) {
        byte[] shortBuf = new byte[2];
        for (int i = 1; i >= 0; i--) {
            int offset = i * 8;
            shortBuf[i] = (byte) ((value >>> offset) & 0xff);
        }
        return shortBuf;
    }

    public static final short byteArrayToShort(byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToShort(b, 0);
    }

    public static final short byteArrayToShort(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        return (short) (((b[startIndex + 1] & 0xFF) << 8) | (b[startIndex] & 0xFF));
    }

    /**
     * 鍙屽瓧鑺傝浆鎹负short
     *
     * @param b1
     *            浣庝綅瀛楄妭
     * @param b2
     *            楂樹綅瀛楄妭
     * @return short绫诲瀷鏁板��
     */
    public static final short byteToShort(byte b1, byte b2) {
        return (short) ((b2 << 8) | (b1 & 0xFF));
    }

    /**
     * int绫诲瀷杞崲涓哄熀鏈暟鎹被鍨嬪瓧鑺傛暟缁�
     *
     * @param value
     *            鍥涘瓧鑺傛暣鍨�
     * @return 鍩烘湰鏁版嵁绫诲瀷瀛楄妭鏁扮粍byte[]
     */
    public static byte[] intTobyteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            int offset = i * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static final int byteArrayToInt(byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToInt(b, 0);
    }

    public static int byteArrayToInt(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        return ((b[startIndex + 3] & 0xFF) << 24) | ((b[startIndex + 2] & 0xFF) << 16)
                | ((b[startIndex + 1] & 0xFF) << 8) | (b[startIndex] & 0xFF);
    }

    /**
     * long绫诲瀷杞崲涓哄熀鏈暟鎹被鍨嬪瓧鑺傛暟缁�
     *
     * @param value
     *            鍏瓧鑺傛暣鍨�
     * @return 鍩烘湰鏁版嵁绫诲瀷瀛楄妭鏁扮粍byte[]
     */
    public static byte[] longTobyteArray(long value) {
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            int offset = i * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static long byteArrayToLong(byte[] b) throws NullPointerException,
            IndexOutOfBoundsException {
        // long l = 0;
        // ByteArrayInputStream bais = new ByteArrayInputStream(b);
        // DataInputStream dis = new DataInputStream(bais);
        // try {
        // l = dis.readLong();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // return l;

        return byteArrayToLong(b, 0);
    }

    public static final long byteArrayToLong(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {

        return (long) (((b[startIndex + 7] & 0xFF) << 56)
                | ((b[startIndex + 6] & 0xFF) << 48)
                | ((b[startIndex + 5] & 0xFF) << 40)
                | ((b[startIndex + 4] & 0xFF) << 32)
                | ((b[startIndex + 3] & 0xFF) << 24)
                | ((b[startIndex + 2] & 0xFF) << 16)
                | ((b[startIndex + 1] & 0xFF) << 8)
                | (b[startIndex] & 0xFF));
    }

    /**
     * float绫诲瀷杞崲涓哄熀鏈暟鎹被鍨嬪瓧鑺傛暟缁�
     *
     * @param value
     *            鍥涘瓧鑺傛诞鐐规暟
     * @return 鍩烘湰鏁版嵁绫诲瀷瀛楄妭鏁扮粍byte[]
     */
    public static byte[] singleTobyteArray(float value) {
        int n = Float.floatToIntBits(value);
        return intTobyteArray(n);
        // byte[] b = new byte[4];
        // for (int i = 0; i < 4; i++) {
        // b[i] = (byte) (n >> (24 - i * 8));
        // }
        // return b;
    }

    public static float byteArrayToSingle(byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToSingle(b, 0);
    }

    public static float byteArrayToSingle(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        int value = byteArrayToInt(b, startIndex);
        return Float.intBitsToFloat(value);
    }

    /**
     * double绫诲瀷杞崲涓哄熀鏈暟鎹被鍨嬪瓧鑺傛暟缁�
     *
     * @param value
     *            鍏瓧鑺傛诞鐐规暟
     * @return 鍩烘湰鏁版嵁绫诲瀷瀛楄妭鏁扮粍byte[]
     */
    public static byte[] doubleTobyteArray(double value) {
        long l = Double.doubleToLongBits(value);
        return longTobyteArray(l);
        // byte[] b = new byte[8];
        // for (int i = 0; i < 8; i++) {
        // b[i] = (byte) (l >> (56 - i * 8));
        // }
        // return b;
    }

    public static double byteArrayToDouble(byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToDouble(b, 0);
    }

    public static double byteArrayToDouble(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        long l;
        l = b[startIndex];
        l &= 0xFF;
        l |= (long) (b[startIndex + 1] & 0xFF) << 8;
        l &= 0xFFFF;
        l |= ((long) (b[startIndex + 2] & 0xFF) << 16);
        l &= 0xFFFFFF;
        l |= ((long) (b[startIndex + 3] & 0xFF) << 24);
        l &= 0xFFFFFFFFl;
        l |= ((long) (b[startIndex + 4] & 0xFF) << 32);
        l &= 0xFFFFFFFFFFl;

        l |= ((long) (b[startIndex + 5] & 0xFF) << 40);
        l &= 0xFFFFFFFFFFFFl;
        l |= ((long) (b[startIndex + 6] & 0xFF) << 48);
        l &= 0xFFFFFFFFFFFFFFl;
        l |= ((long) (b[startIndex + 7] & 0xFF) << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 瀛楄妭鏁扮粍杞瓧绗︿覆(UTF-8缂栫爜)
     *
     * @param b
     * @return
     * @throws NullPointerException
     *             if b == null
     * @throws IndexOutOfBoundsException
     *             if count < 0 || startIndex < 0 || startIndex + count > b.length.
     */
    public static String byteArrayToString(byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToString(b, 0, b.length);
    }

    /**
     * 瀛楄妭鏁扮粍杞瓧绗︿覆(UTF-8缂栫爜)
     *
     * @param b
     * @param length
     *            闇�杞崲瀛楄妭闀垮害
     * @return
     * @throws NullPointerException
     *             if b == null
     * @throws IndexOutOfBoundsException
     *             if count < 0 || startIndex < 0 || startIndex + count > b.length.
     */
    public static String byteArrayToString(byte[] b, int length)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToString(b, 0, length);
    }

    /**
     * 瀛楄妭鏁扮粍杞瓧绗︿覆(UTF-8缂栫爜)
     *
     * @param b
     * @param startIndex
     *            鏁扮粍璧峰浣嶇疆绱㈠紩
     * @param count
     *            闇�杞崲瀛楄妭鏁�
     * @return
     * @throws NullPointerException
     *             if b == null
     * @throws IndexOutOfBoundsException
     *             if count < 0 || startIndex < 0 || startIndex + count > b.length.
     */
    public static String byteArrayToString(byte[] b, int startIndex, int count)
            throws NullPointerException, IndexOutOfBoundsException {
        return new String(b, startIndex, count);
    }

    /**
     * short绫诲瀷杞崲涓哄瓧鑺傚皝瑁呯被鏁扮粍
     *
     * @param value
     *            鍙屽瓧鑺傛暣鍨�
     * @return 瀛楄妭灏佽绫绘暟缁凚yte[]
     */
    public static Byte[] shortToByteArray(short value) {
        Byte[] shortBuf = new Byte[2];
        for (int i = 1; i >= 0; i--) {
            int offset = i * 8;
            shortBuf[i] = (byte) ((value >>> offset) & 0xff);
        }
        return shortBuf;
    }

    public static short byteArrayToShort(Byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToShort(b, 0);
    }

    public static short byteArrayToShort(Byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        return (short) ((b[startIndex + 1] << 8) | (b[startIndex] & 0xFF));
    }

    /**
     * 鍙屽瓧鑺傝浆鎹负short
     *
     * @param b1
     *            浣庝綅瀛楄妭
     * @param b2
     *            楂樹綅瀛楄妭
     * @return short绫诲瀷鏁板��
     */
    public static short ByteToShort(Byte b1, Byte b2) {
        return (short) ((b2 << 8) | (b1 & 0xFF));
    }

    /**
     * int绫诲瀷杞崲涓哄瓧鑺傚皝瑁呯被鏁扮粍
     *
     * @param value
     *            鍥涘瓧鑺傛暣鍨�
     * @return 瀛楄妭灏佽绫绘暟缁凚yte[]
     */
    public static Byte[] intToByteArray(int value) {
        Byte[] b = new Byte[4];
        for (int i = 3; i >= 0; i++) {
            int offset = i * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static final int byteArrayToInt(Byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToInt(b, 0);
    }

    public static int byteArrayToInt(Byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        return (b[startIndex] << 24) | ((b[startIndex + 1] & 0xFF) << 16)
                | ((b[startIndex + 2] & 0xFF) << 8)
                | (b[startIndex + 3] & 0xFF);
    }

    /**
     * long绫诲瀷杞崲涓哄瓧鑺傚皝瑁呯被鏁扮粍
     *
     * @param value
     *            鍏瓧鑺傛暣鍨�
     * @return 瀛楄妭灏佽绫绘暟缁凚yte[]
     */
    public static Byte[] longToByteArray(long value) {
        Byte[] b = new Byte[8];
        for (int i = 7; i >= 0; i--) {
            int offset = i * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public static long byteArrayToLong(Byte[] b) throws NullPointerException,
            IndexOutOfBoundsException {
        return byteArrayToLong(b, 0);
    }

    public static final long byteArrayToLong(Byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {

        return (long) ((b[startIndex] << 56)
                | ((b[startIndex + 1] & 0xFF) << 48)
                | ((b[startIndex + 2] & 0xFF) << 40)
                | ((b[startIndex + 3] & 0xFF) << 32)
                | ((b[startIndex + 4] & 0xFF) << 24)
                | ((b[startIndex + 5] & 0xFF) << 16)
                | ((b[startIndex + 6] & 0xFF) << 8) | (b[startIndex + 7] & 0xFF));
    }

    /**
     * float绫诲瀷杞崲涓哄瓧鑺傚皝瑁呯被鏁扮粍
     *
     * @param value
     *            鍥涘瓧鑺傛诞鐐规暟
     * @return 瀛楄妭灏佽绫绘暟缁凚yte[]
     */
    public static Byte[] singleToByteArray(float value) {
        // Byte[] b = new Byte[4];
        int n = Float.floatToIntBits(value);
        return intToByteArray(n);
        // for (int i = 0; i < 4; i++) {
        // b[i] = (byte) (n >> (24 - i * 8));
        // }
        // return b;
    }

    public static float byteArrayToSingle(Byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToSingle(b, 0);
    }

    public static float byteArrayToSingle(Byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        int value = byteArrayToInt(b, startIndex);
        return Float.intBitsToFloat(value);
    }

    /**
     * double绫诲瀷杞崲涓哄瓧鑺傚皝瑁呯被鏁扮粍
     *
     * @param value
     *            鍏瓧鑺傛诞鐐规暟
     * @return 瀛楄妭灏佽绫绘暟缁凚yte[]
     */
    public static Byte[] doubleToByteArray(double value) {
        long l = Double.doubleToLongBits(value);
        return longToByteArray(l);
        // Byte[] b = new Byte[8];
        // for (int i = 0; i < 8; i++) {
        // b[i] = (byte) (l >> (56 - i * 8));
        // }
        // return b;
    }

    public static double byteArrayToDouble(Byte[] b)
            throws NullPointerException, IndexOutOfBoundsException {
        return byteArrayToDouble(b, 0);
    }

    public static double byteArrayToDouble(Byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        long l;
        l = b[startIndex];
        l &= 0xFF;
        l |= (long) b[startIndex + 1] << 8;
        l &= 0xFFFF;
        l |= ((long) b[startIndex + 2] << 16);
        l &= 0xFFFFFF;
        l |= ((long) b[startIndex + 3] << 24);
        l &= 0xFFFFFFFFl;
        l |= ((long) b[startIndex + 4] << 32);
        l &= 0xFFFFFFFFFFl;

        l |= ((long) b[startIndex + 5] << 40);
        l &= 0xFFFFFFFFFFFFl;
        l |= ((long) b[startIndex + 6] << 48);
        l &= 0xFFFFFFFFFFFFFFl;
        l |= ((long) b[startIndex + 7] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 姣旇緝涓や釜瀛楄妭鏁扮粍鏄惁鐩哥瓑
     * @param b1 byte鏁扮粍1
     * @param b2 byte鏁扮粍2
     * @return
     */
    public static  boolean isEquals(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null)
            return false;
        if (b1.length != b2.length)
            return false;
        for (int i = 0; i < b1.length; i++)
            if (b1[i] != b2[i])
                return false;
        return true;
    }

    /**
     * 瀛楄妭杞崲涓�8浣嶆棤绗﹀彿鏁存暟锛�0 ~ 255锛�
     * @param b
     * @return
     */
    public static int toUInt8(byte b){
        return b & 0xFF;
    }


    /**
     * byte鏁扮粍杞崲涓�16浣嶆棤绗﹀彿鏁存暟锛堝嵆C涓殑unsigned short锛�0 ~ 65535锛夛級
     * @param b 鏁扮粍
     * @return
     */
    public static int toUInt16(byte[] b, int start){
        return byteArrayToShort(b, start) & 0xFFFF;
    }

    /**
     * byte鏁扮粍杞崲涓�32浣嶆棤绗﹀彿鏁存暟(鍗矯涓殑unsigned int(0 ~ 4294967295))
     * @param b 鏁扮粍
     * @return
     */
    public static long toUInt32(byte[] b, int start){
        return byteArrayToInt(b, start) & 0xFFFFFFFFL;
    }


    /**
     * 杞崲瀛楄妭鏁扮粍鐨勯『搴�
     *
     * @param b 寰呰浆鎹㈢殑瀛楄妭鏁扮粍
     */
    public static void swap(byte[] b) {
        byte tb = (byte) 0;
        for (int i = 0; i < b.length / 2; i++) {
            tb = b[i];
            b[i] = b[b.length - i - 1];
            b[b.length - i - 1] = tb;
        }
    }
}
