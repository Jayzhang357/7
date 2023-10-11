package com.zhd.commonhelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 *
 *
 * @author Administrator
 *
 */
public class Exchange {

    public Exchange() {
        boutput = new ByteArrayOutputStream();
        doutput = new DataOutputStream(boutput);
    }

    public void finalize() {
        try {
            doutput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            boutput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] InttoBytes(int value) {
        try {
            doutput.writeInt(value);
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ByteArrayReverse(boutput.toByteArray());
    }

    public byte[] LongtoBytes(long value) {
        try {
            doutput.writeLong(value);
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ByteArrayReverse(boutput.toByteArray());
    }

    public byte[] FloattoBytes(float value) {
        try {
            doutput.writeFloat(value);
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ByteArrayReverse(boutput.toByteArray());
    }

    public byte[] DoubletoBytes(double value) {
        try {
            doutput.writeDouble(value);
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ByteArrayReverse(boutput.toByteArray());
    }

    /**
     *
     *
     * @return
     */
    public byte[] StringtoBytes(String value) {
        try {
            byte[] bs = value.getBytes("UTF-8");
            return bs;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public void AddIntAsBytes(int value) {
        try {
            ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
            DataOutputStream temdoutput = new DataOutputStream(temboutput);
            temdoutput.writeInt(value);
            temdoutput.flush();
            doutput.write(ByteArrayReverse(temboutput.toByteArray()));
            temdoutput.close();
            temboutput.close();
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddLongAsBytes(long value) {
        try {
            ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
            DataOutputStream temdoutput = new DataOutputStream(temboutput);
            temdoutput.writeLong(value);
            doutput.write(ByteArrayReverse(temboutput.toByteArray()));
            temdoutput.close();
            temboutput.close();
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddFloatAsBytes(float value) {
        try {
            ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
            DataOutputStream temdoutput = new DataOutputStream(temboutput);
            temdoutput.writeFloat(value);
            doutput.write(ByteArrayReverse(temboutput.toByteArray()));
            temdoutput.close();
            temboutput.close();
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddDoubleAsBytes(double value) {
        try {
            ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
            DataOutputStream temdoutput = new DataOutputStream(temboutput);
            temdoutput.writeDouble(value);
            doutput.write(ByteArrayReverse(temboutput.toByteArray()));
            temdoutput.close();
            temboutput.close();
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddStringAsBytes(String value) {
        try {
            byte[] bs = value.getBytes("UTF-8");

            ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
            DataOutputStream temdoutput = new DataOutputStream(temboutput);
            temdoutput.writeInt(bs.length);
            doutput.write(ByteArrayReverse(temboutput.toByteArray()));

            doutput.write(bs);
            doutput.flush();
            temdoutput.close();
            temboutput.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddBooleanAsBytes(boolean value) {
        if (value == true)
            try {
                doutput.write(1);
                doutput.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        else
            try {
                doutput.write(0);
                doutput.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public void AddByte(byte val) {
        try {
            doutput.write(val);
            doutput.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AddBytes(byte[] bs, boolean isNeedPrefixLength) {
        try {
            if (isNeedPrefixLength == true) {

                ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
                DataOutputStream temdoutput = new DataOutputStream(temboutput);
                temdoutput.writeInt(bs.length);
                doutput.write(ByteArrayReverse(temboutput.toByteArray()));
                temboutput.close();
                temdoutput.close();
            }

            doutput.write(bs);
            doutput.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] GetAllBytes() {
        byte[] bs = boutput.toByteArray();
        try {
            boutput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            boutput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bs;
    }

    public int BytestoInt(byte[] buf, int index) throws IOException {
        byte[] buff = new byte[4];
        index += 3;
        for (int i = 0; i < 4; i++, index--)
            buff[i] = buf[index];
        ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
        DataInputStream dintput = new DataInputStream(bintput);
        try {
            int value = dintput.readInt();
            dintput.close();
            bintput.close();
            return value;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dintput.close();
            bintput.close();
            throw e;
        }

    }

    public static int BytestoUInt16(byte[] buf, int index) throws IOException {
        byte[] buff = new byte[2];
        buff[0] = buf[index + 1];
        buff[1] = buf[index];


        ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
        DataInputStream dintput = new DataInputStream(bintput);
        try {
            int value = dintput.readUnsignedShort();
            dintput.close();
            bintput.close();
            return value;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dintput.close();
            bintput.close();
            throw e;
        }

    }

    public static short byteArrayToShort(byte[] b, int startIndex)
            throws NullPointerException, IndexOutOfBoundsException {
        return (short) ((b[startIndex + 1] << 8) | (b[startIndex] & 0xFF));
    }

    public long BytestoLong(byte[] buf, int index) throws IOException {
        byte[] buff = new byte[8];
        index += 7;
        for (int i = 0; i < 8; i++, index--)
            buff[i] = buf[index];
        ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
        DataInputStream dintput = new DataInputStream(bintput);
        try {
            long value = dintput.readLong();
            dintput.close();
            bintput.close();
            return value;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dintput.close();
            bintput.close();
            throw e;
        }
    }

    public static float BytestoFloat(byte[] buf, int index) throws IOException {
        byte[] buff = new byte[4];
        index += 3;
        for (int i = 0; i < 4; i++, index--)
            buff[i] = buf[index];
        ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
        DataInputStream dintput = new DataInputStream(bintput);
        try {
            float value = dintput.readFloat();
            dintput.close();
            bintput.close();
            return value;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dintput.close();
            bintput.close();
            throw e;
        }
    }

    public static double BytestoDouble(byte[] buf, int index) throws IOException {
        byte[] buff = new byte[8];

        index += 7;
        for (int i = 0; i < 8; i++, index--)
            buff[i] = buf[index];
        ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
        DataInputStream dintput = new DataInputStream(bintput);
        try {
            double value = dintput.readDouble();
            dintput.close();
            bintput.close();
            return value;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            dintput.close();
            bintput.close();
            throw e;
        }
    }

    /**
     *
     *
     * @return
     */
    public String BytestoString(byte[] buf, int index, int length,
                                boolean resolveLen) throws IOException {
        byte[] bs;
        if (resolveLen) {
            int len = BytestoInt(buf, index);
            //
            bs = new byte[len];
            System.arraycopy(buf, index + 4, bs, 0, bs.length);
        } else {
            bs = new byte[length];
            System.arraycopy(buf, index, bs, 0, bs.length);
        }
        return new String(bs, "UTF-8");
    }

    public boolean ByteToBoolean(byte[] buf, int index) throws IOException {
        if (index >= buf.length)
            throw new IOException();
        else {
            if (buf[index] == 1)
                return true;
            else
                return false;
        }
    }

    public byte[] ExtractBytes(byte[] buf, int index, int length,
                               boolean resolveLen) throws IOException {
        byte[] bs;
        if (resolveLen) {
            int len = BytestoInt(buf, index);
            //
            bs = new byte[len];
        } else {
            bs = new byte[length];
        }
        System.arraycopy(buf, index + 4, bs, 0, bs.length);
        return bs;
    }

    //
    private byte[] ByteArrayReverse(byte[] bs) {
        for (int h = 0, t = bs.length - 1;; h++, t--) {
            if (t == h || h > t)
                break;
            byte tem = bs[h];
            bs[h] = bs[t];
            bs[t] = tem;
        }
        return bs;
    }

    private ByteArrayOutputStream boutput;
    private DataOutputStream doutput;
}
