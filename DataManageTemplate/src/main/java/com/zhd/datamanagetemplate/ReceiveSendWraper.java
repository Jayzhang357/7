package com.zhd.datamanagetemplate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 该类是一个协调类，将“数据源”、“数据拼接”、“数据解析”、“数据处理”等元素连接起来
 *
 * @author Administrator
 *
 */
public class ReceiveSendWraper {

	private ReceiveSendManager mReceiveSendManager;// 外部数据源
	private VarMessageReceive mVarMessageReceive;// 内部使用的通用数据拼接类
	private Thread mReadThread;// 主动读取外部数据源数据
	private AtomicBoolean mIsPaused = new AtomicBoolean();// 是否暂停与数据源的交互
	private boolean flag = true;

	/**
	 *
	 * @param
	 *
	 * @param messageDescriptions
	 *            需要从数据流中提取的数据描述
	 * @throws Exception
	 */
	public ReceiveSendWraper(ReceiveSendManager receiveSendManager,
							 MessageDescription[] messageDescriptions) throws Exception {
		if (receiveSendManager == null || messageDescriptions == null)
			throw new Exception(
					"messageDescriptions or receiveSendManager must not be null");

		mIsPaused.set(false);
		mReceiveSendManager = receiveSendManager;
		mVarMessageReceive = new VarMessageReceive(messageDescriptions);

		// if (mReceiveSendManager.getIsEof() == false) {
		//
		// try {
		// mVarMessageReceive.ReceiveFragementData(mReceiveSendManager
		// .FetchNext());
		//
		// } catch (Exception ep) {
		//
		// String h = ep.getMessage();
		// String f = h;
		// }
		// }

		mReadThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (flag) {
					if (mIsPaused.get()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (mReceiveSendManager.getIsEof() == false) {

						try {
							mVarMessageReceive
									.ReceiveFragementData(mReceiveSendManager
											.FetchNext());

						} catch (Exception ep) {

							FileHelper.record(ep.toString());
						}
					}

				}
			}

		});
		mReadThread.start();
	}

	public void finalize() {
		mReadThread.interrupt();
	}

	public void Start() {
		mReceiveSendManager.Start();
	}

	public void ReStart() {
		mIsPaused.set(false);
		mReceiveSendManager.ReStart();
	}

	public void Pause() {
		mIsPaused.set(true);
		mReceiveSendManager.Pause();
	}

	public void Stop() {
		mReceiveSendManager.Stop();
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @param message
	 *            上层应用构造的需要发送的数据
	 * @return
	 */
	public boolean Send(byte[] message) {
		if (mIsPaused.get() == false)
			return mReceiveSendManager.Send(message);
		else
			return false;
	}
}
