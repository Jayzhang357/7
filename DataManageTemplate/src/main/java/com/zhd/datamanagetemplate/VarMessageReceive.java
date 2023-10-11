package com.zhd.datamanagetemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import android.util.Log;

/**
 * 1、该类完成NMEA数据的拼接过程支持，从串口接收到不完整的数据，在该类中完成拼接后，将完整的数据提供给外界 2、该类默认处理的数据结尾是“\r\n”
 *
 * @author Administrator
 *
 */
class VarMessageReceive {

	private MessageDescription[] mMessageDescriptions;// 需要拼接的数据格式说明
	private MessageDescription mCurrentMatchMessage;// 当前匹配的数据
	private int mCurrentMessageReceiveLen;// 当前已经接收到的数据长度
	private int mCurrentMessageLen;// 当前要接收的数据的长度
	private byte[] mCurrentMessage;// 当前的待拼接完整的数据，只存放按“数据长度”接收的数据
	private int mMaxHeadLen;// 注册进来的数据头最大的长度
	private int mMinHeadLen;// 注册进来的数据头最小的长度
	private byte[] mCollectData;// 用于拼接数据的缓存
	private int mMinMatchHeadPos;// 当前已经匹配第一个字节的最小位置
	private int mMaxInvalidDataLen;// 无效数据的最大长度
	private boolean mBeAskedToContinueReceiveLenData;// 是否被要求继续接收指定长度的数据
	private int mCount = 0;
	private boolean isFirst = true;

	/**
	 *
	 * @param messageDescriptions
	 *            需要提取的数据类型
	 * @throws Exception
	 */
	public VarMessageReceive(MessageDescription[] messageDescriptions)
			throws Exception {
		if (messageDescriptions == null || messageDescriptions.length == 0)
			throw new Exception(
					"messageDescriptions must not be null and messageDescriptions.length must bigger than zero");
		mMessageDescriptions = messageDescriptions;
		int maxMessageLen = 0;
		try {
			mMaxHeadLen = mMinHeadLen = mMessageDescriptions[0].mHead.length;
			maxMessageLen = mMessageDescriptions[0].mMaxMessageLen;
			for (int i = 0; i < mMessageDescriptions.length; i++) {
				if (mMessageDescriptions[i].mHead.length > mMaxHeadLen)
					mMaxHeadLen = mMessageDescriptions[i].mHead.length;
				if (mMessageDescriptions[i].mHead.length < mMinHeadLen)
					mMinHeadLen = mMessageDescriptions[i].mHead.length;
				if (mMessageDescriptions[i].mMaxMessageLen > maxMessageLen)
					maxMessageLen = mMessageDescriptions[i].mMaxMessageLen;

			}
		} catch (Exception ep) {
			FileHelper.record(ep.toString());
		}

		// 表示还没有识别出具体的数据头
		mCurrentMessageReceiveLen = 0;
		mCollectData = new byte[maxMessageLen + maxMessageLen];
		mMinMatchHeadPos = 0;
		mCurrentMatchMessage = null;
		mMaxInvalidDataLen = maxMessageLen;
		mCurrentMessageLen = 0;
		mBeAskedToContinueReceiveLenData = false;
		if (maxMessageLen < mMaxHeadLen)
			FileHelper
					.record("maxMessageLen<mMaxHeadLen,invalid maxMessageLen");
	}

