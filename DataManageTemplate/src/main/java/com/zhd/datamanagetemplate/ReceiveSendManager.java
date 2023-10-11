package com.zhd.datamanagetemplate;

/**
 * 该类抽象描述了“蓝牙”、“串口”、“网络”等各种通信介质的行为
 * @author Administrator
 *
 */
public class ReceiveSendManager {

	public ReceiveSendManager(){

	}

	public void Start(){

	}

	public void ReStart(){

	}

	public void Pause(){

	}

	public void Stop(){

	}
	public void Dispose(){

	}
	/**
	 *
	 * @param message 上层应用构造的需要发送的数据
	 * @return
	 */
	public boolean Send(byte[] message){

		return true;
	}

	/**
	 * 子类集成该接口，外界通过该接口主动获取数据
	 * @return 如果子类的该函数返回了null，那么认为已经到读取到数结尾了
	 */
	public byte[] FetchNext(){

		return null;
	}

	/**
	 * 通知外界是否已经读取到结尾了
	 * @return
	 */
	public boolean getIsEof(){
		return false;
	}
}
