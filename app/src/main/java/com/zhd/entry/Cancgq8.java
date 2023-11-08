package com.zhd.entry;

public class Cancgq8 {
    public  String contentsum;
    public int leix;
    public int a,b,c,d;

    public Cancgq8 (String contentsum)
    {
       leix= Integer.parseInt(contentsum.substring(2, 4), 16);
        a= Integer.parseInt(contentsum.substring(4, 6), 16);
        b= Integer.parseInt(contentsum.substring(6, 8), 16);
        c= Integer.parseInt(contentsum.substring(8, 12), 16);
        d= Integer.parseInt(contentsum.substring(12, 16), 16);
    }


}
