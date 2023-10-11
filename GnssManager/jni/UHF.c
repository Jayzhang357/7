#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <errno.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include "port.h"
#include "uhf.h"
#include "define.h"

#define  ADC_TO_VOLTAGE(val)  ((3300*((int)val))/(4096))
#define  LOG_TAG    "UHF"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

int Com_fd = 0;          //串口文件句柄
int RTCM_fd = 0;         //差分串口文件句柄
int Rssi_fd = 0;         //差分串口文件句柄
char Rec_Buff[50] = {0}; //设置命令返回信息接收缓冲

/*********************************************************
描述：模块设置主调用
输入:    cmd - 命令 argc - 参数
返回:    命令响应状态
协议:    无
打印:    无
**********************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_IOCTLVIB( JNIEnv*  env, jobject  thiz, jint cmd, jint argc)
{
	int ret = -1;               //函数返回
	int driver = cmd;
	int para = argc;
	int fd = 0;

	Radio_rm_state(0);    //电台状态引脚拉低，进入设置模式

	switch (driver)
	{
		case 1:    //设置电台频道
			ret = SetUHF_CH(para);       //0xff 失败 00~15 成功
			break;
		case 2:   //获取电台频道
			ret = GetUHF_CH();           //0xff 失败 00~15 成功
			break;
		case 3:   //设置电台空中波特率
			ret = SetUHF_BT(para);       //-1 失败 0成功
			break;
		case 4:   //获取电台波特率
			ret = GetUHF_BT();           //0 失败 1成功
			break;
		case 5:  //设置电台功率
			ret = SetUHF_PW(para);       //0 失败 1成功
			break;
		case 6:  //获取电台功率
			ret = GetUHF_PW();           //返回0xAA 0x55 0x00
			break;
		case 7:  //设置电台单收模式
			ret = SetSZUHF_Mode(para);   //返回1成功
			break;
		case 8:  //初始化电台配置
			ret = UHF_Init();            //返回1成功
			LOGE("UHF_Init  %d",ret);
			break;
		default:
			printmenu();
			ret = -1;
			break;
	}

	Radio_rm_state(1);   //电台状态引脚拉高，进入数据模式
	return ret;
}

/*****************************************************************************
 函 数 名  : UhfRecvData
 功能描述  : UHF电台接收数据
 输入参数  : jint fd
                             jbyteArray jbuf
                             jint len
 输出参数  : 无
 返 回 值  : 错误:-1，正确:实际接收数据的长度
*****************************************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_UhfRecvData(JNIEnv* env, jobject thiz, jint fd, jbyteArray jbuf, jint len)
{
	int retval = -1;
	int i = 0;
	int maxfd = -1;
	char readBuf[2048] = {0};
	fd_set rfds;
	struct timeval tv;
	tv.tv_sec = 0;

	// clear 
	FD_ZERO(&rfds);
	// add fd
	FD_SET(Com_fd, &rfds);
	tv.tv_sec = 1;
	tv.tv_usec = 0;
	maxfd = Com_fd;

	//select 
	retval = select(maxfd + 1, &rfds, NULL, NULL, &tv);

	if (retval == -1)
	{
		retval = -1;
	}
	else if (retval == 0)
	{
		retval = 0;
	}
	else
	{
		if (FD_ISSET(Com_fd, &rfds))
		{
			memset(readBuf, 0x0, 2048);
			retval = read(Com_fd, readBuf, len);
			usleep(5000);

			LOGI("retval = %d, readBuf = %s", retval, readBuf);
			(*env)->SetByteArrayRegion(env, jbuf, 0, len, readBuf);
			//write(RTCM_fd, readBuf, retval);
		}
    }

	return retval;
}

/*****************************************************************************
 函 数 名  : UhfSendData
 功能描述  : 电台发送数据
 			 jint fd
 输入参数  : jbyteArray jbuf  需要发送的数据内容
             jint len    需要发送的数据长度
 输出参数  : 无
 返 回 值  : 错误:-1，正确:实际发送数据的长度
*****************************************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_UhfSendData(JNIEnv* env, jobject thiz, jint fd, jbyteArray jbuf, jint len)
{
	int retval = 0;

	if (len <= 0)
	{
	    return -1;
	}

	char *sendData = (*env)->GetByteArrayElements(env, jbuf, NULL);
	retval = write(RTCM_fd, sendData, len);
	(*env)->ReleaseByteArrayElements(env, jbuf, sendData, 0);

	return retval;
}

/*****************************************************************************
 函 数 名  : UhfRssiRead
 功能描述  : 电台场强值读取
 			 jint fd
 输入参数  : ?
 输出参数  : 无
 返 回 值  : 错误:-1，正确:实际发送数据的长度
*****************************************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_UhfRssiRead(JNIEnv* env, jobject thiz)
{
	int retval = 0;
	int vol = 0;
	int rssibuf[20] = {0};
	short *pval,val;

	//打开电台Rssi节点文件
    Rssi_fd = Open_Port(Rssi_fd, RM_RSSI_PATH );
	if(Rssi_fd < 0)
	{
		LOGE("Opent_Port error!");
		return -1;
	}

	retval = read(Rssi_fd, rssibuf, 19);
	pval  = (short *)rssibuf;
	val  = *pval;
	LOGI("rssibuf = %d", val);

	vol = ADC_TO_VOLTAGE(val);

	if(vol <= 1241)                                     // 1V以下
		retval = 1;
	else if((vol > 1241)&&(vol <= 1861))    // 1~1.5V
		retval = 2;
	else if((vol > 1861)&&(vol <= 2482))    // 1.5~2.0V
		retval = 3;
	else if((vol > 1861)&&(vol <= 2482))    // 2.0~2.4V
		retval = 4;

	close(Rssi_fd);

	return retval;
}

/*****************************************************************************
 函 数 名  : UhfRssiRead
 功能描述  : 电台场强值读取
 			 jint fd
 输入参数  : ?
 输出参数  : 无
 返 回 值  : 错误:-1，正确:实际发送数据的长度
*****************************************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_UhfRssiRead2(JNIEnv* env, jobject thiz)
{
   int retval = 0;
   int vol = 0;
   int rssibuf[20] = {0};
   short *pval,val;

   //打开电台Rssi节点文件
    Rssi_fd = Open_Port(Rssi_fd, RM_RSSI_PATH );
   if(Rssi_fd < 0)
   {
      LOGE("Opent_Port error!");
      return -1;
   }

   retval = read(Rssi_fd, rssibuf, 19);
   pval  = (short *)rssibuf;
   val  = *pval;
   LOGI("rssibuf = %d", val);

   vol = ADC_TO_VOLTAGE(val);

   close(Rssi_fd);

   return vol;
}

/*********************************************************
描述：打开串口
输入:    无
返回:    无
协议:    无
打印:    无
**********************************************************/
JNIEXPORT jint JNICALL
Java_com_uhf_control_UHFClass_OpenCOM(JNIEnv* env, jobject thiz)
{
	//打开电台串口
    Com_fd = Open_Port(Com_fd, COMPATH);
	if(Com_fd < 0)
	{
		LOGE("Opent_Port error!");
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		LOGE("Set_opt error");
		close(Com_fd);
		return -1;
	}

	//打开差分串口
    RTCM_fd = Open_Port(RTCM_fd, RTCMPATH);
	if(RTCM_fd < 0)
	{
		LOGE("Opent_Port error!");
		return -1;
	}
	//设置差分串口波特率
	if (Set_Port(RTCM_fd, 19200, 8, 'N', 1) < 0)
	{
		LOGE("Set_opt error");
		close(RTCM_fd);
		return -1;
	}

	return 1;
}


