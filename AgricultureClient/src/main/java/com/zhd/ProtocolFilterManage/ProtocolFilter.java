package com.zhd.ProtocolFilterManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/// <summary>
/// 1、从字节流中提取完整的数据包，并完成CRC校验，将消息头和消息体抛出
/// 2、该对象处理的消息格式是“标识位|消息头|消息体|校验码|标识位”
/// 3、该对象还处理消息包组分包情况
/// </summary>
public class ProtocolFilter {
	// 标识位，起到消息分隔的作用
	private byte MessageDelimeter = 0x7e;
	// 用于收集碎片数据的缓存
	private ArrayList<Byte> m_FragmentBytes;
	// 表示经过组包后的属于同一组的消息包
	private ArrayList<ProtocolMessage> m_Messages;
	// 表示当前消息分包总数
	private int m_FragmentMessageCount;
	// 表示目前接收到的是第几包
	private int m_CurrentFragmentMessageIndex;

	public ProtocolFilter() {
		m_FragmentBytes = new ArrayList<Byte>(500);
	}

	// / <summary>
	// / 从碎片数据流中提取消息包
	// / </summary>
	// / <param name="fragmentDatas"></param>
	// / <returns>一次只会返回一个数据包或者返回若干个相关的分包</returns>
	public ProtocolMessage[] Filter(byte[] fragmentDatas) throws IOException {
		Byte b;
		m_FragmentBytes.clear();
		for (int i = 0; i < fragmentDatas.length; i++) {
			b = fragmentDatas[i];
			// 遇到消息包分割符
			if (b == MessageDelimeter) {
				// 说明已经收集到了一条完整的数据包
				if (m_FragmentBytes.size() > 0) {
					// 当前最后一个字节是校验码
					Restore(m_FragmentBytes);
					byte crcCheck = m_FragmentBytes
							.get(m_FragmentBytes.size() - 1);
					m_FragmentBytes.remove(m_FragmentBytes.size() - 1);
					// 处理转义还原
					
					byte abc=CRC(m_FragmentBytes);
					// 进行CRC校验
					if (crcCheck != CRC(m_FragmentBytes))
						continue;

					// 生成实际的数据包
					byte[] tmp = new byte[m_FragmentBytes.size()];
					for (int j = 0; j < m_FragmentBytes.size(); j++)
						tmp[j] = m_FragmentBytes.get(j);
					ProtocolHead ph = new ProtocolHead();
					int index = ph.FromBytes(tmp, 0);
					if (ph.MessageID == (short) 0x0700) {
						index += 12;
					}
					
					Object[] content = (m_FragmentBytes.subList(
							m_FragmentBytes.size() - index,
							m_FragmentBytes.size()).toArray());
					if(ph.MessageID == (short) 0x0711)
						content=m_FragmentBytes.subList(
								m_FragmentBytes.size() - 1,
								m_FragmentBytes.size()).toArray();
					byte[] tmp2 = new byte[content.length];
					for (int j = 0; j < content.length; j++)
						tmp2[j] = (Byte) content[j];
					ProtocolMessage pm = new ProtocolMessage(ph, tmp2);
					// 查看分包情况
					if (ph.BodyAttri.isHasFragment) {
						m_CurrentFragmentMessageIndex = ph.FragmentInfo.CurrentFragmentNum;
						m_FragmentMessageCount = ph.FragmentInfo.FragmentTotal;
					}

					if (m_FragmentMessageCount > 0) {
						// 说明组包完成
						if (m_CurrentFragmentMessageIndex == m_FragmentMessageCount) {
							m_Messages.add(pm);
							ProtocolMessage[] pms = (ProtocolMessage[]) (m_Messages
									.toArray());
							m_Messages.clear();

							// 提取完整数据后，需要恢复各种接收状态
							m_FragmentMessageCount = 0;
							m_CurrentFragmentMessageIndex = 0;
							return pms;
						} else if (m_CurrentFragmentMessageIndex < m_FragmentMessageCount) {
							m_Messages.add(pm);
						} else {
							// 说明出问题了
							m_FragmentMessageCount = 0;
							m_CurrentFragmentMessageIndex = 0;
						}
					} else {
						// 说明只有一条数据
						return new ProtocolMessage[] { pm };
					}
				} else {
					// 不需要做任何处理，说明开始新数据的收集

				}
			} else {
				// 添加到缓存中
				m_FragmentBytes.add(b);
			}
		}
		// 找不到任何数据，返回空值
		return null;
	}

