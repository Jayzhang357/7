package com.zhd.ProtocolFilterManage;

import java.io.UnsupportedEncodingException;

    public  class BCDDecode
    {
        public static byte[] str2Bcd1(String asc) {
            byte[] a = asc.getBytes();
            byte[] b = new byte[16];
            for (int i = 0; i < a.length; i++)
                b[i] = a[i];
            return b;
        }

        public static String bcd2Str(byte[] bytes)
        {
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)
            {
                temp.append((byte)((bytes[i] & 0xf0) >> 4));
                temp.append((byte)(bytes[i] & 0x0f));
            }
            return temp.toString();
        }


        public static byte[] str2Bcd(String asc) throws UnsupportedEncodingException 
        {  
        int len = asc.length();  
        int mod = len % 2;  
        if (mod != 0) {  
            asc = "0" + asc;  
            len = asc.length();  
        }  
        byte  [] abt = new byte[len];  
        if (len >= 2) {  
            len = len / 2;  
        }  
        byte [] bbt = new byte[len];
        abt =  asc.getBytes("ASCII");
        int j, k;  
        for (int p = 0; p < asc.length() / 2; p++) {  
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
                j = abt[2 * p] - '0';  
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
                j = abt[2 * p] - 'a' + 0x0a;  
            } else {  
                j = abt[2 * p] - 'A' + 0x0a;  
            }  
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
                k = abt[2 * p + 1] - '0';  
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
                k = abt[2 * p + 1] - 'a' + 0x0a;  
            } else {  
                k = abt[2 * p + 1] - 'A' + 0x0a;  
            }  
            int a = (j << 4) + k;  
            byte b = (byte) a;  
            bbt[p] = b;  
        }  
        return bbt;  
    }
    }

