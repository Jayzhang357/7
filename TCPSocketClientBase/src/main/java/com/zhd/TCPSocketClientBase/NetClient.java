package com.zhd.TCPSocketClientBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

import android.util.Log;

//1、线程创建和启动；2、集合的使用；3、线程池的使用；4、线程的同步；6、套接字操作；7、事件响应的修改；8、文件操作；9、高效的字节拷贝
//需要解决的线程同步：通知发送线程执行；发送线程内部从发送缓冲区中获取数据进行发送
//需要解决的问题：事件的广播处理；协议解析器逻辑调整；从字节流转类型
public class NetClient {

	// 处理网络连接成功线程池执行体
	private class TellConnectSuccessfulPoolWork implements Runnable {
		public TellConnectSuccessfulPoolWork(NetClient netClient,
											 TCPEventListener basicNEL) {
			this.netClient = netClient;
			this.basicNEL = basicNEL;
		}

		public void run() {
			// TODO Auto-generated method stub
			basicNEL.ConnectSuccessful(netClient);
		}

		private NetClient netClient;
		private TCPEventListener basicNEL;
	}

	private class TellConnectFailedPoolWork implements Runnable {
		public TellConnectFailedPoolWork(NetClient netClient,
										 TCPEventListener basicNEL) {
			this.netClient = netClient;
			this.basicNEL = basicNEL;
		}

		public void run() {
			// TODO Auto-generated method stub
			basicNEL.ConnectBreak(netClient);
		}

		private NetClient netClient;
		private TCPEventListener basicNEL;
	}

	private class TellReceiveFileProgressPoolWork implements Runnable {
		public TellReceiveFileProgressPoolWork(NetClient netClient,
											   String FilePath, double Progress, Object CustomerClient,
											   TCPEventListener basicNEL) {
			this.netClient = netClient;
			this.filePath = FilePath;
			this.progress = Progress;
			this.customerClient = CustomerClient;
			this.basicNEL = basicNEL;
		}

		public void run() {
			// TODO Auto-generated method stub
//			basicNEL.ReceiveFileProgress(filePath, netClient, customerClient,
//					progress);
		}

		private NetClient netClient;
		private Object customerClient;
		private String filePath;
		private double progress;
		private TCPEventListener basicNEL;
	}

	private class TellSendFileProgressPoolWork implements Runnable {
		public TellSendFileProgressPoolWork(NetClient netClient,
											String FilePath, double Progress, Object CustomerClient,
											TCPEventListener basicNEL) {
			this.netClient = netClient;
			this.filePath = FilePath;
			this.progress = Progress;
			this.customerClient = CustomerClient;
			this.basicNEL = basicNEL;
		}

		public void run() {
			// TODO Auto-generated method stub
//			basicNEL.SendFileProgress(filePath, netClient, customerClient,
//					progress);
		}

		private NetClient netClient;
		private Object customerClient;
		private String filePath;
		private double progress;
		private TCPEventListener basicNEL;
	}

	private class TellSendFileFinishPoolWork implements Runnable {
		public TellSendFileFinishPoolWork(NetClient netClient, String FilePath,
										  double TimeConsume, Object CustomerClient,
										  ProtocolEventListener networkCEL) {
			this.netClient = netClient;
			this.filePath = FilePath;
			this.timeConsume = TimeConsume;
			this.customerClient = CustomerClient;
			this.networkCEL = networkCEL;
		}

		public void run() {
			// TODO Auto-generated method stub
			networkCEL.SendFileSuccessful(filePath, netClient, customerClient,
					timeConsume);
		}

		private NetClient netClient;
		private Object customerClient;
		private String filePath;
		private double timeConsume;
		private ProtocolEventListener networkCEL;
	}

	private class TellResolveProtocolPoolWork implements Runnable {
		public TellResolveProtocolPoolWork(NetClient netClient,
										   Object CustomerClient, Protocol pol,
										   ProtocolEventListener networkCEL) {
			this.netClient = netClient;
			this.customerClient = CustomerClient;
			this.pol = pol;
			this.networkCEL = networkCEL;
		}

		public void run() {
			// TODO Auto-generated method stub

			networkCEL.ReceiveProtocolData(pol, netClient, customerClient);
		}

		private NetClient netClient;
		private Object customerClient;
		private Protocol pol;
		private ProtocolEventListener networkCEL;
	}

	private class ResolveFileDescriptionPoolWork implements Runnable {
		public ResolveFileDescriptionPoolWork(byte[] FileDescription) {
			this.FileDescription = FileDescription;
		}

		public void run() {
			// TODO Auto-generated method stub
			AnalyzeFileDescriptionInfo(FileDescription);
		}

		private byte[] FileDescription;
	}

	private class RejectReceiveFilePoolWork implements Runnable {
		public RejectReceiveFilePoolWork(NetClient netClient, String FilePath,
										 Object CustomerClient, ProtocolEventListener networkCEL) {
			this.netClient = netClient;
			this.filePath = FilePath;
			this.customerClient = CustomerClient;
			this.networkCEL = networkCEL;
		}

		public void run() {
			// TODO Auto-generated method stub
			networkCEL.RejectReceiveFile(filePath, netClient, customerClient);
		}

		private NetClient netClient;
		private Object customerClient;
		private String filePath;
		private ProtocolEventListener networkCEL;
	}

	private class TellReceiveFileSuccessfulPoolWork implements Runnable {
		public TellReceiveFileSuccessfulPoolWork(NetClient netClient,
												 String FilePath, double TimeConsume, Object CustomerClient,
												 ProtocolEventListener networkCEL) {
			this.netClient = netClient;
			this.filePath = FilePath;
			this.timeConsume = TimeConsume;
			this.customerClient = CustomerClient;
			this.networkCEL = networkCEL;
		}

		public void run() {
			// TODO Auto-generated method stub

			networkCEL.ReceiveFileSuccessful(filePath, netClient,
					customerClient, timeConsume);

		}

		private NetClient netClient;
		private Object customerClient;
		private String filePath;
		private double timeConsume;
		private ProtocolEventListener networkCEL;
	}

	private boolean m_IsApplyProtocolFrame;//是否启用协议框架层
	private Socket m_Sock;
	private OutputStream m_OStream;// 与套接字相对应的发送流
	private InputStream m_InStream;// 与套接字相对应的接收流
	private String m_IpStr = "";
	private int m_TcpPort = 0;
	private AtomicBoolean m_isHasconnected;
	private boolean m_isConnectReset = false;
	private boolean m_IsAlertConnectBreak = false;
	// 标记登录时间间隔，用于防止发送过快过多的连接请求，时间间隔控制在1秒内
	private boolean m_IsAllowAutomaticReConnnect = true;
	private final int m_TryConnectMaxSpan = 1000;
	private int m_TryConnectTimeSpan = m_TryConnectMaxSpan + 1;
	// 测试网络连接性有关
	private long m_SendHeartPeriod;// 30秒，该处数字以毫秒为单位
	private Date m_LastPollConnectTimePos = new Date();
	// private ConnectSuccessfulPoolWork m_ConnectSuccessfulPoolWork;
	private Date m_TimeSynchoronousWithServer = new Date();// 与服务器同步的时间
	private long m_LastLocalTimeTicks;

