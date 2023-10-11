
package com.zhd.ProtocolFilterManage;

import java.util.ArrayList;

    /// <summary>
    /// 娑堟伅鐨勫熀绫�
    /// </summary>
    public class ProtocolMessage31
    {
        public byte id;
        public byte[] mProtocolContent;

        /// <summary>
        /// 鏋勯�犱竴涓秷鎭�
        /// </summary>
        /// <param name="hd"></param>
        /// <param name="protocolContent"></param>
        public ProtocolMessage31(byte mid, byte[] protocolContent)
        {
        	id = mid;
            mProtocolContent = protocolContent;
        }
   
     

    }