/*********************************************************
描述：关闭串口
输入:    无
返回:    无
协议:    无
打印:    无
**********************************************************/
JNIEXPORT jint JNICALL
//Java_com_example_hellojni_UHFConf_Close(JNIEnv* env, jobject thiz)
Java_com_uhf_control_UHFClass_Close(JNIEnv* env, jobject thiz)
{
	close(Com_fd);
	close(RTCM_fd);
}


/*********************************************************
描述：菜单选项打印
输入:    无
返回:    无
协议:    无
打印:    无
**********************************************************/
void printmenu(void)
{
	printf("\
*******************************************************************************\n\
*           UHF-Tool DDTTR module SetType Usage: dirver  parameter\n\
*-------------------------------------------------------------------------------\n\
*           dirver   parameter    ---->>>>---->>>>  hard driver\n\
*             1       iuhfch      ---->>>>---->>>>  SetUHF_CH\n\
*             2          0        ---->>>>---->>>>  GetUHF_CH\n\
*             3          bt        ---->>>>---->>>>  SetUHF_BT\n\
*             4          0        ---->>>>---->>>>  GetUHF_BT\n\
*             5          pw       ---->>>>---->>>>  SetUHF_PW\n\
*             6          0        ---->>>>---->>>>  ReadUHF_PW\n\
*             7          0        ---->>>>---->>>>  SetSZUHF_Mode\n\
*             8          0        ---->>>>---->>>>  UHF_Init\n\
*-------------------------------------------------------------------------------\n\
*             iuhfch:           0~15....................................................................................\n\
*             bt:                0 --19200  1--9600..........................................................................\n\
*             pw:               0 --low  1--midle  2--high................................................................\n\
*             UHF_Mode:   1-SendOnly 2-ReceiveAndSend 3-ReceiveOnly....................................\n\
*******************************************************************************\n");
}

