package com.zhd.ProtocolFilterManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/// <summary>
/// 1銆佷粠瀛楄妭娴佷腑鎻愬彇瀹屾暣鐨勬暟鎹寘锛屽苟瀹屾垚CRC鏍￠獙锛屽皢娑堟伅澶村拰娑堟伅浣撴姏鍑�
/// 2銆佽瀵硅薄澶勭悊鐨勬秷鎭牸寮忔槸鈥滄爣璇嗕綅|娑堟伅澶磡娑堟伅浣搢鏍￠獙鐮亅鏍囪瘑浣嶁��
/// 3銆佽瀵硅薄杩樺鐞嗘秷鎭寘缁勫垎鍖呮儏鍐�
/// </summary>
public class ThreeInOneFilter {
	// 鏍囪瘑浣嶏紝璧峰埌娑堟伅鍒嗛殧鐨勪綔鐢�
	private byte MessageDelimeter = 0x7e;
	// 鐢ㄤ簬鏀堕泦纰庣墖鏁版嵁鐨勭紦瀛�
	private ArrayList<Byte> m_FragmentBytes;
	// 琛ㄧず缁忚繃缁勫寘鍚庣殑灞炰簬鍚屼竴缁勭殑娑堟伅鍖�
	private ArrayList<ProtocolMessage> m_Messages;
	// 琛ㄧず褰撳墠娑堟伅鍒嗗寘鎬绘暟
	private int m_FragmentMessageCount;
	// 琛ㄧず鐩墠鎺ユ敹鍒扮殑鏄鍑犲寘
	private int m_CurrentFragmentMessageIndex;

	public ThreeInOneFilter() {
		m_FragmentBytes = new ArrayList<Byte>(500);
	}

	// / <summary>
	// / 浠庣鐗囨暟鎹祦涓彁鍙栨秷鎭寘
	// / </summary>
	// / <param name="fragmentDatas"></param>
	// / <returns>涓�娆″彧浼氳繑鍥炰竴涓暟鎹寘鎴栬�呰繑鍥炶嫢骞蹭釜鐩稿叧鐨勫垎鍖�</returns>
	public ProtocolMessage31 Filter(byte[] fragmentDatas) throws IOException {
		Byte b;
		m_FragmentBytes.clear();
		boolean getbyte = false;
		if (fragmentDatas.length > 10) {
			for (int i = 0; i < fragmentDatas.length - 3; i++) {

				if (fragmentDatas[i] ==(byte) 0xAA && fragmentDatas[i + 1] == (byte) 0x55) {
					getbyte = true;
					m_FragmentBytes.add(fragmentDatas[i]);
					m_FragmentBytes.add(fragmentDatas[i+1]);
					i += 2;
					
				}
				
				if (fragmentDatas[i] == 0x40 && fragmentDatas[i + 1] == 0x40
						&& fragmentDatas[i + 2] == 0x24
						&& fragmentDatas[i + 3] == 0x24) {
				
					byte crcCheck = m_FragmentBytes
							.get(m_FragmentBytes.size() - 2);
					byte crcCheck1 = m_FragmentBytes
							.get(m_FragmentBytes.size() - 1);
					m_FragmentBytes.remove(m_FragmentBytes.size() - 1);
					m_FragmentBytes.remove(m_FragmentBytes.size() - 1);
					
					byte[] abc=getCRC16(m_FragmentBytes);
					// 进行CRC校验
					if (crcCheck == abc[0]&&crcCheck1 ==abc[1])
					{
						byte akk=m_FragmentBytes.get(24);
						
						byte[] tmp2 = new byte[m_FragmentBytes.size()-27];
						int x=0;
						for (int j = 27; j < m_FragmentBytes.size(); j++)
						{
							tmp2[x] = (Byte)m_FragmentBytes.get(j);
							x++;
						}
						ProtocolMessage31 qq=new ProtocolMessage31(m_FragmentBytes.get(24),tmp2);
					return qq;	
					}
					
				}
				if (getbyte) {
					m_FragmentBytes.add(fragmentDatas[i]);
				}
			}
		}
		// 鎵句笉鍒颁换浣曟暟鎹紝杩斿洖绌哄��
		return null;
	}
	public static byte[] getCRC16(ArrayList<Byte> byttes) {
		// CRC寄存器全为1
		int CRC = 0x0000ffff;
		// 多项式校验值
		int POLYNOMIAL = 0x0000a001;
		int i, j;
		for (i = 0; i < byttes.size(); i++) {
			CRC ^= ((int) byttes.get(i) & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		// 结果转换为16进制
		String result = Integer.toHexString(CRC).toUpperCase();
		if (result.length() != 4) {
			StringBuffer sb = new StringBuffer("0000");
			result = sb.replace(4 - result.length(), 4, result).toString();
		}
		String a1 = result.substring(2, 4);
		int aa = Integer.parseInt(a1, 16);
		String a2 = result.substring(0, 2);
		int bb = Integer.parseInt(a2, 16);
		byte[] qq = new byte[] { (byte) aa, (byte) bb

		};
		// 高位在前地位在后
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// 交换高低位，低位在前高位在后
		return qq;
	}
	// / <summary>
	// / 灏嗘秷鎭敖蹇冨簭鍒楀寲銆佽浆涔夊鐞嗐�佸皝瑁呯瓑鎿嶄綔
	// / </summary>
	// / <param name="pms">璇ユ暟缁勪腑鐨勬秷鎭寘涔嬮棿鍙兘鏄�昏緫涓婄殑鍒嗗寘鎴栬�呮槸鐙珛鐨勫寘</param>
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

	public byte[] ToByte(ProtocolMessage_Can[] pms) {
		ArrayList<Byte> bs = new ArrayList<Byte>();
		for (int i = 0; i < pms.length; i++) {
			ArrayList<Byte> tmp = pms[i].ToByte();

			Random random = new Random();// 鎸囧畾绉嶅瓙鏁板瓧
			int i1 = random.nextInt(127);
			Random random1 = new Random();// 鎸囧畾绉嶅瓙鏁板瓧
			int i2 = random1.nextInt(127);

			tmp.add((byte) (i1));
			tmp.add((byte) (i2));
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
	// /
	// / </summary>
	// / <param name="buf"></param>
	// / <param name="sindex">璧峰瀛楄妭</param>
	// / <param name="len">闇�瑕佹牎楠岀殑瀛楄妭鏁�</param>
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
	// / <param name="sindex">璧峰瀛楄妭</param>
	// / <param name="len">闇�瑕佹牎楠岀殑瀛楄妭鏁�</param>
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

	// / <summary>
	// / 瀵规秷鎭綋涓殑鐗规畩瀛楄妭杩涜杞箟澶勭悊
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
	// / 澶勭悊杞箟杩樺師
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
	// / 澶勭悊杞箟杩樺師
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
