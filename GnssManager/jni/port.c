/*******************************************************************************
 * ��������:VNet��һ�����̹̼�
 * ģ������:piddb_operate
 * �ļ�����:pid_db.c
 * ʵ�ֹ���:pid.db���ݿ�һЩ����
 * ��ע:
------------------------------------ԭʼ�汾--------------------------------------
* ����:2013/3/4
* �汾:V1.0
* ����:��ΰ��
* ����:
* ˵��:��pid.db���ݿ������һЩ�������ж���
------------------------------------���°汾--------------------------------------
------
*******************************************************************************/

/**********************************����ͷ�ļ�************************************/
#include "port.h"

/**********************************��������**************************************/

/*******************************************************************************
 * ����:Open_Port
 * ����:�򿪴���
 * �β�:int Fd --> �ļ����
 *     char *Port_Dev --> �����豸��
 * ����:�ɹ������ļ����,ʧ�ܷ���-1
 * ˵��:�����豸���ڡ�"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port(int fd, char *port_dev)
{
	//�򿪴���
	fd = open(port_dev, O_RDWR | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//�ָ�����Ϊ����״̬
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//�����Ƿ�Ϊ�ն��豸
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	//printf("Open_Port %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * ����:Open_Port_R
 * ����:�򿪴���
 * �β�:int Fd --> �ļ����
 *     char *Port_Dev --> �����豸��
 * ����:�ɹ������ļ����,ʧ�ܷ���-1
 * ˵��:�����豸���ڡ�"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port_R(int fd, char *port_dev)
{
	//�򿪴���
	fd = open(port_dev, O_RDONLY | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//�ָ�����Ϊ����״̬
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//�����Ƿ�Ϊ�ն��豸
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	printf("Open_Port_R %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * ����:Open_Port_W
 * ����:�򿪴���
 * �β�:int Fd --> �ļ����
 *     char *Port_Dev --> �����豸��
 * ����:�ɹ������ļ����,ʧ�ܷ���-1
 * ˵��:�����豸���ڡ�"/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3",
	"/dev/ttyS4", "/dev/ttyS5", "/dev/ttyS6", "/dev/ttyS7", "/dev/ttyS8",
	"/dev/ttyS9", "/dev/ttyS10", "/dev/ttyS11", "/dev/ttyS12"
*******************************************************************************/
int Open_Port_W(int fd, char *port_dev)
{
	//�򿪴���
	fd = open(port_dev, O_WRONLY | O_NOCTTY | O_NDELAY);
	if (-1 == fd) {
		perror("Open Serial Port Failed! 1/4 !!\n");
		return(-1);
	}
	//�ָ�����Ϊ����״̬
	if (fcntl(fd, F_SETFL, 0) < 0)
		printf("Fcntl Serial Port Failed! 2/4 !!\n");

	//�����Ƿ�Ϊ�ն��豸
	if (isatty(STDIN_FILENO) == 0)
		printf("Serial Port is not a terminal device! 3/4 !!\n");

	printf("Open_Port_W %s as fd:%d OK!\n", port_dev, fd);
	return fd;
}

/*******************************************************************************
 * ����:Set_Port
 * ����:���ô���
 * �β�:int fd --> ,
 *     int nSpeed --> ,
 *     int nBits --> ,
 *     char nEvent --> ,
 *     int nStop -->
 * ����:�ɹ�����PIDֵ,ʧ�ܷ���-1
 * ˵��:��
*******************************************************************************/
int Set_Port(int fd,int nSpeed,int nBits,char nEvent,int nStop)
{
	struct termios Old_Port_Info, New_Port_Info;

	//����������д��ڲ������ã�������������ںŵȳ���������صĳ�����Ϣ
	if (tcgetattr(fd, &Old_Port_Info) != 0) {
		perror("Get Pre_Serial Info error!!!\n");
		return -1;
	}
	//��մ�����Ϣ
	bzero(&New_Port_Info, sizeof(New_Port_Info));

	//����ѡ�CLOCALΪ�������ӣ�CREADΪ����ʹ��
	New_Port_Info.c_cflag |= CLOCAL | CREAD;

	//�����ַ���С
	New_Port_Info.c_cflag &= ~CSIZE;

	//��������λ
	if (7 == nBits) {
		New_Port_Info.c_cflag |= CS7;
	} else
		New_Port_Info.c_cflag |= CS8;

	//������żУ��λ
	switch (nEvent) {
		case 'O'://����
			New_Port_Info.c_cflag |= PARENB;
			New_Port_Info.c_cflag |= PARODD;
			New_Port_Info.c_cflag |= (INPCK | ISTRIP);
		break;
		case 'E'://ż��
			New_Port_Info.c_cflag |= (INPCK | ISTRIP);
			New_Port_Info.c_cflag |= PARENB;
			New_Port_Info.c_cflag &= ~PARODD;
		break;
		case 'N'://����żУ��λ
			New_Port_Info.c_cflag &= ~PARENB;
		break;
	}

	//���ò�����

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

	/*����ֹͣλ*/
	if(nStop == 1)
	{
		New_Port_Info.c_cflag &= ~CSTOPB;
	}
	else if(nStop == 2)
	{
		New_Port_Info.c_cflag |= CSTOPB;
	}

	//���õȴ�ʱ�����С�����ַ�
	New_Port_Info.c_cc[VTIME] = 0;
	New_Port_Info.c_cc[VMIN] = 0;

	//����δ�����ַ�
	tcflush(fd, TCIFLUSH);

	//����������
	if((tcsetattr(fd,TCSANOW,&New_Port_Info)) != 0) {
		perror("com set error");
		return -1;
	}

	//printf("Set_Port fd:%d %d %d %c %d !!\n", fd, nSpeed, nBits, nEvent, nStop);
	return fd;
}


/**********************************�ļ�����**************************************/