/*********************************************************
描述：设置电台信道
输入：待设信道值(0~115)
返回：当前信道值(0~115)  OR  -1 ->设置失败
协议：Header+length+direction+commend+address+package+check_byte
读            55 06 80 88   88 88  ch   00
打印：55 08 80 76/77 00 00 ch ch ch ck  ;76/77->TRUE/FALSE
**********************************************************/
s8 SetUHF_CH(u8 iuhfch)
{
	u8 cbuff[50] = {0} ,ch = 0, len = 0, csm=0;
	int Rec_Len = 0, Clr_Len = 1, TimeOver = 0;
    u16 wpt = 0;
	int i=0, SendCnt = 0;
	s8 tmpch = -1;   //电台频道

   	usleep(100000);    //0.1s

	//打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
	if(Com_fd < 0)
	{
		LOGE("Opent_Port error!");
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		LOGE("Set_opt error");
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		i++;
		if(i > 1000)
		{
			LOGE("Com_fd Maybe wrong!!\n");
			close(Com_fd);
			return -1;
		}
	}

	cbuff[0] = 0x55;
    cbuff[1] = 0x06;
    cbuff[2] = 0x80;
    cbuff[3] = 0x88;
    cbuff[4] = 0x88;
    cbuff[5] = 0x88;
    cbuff[6] = iuhfch;
    cbuff[7] = 0x00;
    for(ch = 0; ch < 7; ch++)
    {
       	 cbuff[7] ^= cbuff[ch];
    }

	//发送命令
	write(Com_fd, cbuff, 8);
	usleep(100000);
	memset(Rec_Buff, 0, sizeof(Rec_Buff));
	SendCnt++;

	while(1)
	{
		Rec_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		if(Rec_Len > 0)
		{
			//for(i=0; i<Rec_Len; i++)
			//{
				//printf("%x ", Rec_Buff[i]);
			//}

			if(Rec_Buff[wpt + 0] == 0x55)
			{
				if(Rec_Buff[wpt + 3]==0x76)   //set succeed
				{
					len = Rec_Buff[wpt + 1] ;    //Package size
	                if(Rec_Len > len)
	                {
	                    len += 1;                //check Length
	                    for(ch = 0; ch < len; ch++)
	                    {
	                        csm ^= Rec_Buff[wpt + ch] ;    //check sum
	                    }
	                    if(csm == Rec_Buff[wpt + len])
	                    {
	                        tmpch = Rec_Buff[wpt + len - 1]; //UHF_ch
	                        break;
	                    }
	                    else
	                    {
	                        break;
	                    }
	                }
				}
				else if(Rec_Buff[wpt+3] == 0x77)   //set unsuccessful
	            {
	            	break;   //set unsuccessful ,退出SetUHF_CH()
	            }
	            else
	            {
	               	 break ;                 //退出当前while
	            }
			}
		}
		else
		{
			TimeOver++;
			if (TimeOver >= 15)
			{
				TimeOver = 0;
				SendCnt++;
				if (SendCnt >= 3)
				{
					break;
				}

				LOGE("send cmd to uhf again");

				close(Com_fd);
				//打开串口
	    		Com_fd = Open_Port(Com_fd, COMPATH );
				if(Com_fd < 0)
				{
					LOGE("Opent_Port error!");
					return -1;
				}
				//设置串口波特率
				if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
				{
					LOGE("Set_opt error");
					close(Com_fd);
					return -1;
				}

				//发送命令
				write(Com_fd, cbuff, 8);
				usleep(100000);
				memset(Rec_Buff, 0, sizeof(Rec_Buff));
			}
		}
	}

	close(Com_fd);

	if(tmpch > 115)
	{
		tmpch = -1;
	}
    return  tmpch;
}

