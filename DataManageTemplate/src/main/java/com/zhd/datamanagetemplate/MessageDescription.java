package com.zhd.datamanagetemplate;

public class MessageDescription {

	/**
	 * 该数据的开头
	 */
	public byte[] mHead;
	public ByteIndex[] mIreHead;
	private boolean mIsIrregularHead;
	public int mMaxIreHeadLen;

	/**
	 * 数据长度域的最后一个字节相对于第一个字节的位置，第一个字节被认为位置为0
	 */
	public int mMessageLenFieldFirstByteIndex;
	/**
	 * 数据长度域的最后一个字节相对于第一个字节的位置，第一个字节被认为位置为0
	 */
	public int mMessageLenFieldLastByteIndex;
	/**
	 * 该数据的最后一个字节
	 */
	public byte mMessageLastByte;
	private boolean mIsHasLenField;

	/**
	 * 是否具有数据长度域
	 * @return
	 */
	public boolean getIsHasLenField() {
		return mIsHasLenField;
	}

	/**
	 * 当前格式的数据在碎片数据中，匹配的第一个字节的位置
	 */
	public int mStartMatchHeadIndex;
	/**
	 * 外部对以某种“数据头”的数据格式的编号
	 */
	public int mMessageDescriptionType;
	/**
	 * 该类数据可能的最大长度
	 */
	public int mMaxMessageLen;
	/**
	 * 是否是天宝主板
	 */
	private boolean mIsTrimble = false;

	/**
	 * 是否是天宝主板
	 */
	public boolean getIsTrimble() {
		return mIsTrimble;
	}

	/**
	 * /**
	 * 初始化数据格式，该数据格式包括“数据长度域”，并且不具有固定的“结尾字节”
	 * @param head该数据的开头
	 * @param messageLenFieldFirstByteIndex数据长度域的第一一个字节相对于整条数据的第一个字节的位置，第一个字节被认为位置为0
	 * @param messageLenFieldLastByteIndex数据长度域的最后一个字节相对于整条数据的第一个字节的位置，第一个字节被认为位置为0
	 */
	public MessageDescription(byte[] head, int messageDescriptionType,
							  int messageLenFieldFirstByteIndex, int messageLenFieldLastByteIndex,
							  int maxMessageLen, boolean isTrimble) {
		mHead = head;
		mMessageLenFieldFirstByteIndex = messageLenFieldFirstByteIndex;
		mMessageLenFieldLastByteIndex = messageLenFieldLastByteIndex;
		mIsHasLenField = true;
		mMessageDescriptionType = messageDescriptionType;
		mMaxMessageLen = maxMessageLen;//
		mIsTrimble = isTrimble;
	}

	/**
	 * 初始化数据格式，该数据格式不包括“数据长度域”
	 * @param head该数据的开头
	 * @param messageLastByte该数据的最后一个字节
	 */
	public MessageDescription(byte[] head, int messageDescriptionType,
							  byte messageLastByte, int maxMessageLen) {
		mHead = head;
		mMessageLenFieldFirstByteIndex = -1;
		mMessageLenFieldLastByteIndex = -1;
		mIsHasLenField = false;
		mMessageLastByte = messageLastByte;
		mMessageDescriptionType = messageDescriptionType;
		mMaxMessageLen = maxMessageLen;
	}
	public int getMaxIreHeadLen()
	{
		return mMaxIreHeadLen;
	}
	public MessageDescription(ByteIndex[] head, int messageDescriptionType,
							  byte messageLastByte, int maxMessageLen) {
		mIreHead = head;
		mMessageLenFieldFirstByteIndex = -1;
		mMessageLenFieldLastByteIndex = -1;
		mIsHasLenField = false;
		mMessageLastByte = messageLastByte;
		mMessageDescriptionType = messageDescriptionType;
		mMaxMessageLen = maxMessageLen;
		mIsIrregularHead = true;
		mMaxIreHeadLen = head[0].mIndex;
		for (ByteIndex bi : head) {
			if (bi.mIndex > mMaxIreHeadLen)
				mMaxIreHeadLen = bi.mIndex;
		}
		mMaxIreHeadLen++;
	}

	/**
	 * 计算当前该数据的长度，如果当前数据含有数据长度域的话，子类必须重载该方法负责计算出正确的数据长度
	 * @param lenBs，包含了数据长度域字节数组，从mMessageLenFieldFirstByteIndex位置开始计算长度
	 * @return 返回值一定是大于0的数，并且该长度指的是包括“数据头”和“数据尾”在内的整条数据的长度
	 */
	public int ComputeLen(byte[] lenBs) {

		return 0;
	}

	/**
	 * 通知数据拼接类继续拼接指定长度的数据
	 * @return 待继续接收的数据长度，假如不需要再接收任何数据，返回值为0
	 */
	public int AskToContinueReceiveLenData() {

		return 0;
	}

	/**
	 * 子类重载该函数，当收到一条完整的数据后，调用该函数处理数据
	 * @param entireMessage
	 */
	public void ResolveReceiveMesssage(byte[] entireMessage) {
	}

	/**
	 * 通知数据拼接器数据检验是否合格
	 * @return
	 */
	public boolean IsMessageVerifyValid() {

		return true;
	}
}
