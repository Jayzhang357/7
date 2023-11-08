package com.zhd.shj.sqlite;

/**
 * 会员信息的javabean
 * @author bixiaopeng 2013-2-16 下午3:07:02
 */
public class MemberInfo {

    public int    _id;
    public String name;

    public String jobtype;
    public String Cover;
    public String SetH;
    public String Sensitivity;
    public String Width;
    public MemberInfo(){}
    public MemberInfo(int _id,String name,String jobtype, String Cover, String SetH,String Sensitivity){
        this._id = _id;
        this.name = name;
        this.jobtype = jobtype;
        this.Cover = Cover;
        this.SetH = SetH;
        this.Width = Width;

        this.Sensitivity = Sensitivity;
        
    }

}