/*********************************************************
描述：读电台信道，UHF_ch=当前信道
输入：无
返回：当前信道值(0~115)  OR  -1 ->读失败
协议：Header+length+direction+commend+address+package+check_byte
读  : 96   05   80  6B  00 01             79
打印：96   0A   80  6C  00 00 ch 00 00 24 15  ck
**********************************************************/
s8 GetUHF_CH(void)
{
	u8 cbuff[50] = {0}, ch = 0, len = 0,csm=0;
    int Rec_Len = 0, Clr_Len = 1, TimeOver = 0;
    u16 wpt = 0 ;
	int i = 0, SendCnt = 0;
    s8 tmpch = -1;

   	usleep(100000);

	//打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
	if(Com_fd < 0)
	{
		LOGE("Opent_Port error!");
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		LOGE("Set_opt error");
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		i++;
		if(i > 1000)
		{
			LOGE("Com_fd Maybe wrong!!\n");
			close(Com_fd);
			return -1;
		}
	}

    cbuff[0] = 0x96;
    cbuff[1] = 0x05;
    cbuff[2] = 0x80;
    cbuff[3] = 0x6B;
    cbuff[4] = 0x00;
    cbuff[5] = 0x01;
    cbuff[6] = 0x79;

   	 //发送命令
	write(Com_fd, cbuff, 7);
	usleep(100000);
	memset(Rec_Buff, 0, sizeof(Rec_Buff));
	SendCnt++;

    while(1)
    {
    	Rec_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		if(Rec_Len > 0)
		{
			//for(i = 0; i < Rec_Len; i++)
			//{
				//printf("%x ", Rec_Buff[i]);
			//}

			if(Rec_Buff[wpt] == 0x96)   //set succeed
			{
	        	if(Rec_Buff[wpt + 3] == 0x6C )   //command
	            {
	            	len=Rec_Buff[wpt + 1] ;    //Package size
	                if(Rec_Len > len)       //len
	                {
	                  	len += 1;                //check Length
	                    for(ch = 0; ch < len; ch++)
	                    {
	                      	csm ^= Rec_Buff[wpt + ch];
	                    }
	                    if(csm == Rec_Buff[wpt + len])
	                    {
	                       	tmpch = Rec_Buff[wpt + len - 5];
							break;
	                    }
	                    else
	                    {
	                       	break; //getUHF_CH 返回数据有误码
	                    }
	                }
	            }
	            else
	            {
	            	break ;       //退出当前while(Timing60msD)
	            }
	        }
	    }
		else
		{
			TimeOver++;
			if (TimeOver >= 15)
			{
				TimeOver = 0;
				SendCnt++;
				if (SendCnt >= 3)
				{
					break;
				}

				LOGE("send cmd to uhf again");

				close(Com_fd);
				//打开串口
	    		Com_fd = Open_Port(Com_fd, COMPATH );
				if(Com_fd < 0)
				{
					LOGE("Opent_Port error!");
					return -1;
				}
				//设置串口波特率
				if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
				{
					LOGE("Set_opt error");
					close(Com_fd);
					return -1;
				}

				//发送命令
				write(Com_fd, cbuff, 8);
				usleep(100000);
				memset(Rec_Buff, 0, sizeof(Rec_Buff));
			}
		}
	}

    close(Com_fd);

    if(tmpch > 115)
    {
    	tmpch = -1;
    }

    return  tmpch;
}

