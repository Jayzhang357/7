package com.zhd.TCPSocketClientBase;

import java.util.Date;

public class TimeExchangeBetweenJavaAndDotNet
{


    public static long convertDotNetLongTimeToJavaLongTime(long dotNet_ticks)
    {

        long spanTicks = 621356256000000000l;
        long java_Ticks = (dotNet_ticks - spanTicks) / 10000;
        return java_Ticks;
    }


    public static long convertJavaLongTimeToDotNetLongTime(long java_ticks)
    {
         long spanTicks = 621356256000000000l;
        long dotNet_ticks = java_ticks * 10000 + spanTicks;
        return dotNet_ticks;
    }

    public static long convertJavaTimeToDotNetLongTime(Date java_time)
    {
        return convertJavaLongTimeToDotNetLongTime(java_time.getTime());
    }


    public static Date convertDotNetLongTimeToJavaTime(long dotNet_ticks)
    {
        return new Date(convertDotNetLongTimeToJavaLongTime(dotNet_ticks));
    }
}
