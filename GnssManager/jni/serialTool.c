#include <stdio.h>/*标准输入输出定义*/
#include <string.h>
#include <sys/types.h>
#include <errno.h>/*错误号定义*/
#include <sys/stat.h>
#include <fcntl.h>/*文件控制定义*/
#include <unistd.h>/*Unix 标准函数定义*/
#include <termios.h>/*PPSIX 终端控制定义*/
#include <stdlib.h>/*标准函数库定义*/

#include "../include.h"                       //文件路径
#include "../configure.h"                     //全局变量

void SendEnterCommand(int fd,char *buf, int len)
{
	printf("%s\n", buf);
	write(fd, buf,len - 1);
	char buf1[] = {0x0d, 0x0a};
	write(fd, buf1, 2);
	usleep(200000);
}

#define BUF_LEN	1024
/*读串口主程序*/
int main(int argc, char *argv[])
{
	if (argc != 4) {
		printf("\
*******************************************************************************\n\
*           serialTool Usage:serialTool driver Baud OutputType\n\
*-------------------------------------------------------------------------------\n\
*           dirver  ---->>>>---->>>>  hard driver\n\
*             1     ---->>>>---->>>>  UC864Debug(%s)\n\
*             2     ---->>>>---->>>>  OEMCom1(%s)\n\
*             3     ---->>>>---->>>>  OEMCom2(%s)\n\
*             4     ---->>>>---->>>>  OEMCom3(%s)\n\
*             5     ---->>>>---->>>>  RS485(%s)\n\
*             6     ---->>>>---->>>>  RS232(%s)\n\
*-------------------------------------------------------------------------------\n\
*             Baud : 9600|19200|38400|115200|...........\n\
*             OutputType: 1-ASIIC 2-HEX16\n\
********************************************************************************\n",
	UC864COM, OEMCOM1, OEMCOM2, OEMCOM3, RS485COM, RS232COM
);
		exit(0);
	}

	int driver = atoi(argv[1]);
	int port = atoi(argv[2]);
	int fd = 0;
	int OutputType = atoi(argv[3]);
	switch (driver) {
		case 1:
			fd = Open_Port(fd, UC864COM);
			break;
		case 2:
			fd = Open_Port(fd, OEMCOM1);
			break;
		case 3:
			fd = Open_Port(fd, OEMCOM2);
			break;
		case 4:
			fd = Open_Port(fd, OEMCOM3);
			break;
		case 5:
			fd = Open_Port(fd, RS485COM);
			break;
		case 6:
			fd = Open_Port(fd, RS232COM);
			break;
		default:
		printf("\
*******************************************************************************\n\
*           serialTool Usage:serialTool driver Baud\n\
*-------------------------------------------------------------------------------\n\
*           dirver  ---->>>>---->>>>  hard driver\n\
*             1     ---->>>>---->>>>  UC864Debug(%s)\n\
*             2     ---->>>>---->>>>  OEMCom1(%s)\n\
*             3     ---->>>>---->>>>  OEMCom2(%s)\n\
*             4     ---->>>>---->>>>  OEMCom3(%s)\n\
*             5     ---->>>>---->>>>  RS485(%s)\n\
*             6     ---->>>>---->>>>  RS232(%s)\n\
*-------------------------------------------------------------------------------\n\
*             Baud : 9600|19200|38400|115200|...........\n\
********************************************************************************\n",
	UC864COM, OEMCOM1, OEMCOM2, OEMCOM3, RS485COM, RS232COM
);
			exit(0);
	}

	if(fd < 0) {
		perror("open_port error");
		return;
	}

	if((Set_Port(fd, port, 8, 'N', 1)) < 0) {
		perror("set_opt error");
		return;
	}

	fd_set rfds;
	struct timeval tv;
	tv.tv_sec = 0;
	int retval, maxfd = -1;
	maxfd = fd;
	char Com_Read[2048];
	char Com_Send[1024];
	int Read_Len;
	int i;
	while(1)
	{
		FD_ZERO(&rfds);
		FD_SET(fd, &rfds);
		FD_SET(0, &rfds);
		tv.tv_usec = 500000;
		maxfd = fd;

		/* select */
		retval = select(maxfd + 1, &rfds, NULL, NULL, &tv);
		if (retval == -1) {
			printf("analyCom2:select is error！ %s", strerror(errno));
			sleep(1);
			exit(1);
        } else if (retval == 0) {
			usleep(100000);
//			printf("analyCom2:time is over 1s\n");
			continue;
		} else {
			if (FD_ISSET(fd, &rfds)) { //com有数据
				memset(Com_Read, 0, sizeof(Com_Read));
				Read_Len = read(fd, Com_Read, sizeof(Com_Read));

				if (0 >= Read_Len)
					continue;
				if (OutputType == 1)
					printf("%s\n", Com_Read);
				else if (OutputType == 2) {
					for (i = 0; i < Read_Len; i++)
						printf("%.2x,", Com_Read[i]);
					printf("\r\n");
				}
			}

			if(FD_ISSET(0, &rfds)) {
				bzero(Com_Send, BUF_LEN + 1);
				fgets(Com_Send, BUF_LEN, stdin);
				SendEnterCommand(fd, Com_Send, strlen(Com_Send));
			}

		}
	}

	close(fd);
}