/*********************************************************
描述：设置UHF 串口空中波特率
输入：1 -> 9600   其他-> 19200
返回：成功返回0，失败返回-1
协议：Header+length+direction+commend+address+package+check_byte
9600
写  : 96 0A 80 70 00 01 20 CC 00 00 55 D4
19200
写  : 96 0A 80 70 00 01 20 CC 00 00 40 C1
打印：96 0A 80 71 00 00 20 CC 00 00 40 C1
**********************************************************/
s8 SetUHF_BT(u8 bt)
{
	u8 cbuff[50] = {0};

    usleep(100000);

	cbuff[0]  = 0x96;
	cbuff[1]  = 0x0A;
	cbuff[2]  = 0x80;
	cbuff[3]  = 0x70;
	cbuff[4]  = 0x00;
	cbuff[5]  = 0x01;
	cbuff[6]  = 0x20;
	cbuff[7]  = 0xCC;
	cbuff[8]  = 0x00;
	cbuff[9]  = 0x00;
	cbuff[10] = 0x40;
	cbuff[11] = 0xC1;

	if(bt == 1)
	{
		//cbuff[10] = 0xC0;
		//cbuff[11] = 0x41;
		cbuff[10] = 0x55;
		cbuff[11] = 0xD4;
	}

    //打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
    if(Com_fd < 0)
    {
		LOGE("Opent_Port error!");
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		LOGE("Set_opt error");
		close(Com_fd);
		return -1;
	}

	//发送命令
	write(Com_fd, cbuff, 12);
    usleep(100000);
	close(Com_fd);

	return 0;
}

