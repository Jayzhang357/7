package com.zhd.parserinterface;

import com.zhd.bd970.manage.interfaces.ReceiveABangleListner;
import com.zhd.bd970.manage.interfaces.ReceiveAGRICAListner;
import com.zhd.bd970.manage.interfaces.ReceiveBESTPOSAListner;
import com.zhd.bd970.manage.interfaces.ReceiveBestvelaListner;
import com.zhd.bd970.manage.interfaces.ReceiveGGAListner;
import com.zhd.bd970.manage.interfaces.ReceiveGGAMessageListner;
import com.zhd.bd970.manage.interfaces.ReceiveGSAListner;
import com.zhd.bd970.manage.interfaces.ReceiveGSVListner;
import com.zhd.bd970.manage.interfaces.ReceiveECUListner;
import com.zhd.bd970.manage.interfaces.ReceiveInsVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveMCUVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveMduVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveNAKListner;
import com.zhd.bd970.manage.interfaces.ReceiveNodeListner;
import com.zhd.bd970.manage.interfaces.ReceiveOemListner;
import com.zhd.bd970.manage.interfaces.ReceiveOpenedListner;
import com.zhd.bd970.manage.interfaces.ReceivePASHRListner;
import com.zhd.bd970.manage.interfaces.ReceiveQXwzListner;
import com.zhd.bd970.manage.interfaces.ReceiveRMCListner;
import com.zhd.bd970.manage.interfaces.ReceiveRangeListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadDeadZoneDateListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadSenListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadZXZYListner;
import com.zhd.bd970.manage.interfaces.ReceiveReadcarListner;
import com.zhd.bd970.manage.interfaces.ReceiveReaduhfListner;
import com.zhd.bd970.manage.interfaces.ReceiveSATVISListener;
import com.zhd.bd970.manage.interfaces.ReceiveSDKListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfOneListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfRetListner;
import com.zhd.bd970.manage.interfaces.ReceiveSelfTwoListner;
import com.zhd.bd970.manage.interfaces.ReceiveServerOffListner;
import com.zhd.bd970.manage.interfaces.ReceiveServerOkListner;
import com.zhd.bd970.manage.interfaces.ReceiveTB2VersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveTrackStatListner;
import com.zhd.bd970.manage.interfaces.ReceiveTrimble40hListener;
import com.zhd.bd970.manage.interfaces.ReceiveUMVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveVTGListner;
import com.zhd.bd970.manage.interfaces.ReceiveVersionListner;
import com.zhd.bd970.manage.interfaces.ReceiveZDAListner;
import com.zhd.bd970.manage.interfaces.ReceiveBaseStationListner;
import com.zhd.bd970.manage.interfaces.ReceiveZcbyDateListner;
import com.zhd.bd970.manage.interfaces.ReceiveZcbyListner;
import com.zhd.bd970.manage.interfaces.ReceivebootListner;
import com.zhd.bd970.manage.interfaces.ReceivereadpidListner;
import com.zhd.bd970.manage.interfaces.ReceiveCorrectimuListner;
import com.zhd.bd970.manage.interfaces.ReceiveRegListner;
import com.zhd.bd970.manage.interfaces.ReceiveZC31Listner;

public interface IParser {
	public void parseoem(byte[] message);
	public void parseBESTPOSA(byte[] message);
	public void parseServerSDK(byte[] message);
	public void parseOpened(byte[] message);
	public void parseServerQXwz(byte[] message);
	public void parseServerNode(byte[] message);
	public void parseServeroff(byte[] message);
	
	public void parseSelf(byte[] message);
	public void parseServerOk(byte[] message);
	public void parseZC31(byte[] message);
	public void parseRegVersion(byte[] message);
	public void parseCorrectIMU(byte[] message);
	public void parseMduVersion(byte[] message);
	public void parseInsVersion(byte[] message);
	public void parseReadPid(byte[] message);
	public void parseAGRICA(byte[] message);
	public void parseboot(byte[] message);
	public void parseDeadZone(byte[] message);
	public void parseABangle(byte[] message);
	public void parseECU(byte[] message);
	public void parseReadSen(byte[] message);
	public void parseUMVersion(byte[] message);
	
	public void parseReadZXZY(byte[] message);

	public void parseVersion(byte[] message);
	public void parseTB2Version(byte[] message);
	public void parseRASHR(byte[] message);

	public void parseZcby(byte[] message);

	public void parseReaduhf(byte[] message);

