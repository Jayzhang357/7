package com.zhd.ProtocolFilterManage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * 实现基本数据类型与字节数组之间的相互转换，利用java提供的转化机制，不实现底层
 * 序列化出去的数据使用小字节序列与C#的序列化一直；反序列化的原始数组是小字节序列
 *
 * @author Administrator
 *
 */
public class Exchange {

	private static final String TAG = "Exchange";
	public Exchange() {
		boutput = new ByteArrayOutputStream();
		doutput = new DataOutputStream(boutput);
	}
	byte[] Stringtobytes(String abc) {
		byte[] bs = new byte[abc.length() / 2];
		for(int i=0;i<abc.length()/2;i++)
		{
			bs[i]= (byte) Integer.parseInt(abc.substring(i*2,i*2+2));
		}
		return bs;
	}
	public void finalize() {
		try {
			if(doutput != null) {
				doutput.close();
				doutput = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Log.e(TAG, e.getMessage());
		}
		try {
			if(boutput != null) {
				boutput.close();
				boutput = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public byte[] ShorttoBytes(short value) {
		try {
			doutput.writeShort(value);
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
		return ByteArrayReverse(boutput.toByteArray());
	}

	public byte[] InttoBytes(int value) {
		try {
			doutput.writeInt(value);
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
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
			//Log.e(TAG, e.getMessage());
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
			//Log.e(TAG, e.getMessage());
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
			//Log.e(TAG, e.getMessage());
		}
		return ByteArrayReverse(boutput.toByteArray());
	}

	/**
	 * 只支持UTF-8编码
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
			//Log.e(TAG, e.getMessage());
			return null;
		}
	}


	public void AddShortAsBytes(short value) {
		try {
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeShort(value);
			temdoutput.flush();
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temdoutput = null;
			temboutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddIntAsBytes(int value) {
		try {
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeInt(value);
			temdoutput.flush();
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temdoutput = null;
			temboutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddLongAsBytes(long value) {
		try {
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeLong(value);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temdoutput = null;
			temboutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddFloatAsBytes(float value) {
		try {
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeFloat(value);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temdoutput = null;
			temboutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddDoubleAsBytes(double value) {
		try {
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeDouble(value);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temboutput = null;
			temdoutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddStringAsBytes(String value) {
		try {
			byte[] bs = value.getBytes("UTF-8");
			// 处理长度写入过程
			ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeInt(bs.length);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			// 写入字符串内容
			doutput.write(bs);
			doutput.flush();
			temdoutput.close();
			temboutput.close();
			temboutput = null;
			temdoutput = null;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}
	public void AddStringAsBytes_1(String value) {
		try {
			byte[] bs = value.getBytes("UTF-8");
			// 处理长度写入过程
			/*ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeInt(bs.length);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			*/// 写入字符串内容
			doutput.write((byte)bs.length);
			doutput.write(bs);
			doutput.flush();
			/*temdoutput.close();
			temboutput.close();
			temboutput = null;
			temdoutput = null;*/

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}
	public void AddStringAsBytes_2(String value) {
		try {
			byte[] bs = value.getBytes("UTF-8");
			// 处理长度写入过程
			/*ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
			DataOutputStream temdoutput = new DataOutputStream(temboutput);
			temdoutput.writeInt(bs.length);
			doutput.write(ByteArrayReverse(temboutput.toByteArray()));
			*/// 写入字符串内容

			doutput.write(bs);
			doutput.flush();
			/*temdoutput.close();
			temboutput.close();
			temboutput = null;
			temdoutput = null;*/

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
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
				//Log.e(TAG, e.getMessage());
			}
		else
			try {
				doutput.write(0);
				doutput.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.e(TAG, e.getMessage());
			}
	}

	public void AddByte(byte val) {
		try {
			doutput.write(val);
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public void AddBytes(byte[] bs, boolean isNeedPrefixLength) {
		try {
			if (isNeedPrefixLength == true) {
				// 处理长度写入过程
				ByteArrayOutputStream temboutput = new ByteArrayOutputStream();
				DataOutputStream temdoutput = new DataOutputStream(temboutput);
				temdoutput.writeInt(bs.length);
				doutput.write(ByteArrayReverse(temboutput.toByteArray()));
				temboutput.close();
				temdoutput.close();
				temboutput = null;
				temdoutput = null;
			}
			// 处理内容写入过程
			doutput.write(bs);
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
	}

	public byte[] GetAllBytes() {
		byte[] bs = null;
		try {
			bs = boutput.toByteArray();
		} catch (OutOfMemoryError oom) {
			//Log.e("Exchange", "字节长度"+boutput.size() + oom.getMessage());
		}
		try {
			boutput.close();
			boutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
		try {
			doutput.close();
			doutput = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
		return bs;
	}


	public short BytestounShort(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[2];
		//	index += 1;
		for (int i = 0; i < 2; i++, index++)
			buff[i] = buf[index];
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			int value = dintput.readUnsignedShort();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return (short)value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}

	}
	public short BytestoShort(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[2];

		for (int i = 0; i < 2; i++, index++)
		{
			buff[i] = buf[index];

		}
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			short value = dintput.readShort();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}

	}
	public int BytestoInt(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[4];
		//	index += 3;
		for (int i = 0; i < 4; i++, index++)
			buff[i] = buf[index];
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			int value = dintput.readInt();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}

	}

	public long BytestoLong(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[8];
		//index += 7;
		for (int i = 0; i < 8; i++, index++)
			buff[i] = buf[index];
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			long value = dintput.readLong();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}
	}

	public float BytestoFloat(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[4];
		//	index += 3;
		for (int i = 0; i < 4; i++, index++)
			buff[i] = buf[index];
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			float value = dintput.readFloat();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}
	}

	public double BytestoDouble(byte[] buf, int index) throws IOException {
		byte[] buff = new byte[8];
		// 需要反序拷贝
		//	index += 7;
		for (int i = 0; i < 8; i++, index++)
			buff[i] = buf[index];
		ByteArrayInputStream bintput = new ByteArrayInputStream(buff);
		DataInputStream dintput = new DataInputStream(bintput);
		try {
			double value = dintput.readDouble();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dintput.close();
			bintput.close();
			dintput = null;
			bintput = null;
			throw e;
		}
	}

	/**
	 * 只支持UTF-8编码，在该函数中处理字符长度，并提取字符串
	 *
	 * @return
	 */
	public String BytestoString(byte[] buf, int index, int length,
								boolean resolveLen) throws IOException {
		byte[] bs;
		if (resolveLen) {
			int len = BytestoInt(buf, index);
			// 拷贝足够长度的字节数组
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
			// 拷贝足够长度的字节数组
			bs = new byte[len];
		} else {
			bs = new byte[length];
		}
		System.arraycopy(buf, index + 4, bs, 0, bs.length);
		return bs;
	}

	public byte[] state_toBytes(int state,int  msorn,int meorw)  {
		byte[] temp=new byte[4];

		for(int i=0;i<4;i++)
		{
			temp[i]=0;
		}
		temp[0]=(byte)(2*state);
		temp[3]=(byte)((4*1)+(8*1)+(16*1)+(64*msorn)+(128*meorw));
		try {
			doutput.write(temp);
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.e(TAG, e.getMessage());
		}
		return ByteArrayReverse(boutput.toByteArray());

	}

	// 将字节数组倒序，从而与windows平台兼容
	private byte[] ByteArrayReverse(byte[] bs) {
		/*for (int h = 0, t = bs.length - 1;; h++, t--) {
			if (t == h || h > t)
				break;
			byte tem = bs[h];
			bs[h] = bs[t];
			bs[t] = tem;
		}*/
		return bs;
	}

	private ByteArrayOutputStream boutput;
	private DataOutputStream doutput;
}