/*********************************************************
描述：读电台UHF串口波特率和空中波特率
输入：无
输出：非0 is OK
协议：Header+length+direction+commend+address+package+check_byte
读  : 96 05 80 72 00 01 60
返回：96 0A 80 73 00 00 20 CC 00 00 40 C3
**********************************************************/
int GetUHF_BT(void)
{
	u8 cbuff[50] = {0}, ch = 0, len = 0, uhfbt=0, csm=0;
	int ret = 0, Rec_Len = 0, Clr_Len = 1, TimeOver = 0;
    u16 wpt=0;
	int i = 0, SendCnt = 0;

   	usleep(100000);

    //打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
	if(Com_fd < 0)
	{
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Len));
		i++;
		if(i > 1000)
		{
			LOGE("Com_fd Maybe wrong!!\n");
			close(Com_fd);
			return -1;
		}
	}

    cbuff[0] = 0x96;
    cbuff[1] = 0x05;
    cbuff[2] = 0x80;
    cbuff[3] = 0x72;
    cbuff[4] = 0x00;
    cbuff[5] = 0x01;
    cbuff[6] = 0x60;

   	//发送命令
	write(Com_fd, cbuff, 7);
    usleep(100000);
    memset(Rec_Buff, 0, sizeof(Rec_Buff));
	SendCnt++;

    while(1)
    {
    	Rec_Len = read(Com_fd, Rec_Buff, 50);
		if(Rec_Len > 0)
		{
			//for(i=0; i<Rec_Len; i++)
			//{
				//printf("%x ", Rec_Buff[i]);
			//}

			if(Rec_Buff[wpt] == 0x96)   //set succeed
			{
            	if(Rec_Buff[wpt + 3]==0x73 )   //command
                {
                	len = Rec_Buff[wpt + 1] ;    //Package size
                    if(Rec_Len > len)       //len
                    {
                    	len +=1;                //check Length
                        for(ch=0; ch < len; ch++)
                        {
                        	csm ^= Rec_Buff[wpt + ch] ;
                        }
                        if(csm == Rec_Buff[wpt + len])
                        {
                        	uhfbt = Rec_Buff[wpt + 6];

							LOGE("\r\nUHF COM at: ");

							switch(uhfbt)
							{
								case 0x01: LOGE("600 OK!\r\n");    break;
								case 0x02: LOGE("1200 OK!\r\n");   break;
								case 0x04: LOGE("2400 OK!\r\n");   break;
								case 0x08: LOGE("4800 OK!\r\n");   break;
								case 0x10: LOGE("9600 OK!\r\n");   break;
								case 0x20: LOGE("19200 OK!\r\n");  break;
								default:   LOGE("unknown!\r\n");   break;
							}

                            ret = 0;
                            ret = Rec_Buff[wpt+10];

							LOGE("\r\nUHF RF at: ");

							switch(ret)
							{
								case 0x40: LOGE("19200 OK!\r\n"); break;
								case 0xC0: LOGE("9600 OK!\r\n");  break;
								default:   LOGE("unknown!\r\n");  break;
							}

							break;
                        }
                        else
                        {
							break;
                        }
                    }
                }
                else
                {
                     break ;
                }
            }
        }
		else
		{
			TimeOver++;
			if (TimeOver >= 15)
			{
				TimeOver = 0;
				SendCnt++;
				if (SendCnt >= 3)
				{
					break;
				}

				LOGE("send cmd to uhf again");

				close(Com_fd);
				//打开串口
	    		Com_fd = Open_Port(Com_fd, COMPATH );
				if(Com_fd < 0)
				{
					LOGE("Opent_Port error!");
					return -1;
				}
				//设置串口波特率
				if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
				{
					LOGE("Set_opt error");
					close(Com_fd);
					return -1;
				}

				//发送命令
				write(Com_fd, cbuff, 8);
				usleep(100000);
				memset(Rec_Buff, 0, sizeof(Rec_Buff));
			}
		}
    }

	close(Com_fd);

	return  ret;
}