	// / <summary>
	// / 将消息尽心序列化、转义处理、封装等操作
	// / </summary>
	// / <param name="pms">该数组中的消息包之间可能是逻辑上的分包或者是独立的包</param>
	// / <returns></returns>
	public byte[] ToByte(ProtocolMessage[] pms) {
		ArrayList<Byte> bs = new ArrayList<Byte>();
		for (int i = 0; i < pms.length; i++) {
			ArrayList<Byte> tmp = pms[i].ToByte();
			byte check = CRC(tmp);
			tmp.add(check);
			bs.add((byte) 0x7e);
			byte[] tmp2 = Transfer((tmp.toArray()));
			for (int j = 0; j < tmp2.length; j++)
				bs.add(tmp2[j]);
			bs.add((byte) 0x7e);
		}
		byte[] tmp = new byte[bs.size()];
		for (int i = 0; i < bs.size(); i++)
			tmp[i] = (Byte) bs.get(i);
		return tmp;
	}

	// / <summary>
	// /
	// / </summary>
	// / <param name="buf"></param>
	// / <param name="sindex">起始字节</param>
	// / <param name="len">需要校验的字节数</param>
	// / <returns></returns>
	private byte CRC(ArrayList<Byte> buf) {
		byte tem = 0x00;
		if (buf.size() - 0 <= 0)
			return tem;
		tem = buf.get(0);
		for (int i = 1; i < buf.size(); i++) {
			tem = (byte) (tem ^ buf.get(i));
		}
		return tem;
	}

	// / <summary>
	// /
	// / </summary>
	// / <param name="buf"></param>
	// / <param name="sindex">起始字节</param>
	// / <param name="len">需要校验的字节数</param>
	// / <returns></returns>
	private byte CRC(byte[] buf) {
		byte tem = 0x00;
		if (buf.length <= 0)
			return tem;
		tem = buf[0];
		for (int i = 1; i < buf.length; i++) {
			tem = (byte) (tem ^ buf[i]);
		}
		return tem;
	}
	public byte[] ToByte(ProtocolMessage_Can[] pms) {
		ArrayList<Byte> bs = new ArrayList<Byte>();
		for (int i = 0; i < pms.length; i++) {
			ArrayList<Byte> tmp = pms[i].ToByte();

			Random random = new Random();//指定种子数字
			int i1 = random.nextInt(127);
			Random random1 = new Random();//指定种子数字
			int i2 = random1.nextInt(127);

			tmp.add( (byte) (i1));
			tmp.add( (byte) (i2));
			byte check = CRC(tmp);
			tmp.add(check);
			bs.add((byte) 0xf1);
			bs.add((byte) 0xf2);
			bs.add((byte) 0xff);

			for (int j = 0; j < tmp.size(); j++)
				bs.add(tmp.get(j));
			bs.add((byte) 0x0d);
		}
		byte[] tmp = new byte[bs.size()];
		for (int i = 0; i < bs.size(); i++)
			tmp[i] = (Byte) bs.get(i);
		return tmp;
	}
	// / <summary>
	// / 对消息体中的特殊字节进行转义处理
	// / </summary>
	// / <param name="buf"></param>
	// / <returns></returns>
	private byte[] Transfer(Object[] buf) {
		int num = 0;
		byte[] tem = new byte[buf.length * 2];
		for (int i = 0; i < buf.length; num++, i++) {
			if ((Byte) buf[i] == 0x7e) {
				tem[num] = 0x02;
				num++;
				tem[num] = 0x7d;
			} else if ((Byte) buf[i] == 0x7d) {
				tem[num] = 0x01;
				num++;
				tem[num] = 0x7d;
			} else {
				tem[num] = (Byte) buf[i];
			}
		}
		byte[] tem2 = new byte[num];
		System.arraycopy(tem, 0, tem2, 0, num);
		return tem2;
	}

	// / <summary>
	// / 处理转义还原
	// / </summary>
	// / <param name="buf"></param>
	// / <returns></returns>
	private byte[] Restore(byte[] buf) {
		int num = 0;
		byte[] tem = new byte[buf.length];
		for (int i = 0; i < buf.length - 1; num++, i++) {
			if (buf[i] == 0x7d && buf[i + 1] == 0x02) {
				tem[num] = 0x7e;
				i++;
			}
			if (buf[i] == 0x7d && buf[i + 1] == 0x01) {
				tem[num] = 0x7d;
				i++;
			}
			tem[num] = buf[i];
		}
		byte[] tem2 = new byte[num];
		System.arraycopy(tem, 0, tem2, 0, num);
		return tem2;
	}

	// / <summary>
	// / 处理转义还原
	// / </summary>
	// / <param name="fragmentBytes"></param>
	private void Restore(ArrayList<Byte> fragmentBytes) {
		int i = 0;
		while (i < fragmentBytes.size()) {
			if (fragmentBytes.get(i) == 0x7d) {
				if (++i < fragmentBytes.size()) {
					if (fragmentBytes.get(i) == 0x01) {
						fragmentBytes.remove(i);
					} else if (fragmentBytes.get(i) == 0x02) {
						fragmentBytes.remove(i);
						fragmentBytes.set(--i, (byte) 0x7e);
					}
				}
			} else
				i++;
		}
	}

}
