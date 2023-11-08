package com.zhd.entry;

import android.util.Log;

import com.zhd.AppHelper;
import com.zhd.utils.Utils;

import java.util.ArrayList;

public class KZQupdate {
    public byte[] content =new byte[0];



    public byte[] setCommand() {
        ArrayList<Byte> contentlist = new ArrayList();
        contentlist.add((byte)(0x00));
        contentlist.add((byte)(0x00));
        contentlist.add((byte)(0x00));
        contentlist.add((byte)(0x00));
        contentlist.add((byte)(0x00));
        for(int i=0;i<content.length;i++)
        {
            contentlist.add(content[i]);
        }


        ArrayList<Byte> abc = new ArrayList();
        abc.add((byte)(0xEB));
        abc.add((byte)(0x90));
        abc.add((byte)(0xEB));
        abc.add((byte)(0x90));
        byte[] temp=  Utils.Inttobyte(contentlist.size()+2);
        abc.add(temp[0]);
        abc.add(temp[1]);
        for (int i = 0; i < contentlist.size(); i++) {
            abc.add(contentlist.get(i));
        }
        temp=  Utils.getCRC(abc,4,abc.size());
        abc.add(temp[0]);
        abc.add(temp[1]);
        byte[] sum = new byte[abc.size()];
        for (int i = 0; i < abc.size(); i++) {
            sum[i] = abc.get(i);
        }
        String abcd = "";
        for (int i = 0; i < sum.length; i++) {
            abcd +=  String.format("%02X", sum[i]) + " ";
        }
        Log.e("返回字符串",abcd);
        return sum;
    }


}