/*********************************************************
描述：内置发射电台高低功率设置
输入：0  1 2   低中高
输出：0/1       -1 ->设置失败     1->设置成功
协议：Header+length+direction+commend+address+package+check_byte
读  : 55 06 80 74 74 74 PP CK
返回：55 08 80 75 00 00 01 07 09 A7          75->TRUE/FALSE
**********************************************************/
s8 SetUHF_PW(u8 pw)
{
    u8 cbuff[50] = {0}, ch = 0, len = 0, csm=0;
	int Rec_Len = 0, Clr_Len = 1, TimeOver = 0;
    u16 wpt = 0;
	s8 uhfpw = 0;          //电台功率
	int i = 0, SendCnt = 0;

	usleep(100000);

    //打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
	if(Com_fd < 0)
	{
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, 50);
		i++;
		if(i > 1000)
		{
			close(Com_fd);
			return -1;
		}
	}

    cbuff[0] = 0x55;
    cbuff[1] = 0x08;
    cbuff[2] = 0x80;
    cbuff[3] = 0x74;
    cbuff[4] = 0x88;
    cbuff[5] = 0x88;
    switch(pw)
    {
       	case 0: cbuff[6] = 0x55; break; //0.1W
        case 1: cbuff[6] = 0x99; break; // 1W
        case 2: cbuff[6] = 0xAA; break; // 2W
        default:     cbuff[6] = 0x55; break; //0.1W
    }

    cbuff[7]=0x00;
	cbuff[8]=0x00;
	cbuff[9]=0x00;
    for(ch=0;ch<9;ch++)
    {
       	cbuff[9] ^= cbuff[ch];
   	}

    	//发送命令
	write(Com_fd, cbuff, 10);
   	usleep(100000);
	SendCnt++;

    while(1)
    {
        Rec_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		if(Rec_Len > 0)
		{
			//for(i=0; i<Rec_Len; i++)
			//{
				//printf("%x ", Rec_Buff[i]);
			//}

			if(Rec_Buff[wpt]==0x55)   //set succeed
			{
            	if(Rec_Buff[wpt + 3]==0x75)    //set succeed
                {
                	uhfpw = 1; //return ok
                	break;
                }
                else
                {
                	break; //setUHF_CH 返回数据有误码
                }
            }
        }
		else
		{
			TimeOver++;
			if (TimeOver >= 15)
			{
				TimeOver = 0;
				SendCnt++;
				if (SendCnt >= 3)
				{
					break;
				}

				LOGE("send cmd to uhf again");

				close(Com_fd);
				//打开串口
	    		Com_fd = Open_Port(Com_fd, COMPATH );
				if(Com_fd < 0)
				{
					LOGE("Opent_Port error!");
					return -1;
				}
				//设置串口波特率
				if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
				{
					LOGE("Set_opt error");
					close(Com_fd);
					return -1;
				}

				//发送命令
				write(Com_fd, cbuff, 8);
				usleep(100000);
				memset(Rec_Buff, 0, sizeof(Rec_Buff));
			}
		}
    }

	close(Com_fd);

	return  uhfpw;
}


/*********************************************************
描述：读取内置发射电台高低功率
输入：无
返回：0xAA:UHF_POWERH;0x55:UHF_POWERL; 0X00:无效;  -1:失败
协议：Header+length+direction+commend+address+package+check_byte
读  : 0x55 0X06 0x80 0x76 0x76 0x76 0x76 CK
打印：55 08 80 77 00 00 PP PP PP CK
**********************************************************/
s8 GetUHF_PW(void)
{
	u8 cbuff[50] = {0}, ch = 0, len = 0, uhfpw = 0, csm = 0;
	int Rec_Len = 0, Clr_Len = 1, TimeOver = 0;
	u16 wpt = 0;
	int i = 0, SendCnt = 0;

   	usleep(100000);

    //打开串口
    Com_fd = Open_Port(Com_fd, COMPATH );
	if(Com_fd < 0)
	{
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, 50);
		i++;
		if(i > 1000)
		{
			close(Com_fd);
			return -1;
		}
	}

	cbuff[0] = 0x55;
    cbuff[1] = 0x05;
    cbuff[2] = 0x80;
    cbuff[3] = 0x76;
    cbuff[4] = 0x00;
    cbuff[5] = 0x00;
    cbuff[6] = 0xA6;

	//发送命令
	write(Com_fd, cbuff, 7);
    usleep(100000);
    memset(Rec_Buff, 0, sizeof(Rec_Buff));
	SendCnt++;

    while(1)
    {
        Rec_Len = read(Com_fd, Rec_Buff, 50);
		if(Rec_Len > 0)
		{
			//for(i=0; i<Rec_Len; i++)
			//{
				//printf("%x ", Rec_Buff[i]);
			//}

			if(Rec_Buff[wpt] == 0x55)   //set succeed
			{
            	if(Rec_Buff[wpt + 3] == 0x77)    //set succeed
                {
                	uhfpw = Rec_Buff[wpt + 7]; //return oK
                	break;
                }
                else
                {
                    break; //setUHF_CH 返回数据有误码
                }
            }
        }
		else
		{
			TimeOver++;
			if (TimeOver >= 15)
			{
				TimeOver = 0;
				SendCnt++;
				if (SendCnt >= 3)
				{
					break;
				}

				LOGE("send cmd to uhf again");

				close(Com_fd);
				//打开串口
	    		Com_fd = Open_Port(Com_fd, COMPATH );
				if(Com_fd < 0)
				{
					LOGE("Opent_Port error!");
					return -1;
				}
				//设置串口波特率
				if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
				{
					LOGE("Set_opt error");
					close(Com_fd);
					return -1;
				}

				//发送命令
				write(Com_fd, cbuff, 8);
				usleep(100000);
				memset(Rec_Buff, 0, sizeof(Rec_Buff));
			}
		}
    }

	close(Com_fd);

	return  uhfpw;
}


