package com.zhd.TCPSocketClientBase;

/**
 * 应用层客户端会继承该事件，但是网络客户端不支持事件的广播机制
 * @author Administrator
 *
 */
public interface ProtocolEventListener {
    void ReceiveProtocolData(Protocol P, NetClient netClient, Object CustomerClient);
    void SendFileSuccessful(String FilePath, NetClient netClient, Object CustomerClient, double TimeConsume);
    void RejectReceiveFile(String FilePath, NetClient netClient, Object CustomerClient);
    void ReceiveFileSuccessful(String FilePath, NetClient netClient, Object CustomerClient, double TimeConsume);
    void SendFileFailed(String FilePath, NetClient netClient, Object CustomerClient);
    void SendFileProgress(String FilePath, NetClient netClient,
                          Object CustomerClient, double Progress);
    void ReceiveFileProgress(String FilePath, NetClient netClient,
                             Object CustomerClient, double Progress);
}

