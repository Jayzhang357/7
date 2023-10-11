package com.zhd.TCPSocketClientBase;

/**
 * 项目层对象继承该事件，但是网络客户端不支持事件的广播机制
 *
 * @author Administrator
 *
 */
public interface TCPEventListener
{
    void ConnectSuccessful(NetClient client);
    void ConnectBreak(NetClient netClient);
    void Receive(byte[] Data, NetClient netClient, Object CustomerClient);
}