//******************************************************************************
//描述：设置电台工作模式
//输入：0 - 单收  1 - 单发  2 - 收发一体
//输出：TRUE/FALSE
//******************************************************************************
s8 SetSZUHF_Mode(u8 TempUHF_Mode)
{
	u8 cbuff[50] = {0};
	int Rec_Len = 0, Clr_Len = 1;
	int i = 0;

	//打开串口
    Com_fd = Open_Port(Com_fd, COMPATH);
	if(Com_fd < 0)
	{
		return -1;
	}
	//设置串口波特率
	if (Set_Port(Com_fd, 19200, 8, 'N', 1) < 0)
	{
		close(Com_fd);
		return -1;
	}
    //清空数据防止异常
	while (Clr_Len)
	{
		Clr_Len = read(Com_fd, Rec_Buff, sizeof(Rec_Buff));
		i++;
		if(i > 1000)
		{
			close(Com_fd);
			return -1;
		}
	}

    cbuff[0] = 0x55;
    cbuff[1] = 0x08;
    cbuff[2] = 0x80;
    cbuff[3] = 0xA0;
    cbuff[4] = 0x88;
    cbuff[5] = 0x88;
    cbuff[6] = 0x00;
    cbuff[7] = 0x00;
	cbuff[8] = 0x00;
	cbuff[9] = 0x7D;

	switch(TempUHF_Mode)
    {
		case 0:
		{
	        cbuff[6] = 0x00; //单收工作模式
	        break;
		}
		case 1:
       	{
        	cbuff[6] = 0x55; //单发工作模式
            break;
        }
        case 2:
        {
        	cbuff[6] = 0xAA; //收发一体工作模式
        	break;
        }
        default:
        {
            cbuff[6] = 0x00; //单收工作模式
            break;
        }
    }
	for(i = 0; i < 9; i++)
    {
        cbuff[9] ^= cbuff[i];
    }

    	//发送命令
	write(Com_fd, cbuff, 10);
    usleep(100000);
    close(Com_fd);

	return 1;
}

//******************************************************************************
//描述：电台配置初始化 频道:0  波特率:19200
//输入：工作模式
//输出：TRUE/FALSE
//******************************************************************************
s8 UHF_Init(void)
{
	if(0xff != SetUHF_CH(0x00))                          //初始化频道为0
	{
		LOGE("\r\nInit_SetUHF_CH sucess\r\n");
		if(0 == SetUHF_BT(0))                             //初始化空中波特率19200
		{
			LOGE("\r\nInit_SetUHF_BT sucess\r\n");
			if(1 == SetUHF_PW(0) )                   //初始化功率为低
			{
				LOGE("\r\nInit_SetUHF_PW sucess\r\n");
				if(1 == SetSZUHF_Mode(0))     //初始化电台模式为单收
				{
					LOGE("\r\nInit_SetSZUHF_ReceiveOnlyMode\r\n");
					return 1;
				}
			}
		}
	}
	return -1;
}

//******************************************************************************
//描述：电台状态设置
//输入：1 数据模式  0 命令模式
//输出：无
//******************************************************************************
void Radio_rm_state(int sta)
{
	int fd;
	unsigned char buf_on[]="on\n";
	unsigned char buf_off[]="off\n";

	fd = open(RM_STATE_PATH,O_RDWR);
	if(fd == -1)
	{
		return;
	}

	if(sta == 1)
	{
		write(fd,buf_on,3);
	}
	else if(sta == 0)
	{
		write(fd,buf_off,4);
	}

	close(fd);
}

