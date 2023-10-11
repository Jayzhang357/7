package com.zhd.ProtocolFilterManage;

import java.io.IOException;


    /// <summary>
    /// 表示消息体属性，支持序列化和反序列化方法
    /// </summary>
	 class ProtocolBodyAttribute
    {
        //是否分包发送
        public Boolean isHasFragment=false;
        //加密方式
        public Boolean isEncrypt;
        //消息主体的长度
        public int bodyLength;

        /// <summary>
        /// 序列化成字节数组
        /// </summary>
        /// <returns></returns>
        public byte[] ToByteCode()
        {
            short temp=0;
            Exchange ex = new  Exchange();
            return ex.ShorttoBytes(temp);
        }

        /// <summary>
        /// 从字节数组中反序列化回来得到具体的数据项
        /// </summary>
        /// <param name="buf"></param>
        /// <param name="sindex"></param>
        /// <returns>下一个数据处理位置索引</returns>
        public int FromBytes(byte[] buf, int sindex) throws IOException
        {
        	 Exchange ex = new  Exchange();
            short s = ex.BytestoShort(buf, sindex);
            short tmp = s;

            return sindex + 2;
        }
    }

    /// <summary>
    /// 该对象表示如何处理分包编号的问题，支持序列化和反序列化方法
    /// </summary>
    class ProtocolFragmentInfo
    {
        //总包数
        public short FragmentTotal = 1;
        //当前包编号
        public short CurrentFragmentNum = 1;

        /// <summary>
        /// 序列化成字节数组
        /// </summary>
        /// <returns></returns>
        public byte[] ToByteCode()
        {
        	Exchange ex = new Exchange();
            byte[] temp = new byte[4];
            System.arraycopy(ex.ShorttoBytes(FragmentTotal),0, temp,0, 2);
            System.arraycopy(ex.ShorttoBytes(CurrentFragmentNum), 0, temp, 2, 2);
            return temp;
        }
        /// <summary>
        /// 从字节数组中反序列化回来得到具体的数据项
        /// </summary>
        /// <param name="buf"></param>
        /// <param name="sindex"></param>
        /// <returns>下一个数据处理位置索引</returns>
        public int FromBytes(byte[] buf, int sindex) throws IOException
        {
        	Exchange ex = new Exchange();
            FragmentTotal = ex.BytestoShort(buf,sindex);
            sindex += 2;
            CurrentFragmentNum = ex.BytestoShort(buf,sindex);
            sindex += 2;
            return sindex;
        }
    }

    /// <summary>
    /// 表示消息头，支持序列化和反序列化方法
    /// </summary>
    public class ProtocolHead
    {
        //消息ID
        public short MessageID;
        //消息体属性
        public ProtocolBodyAttribute BodyAttri = new ProtocolBodyAttribute();
        //电话号码
        public byte[] PhoneNum = new byte[6];
        //消息流水号
        public short MessageSerialNum = 0;

        //消息流水号
        public short lenght = 0;
        //分包信息
        public ProtocolFragmentInfo FragmentInfo = null;

        /// <summary>
        /// 序列化成字节数组
        /// </summary>
        /// <returns></returns>
        public byte[] ToByteCode()
        {
        	Exchange ex = new Exchange();
        	ex.AddShortAsBytes(MessageID);
            ex.AddShortAsBytes(lenght);

        	ex.AddBytes(PhoneNum, false);
        	ex.AddShortAsBytes(MessageSerialNum);
        	if(FragmentInfo!=null)
        		ex.AddBytes(FragmentInfo.ToByteCode(),false);
            return ex.GetAllBytes();
        }
        /// <summary>
        /// 从字节数组中反序列化回来得到具体的数据项
        /// </summary>
        /// <param name="buf"></param>
        /// <param name="sindex"></param>
        public int FromBytes(byte[] buf,int sindex) throws IOException
        {
        	Exchange ex = new Exchange();
            MessageID = ex.BytestoShort(buf, sindex);
            sindex += 2;
            BodyAttri.FromBytes(buf, sindex);
            sindex += 2;
            System.arraycopy(buf, sindex, PhoneNum,0, 6);
            sindex += 6;
            MessageSerialNum = ex.BytestoShort(buf, sindex);
            sindex += 2;

            if (BodyAttri.isHasFragment)
            {
                FragmentInfo = new ProtocolFragmentInfo();
                FragmentInfo.FragmentTotal = ex.BytestoShort(buf, sindex);
                sindex += 2;
                FragmentInfo.CurrentFragmentNum = ex.BytestoShort(buf, sindex);
                sindex += 2;
            }

            return sindex;
        }
    }