
package com.zhd.ProtocolFilterManage;
import java.util.ArrayList;

/// <summary>
/// 消息的基类
/// </summary>
public class ProtocolMessage
{
    public ProtocolHead mHead;
    public byte[] mProtocolContent;

    /// <summary>
    /// 构造一个消息
    /// </summary>
    /// <param name="hd"></param>
    /// <param name="protocolContent"></param>
    public ProtocolMessage(ProtocolHead hd, byte[] protocolContent)
    {
        mHead = hd;
        mProtocolContent = protocolContent;
    }

    /// <summary>
    /// 序列化消息头，并将消息头和消息体组合起来
    /// </summary>
    /// <returns></returns>
    public ArrayList<Byte>  ToByte()
    {
        ArrayList<Byte> al = new ArrayList<Byte>();
        byte[] tmp =  mHead.ToByteCode();
        for(int i = 0 ;i<tmp.length;i++)
            al.add(tmp[i]);
        if (mProtocolContent != null)
        {
            for(int i = 0 ;i<mProtocolContent.length;i++)
                al.add(mProtocolContent[i]);
        }
        return al;
    }

}