	// 当前正在接收的文件，包括路径金额文件名在内
	private String m_NowReceiveFile;
	private long m_ReceiveFileSize = 0;// 待接收文件的大小
	private long m_NowAccumulateFileSize = 0;// 已经接收到的文件大小
	private boolean m_IsNeedNextBufferForSeveralFileSlices = true;// 控制是否需要申请新的缓冲区
	private ReceiveFileSliceInfo m_CurrentReceiveFileSliceInfo;// 当前正在接收并承载文件块数据的缓冲区
	// 由于采用同步的方式来完成文件块内容写入，故不需要同步
	private Queue<ReceiveFileSliceInfo> m_ReceiveFileSlices = new LinkedList<ReceiveFileSliceInfo>();// 缓存的接收到的文件块，一次性写入，降低文件IO操作次数
	private Queue<ReceiveFileSliceInfo> m_FileSlicesWaitToWrite = new LinkedList<ReceiveFileSliceInfo>();// 数据写入线程使用的数据结构
	// private WaitPoolWork m_WriteFileSlicesPoolWork;//写入文件
	private boolean m_IsFinishCurrentWriteTask = true;// 当前写入线程通知是否完成写入

	private byte[] m_FileDescriptionOKFeedback;

	// 接收到文件后的存储路径
	private String m_SaveDirectory;
	private TellReceiveFileProgressPoolWork m_TellReceiveFileProgressPoolWork;
	private Date m_BeginToReceiveFileTimePos;// 开始接收文件的时间

	private byte[] m_Heart;
	private byte[] m_Disconnect;
	private byte[] m_FileReject;

	private Thread m_tdSend;
	private boolean m_istdSendRunning = true;

	// 协议发送线程与请求发送协议的线程之间没有比较强的控制关系，得使用互斥量来完成同步
	private ConcurrentLinkedQueue<byte[]> m_DatasToSend = new ConcurrentLinkedQueue<byte[]>();
	private ManualResetEvent m_AskToSend;

	// 由以下两个变量来控制当前要发送的文件内容
	private long m_NextBeginReadPos = 0;// 上回发送的开始位置
	private long m_FinishSendFileSliceSize = 0;// 已经发送的文件大小
	private long m_FileToSendSize = 0;// 当前要发送的文件的大小
	private String m_FileSending = "";// 当前正在被发送的文件的文件名
	private boolean m_IsSendingFile = false;// 是否正在发送文件
	private boolean m_IsPauseNowSendingFile = true;// 暂停当前正在发送的文件
	private TellSendFileFinishPoolWork m_SendFileFinishPoolWork;// 启用线程池线程通知上层文件发送完毕
	// 待发送的文件数据，一次性读取多个文件块;由于采用同步的方式，故不需要进行同步处理
	private Queue<byte[]> m_FileSlicesToSend = new LinkedList<byte[]>();// 发送线程中使用的文件块队列
	private Queue<byte[]> m_FileSliceWaitToSend = new LinkedList<byte[]>();// 文件块加载线程中使用的临时
	private int m_ThresholdToPreloadFileSlice = 2;// 触发下一次预加载的内存中剩余的文件块数
	private TellSendFileProgressPoolWork m_TellSendFileProgressPoolWork;
	private Date m_BeginToSendFileTimePos;// 开始发送文件的时间
	private Protocol m_SendFileRequestProtocol;// 当断开重新连接上时用到

	private boolean m_IsForceToSendNextFile = false;// 是否强制发送下一个文件
	// private ManualResetEvent m_AskToclearFileSlices = new
	// ManualResetEvent(true);//强制性删除是否完成
	private boolean m_IsLoadNextFileSliceFinish = true;// 加载下一个文件块是否完成

	private Thread m_tdReceive;
	private boolean m_istdReceiveRunning = true;
	// 使用较大的接收缓存，目的是加快网络数据的接收速度
	private byte[] m_ReceptBuffer = new byte[1024];// 因为服务器方发送文件块的大小是1M
	private int m_receivedLen = 0;// 标记从网络上读取的数据量
	private int m_ProtocolCorrectHeadBSIndex = 0;// 已经接收的头部长度
	private byte[] m_ProtocolLenBS = new byte[4];// 不完整的协议长度数组
	private int m_ProtocolCorrectLenBSIndex = 0;// 已经接收的长度字节数
	private int m_CurrentProtocolType;
	private byte[] m_ProtocolTypeBS = new byte[4];// 不完整的协议类型数组
	private int m_ProtocolCorrectTypeBSIndex = 0;// 已经接收的协议类型字节数
	private byte[] m_CurrentProtocolContent;// 当前正在接收协议内容字节，一般记录非文件块协议内容
	private int m_ProtocolCorrectContentIndex = 0;// 当前正在接收的内容索引

	private long m_LeagelLifeTimeNotReceiveAnyData = 1200000000;// 60秒,单位：100毫微秒：1秒
	// = 1千万毫微秒
	private long m_StartReceiveLegalDataTime = System.currentTimeMillis();// 标记接收到合法数据的时间
	private ArrayList<Protocol> m_liReceiveProtocol = new ArrayList<Protocol>(
			10);
	// private ResolveProtocolPoolWork m_ResolveProtocolPoolWork;
	// 线程池对象
	ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 100, 5,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	// 各种消息的开头字节数组
	private byte[] m_ZHDGISNetProtocolHead = new byte[] { 30, 30, 34, 57, 68,
			63, 66, 83 };
	// 网络通知协议事件
	private Vector<TCPEventListener> basicNELs;
	private Vector<ProtocolEventListener> networkCELs;
	private ReceiveFileSendRequestListner fileSRL;
	// 应用层创建的客户端代理对象
	private Object m_CustomerClient;
	// 网络底层生成的随机字节序列用于网络过程的加密和解密
	private byte[] m_EncryptSeed;

	//关于自动检测网络是否断开相关
	private long mLastCommunicateTimePos;
	private int mThreshholdKeepNoCommunicationTime;//毫秒为单位

	public String getIpStr() {
		return m_IpStr;
	}

	public int getTcpPort() {
		return m_TcpPort;
	}

	public boolean getIsHasConnected() {
		return m_isHasconnected.get();
	}

	public boolean getIsAllowAutomaticReConnnect() {
		return m_IsAllowAutomaticReConnnect;
	}

	public void setIsAllowAutomaticReConnnect(boolean b) {
		m_IsAllowAutomaticReConnnect = b;
	}

	public Date getTimeSynchoronousWithServer() {
		return m_TimeSynchoronousWithServer;
	}

	public byte[] getEncryptSeed() {
		return m_EncryptSeed;
	}

	/**
	 *
	 * @param ipStr 服务器IP
	 * @param tcpPort 服务器监听端口
	 * @param isApplyProtocolFrame 是否启用协议框架层
	 */
	public NetClient(String ipStr, int tcpPort,boolean isApplyProtocolFrame) {
		mThreshholdKeepNoCommunicationTime=120000;
		mLastCommunicateTimePos=new Date().getTime();
		m_IsApplyProtocolFrame=isApplyProtocolFrame;
		m_isHasconnected = new AtomicBoolean();
		m_isHasconnected.set(false);
		m_IpStr = ipStr;
		m_TcpPort = tcpPort;
		m_FileDescriptionOKFeedback = BuildResponseStream2(
				ZHDProtocolType.FileDescriptionOKFeedback.toInt(), new byte[8]);

		m_Heart = BuildResponseStream2(ZHDProtocolType.Heart.toInt(), null);
		m_Disconnect = BuildResponseStream2(ZHDProtocolType.Disconnect.toInt(),
				null);
		m_FileReject = BuildResponseStream2(ZHDProtocolType.FileReject.toInt(),
				null);
		m_SendHeartPeriod = 30 * 1000;
		m_LeagelLifeTimeNotReceiveAnyData = 200 * 1000;

		basicNELs = new Vector<TCPEventListener>();
		networkCELs = new Vector<ProtocolEventListener>();
		m_AskToSend = new ManualResetEvent();
	}