	/**
	 * 外部通过该函数传入需要拼接的碎片数据
	 *
	 * @param fragementData
	 */
	public void ReceiveFragementData(byte[] fragementData) {

		if (fragementData != null && fragementData.length > 0)

			TryToFindEntireMessage(fragementData, 0);

		// try {
		// System.out.print(" fragementData:"
		// + new String(fragementData, "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * 匹配指定位置处的数据头
	 *
	 * @param
	 * @return 如果匹配返回true
	 */
	private boolean MatchHead(MessageDescription md) {
		byte[] head = md.mHead;
		int headLen = head.length;
		// 如果当前收集的数据长度不足
		if (headLen > (mCurrentMessageReceiveLen - md.mStartMatchHeadIndex))
			return false;
		int startMatchPos = md.mStartMatchHeadIndex;
		for (int i = 0; i < headLen; i++, startMatchPos++) {
			// 加上是天宝格式的判断
			if (md.getIsTrimble() && (i == 1))
				continue;

			if (head[i] != mCollectData[startMatchPos]) {
				// 将匹配位置向移一位
				md.mStartMatchHeadIndex++;
				return false;
			}
		}
		return true;
	}

	/**
	 * 尝试在拼接数据缓冲区中寻找可能存在的“数据头”
	 *
	 * @return 如果找到了返回true
	 */
	private boolean FindHeadInCollectData() {
		for (int headIndex = 0; headIndex < mMessageDescriptions.length; headIndex++) {
			MessageDescription md = mMessageDescriptions[headIndex];
			// 如果在收集的数据中找到了数据头
			if (MatchHead(md)) {
				// 删除之前的无效数据，将其他数据头的匹配位置恢复，将有效数据向前移动
				if (md.mStartMatchHeadIndex != 0) {
					int k = 0;
					for (int i = md.mStartMatchHeadIndex; i < mCurrentMessageReceiveLen; i++, k++) {
						mCollectData[k] = mCollectData[i];
					}
					mCurrentMessageReceiveLen = k;
				}
				// 恢复其他数据头的匹配位置
				for (int i = 0; i < mMessageDescriptions.length; i++)
					mMessageDescriptions[i].mStartMatchHeadIndex = 0;
				// 标记找到的数据头
				mCurrentMatchMessage = md;
				return true;
			} else {

			}
		}
		// 当累积的数据量达到一定程度的时候，清理无效数据
		if (mCurrentMessageReceiveLen > mMaxInvalidDataLen) {
			mMinMatchHeadPos = mMessageDescriptions[0].mStartMatchHeadIndex;
			for (int i = 0; i < mMessageDescriptions.length; i++)
				if (mMessageDescriptions[i].mStartMatchHeadIndex < mMinMatchHeadPos)
					mMinMatchHeadPos = mMessageDescriptions[i].mStartMatchHeadIndex;
			if (mMinMatchHeadPos >= mMaxInvalidDataLen) {

				int k = 0;
				for (int i = mMinMatchHeadPos; i < mCurrentMessageReceiveLen; i++, k++) {
					mCollectData[k] = mCollectData[i];
				}

				try {
					byte[] writeBuf = new byte[mCurrentMessageReceiveLen];
					System.arraycopy(mCollectData, 0, writeBuf, 0,
							mCurrentMessageReceiveLen);
					System.out.print(" mCollectData:"
							+ new String(writeBuf, "UTF-8"));
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
				mCurrentMessageReceiveLen = k;
				// 将其他数据头的匹配位置前移
				for (int headIndex = 0; headIndex < mMessageDescriptions.length; headIndex++)
					mMessageDescriptions[headIndex].mStartMatchHeadIndex -= mMinMatchHeadPos;

				mMinMatchHeadPos = 0;
			}
		}
		return false;
	}

	/**
	 * 根据计算出来的数据长度接收剩余的数据，并通知外界是否接收了一条完整的数据
	 *
	 * @param fragementData
	 * @param startIndex
	 */
	private void ReceiveRestMessageAccordingLen(byte[] fragementData,
												int startIndex) {

		try {
			// 剩余的数据量大于需要的数据量
			if ((fragementData.length - startIndex) >= (mCurrentMessageLen - mCurrentMessageReceiveLen)) {
				// 一次性拷贝足够的数据量
				while (mCurrentMessageReceiveLen < mCurrentMessageLen)
					mCurrentMessage[mCurrentMessageReceiveLen++] = fragementData[startIndex++];
				// 通知外界已经接收到了一条完整的数据
				TellReceiveLenEntireMessage();
				// 开始下一轮的数据接收过程
				if (startIndex < fragementData.length) {
					// 递归处理剩余的数据
					TryToFindEntireMessage(fragementData, startIndex);
				}

			} else {
				while (startIndex < fragementData.length)
					mCurrentMessage[mCurrentMessageReceiveLen++] = fragementData[startIndex++];
			}
		} catch (Exception e) {

		}

	}

	/**
	 * 回复内部变量状态，准备接收下一条数据
	 */
	private void RestoreToReceiveNextMessage() {
		mCurrentMatchMessage = null;
		mCurrentMessageReceiveLen = 0;
		mMinMatchHeadPos = 0;
		mCurrentMessageLen = 0;
		mBeAskedToContinueReceiveLenData = false;
	}

	/**
	 * 通知外界按数据头+数据尾的方式拼接了一条完整的数据
	 */
	private void TellReceiveEntireMessage() {
		// 通知外界已经接收了一条完整的数据
		byte[] entireMessage = new byte[mCurrentMessageReceiveLen];
		// 从拼接缓冲区中拷贝完整的数据
		for (int i = 0; i < mCurrentMessageReceiveLen; i++)
			entireMessage[i] = mCollectData[i];

		mCount++;

		System.out.println(" 收到完整数据计数:" + Integer.toString(mCount));
		char[] a = getChars(entireMessage);
		//

		try {
			// 调用外部的数据处理过程处理接收到的数据
			mCurrentMatchMessage.ResolveReceiveMesssage(entireMessage);
		} catch (Exception e) {

		}
		// 恢复接收状态
		RestoreToReceiveNextMessage();

	}

	/**
	 * 通知外界按长度拼接了一条完整的数据
	 */
	private void TellReceiveLenEntireMessage() {
		// 通知外界已经接收了一条完整的数据
		try {
			mCurrentMatchMessage.ResolveReceiveMesssage(mCurrentMessage);

			mCurrentMessageLen = mCurrentMatchMessage
					.AskToContinueReceiveLenData();

			if (mCurrentMessageLen <= 0) {
				// 恢复接收状态
				RestoreToReceiveNextMessage();
			} else {
				// 通知拼接过程继续接收数据
				mBeAskedToContinueReceiveLenData = true;
				mCurrentMessageReceiveLen = 0;
				mCurrentMessage = new byte[mCurrentMessageLen];
			}

		} catch (Exception ep) {
			// System.out.println(ep.toString());
			//	String g = ep.getMessage();
			//FileHelper.record("TellReceiveLenEntireMessage" + g);
		}
	}

	private char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(" ");
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 *
	 * @param fragementData
	 *            外部传入的碎片数据
	 * @param startIndex
	 *            碎片数据遍历的起始位置
	 */
	private void TryToFindEntireMessage(byte[] fragementData, int startIndex) {

		try {
			// 如果未确定数据标识头

			if (mCurrentMatchMessage == null) {
				Log.v("测试", "未确定数据标识头");
				while (startIndex < fragementData.length
						&& mCurrentMessageReceiveLen < mCollectData.length) {
					mCollectData[mCurrentMessageReceiveLen++] = fragementData[startIndex++];

					if ((mCurrentMessageReceiveLen - mMinMatchHeadPos) >= mMinHeadLen) {
						// 可以立即启动数据头匹配过程
						if (FindHeadInCollectData()) {
							// 开始接收数据头以外的数据
							TryToFindEntireMessage(fragementData, startIndex);
							return;
						}
					}

				}

			}
			// 已经批匹配到了某种数据头
			else {
				Log.v("测试", "已经批匹配到了某种数据头");
				// 需要处理“数据长度域”
				if (mCurrentMatchMessage.getIsHasLenField()) {
					// 继续
					if (this.mBeAskedToContinueReceiveLenData == true) {
						ReceiveRestMessageAccordingLen(fragementData,
								startIndex);
					} else if (mCurrentMessageLen == 0) {
						while (startIndex < fragementData.length
								&& mCurrentMessageReceiveLen < mCollectData.length) {

							mCollectData[mCurrentMessageReceiveLen++] = fragementData[startIndex++];
							// 如果已经接收了足够长度的数据域可以开始计算数据长度
							if (mCurrentMessageReceiveLen > mCurrentMatchMessage.mMessageLenFieldLastByteIndex) {
								// 该处函数调用将mCollectData作为参数，严格上来说是要风险，但是为了减少不必要的内存拷贝，所以才这样做
								try {
									mCurrentMessageLen = mCurrentMatchMessage
											.ComputeLen(mCollectData);
								} catch (Exception e) {
									mCurrentMessageLen = -1;

								}
								// 假如计算出来的数据长度太大超出了实际可能的值或者为负数，那么直接认为当前数据无效或有问题
								if (mCurrentMessageLen < 0
										|| mCurrentMessageLen > mCollectData.length
										|| mCurrentMessageLen < mCurrentMatchMessage.mHead.length
										|| mCurrentMessageLen < mCurrentMessageReceiveLen) {
									RestoreToReceiveNextMessage();
									TryToFindEntireMessage(fragementData,
											startIndex);
									return;
								}
								// 创建提交给外部的最终的数据
								mCurrentMessage = new byte[mCurrentMessageLen];
								// 拼接缓冲区中的有效数据拷贝到mCurrentMessage中
								for (int i = 0; i < mCurrentMessageReceiveLen; i++)
									mCurrentMessage[i] = mCollectData[i];
								// 剩余的数据直接拷贝到mCurrentMessage
								ReceiveRestMessageAccordingLen(fragementData,
										startIndex);
								break;
							}
						}
					}
					// 已经知道数据的长度
					else
						ReceiveRestMessageAccordingLen(fragementData,
								startIndex);
				}
				// 不需要处理数据长度
				else {
					Log.v("测试", "不需要处理数据长度");
					// 一直收集数据直到遇到回车换行
					while (startIndex < fragementData.length
							&& mCurrentMessageReceiveLen < mCollectData.length) {

						// 检查是否遇到了数据结尾“\n”
						if (fragementData[startIndex] == mCurrentMatchMessage.mMessageLastByte) {
							// 说明数据已经接收完成
							mCollectData[mCurrentMessageReceiveLen++] = fragementData[startIndex++];
							// 通知外界已经接收到了一条完整的数据
							TellReceiveEntireMessage();
							// 开始下一轮的数据接收过程
							if (startIndex < fragementData.length) {
								// 递归处理剩余的数据
								TryToFindEntireMessage(fragementData,
										startIndex);
								return;
							}
						} else {
							// 还没有遇到数据结尾
							mCollectData[mCurrentMessageReceiveLen++] = fragementData[startIndex++];
							// 如果一直无法找到数据结尾，当数据量比该类数据可能的长度长时，认为当前数据无效
							if (mCurrentMessageReceiveLen >= mCurrentMatchMessage.mMaxMessageLen) {
								RestoreToReceiveNextMessage();
								// 开始下一轮的数据接收过程
								if (startIndex < fragementData.length) {
									// 递归处理剩余的数据
									TryToFindEntireMessage(fragementData,
											startIndex);
									return;
								}
							}
						}
					}
				}

			}
		} catch (Exception e) {

		}
	}
}