	public void parseReadcar(byte[] message);

	public void parseZcbyDate(byte[] message);

	public void parseMB2(byte[] message);

	public void parseGGA(byte[] message);

	public void parseGSV(byte[] message);

	public void parseRMC(byte[] message);

	public void parseTrackState(byte[] message);

	public void parseTrimble4hData(byte[] message);

	public void parseTrimble35hData(byte[] message);

	public void parseVTG(byte[] entireMessage);

	public void parseGSA(byte[] entireMessage);

	public void parseZDA(byte[] entireMessage);

	public void parsePTNL(byte[] entireMessage);

	public void parseRangeCMP(byte[] entireMessage);

	public void parseBestvela(byte[] entireMessage);

	public void setReceiveGGAListener(ReceiveGGAListner listener);

	public ReceiveGGAListner getReceiveGGAListener();

	public void setReceiveGSAListener(ReceiveGSAListner listener);

	public void setReceiveGGAMessageListener(ReceiveGGAMessageListner listener);

	public void setReceiveVTGListener(ReceiveVTGListner listener);

	public void setReceiveRMCListener(ReceiveRMCListner listener);

	public void setReceiveGSVListener(ReceiveGSVListner listener);

	public void setReceiveTrackStatListener(ReceiveTrackStatListner listener);

	public void setReceiveTrimble40hListener(ReceiveTrimble40hListener listener);

	public void setReceiveZDAListener(ReceiveZDAListner listener);

	public void setReceiveRangeListener(ReceiveRangeListner listener);

	public void setReceiveSATVISListener(ReceiveSATVISListener listener);

	public void setReceiveBestvelaListener(ReceiveBestvelaListner listener);

	public void setReceiveBaseStationListener(ReceiveBaseStationListner listener);

	public void removeGgaListener(ReceiveGGAListner listener);

	public void setReceiveZcbyListener(ReceiveZcbyListner listener);

	public void setReceiveReadcarListener(ReceiveReadcarListner listener);

	public void setReceiveReaduhfListener(ReceiveReaduhfListner listener);

	public void setReceiveZcbyDateListener(ReceiveZcbyDateListner listener);

	public void setReceivePASHRListener(ReceivePASHRListner listener);
	public void setReceiveNAKListener(ReceiveNAKListner listener);
	public void setReceiveVersionListener(ReceiveVersionListner listener);
	public void setReceiveInsVersionListener(ReceiveInsVersionListner listener);
	public void setReceiveMduVersionListener(ReceiveMduVersionListner listener);
	public void setReceiveTB2VersionListener(ReceiveTB2VersionListner listener);
	public void setReceiveReadDeadZoneListener(ReceiveReadDeadZoneDateListner listener);
	public void setReceiveReadZXZYListener(ReceiveReadZXZYListner listener);
	public void setReceiveReadPidListener(ReceivereadpidListner listener);

	public void setReceiveReadSenListener(ReceiveReadSenListner listener);
	public void setReceiveECUListener(ReceiveECUListner listener);
	
	public void setReceiveABangleListener(ReceiveABangleListner listener);
	public void setReceiveAGRICAleListener(ReceiveAGRICAListner listener);
	public void setReceiveUMVersionListener(ReceiveUMVersionListner listener);
	public void setReceiveCorrectIMUListener(ReceiveCorrectimuListner listener);
	public void setReceivebooListener(ReceivebootListner listener);
	public void setReceiveRegistener(ReceiveRegListner listener);
	public void setReceiveZC31(ReceiveZC31Listner listener);
	public void setReceiveServerOk(ReceiveServerOkListner listener);
	public void setReceiveSelfOne(ReceiveSelfOneListner listener);
	public void setReceiveSelfTwo(ReceiveSelfTwoListner listener);
	public void setReceiveSelfRet(ReceiveSelfRetListner listener);
	public void setReceiveServerOff(ReceiveServerOffListner listener);
	public void setReceiveQXwzListner (ReceiveQXwzListner listener);
	public void setReceiveNodeListner (ReceiveNodeListner listener);
	public void setReceiveOpendedListner (ReceiveOpenedListner listener);
	public void setReceiveSDKListner(ReceiveSDKListner listener);
	public void setReceiveBESTPOSAListner(ReceiveBESTPOSAListner listener);
	public void setoemListner(ReceiveOemListner listener);

	public void parseMCUVersion(byte[] message);
	public void setReceiveMCUVersionListner(ReceiveMCUVersionListner listener);
}