	public void Start() {
		// 只有在网络数据齐全的情况下才主动登录中心服务器
		if (m_IpStr != "" && (m_IpStr.length() != 0) && (m_TcpPort != 0)) {
			StartReceiveThread();
			StartSendThread();
		} else {

		}
	}

	public void Stop() {
		StopReceiveThread();
		StopSendThread();

		// 尽量回收申请的内存
		// 强制回收用于发送的内存
		while (m_FileSlicesToSend.size() > 0)
			LoadFileSliceBuffersManager
					.ReCycleBufferForOneFileSliceRead(m_FileSlicesToSend.poll());

		// 接收队列中有数据，但是由于没达到条件，文件写入过程没有自动启动，故强制进行文件写入
		if ((m_CurrentReceiveFileSliceInfo != null)
				&& m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen > 0
				&& m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen < LoadFileSliceBuffersManager.m_2MKSizeToWrite) {
			m_FileSlicesWaitToWrite.add(m_CurrentReceiveFileSliceInfo);
			WriteFileSliceToFile();
		}
		m_CurrentReceiveFileSliceInfo = null;// 该句代码的作用时，缓冲区管理对象能及时处理过多的空闲内存
		CloseSocket();
	}

	private void CloseSocket() {
		try {
			m_InStream.close();
		} catch (Exception err) {

		}

		try {
			m_OStream.close();
		} catch (Exception err) {

		}
		// 将网络关闭，套接字的阻塞读取操作会引发异常，从而才能关闭接收线程
		try {
			m_Sock.close();
			m_Sock = null;
		} catch (Exception err) {

		}
	}

	/**
	 * 当客户端对象启动后，建议不能再调用这些函数
	 *
	 * @param basicNEL
	 */
	public void AddBasicNetworkEventListener(TCPEventListener basicNEL) {
		if (basicNEL != null)
			this.basicNELs.add(basicNEL);
	}

	/**
	 * 当客户端对象启动后，建议不能再调用这些函数
	 *
	 * @param basicNEL
	 */
	public void RemoveBasicNetworkEventListener(
			TCPEventListener basicNEL) {
		this.basicNELs.remove(basicNEL);
	}

	/**
	 * 当客户端对象启动后，建议不能再调用这些函数
	 *
	 * @param networkCEL
	 */
	public void AddNetworkContentEventListener(
			ProtocolEventListener networkCEL) {
		if (networkCEL != null)
			this.networkCELs.add(networkCEL);
	}

	/**
	 * 当客户端对象启动后，建议不能再调用这些函数
	 *
	 * @param networkCEL
	 */
	public void RemoveNetworkContentEventListener(
			ProtocolEventListener networkCEL) {
		this.networkCELs.remove(networkCEL);
	}

	public void AddReceiveFileSendRequestListener(
			ReceiveFileSendRequestListner SRL) {
		fileSRL = SRL;
	}

	public void RemoveReceiveFileSendRequestListener() {
		fileSRL = null;
	}

	/**
	 * 应用层代码使用该函数将自定义的客户端对象与网络客户端对象关联起来
	 *
	 * @param CustomerClient
	 */
	public void SetCustomerClient(Object CustomerClient) {
		m_CustomerClient = CustomerClient;
	}

	/**
	 * 当接收到文件发送请求后，给网络底层提供文件的存储路径,在文件发送请求处理时间函数中调用该函数
	 *
	 * @param saveDirectory
	 * @param saveName
	 * @return
	 */
	public boolean SetFileSavePath(String saveDirectory, String saveName) {
		File Directory = new File(saveDirectory);
		if (Directory.exists() == false) {
			if (Directory.mkdirs() == false)
				return false;
		}
		m_SaveDirectory = saveDirectory;
		m_NowReceiveFile = m_SaveDirectory + "/" + saveName;
		return true;
	}

