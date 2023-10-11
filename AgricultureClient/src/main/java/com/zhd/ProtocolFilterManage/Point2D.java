package com.zhd.ProtocolFilterManage;


    public class Point2D
    {
    	private  int mLatitude;   //纬度
    	private int mLongitude;   //经度

        public int getLatitude()
        {
            return mLatitude;
        }

        public void setLatitude(int value)
        {
        	 mLatitude = value;
        }
        public int getLongitude()
        {
            return mLongitude;
        }
        
        public void setLongitude(int value)
        {
           mLongitude = value; 
        }
        
        public  byte [] ToByte()
        {
        	Exchange ex = new Exchange();
        	ex.AddIntAsBytes(mLatitude);
        	ex.AddIntAsBytes(mLongitude);
        	return ex.GetAllBytes();
        }
    }

