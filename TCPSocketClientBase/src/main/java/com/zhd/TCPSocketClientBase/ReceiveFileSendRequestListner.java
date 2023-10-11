package com.zhd.TCPSocketClientBase;


public interface ReceiveFileSendRequestListner
{
    long ReceiveFileSendRequest(String FileName, NetClient netClient,Object CustomerClient, String FileID, long FileSize);
    
}