	/**
	 * 直接发送外界序列化好的数据
	 * @param data
	 * @return
	 */
	public boolean AddDataToSend(byte[] data){
		if (m_isHasconnected.get() == false || data == null)
			return false;
		if(m_DatasToSend.add(data)){
			m_AskToSend.set();
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @param pl
	 * @return
	 */
	public boolean AddProtocolToSend(Protocol pl) {
		if (m_isHasconnected.get() == false || pl == null)
			return false;
		boolean b = m_DatasToSend.add(BuildResponseStream2(pl.ProtocolType,
				pl.ProtocolContent));
		if (b == true) {
			m_AskToSend.set();
		}
		return b;

	}

	/**
	 * 支持批量协议数据的上传
	 *
	 * @param pls
	 * @return
	 */
	public boolean AddProtocolToSend(Protocol[] pls) {
		if (m_isHasconnected.get() == false || pls == null || pls.length > 1000)
			return false;
		for (Protocol pl : pls)
			m_DatasToSend.add(BuildResponseStream2(pl.ProtocolType,
					pl.ProtocolContent));
		m_AskToSend.set();
		return true;
	}

	/**
	 * 底层网络部分一次只支持发送一个文件
	 *
	 * @param file
	 * @param FileID
	 * @param isForceStopSending
	 * @return
	 */
	public boolean AddFileToSend(String file, String FileID,
								 boolean isForceStopSending) {
		File sfile = new File(file);
		if (sfile.exists() == false)
			return false;
		if ((m_IsSendingFile == true) && (isForceStopSending == false))
			return false;
		m_IsForceToSendNextFile = isForceStopSending;
		m_BeginToSendFileTimePos = new Date();
		// 直接发送文件发送请求
		Exchange ExchB = new Exchange();
		// 构造文件描述数据
		// 文件唯一标识
		ExchB.AddStringAsBytes(FileID);
		// 文件名
		ExchB.AddStringAsBytes(sfile.getName());
		// 文件长度
		ExchB.AddLongAsBytes(sfile.length());
		// 是否强制性发送文件，从而控制对方的接收过程
		ExchB.AddBooleanAsBytes(m_IsForceToSendNextFile);

		m_FileToSendSize = sfile.length();
		m_FileSending = file;

		Protocol p = new Protocol(ZHDProtocolType.FileDescription.toInt(),
				ExchB.GetAllBytes());
		// 文件发送请求通过协议发送渠道进行发送，不走文件发送缓冲区
		boolean isAddSucceful = AddProtocolToSend(p);
		if (isAddSucceful)
			m_SendFileRequestProtocol = p;
		return isAddSucceful;
	}

	public void CancelSendingFile() {
		// 等待清空发送缓冲区数据
		m_IsForceToSendNextFile = true;
		InitToSendNextFile();
	}

	// 暂停当前文件的发送
	public void PauseSendingFile() {
		m_IsPauseNowSendingFile = true;
	}

	// 重新发送当前文件
	public void RestartSendFile() {
		m_IsPauseNowSendingFile = false;

	}

	/**
	 * 该函数即可以被同步调用也可以被异步调用，为了解决异步调用的状态通知，内部采用“事件同步机制” 通知异步过程文件块加载完毕
	 */
	private void LoadNextFileSlice() {
		if (m_NextBeginReadPos >= m_FileToSendSize) {
			m_IsLoadNextFileSliceFinish = true;
			return;
		}
		// m_AskLoadFileSliceFinish.Reset();
		FileInputStream fs = null;
		BufferedInputStream br = null;
		try {
			// 以共享读的方式打开文件进行读取，这样就可避免群发时的文件访问冲突
			fs = new FileInputStream(m_FileSending);
			br = new BufferedInputStream(fs);
			fs.skip(m_NextBeginReadPos);
			long remainLen = new File(m_FileSending).length()
					- m_NextBeginReadPos; // 剩余的文件长度
			int remainSliceCount = 0;// 仍然剩余的块数
			int readAmountOneTime = 0;// 一次性读取的长度
			while (remainLen >= LoadFileSliceBuffersManager.m_FileSliceSize) {
				remainSliceCount++;
				readAmountOneTime += LoadFileSliceBuffersManager.m_FileSliceSize;
				remainLen -= LoadFileSliceBuffersManager.m_FileSliceSize;
				if (remainSliceCount == LoadFileSliceBuffersManager.m_FileSliceCount)// 一次性读取7块文件块
					break;
			}
			// 一次性读取多个文件块
			if (remainSliceCount > 0) {
				byte[] FileSlices = LoadFileSliceBuffersManager.m_BufferForSeveralFileSlicesRead;
				br.read(FileSlices, 0, readAmountOneTime);

				int StartCopyPos = 0;
				// 分块封装协议数据
				while (remainSliceCount > 0) {
					byte[] FileSlice = LoadFileSliceBuffersManager
							.GetBufferForOneFileSliceRead();
					System.arraycopy(FileSlices, StartCopyPos, FileSlice, 16,
							LoadFileSliceBuffersManager.m_FileSliceSize);

					m_FileSliceWaitToSend.add(FileSlice);
					StartCopyPos += LoadFileSliceBuffersManager.m_FileSliceSize;
					remainSliceCount--;
				}
				m_NextBeginReadPos += readAmountOneTime;
			}
			if ((remainLen > 0)
					&& (remainLen < LoadFileSliceBuffersManager.m_FileSliceSize)) {
				byte[] FileSlice = new byte[(int) remainLen];
				br.read(FileSlice, 0, FileSlice.length);

				m_FileSliceWaitToSend.add(BuildResponseStream2(
						ZHDProtocolType.FileSlice.toInt(), FileSlice));
				m_NextBeginReadPos += remainLen;
			}
			br.close();
			fs.close();
			br = null;
			fs = null;
			// 假如强制性发送下一个文件，将当前缓冲区内容清空掉
			if (m_IsForceToSendNextFile == true) {
				while (m_FileSlicesToSend.size() > 0)
					LoadFileSliceBuffersManager
							.ReCycleBufferForOneFileSliceRead(m_FileSlicesToSend
									.poll());
				while (m_FileSliceWaitToSend.size() > 0)
					LoadFileSliceBuffersManager
							.ReCycleBufferForOneFileSliceRead(m_FileSliceWaitToSend
									.poll());
				// 通知进行下一个文件发送准备
				// m_AskToclearFileSlices.Set();
			}
		} catch (Exception err) {
			// ErrorLog.Record("LoadNextFileSlice" + err.getMessage());
			if (fs != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br = null;
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fs = null;
			}

			// m_ProtocolEvent.DoSendFileFailed(m_FileSending, this,
			// m_CustomerClient);
		}
		// 通知发送线程开始运行
		m_IsLoadNextFileSliceFinish = true;
		// m_AskLoadFileSliceFinish.Set();
	}

	private void InitToSendNextFile() {
		m_FileToSendSize = 0;
		m_NextBeginReadPos = 0;
		m_IsPauseNowSendingFile = true;
		m_IsLoadNextFileSliceFinish = true;
		m_FileSending = "";
		m_IsSendingFile = false;
		m_SendFileRequestProtocol = null;
	}

	// 私有方法
	private boolean SendData(byte[] msg) {
		try {
			// Date now = new Date();
			// android.util.Log.i("数据发送", now.toString());
			m_OStream.write(msg);
			m_OStream.flush();
			//更新数据交互时间点
			mLastCommunicateTimePos=new Date().getTime();

			return true;
		} catch (Exception er) {
			m_isHasconnected.set(false);
		}
		return false;
	}

	// 启动主动登录到网络的交互线程
	private void StartReceiveThread() {
		try {
			m_tdReceive = new Thread(new Runnable() {
				// InetAddress sAddress=InetAddress.getByAddress(ipAddress);
				public void run() {
					StartReceive();
				}
				// Console.WriteLine("数据接收线程结束");
			});
			m_tdReceive.start();
		} catch (Exception er) {

		}
	}

	private void StartReceive() {

		m_istdReceiveRunning = true;
		while (m_istdReceiveRunning) {
			try {
				// 检查网络连接状态，如果掉线重新连接
				if (m_isHasconnected.get() == false) {
					ConnectServer();
				} else {
					m_receivedLen = 0;
					// 采用阻塞方式从网络获取数
					try {
						m_receivedLen = m_InStream.read(m_ReceptBuffer);
						Log.v("网络", m_receivedLen+"");
						//更新交互时间点
						//	m_receivedLen=0;
						mLastCommunicateTimePos=new Date().getTime();
					} catch (Exception err) {
						// 接收数据的过程发生网络错误，那么停止当前线程
						m_isHasconnected.set(false);
						continue;
					}
					// 处理接收的数据
					if (m_receivedLen > 0) {
						// 捕捉数据解析过程中出现的错误
						try {
							if (m_IsApplyProtocolFrame) {
								// 进行数据解析
								ReceivedData();
							} else {
								//将接收缓冲区中的数据拷贝出来
								byte[] data=new byte[m_receivedLen];
								System.arraycopy(m_ReceptBuffer, 0, data, 0, m_receivedLen);
								//通知外界接收到数据
								for (TCPEventListener bel : basicNELs) {


									bel.Receive(data, this, m_CustomerClient);
								}
							}
							// Console.WriteLine("数据处理过程结束");
						} catch (Exception err) {
							Log.v("", err+"");
						}
					} else if (m_receivedLen == -1) {
						// 接收数据的过程发生网络错误，那么停止当前线程
						m_isHasconnected.set(false);
						continue;
					}
				}
			} catch (Exception err) {

			}
		}
	}

	/**
	 * 该函数处理无效套接字的释放、重新连接、以及
	 */
	private void ConnectServer() {
		try {
			// 先尝试释放无效套接字资源
			CloseSocket();
			if (m_IsAllowAutomaticReConnnect == true) {
				m_Sock = new Socket(m_IpStr, m_TcpPort);
				m_Sock.setKeepAlive(true);
				m_Sock.setSoTimeout(10* 1000);
				m_Sock.setTcpNoDelay(true);
				m_InStream = m_Sock.getInputStream();
				m_OStream = m_Sock.getOutputStream();
				// 集中回复各种状态
				mLastCommunicateTimePos=new Date().getTime();
				m_isHasconnected.set(true);
				m_IsAlertConnectBreak = false;
				m_TryConnectTimeSpan = m_TryConnectMaxSpan;
				m_ProtocolCorrectHeadBSIndex = 0;
				m_ProtocolCorrectLenBSIndex = 0;
				m_ProtocolCorrectTypeBSIndex = 0;
				m_ProtocolCorrectContentIndex = 0;
				m_liReceiveProtocol.clear();
				// 将文件发送缓冲区中域加载的文件块清空，回复各种与文件发送相关的变量
				m_IsForceToSendNextFile = true;
				m_FileSliceWaitToSend.clear();
				m_FileSlicesToSend.clear();
				m_ReceiveFileSize = 0;
				m_NowAccumulateFileSize = 0;
				m_NowReceiveFile = "";
				m_CurrentReceiveFileSliceInfo = null;
				m_IsFinishCurrentWriteTask = true;
				m_SaveDirectory = "";
				m_DatasToSend.clear();
				m_NextBeginReadPos = 0;
				m_FinishSendFileSliceSize = 0;
				m_IsPauseNowSendingFile = true;
				m_IsLoadNextFileSliceFinish = true;

				// 重新统计时间
				m_StartReceiveLegalDataTime = System.currentTimeMillis();
				m_LastPollConnectTimePos = new Date();

				// 假如网络断开时，仍然有文件未发送成功，那么重新发送
				if (m_SendFileRequestProtocol != null)
					AddProtocolToSend(m_SendFileRequestProtocol);

				try {
					//启用线程池线程， 通知外部网络连接成功
					for (TCPEventListener bel : basicNELs) {
						//		threadPool.execute(new TellConnectSuccessfulPoolWork(this, bel));
					}

				} catch (Exception er) {
				}
			}
		} catch (Exception ex) {
			// 处理网络连接失败的情况，通过控制m_TryConnectTimeSpan值来触发重新连接
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 提示外界网络断开
			if (m_IsAlertConnectBreak == false) {
				m_IsAlertConnectBreak = true;
				try {
					// 在线程池线程中串行触发外部事件
					for (TCPEventListener bel : basicNELs) {
						bel.ConnectBreak(this);
					}

				} catch (Exception er) {// 由于线程池没有足够的线程来执行
				}
			}
		}
	}

	private void StopReceiveThread() {
		m_istdReceiveRunning = false;
		m_tdReceive.interrupt();
	}

	private void StartSendThread() {
		try {
			m_tdSend = new Thread(new Runnable() {
				public void run() {
					StartSend();
				}
			});
			m_tdSend.start();
		} catch (Exception er) {

		}
	}

	private void SendFileSlices() {
		// 文件发送过程与文件读取过程串行执行
		boolean IsNeedSkipSendFile = false;
		// 上层暂停了当前文件的发送
		if (m_IsPauseNowSendingFile == true) {
			// break SendConnectHeart; java不支持break语句
			IsNeedSkipSendFile = true;
		}
		// 假如强制性发送下一个文件，将当前缓冲区内容清空掉
		if (m_IsForceToSendNextFile == true) {
			while (m_FileSlicesToSend.size() > 0)
				LoadFileSliceBuffersManager
						.ReCycleBufferForOneFileSliceRead(m_FileSlicesToSend
								.poll());
			while (m_FileSliceWaitToSend.size() > 0)
				LoadFileSliceBuffersManager
						.ReCycleBufferForOneFileSliceRead(m_FileSliceWaitToSend
								.poll());

			IsNeedSkipSendFile = true;
		}

		if (IsNeedSkipSendFile == false)
			try {
				if (m_FileSlicesToSend.size() == 0) {
					if ((m_FileToSendSize > 0) && (m_NextBeginReadPos > 0)
							&& (m_NextBeginReadPos == m_FileToSendSize)) {

						String FinishFile = m_FileSending;
						InitToSendNextFile();// 一定得先初始化，再触发下一个文件的发送
						// 得启用线程池线程通知外界文件发送完成
						try {
							for (ProtocolEventListener nel : networkCELs) {
								TellSendFileFinishPoolWork TSFFP = new TellSendFileFinishPoolWork(
										this,
										FinishFile,
										(System.currentTimeMillis() - m_BeginToSendFileTimePos
												.getTime()) / 1000,
										m_CustomerClient, nel);

								threadPool.execute(TSFFP);
							}
						} catch (Exception er) {
						}
						// 如果没有协议包要发送，让发送线程进入睡眠状态
						if (m_NextBeginReadPos < m_FileToSendSize) {
							// 直接触发读取文件块
							LoadNextFileSlice();
							while (m_FileSliceWaitToSend.size() > 0)
								m_FileSlicesToSend.add(m_FileSliceWaitToSend
										.poll());
						}
					}
				} else {
					// 发送文件块
					byte[] FileSlice = m_FileSlicesToSend.poll();

					SendData(FileSlice);

					// 将使用完的内存还回去
					LoadFileSliceBuffersManager
							.ReCycleBufferForOneFileSliceRead(FileSlice);
					// 计算发送的进度
					m_FinishSendFileSliceSize += FileSlice.length - 16;
					try {
						for (TCPEventListener bel : basicNELs) {
							TellSendFileProgressPoolWork TSPP = new TellSendFileProgressPoolWork(
									this, m_FileSending,
									(double) m_FinishSendFileSliceSize
											/ (double) m_FileToSendSize,
									m_CustomerClient, bel);

							threadPool.execute(TSPP);

						}

					} catch (Exception er) {
					}

				}

			} catch (Exception err) {
				// ErrorLog.Record("文件发送过程出错!" + err.getMessage());
			}
	}

	private void SendDatas() {
		try {
			if (m_DatasToSend.size() > 1) {
				Exchange ExchB = new Exchange();
				int AggregateCount = 0;
				while (m_DatasToSend.size() > 0) {
					byte[] bs = m_DatasToSend.poll();
					if (bs != null) {
						ExchB.AddBytes(bs, false);
						AggregateCount++;
						if (AggregateCount > 10)
							break;
					}
				}
				SendData(ExchB.GetAllBytes());

			} else if (m_DatasToSend.size() == 1) {
				byte[] bs = m_DatasToSend.poll();
				if (bs != null) {
					SendData(bs);
				}
			}

		} catch (Exception er) {
		}
	}

	private void StartSend() {
		m_istdSendRunning = true;
		while (m_istdSendRunning) {
			try {
				if (m_IsApplyProtocolFrame) {
					if ((m_FileSlicesToSend.size() == 0 &&m_DatasToSend.size() == 0)) {
						m_AskToSend.wait(100, TimeUnit.MILLISECONDS);

						//如果网络没有数据交互的时间超过指定的阈值，那么认为断掉了
						if((new Date().getTime()-mLastCommunicateTimePos)>mThreshholdKeepNoCommunicationTime){
							m_isHasconnected.set(false);
						}

						if(m_isHasconnected.get() == false){
							ConnectServer();
						}
						continue;
					}
					SendDatas();
					// 发送文件块
					SendFileSlices();
				} else {
					if ( m_DatasToSend.size() == 0 || m_isHasconnected.get() == false) {
						m_AskToSend.wait(100, TimeUnit.MILLISECONDS);
						continue;
					}
					SendDatas();

				}

			} catch (Exception err) {
				// ErrorLog.Record("数据发送过程出错！" + err.getMessage());
				err.printStackTrace();
			}

		}
		System.out.println("Exit StartSend!");
	}

	private void StopSendThread() {
		m_istdSendRunning = false;
		m_tdSend.interrupt();
	}

	// 与数据解析有关的函数

	/**
	 * 该函数运行的环境是多线程的，但是该函数不会同时在多个线程中被执行
	 */
	private void ReceivedData() {
		// 在缓冲区内寻找符合条件的信息块，并存到对象m_liProtocol里
		FindProtocolType(0);

		// 提取协议数据后，启用线程池线程来完成协议数据分析及处理
		for (int i = 0; i < m_liReceiveProtocol.size(); i++) {
			Protocol p = m_liReceiveProtocol.get(i);
			ZHDProtocolType zhdtype = ZHDProtocolType.fromInt(p.ProtocolType);
			// 非中海达内置协议
			if (zhdtype == null) {
				try {
					// 在线程池线程中串行触发外部事件
					for (ProtocolEventListener nel : networkCELs) {
						// TellResolveProtocolPoolWork RPP = new
						// TellResolveProtocolPoolWork(
						// this, m_CustomerClient, p, nel);
						// threadPool.execute(RPP);
						nel.ReceiveProtocolData(p, this, m_CustomerClient);
					}
				} catch (Exception er) {// 由于线程池没有足够的线程来执行
				}
			} else {
				// 非枚举值，好像运行行为不对
				switch (zhdtype) {
					case FileDescription:// 需要提交线程池
						try {
							ResolveFileDescriptionPoolWork RFDP = new ResolveFileDescriptionPoolWork(
									p.ProtocolContent);
							threadPool.execute(RFDP);
						} catch (Exception er) {// 由于线程池没有足够的线程来执行
						}
						break;
					case FileDescriptionOKFeedback:// 不需要提交线程池
						AnalyzeReceiveFileDescriptionFeedback(p.ProtocolContent);
						break;
					case FileReject:// 需要提交线程池
						try {
							for (ProtocolEventListener nel : networkCELs) {
								RejectReceiveFilePoolWork RRFP = new RejectReceiveFilePoolWork(
										this, m_FileSending, m_CustomerClient, nel);
								threadPool.execute(RRFP);
							}

						} catch (Exception er) {// 由于线程池没有足够的线程来执行
						}
						break;
					case Disconnect:// 需要提交线程池
						try {
							// 在线程池线程中串行触发外部事件
							for (TCPEventListener bel : basicNELs) {
								TellConnectFailedPoolWork CFP = new TellConnectFailedPoolWork(
										this, bel);
								threadPool.execute(CFP);
							}
						} catch (Exception er) {// 由于线程池没有足够的线程来执行
						}
						break;
					case ServerTime:// 不需要提交线程池
						Exchange ExchB = new Exchange();
						long DotNetLongTime;
						try {
							DotNetLongTime = ExchB
									.BytestoLong(p.ProtocolContent, 0);
							m_TimeSynchoronousWithServer = TimeExchangeBetweenJavaAndDotNet
									.convertDotNetLongTimeToJavaTime(DotNetLongTime);
							m_LastLocalTimeTicks = new Date().getTime();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						break;
					case EncryptSeed:// 不需要提交线程池
						m_EncryptSeed = p.ProtocolContent;
						break;
					default:
						break;
				}
			}
		}
		// 清空协议缓存
		m_liReceiveProtocol.clear();
	}

	/**
	 * 网络过程与文件写入过程串行执行
	 */
	private void FileSliceBufferAndWriteManage() {
		m_FileSlicesWaitToWrite.add(m_CurrentReceiveFileSliceInfo);
		WriteFileSliceToFile();
		m_IsNeedNextBufferForSeveralFileSlices = true;
	}

	/**
	 * 该函数即可以被同步调用也可以被异步调用
	 */
	private void WriteFileSliceToFile() {
		FileOutputStream fs = null;
		BufferedOutputStream bw = null;
		try {
			if (m_NowReceiveFile == "")
				return;
			long NowFileSize = new File(m_NowReceiveFile).length();
			fs = new FileOutputStream(m_NowReceiveFile, true);
			bw = new BufferedOutputStream(fs);
			if (m_ReceiveFileSize == NowFileSize)// 其实文件已经接收完成，由于不可控的因素导致仍然接收到数据
			{
				// 强制回收相关内存
				while (m_ReceiveFileSlices.size() > 0)
					LoadFileSliceBuffersManager
							.ReCycleBufferForSeveralFileSlicesWrite(m_ReceiveFileSlices
									.poll());
				bw.close();
				bw = null;
				fs.close();
				fs = null;
				return;
			}

			while (m_FileSlicesWaitToWrite.size() > 0) {
				ReceiveFileSliceInfo RFSI = (ReceiveFileSliceInfo) m_FileSlicesWaitToWrite
						.poll();
				// 调整为每回只写4K的文件内容，防止一次写入文件内容太多导致，文件写入函数崩溃
				int writeIndex = 0;// 开始写入数据的索引
				while (writeIndex < RFSI.m_CurrentAllFileSliceLen) {
					// 剩余的未写入的内容
					int remainLen = RFSI.m_CurrentAllFileSliceLen - writeIndex;
					if (remainLen >= 4096) {
						bw.write(RFSI.m_SeveralFileSlices, writeIndex, 4096);
						writeIndex += 4096;
					} else {
						bw.write(RFSI.m_SeveralFileSlices, writeIndex,
								remainLen);
						writeIndex += remainLen;
					}
					bw.flush();
				}
				// 累加写入的文件的长度
				NowFileSize += RFSI.m_CurrentAllFileSliceLen;
				// 回收内存
				LoadFileSliceBuffersManager
						.ReCycleBufferForSeveralFileSlicesWrite(RFSI);
			}

			// 通知文件接收进度
			try {
				for (TCPEventListener bel : basicNELs) {
					TellReceiveFileProgressPoolWork TRFPP = new TellReceiveFileProgressPoolWork(
							this, m_NowReceiveFile, (double) NowFileSize
							/ (double) m_ReceiveFileSize,
							m_CustomerClient, bel);

					threadPool.execute(TRFPP);
				}
			} catch (Exception er) {
			}

			if (m_ReceiveFileSize == NowFileSize) {
				bw.close();
				bw = null;
				fs.close();
				fs = null;

				// 通知外界接收文件成功
				try {
					for (ProtocolEventListener nel : networkCELs) {
						TellReceiveFileSuccessfulPoolWork TSFSP = new TellReceiveFileSuccessfulPoolWork(
								this,
								m_NowReceiveFile,
								(double) (System.currentTimeMillis() - m_BeginToReceiveFileTimePos
										.getTime()) / 1000, m_CustomerClient,
								nel);
						threadPool.execute(TSFSP);
					}
				} catch (Exception er) {
				}

			} else {
				bw.close();
				bw = null;
				fs.close();
				fs = null;
			}
		} catch (Exception err) {
			// ErrorLog.Record("文件写入失败!" + err.getMessage());
			if (fs != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bw = null;
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fs = null;
			}
			// 回收内存
			while (m_FileSlicesWaitToWrite.size() > 0)
				LoadFileSliceBuffersManager
						.ReCycleBufferForSeveralFileSlicesWrite(m_FileSlicesWaitToWrite
								.poll());
		}
	}

	/**
	 * 协议框架数据提取过程；该函数是递归函数
	 *
	 * @param
	 */
	private void FindProtocolType(int CheckIndex) {
		// 处理协议框架头的匹配过程
		for (; (m_ProtocolCorrectHeadBSIndex < m_ZHDGISNetProtocolHead.length)
				&& (CheckIndex < m_receivedLen); CheckIndex++) {
			if (m_ZHDGISNetProtocolHead[m_ProtocolCorrectHeadBSIndex] != m_ReceptBuffer[CheckIndex]) {
				// 如果不匹配，从CheckIndex指定的下个位置开始从头匹配
				m_ProtocolCorrectHeadBSIndex = 0;
			} else {
				// 如果匹配了某个字节
				m_ProtocolCorrectHeadBSIndex++;
			}
		}

		// 处理拷贝协议内容长度的过程
		// 直接进行字节拷贝
		for (; (m_ProtocolCorrectLenBSIndex < m_ProtocolLenBS.length)
				&& (CheckIndex < m_receivedLen); m_ProtocolCorrectLenBSIndex++, CheckIndex++)
			m_ProtocolLenBS[m_ProtocolCorrectLenBSIndex] = m_ReceptBuffer[CheckIndex];

		// 处理获取协议类型的过程，从而决定是否使用预先申请号的内存来装载协议数据
		// 直接进行字节拷贝
		for (; (m_ProtocolCorrectTypeBSIndex < m_ProtocolTypeBS.length)
				&& (CheckIndex < m_receivedLen); m_ProtocolCorrectTypeBSIndex++, CheckIndex++)
			m_ProtocolTypeBS[m_ProtocolCorrectTypeBSIndex] = m_ReceptBuffer[CheckIndex];
		// 协议类型字节已经接收完
		if (m_ProtocolTypeBS.length == m_ProtocolCorrectTypeBSIndex) {
			// 开始接收协议内容
			if (m_ProtocolCorrectContentIndex == 0) {
				Exchange ExchB = new Exchange();
				// 获取协议类型
				try {
					m_CurrentProtocolType = ExchB.BytestoInt(m_ProtocolTypeBS,
							0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					PrepareForNextAnalyze();
					// 进入下一回解析
					if (CheckIndex < m_receivedLen) {
						FindProtocolType(CheckIndex);

					}
					return;
				}
				if (m_CurrentProtocolType < 0)// 标识没有小于0的
				{
					PrepareForNextAnalyze();
					// break HeadAnalyze;
					if (CheckIndex < m_receivedLen) {
						FindProtocolType(CheckIndex);

					}
					return;
				}

				// 计算协议内容长度
				if (m_ProtocolLenBS.length == m_ProtocolCorrectLenBSIndex) {
					// 获取协议长度
					int protocolContentLen = 0;
					try {
						protocolContentLen = ExchB.BytestoInt(m_ProtocolLenBS,
								0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						PrepareForNextAnalyze();
						// 进入下一回解析
						if (CheckIndex < m_receivedLen) {
							FindProtocolType(CheckIndex);

						}
						return;
					}
					if (protocolContentLen < 0)// 长度出现异常
					{
						PrepareForNextAnalyze();
						// break HeadAnalyze;
						if (CheckIndex < m_receivedLen) {
							FindProtocolType(CheckIndex);

						}
						return;
					} else
					// 如果协议内容长度和协议类型没有问题，那么开始读取协议内容
					{
						if (protocolContentLen > 62914560) {
							PrepareForNextAnalyze();
							// break HeadAnalyze;
							if (CheckIndex < m_receivedLen) {
								FindProtocolType(CheckIndex);

							}
							return;
						}

						// 记录接收到正确数据的时间
						m_StartReceiveLegalDataTime = System
								.currentTimeMillis();
						// 根据协议类型来创建或申请协议内容缓冲区
						if (m_CurrentProtocolType == 10003) {
							if (m_IsNeedNextBufferForSeveralFileSlices == true) {
								m_IsNeedNextBufferForSeveralFileSlices = false;
								// 申请一块一次性可以存储几个文件块内容的缓冲区
								m_CurrentReceiveFileSliceInfo = LoadFileSliceBuffersManager
										.GetBufferForSeveralFileSlicesWrite();
							}
							// 设定当前块的长度
							m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen = protocolContentLen;

							// 该处的拷贝可以使用内存拷贝函数来完成
							int remainLen = m_receivedLen - CheckIndex;
							if (remainLen > 0) {
								if (remainLen < m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen) {
									System.arraycopy(
											m_ReceptBuffer,
											CheckIndex,
											m_CurrentReceiveFileSliceInfo.m_SeveralFileSlices,
											m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen,
											remainLen);
									m_ProtocolCorrectContentIndex += remainLen;
									CheckIndex += remainLen;//
									// 记录文件内容的累加长度
									m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen += remainLen;
									m_NowAccumulateFileSize += remainLen;
								} else
								// 一次性获取了足够的文件块长度
								{
									System.arraycopy(
											m_ReceptBuffer,
											CheckIndex,
											m_CurrentReceiveFileSliceInfo.m_SeveralFileSlices,
											m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen,
											m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen);
									CheckIndex += m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen;
									// 完成一块接收后，记录文件内容的累加长度
									m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen += m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen;
									m_NowAccumulateFileSize += m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen;
									m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceCount++;// 统计文件块数
									// 如果缓存的文件量足够
									FileSliceBufferAndWriteManage();

									// 开始下一个协议包的接收
									PrepareForNextAnalyze();
									// break HeadAnalyze;
									if (CheckIndex < m_receivedLen) {
										FindProtocolType(CheckIndex);
									}
								}
							}
						} else
						// 非文件块协议，一般数量比较小
						{
							m_CurrentProtocolContent = new byte[protocolContentLen];
							for (; (CheckIndex < m_receivedLen)
									&& (m_ProtocolCorrectContentIndex < protocolContentLen); CheckIndex++, m_ProtocolCorrectContentIndex++)
								m_CurrentProtocolContent[m_ProtocolCorrectContentIndex] = m_ReceptBuffer[CheckIndex];
							// 一次性读取了足够的协议内容
							if (m_ProtocolCorrectContentIndex == protocolContentLen) {
								if (m_CurrentProtocolType != 10000)
									m_liReceiveProtocol.add(new Protocol(
											m_CurrentProtocolType,
											m_CurrentProtocolContent));
								// 开始下一个协议包的接收
								PrepareForNextAnalyze();
								// break HeadAnalyze;
								if (CheckIndex < m_receivedLen) {
									FindProtocolType(CheckIndex);
								}
							}
						}
					}
				}
			} else
			// 协议内容没有接收完
			{
				// 根据协议类型来创建或申请协议内容缓冲区
				if (m_CurrentProtocolType == 10003) {
					// 计算当前待接收的文件块还有多少数据未接收完成
					int remainLen = m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceLen
							- m_ProtocolCorrectContentIndex;
					if (remainLen <= m_receivedLen) {
						// 可以获取完整的文件块内容
						System.arraycopy(
								m_ReceptBuffer,
								CheckIndex,
								m_CurrentReceiveFileSliceInfo.m_SeveralFileSlices,
								m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen,
								remainLen);
						CheckIndex += remainLen;
						m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen += remainLen;
						m_NowAccumulateFileSize += remainLen;
						m_CurrentReceiveFileSliceInfo.m_CurrentFileSliceCount++;
						// 如果缓存的文件量足够
						FileSliceBufferAndWriteManage();

						// 开始下一个协议包的接收
						PrepareForNextAnalyze();
						// break HeadAnalyze;
						if (CheckIndex < m_receivedLen) {
							FindProtocolType(CheckIndex);
						}
					} else {
						// 只能获取文件块部分内容
						System.arraycopy(
								m_ReceptBuffer,
								CheckIndex,
								m_CurrentReceiveFileSliceInfo.m_SeveralFileSlices,
								m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen,
								m_receivedLen);
						m_ProtocolCorrectContentIndex += m_receivedLen;
						CheckIndex += m_receivedLen;//
						// 记录文件内容的累加长度
						m_CurrentReceiveFileSliceInfo.m_CurrentAllFileSliceLen += m_receivedLen;
						m_NowAccumulateFileSize += m_receivedLen;
					}
				} else
				// 非文件块协议，一般数量比较小
				{
					for (; (CheckIndex < m_receivedLen)
							&& (m_ProtocolCorrectContentIndex < m_CurrentProtocolContent.length); CheckIndex++, m_ProtocolCorrectContentIndex++)
						m_CurrentProtocolContent[m_ProtocolCorrectContentIndex] = m_ReceptBuffer[CheckIndex];
					// 一次性读取了足够的协议内容
					if (m_ProtocolCorrectContentIndex == m_CurrentProtocolContent.length) {
						m_liReceiveProtocol
								.add(new Protocol(m_CurrentProtocolType,
										m_CurrentProtocolContent));
						// 开始下一个协议包的发送
						PrepareForNextAnalyze();
						// break HeadAnalyze;
						if (CheckIndex < m_receivedLen) {
							FindProtocolType(CheckIndex);
						}
					}
				}
			}
		}
	}

	private void PrepareForNextAnalyze() {
		m_ProtocolCorrectHeadBSIndex = 0;
		m_ProtocolCorrectLenBSIndex = 0;
		m_ProtocolCorrectTypeBSIndex = 0;
		m_ProtocolCorrectContentIndex = 0;
	}

	// 文件块等解析过程
	// 解析从仪器发送过来的文件描述信息
	void AnalyzeFileDescriptionInfo(byte[] stream) {
		try {
			Exchange ExchB = new Exchange();
			int startPos = 0;
			// 文件唯一标识
			int fileguidlen = ExchB.BytestoInt(stream, startPos);
			startPos += 4;
			String fileguid = ExchB.BytestoString(stream, startPos,
					fileguidlen, false);
			startPos += fileguidlen;
			// 文件名
			int fileNameLen = ExchB.BytestoInt(stream, startPos);
			startPos += 4;
			String fileName = ExchB.BytestoString(stream, startPos,
					fileNameLen, false);
			startPos += fileNameLen;
			// 文件大小
			long ReceiveFileSize = ExchB.BytestoLong(stream, startPos);
			startPos += 8;
			boolean IsSenderForceToSendNextFile = ExchB.ByteToBoolean(stream,
					startPos);

			// 等待可能的上回的写入线程未完成;该处的等待与其他地方的Reset()，基本上在同一线程内
			// m_AskWriteFileSliceToFile.WaitOne();
			if (IsSenderForceToSendNextFile == true) {
				// 强制回收内存
				while (m_ReceiveFileSlices.size() > 0)
					LoadFileSliceBuffersManager
							.ReCycleBufferForSeveralFileSlicesWrite(m_ReceiveFileSlices
									.poll());
			} else {
				// 如果当前接收文件过程未结束
				if (m_ReceiveFileSize > 0 && m_NowAccumulateFileSize > 0
						&& m_NowAccumulateFileSize == m_ReceiveFileSize) {

				} else if (m_ReceiveFileSize > 0)// 当客户端重新连接上服务器，有可能出现这样的情况
				{
					// 接收队列中有数据，但是由于没达到条件，文件写入过程没有自动启动，故强制进行文件写入

					// 将接收内容放入待写入队列中
					while (m_ReceiveFileSlices.size() > 0)
						m_FileSlicesWaitToWrite.add(m_ReceiveFileSlices.poll());
					WriteFileSliceToFile();
				}
			}

			m_ReceiveFileSize = ReceiveFileSize;

			// 通知外界接收到文件发送请求
			if (fileSRL != null) {
				long fp = fileSRL.ReceiveFileSendRequest(fileName, this,
						m_CustomerClient, fileguid, m_ReceiveFileSize);
				if (fp < 0)// 通知对方拒绝接收文件
				{
					m_DatasToSend.add(m_FileReject);
					return;
				}
				// 要求对方发送指定位置的文件块
				AskToSendFileFromPosition(fp);
				m_BeginToReceiveFileTimePos = new Date();
				m_NowAccumulateFileSize = fp;
				m_IsNeedNextBufferForSeveralFileSlices = true;
			}
			// 回收内存
			while (m_FileSlicesWaitToWrite.size() > 0)
				LoadFileSliceBuffersManager
						.ReCycleBufferForSeveralFileSlicesWrite(m_FileSlicesWaitToWrite
								.poll());
		} catch (Exception e) {
			// ErrorLog.Record("处理文件发送请求出错!" + e.getMessage());
		}
	}

	void AskToSendFileFromPosition(long position) {
		Exchange ExchB = new Exchange();
		byte[] pBS = ExchB.LongtoBytes(position);
		m_FileDescriptionOKFeedback[23] = pBS[7];
		m_FileDescriptionOKFeedback[22] = pBS[6];
		m_FileDescriptionOKFeedback[21] = pBS[5];
		m_FileDescriptionOKFeedback[20] = pBS[4];
		m_FileDescriptionOKFeedback[19] = pBS[3];
		m_FileDescriptionOKFeedback[18] = pBS[2];
		m_FileDescriptionOKFeedback[17] = pBS[1];
		m_FileDescriptionOKFeedback[16] = pBS[0];

		m_DatasToSend.add(m_FileDescriptionOKFeedback);
	}

	void InitToReceiveNextFile() {
		m_NowReceiveFile = "";
		m_ReceiveFileSize = 0;
	}

	// 客户端处理是否文件续传
	void AnalyzeReceiveFileDescriptionFeedback(byte[] stream) {
		Exchange ExchB = new Exchange();
		// 假如是文件续传，根据回复的信息调整读取文件的位置
		try {
			m_NextBeginReadPos = ExchB.BytestoLong(stream, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 如果是强制性发送下一个文件
		if (m_IsForceToSendNextFile == true) {
			// 启动文件发送过程
			m_IsPauseNowSendingFile = false;
			// 通知发送线程开始执行
			m_IsSendingFile = true;

			m_IsForceToSendNextFile = false;

		} else
		// 如果是非强制性发送下一个文件，那么当前不会有文件在发送
		{
			m_IsSendingFile = true;
			// 启动文件发送过程
			m_IsPauseNowSendingFile = false;
		}
		m_FinishSendFileSliceSize = m_NextBeginReadPos;
	}

	// 按照协议框架封装要发送的数据
	public byte[] BuildResponseStream(int ProtocolType, String ProtocolContent) {
		byte[] bs = null;
		try {
			bs = ProtocolContent.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return BuildResponseStream2(ProtocolType, bs);
	}

	public byte[] BuildResponseStream2(int ProtocolType, byte[] ProtocolContent) {
		Exchange ExchB = new Exchange();
		ExchB.AddBytes(m_ZHDGISNetProtocolHead, false);
		if (ProtocolContent != null) {
			ExchB.AddIntAsBytes(ProtocolContent.length);
			ExchB.AddIntAsBytes(ProtocolType);
			ExchB.AddBytes(ProtocolContent, false);
		} else {
			ExchB.AddIntAsBytes(0);
			ExchB.AddIntAsBytes(ProtocolType);
		}
		return ExchB.GetAllBytes();
	}
}
