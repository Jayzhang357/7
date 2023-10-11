/*******************************************************************************
 * 工程名称:VNet新一代工程固件
 * 模块名称:piddb_operate
 * 文件名称:pid_db.c
 * 实现功能:pid.db数据库一些声明
 * 备注:
------------------------------------原始版本--------------------------------------
* 日期:2013/3/4
* 版本:V1.0
* 作者:韩伟浩
* 功能:
* 说明:对pid.db数据库操作的一些函数进行定义
------------------------------------更新版本--------------------------------------
------
*******************************************************************************/

/**********************************包含头文件************************************/
#include "port.h"

/**********************************函数定义**************************************/

/*******************************************************************************
 * 名称:Open_Port
 * 功能:打开串口
 * 形参:int Fd --> 文件句柄
 *     char *Port_Dev --> 串口设备符
 * 返回:成功返回文件句柄,失败返回-1
 * 说明:串口设备符在　"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port(int fd, char *port_dev)
{
	//打开串口
	fd = open(port_dev, O_RDWR | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//恢复串口为阻塞状态
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//测试是否为终端设备
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	//printf("Open_Port %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * 名称:Open_Port_R
 * 功能:打开串口
 * 形参:int Fd --> 文件句柄
 *     char *Port_Dev --> 串口设备符
 * 返回:成功返回文件句柄,失败返回-1
 * 说明:串口设备符在　"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port_R(int fd, char *port_dev)
{
	//打开串口
	fd = open(port_dev, O_RDONLY | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//恢复串口为阻塞状态
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//测试是否为终端设备
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	printf("Open_Port_R %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * 名称:Open_Port_W
 * 功能:打开串口
 * 形参:int Fd --> 文件句柄
 *     char *Port_Dev --> 串口设备符
 * 返回:成功返回文件句柄,失败返回-1
 * 说明:串口设备符在　"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port_W(int fd, char *port_dev)
{
	//打开串口
	fd = open(port_dev, O_WRONLY | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//恢复串口为阻塞状态
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//测试是否为终端设备
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	printf("Open_Port_W %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * 名称:Set_Port
 * 功能:配置串口
 * 形参:int fd --> ,
 *     int nSpeed --> ,
 *     int nBits --> ,
 *     char nEvent --> ,
 *     int nStop -->
 * 返回:成功返回PID值,失败返回-1
 * 说明:无
*******************************************************************************/
int Set_Port(int fd,int nSpeed,int nBits,char nEvent,int nStop)
{
	struct termios Old_Port_Info, New_Port_Info;

	//保存测试现有串口参数设置，在这里如果串口号等出错，会有相关的出错信息
	if (tcgetattr(fd, &Old_Port_Info) != 0) {
		perror("Get Pre_Serial Info error!!!\n");
		return -1;
	}
	//清空串口信息
	bzero(&New_Port_Info, sizeof(New_Port_Info));

	//激活选项，CLOCAL为本地连接，CREAD为接受使能
	New_Port_Info.c_cflag |= CLOCAL | CREAD;

	//设置字符大小
	New_Port_Info.c_cflag &= ~CSIZE;

	//设置数据位
	if (7 == nBits) {
		New_Port_Info.c_cflag |= CS7;
	} else
		New_Port_Info.c_cflag |= CS8;

	//设置奇偶校验位
	switch (nEvent) {
		case 'O'://奇数
			New_Port_Info.c_cflag |= PARENB;
			New_Port_Info.c_cflag |= PARODD;
			New_Port_Info.c_cflag |= (INPCK | ISTRIP);
		break;
		case 'E'://偶数
			New_Port_Info.c_cflag |= (INPCK | ISTRIP);
			New_Port_Info.c_cflag |= PARENB;
			New_Port_Info.c_cflag &= ~PARODD;
		break;
		case 'N'://无奇偶校验位
			New_Port_Info.c_cflag &= ~PARENB;
		break;
	}

	//设置波特率

	switch (nSpeed) {
		case 2400:
			cfsetispeed(&New_Port_Info,B2400);
			cfsetospeed(&New_Port_Info,B2400);
		break;
		case 4800:
			cfsetispeed(&New_Port_Info,B4800);
			cfsetospeed(&New_Port_Info,B4800);
			break;
		case 9600:
			cfsetispeed(&New_Port_Info,B9600);
			cfsetospeed(&New_Port_Info,B9600);
			break;
		case 19200:
			cfsetispeed(&New_Port_Info,B19200);
			cfsetospeed(&New_Port_Info,B19200);
			break;
		case 38400:
			cfsetispeed(&New_Port_Info,B38400);
			cfsetospeed(&New_Port_Info,B38400);
			break;
		case 57600:
			cfsetispeed(&New_Port_Info,B57600);
			cfsetospeed(&New_Port_Info,B57600);
			break;
		case 115200:
			cfsetispeed(&New_Port_Info,B115200);
			cfsetospeed(&New_Port_Info,B115200);
			break;
		case 230400:
			cfsetispeed(&New_Port_Info,B230400);
			cfsetospeed(&New_Port_Info,B230400);
			break;
		case 460800:
			cfsetispeed(&New_Port_Info,B460800);
			cfsetospeed(&New_Port_Info,B460800);
			break;
	}

	/*设置停止位*/
	if(nStop == 1)
	{
		New_Port_Info.c_cflag &= ~CSTOPB;
	}
	else if(nStop == 2)
	{
		New_Port_Info.c_cflag |= CSTOPB;
	}

	//设置等待时间和最小接收字符
	New_Port_Info.c_cc[VTIME] = 0;
	New_Port_Info.c_cc[VMIN] = 0;

	//处理未接收字符
	tcflush(fd, TCIFLUSH);

	//激活新配置
	if((tcsetattr(fd,TCSANOW,&New_Port_Info)) != 0) {
		perror("com set error");
		return -1;
	}

	//printf("Set_Port fd:%d %d %d %c %d !!\n", fd, nSpeed, nBits, nEvent, nStop);
	return fd;
}


/**********************************文件结束**************************************/
