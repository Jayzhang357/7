package com.zhd.shj.qx;


public class CommonEnum {
    public static String getQxSDK(int Sdktype)
    {
        switch(Sdktype)
        {
            case 99:return "断开";
            case 100:return "点击了连接";
            case 101:return "启动成功";
            case 201:return "鉴权成功";
            case -203:return "启动失败,建议重试";
            case -101:return "网络连接不可用,建议重试";
            case -204:return "获取能力信息失败,请联系业务";
            case -207:return "需要手动激活,请联系业务";
            case -208:return "需要在设备端激活,请联系业务";
            case -211:return "系统错误,请联系业务";
            case -212:return "当前账号未包含该功能,请联系业务";
            case -301:return "鉴权失败,建议重试,多次鉴权失败请请联系业务";
            case -302:return "无可用账号,请联系业务";

            case -303:return "需要手动绑定账号,请联系业务";
            case -304:return "账号正在绑定中,无需处理";
            case -305:return "device id与dsk不匹配";
            case -307:return "账号尚未绑定,请联系业务";
            case -308:return "账号已过期,请联系业务";
            case -309:return "账号数不够,请联系业务";
            case -310:return "当前账号不允许此操作,请联系业务";
            case -311:return "账号或秘钥错误,请联系业务";
            case -401:return "调用OPENAPI失败 ,请联系业务";
            case -402:return "OPENAPI响应报⽂错误,请联系业务";
            default:
                break;
        }
        return "";
    }

}
