package com.zhd.TCPSocketClientBase;

public class Protocol {
	  public int ProtocolType;
      public byte[] ProtocolContent;
      public Protocol(int Type, byte[] Content){
          ProtocolType = Type;
          ProtocolContent = Content;
      }
}
