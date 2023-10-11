#include <stdlib.h>
#include <jni.h>
#include <string.h>
#include <jni.h>
#include <fcntl.h> /*包括文件操作，如open() read() close() write()等*/
#include <android/log.h>
#include <errno.h>
#define  LOG_TAG    "hello-jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LED_RED   "/sys/class/leds/red/brightness"
#define  LED_BLUE  "/sys/class/leds/blue/brightness"
#define  LED_GREEN "/sys/class/leds/green/brightness"

int fd;
char rt=0;
unsigned char buf_on[]="255\n";
unsigned char buf_off[]="0\n";
char flag0=0;
char flag1=0;
char flag2=0;

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */


jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}
jint 
Java_com_example_hellojni_HelloJni_Init(JNIEnv* env)
{
   LOGE("ledClass_Init() /n");
   LOGE("ledClass_Init()-> fd = %d  /n",fd);
/*   if(fd == -1)
       {
           LOGE("open device %s error /n ",DEVICE_NAME);//打印调试信息
           return 0;
   }
   else
       {
	   	  write(fd,buff,1);
          return 1;
       }*/
}

jint 
Java_com_example_hellojni_HelloJni_IOCTLVIB( JNIEnv*  env, jobject  thiz, jint cmd,jint args)

 {
	if(args>3)
	{	return 0;
	}
    LOGE("IOCTLVIB() cmd= %x --args==%x\n",cmd,args);

    if(args == 0)  //红灯
    {
    	fd = open(LED_RED,O_RDWR);
    	if(fd == -1)
    	{
    	    LOGE("open device error: %s/n ",strerror(errno));
    	    return 0;
    	}

    	if(flag0 == 0)
    	{
    		write(fd,buf_on,4);
    		LOGE("red led on");
    		flag0 = 1;
    	}
    	else
    	{
    		write(fd,buf_off,2);
    		LOGE("red led off");
    		flag0 = 0;
    	}
    	close(fd);
   		return 1;
    }
    else if(args == 1)  //蓝灯
    {
        fd = open(LED_BLUE,O_RDWR);
        if(fd == -1)
        {
        	LOGE("open device error: %s/n ",strerror(errno));
        	return 0;
        }

        if(flag1 == 0)
        {
            write(fd,buf_on,4);
            LOGE("blue led on");
            flag1 = 1;
        }
        else
        {
        	write(fd,buf_off,2);
        	LOGE("blue led off");
        	flag1 = 0;
        }
    	close(fd);
   		return 1;
    }
    else if(args == 2)  //绿灯
    {
        fd = open(LED_GREEN,O_RDWR);
        if(fd == -1)
        {
            LOGE("open device error: %s/n ",strerror(errno));
            return 0;
        }

        if(flag2 == 0)
        {
        	write(fd,buf_on,4);
        	LOGE("green led on");
        	flag2 = 1;
        }
        else
        {
        	 write(fd,buf_off,2);
        	 LOGE("green led off");
        	 flag2 = 0;
        }
        close(fd);
        return 1;
    }

    return 1;
 }
