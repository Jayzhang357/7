package com.zhd.BussinessMessage;
import com.zhd.ProtocolFilterManage.*;
/// <summary>
    /// 应用层消息包的基类
    /// </summary>
    class GeneralBMessage
    {
        protected int m_MessageID;
        //有可能当前数据是由多分包数据构成
        protected ProtocolMessage[] m_FragmentMessages;

        public int getMessageID()
        {
             return m_MessageID; 
        }

        public GeneralBMessage()
        {
            
        }

        /// <summary>
        ///  子类重载该方法，针对消息作具体的处理，主要完成的是对protocolContent的反序列化处理
        /// </summary>
   
        public  void Resolve(ProtocolMessage[] fragmentMessages)
        { 
        
        }

        /// <summary>
        /// 将本对象序列化成消息包对象
        /// </summary>
        /// <returns></returns>
     
        public  ProtocolMessage[] ToProtocolMessage()
        {
            return m_FragmentMessages;
        }
    }

