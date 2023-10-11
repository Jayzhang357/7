package com.zhd.TCPSocketClientBase;

public class ReceiveFileSliceInfo {
     public int m_CurrentAllFileSliceLen = 0;
     public int m_CurrentFileSliceLen = 0;//当前接收的文件块的长度
     public int m_CurrentFileSliceCount = 0;
     public byte[] m_SeveralFileSlices;//一次性存储几个文件块的缓冲区，文件写入时，一次完成写入
     public ReceiveFileSliceInfo(byte[] SeveralFileSlice)
     {
          m_SeveralFileSlices = SeveralFileSlice;
     }
}
