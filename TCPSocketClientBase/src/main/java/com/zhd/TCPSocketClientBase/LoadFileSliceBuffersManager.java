package com.zhd.TCPSocketClientBase;

import java.util.concurrent.*;

public class LoadFileSliceBuffersManager {
    //曾经尝试过一次发送1M的数据，有可能造成服务器端的IOCP机制没反应，故设定比服务器端的小
    public static int m_FileSliceSize = 1024 * 16;
    public static int m_FileSliceCount = 10;
    public static int m_2MKSizeToWrite = 16 * 1024; //2097152;
    private static byte[] m_ProtocolHead;
    private static int m_WrapFileSliceProtocolLen;
    //能一次性存储几个文件块的缓冲区
    public static byte[] m_BufferForSeveralFileSlicesRead;
    //已经封转了协议框架的文件块
    private static ConcurrentLinkedQueue<byte[]> m_BufferForOneFileSliceRead = new ConcurrentLinkedQueue<byte[]>();
    //只包含文件块内容，不包含协议头的缓冲区
    private static ConcurrentLinkedQueue<ReceiveFileSliceInfo> m_BufferForSeveralFileSlicesWrite = new ConcurrentLinkedQueue<ReceiveFileSliceInfo>();

    static
    {
        Exchange ExchB=new 	Exchange();
        m_BufferForSeveralFileSlicesRead = new byte[m_FileSliceSize * m_FileSliceCount];
        //增加协议框架
        m_ProtocolHead = new byte[16];
        m_WrapFileSliceProtocolLen = m_FileSliceSize + 16;//整个文件块协议包的大小
        byte[] LenBS =ExchB.InttoBytes(m_FileSliceSize);//只记录文件块的大小
        byte[] FileSliceType = ExchB.InttoBytes(10003);
        m_ProtocolHead[0] = 30;
        m_ProtocolHead[1] = 30;
        m_ProtocolHead[2] = 90;
        m_ProtocolHead[3] = 72;
        m_ProtocolHead[4] = 68;
        m_ProtocolHead[5] = 71;
        m_ProtocolHead[6] = 73;
        m_ProtocolHead[7] = 83;

        m_ProtocolHead[8] = LenBS[0];
        m_ProtocolHead[9] = LenBS[1];
        m_ProtocolHead[10] = LenBS[2];
        m_ProtocolHead[11] = LenBS[3];

        m_ProtocolHead[12] = FileSliceType[0];
        m_ProtocolHead[13] = FileSliceType[1];
        m_ProtocolHead[14] = FileSliceType[2];
        m_ProtocolHead[15] = FileSliceType[3];
        for (int i = 0; i < m_FileSliceCount; i++)
        {
            byte[] WrapFileSliceProtocol = new byte[m_WrapFileSliceProtocolLen];
            for (int ii = 0; ii < 16; ii++)
                WrapFileSliceProtocol[ii] = m_ProtocolHead[ii];
            m_BufferForOneFileSliceRead.offer(WrapFileSliceProtocol);
        }
        for (int i = 0; i < 2; i++)
            m_BufferForSeveralFileSlicesWrite.offer(new ReceiveFileSliceInfo(new byte[m_2MKSizeToWrite]));
    }
    //获取一个空闲的文件块缓冲区
    public static byte[] GetBufferForOneFileSliceRead()
    {
        if (m_BufferForOneFileSliceRead.size() == 0)
        {
            for (int i = 0; i < m_FileSliceCount; i++)
            {
                byte[] WrapFileSliceProtocol = new byte[m_WrapFileSliceProtocolLen];
                for (int ii = 0; ii < 16; ii++)
                    WrapFileSliceProtocol[ii] = m_ProtocolHead[ii];
                m_BufferForOneFileSliceRead.offer(WrapFileSliceProtocol);
            }

        }
        if (m_BufferForOneFileSliceRead.size() > 10)
        {
            for (int i = 0; i < m_FileSliceCount; i++)
                m_BufferForOneFileSliceRead.poll();
        }
        return m_BufferForOneFileSliceRead.poll();
    }
    public static void ReCycleBufferForOneFileSliceRead(byte[] BufferForOneFileSlice)
    {
        if (m_BufferForOneFileSliceRead.size() > 10)
        {
            for (int i = 0; i < m_FileSliceCount; i++)
                m_BufferForOneFileSliceRead.poll();
        }
        if (BufferForOneFileSlice.length == m_WrapFileSliceProtocolLen)
            m_BufferForOneFileSliceRead.offer(BufferForOneFileSlice);
    }

    public static ReceiveFileSliceInfo GetBufferForSeveralFileSlicesWrite()
    {
        if (m_BufferForOneFileSliceRead.size() == 0)
        {
            for (int i = 0; i < 2; i++)
                m_BufferForSeveralFileSlicesWrite.offer(new ReceiveFileSliceInfo(new byte[m_2MKSizeToWrite]));
        }
        if (m_BufferForSeveralFileSlicesWrite.size() > 5)
        {
            for (int i = 0; i < 2; i++)
                m_BufferForSeveralFileSlicesWrite.poll();
        }
        ReceiveFileSliceInfo RFSI = m_BufferForSeveralFileSlicesWrite.poll();
        RFSI.m_CurrentAllFileSliceLen = 0;
        RFSI.m_CurrentFileSliceCount = 0;
        RFSI.m_CurrentFileSliceLen = 0;
        return RFSI;
    }
    public static void ReCycleBufferForSeveralFileSlicesWrite(ReceiveFileSliceInfo RFSI)
    {
        m_BufferForSeveralFileSlicesWrite.offer(RFSI);
    }
}
