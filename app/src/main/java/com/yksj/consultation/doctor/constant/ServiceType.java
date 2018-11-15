package com.yksj.consultation.doctor.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hww on 17/9/12.
 * Used for  医生工作室服务类型
 * 5 图文 6 电话 7 包月  8 视频
 */
public class ServiceType {
    public static final String LY = "1";//留言咨询
    public static final String YY = "2";//预约咨询
    public static final String MZ = "3";//门诊预约
    public static final String DZ = "4";//定制服务
    public static final String TW = "5";//图文咨询
    public static final String DH = "6";//电话咨询
    public static final String BY = "7";//包月咨询
    public static final String SP = "8";//视频咨询
    public static final String TY = "9";//体验咨询

    @StringDef({LY, YY, MZ, DZ, TW, DH, BY, SP, TY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{
    }

    public static String toString(@Type String type){
        if (type.equals(LY)){
            return "留言咨询";
        }
        if (type.equals(YY)){
            return "预约咨询";
        }
        if (type.equals(MZ)){
            return "门诊预约";
        }
        if (type.equals(DZ)){
            return "定制服务";
        }
        if (type.equals(TW)){
            return "图文咨询";
        }
        if (type.equals(DH)){
            return "电话咨询";
        }
        if (type.equals(BY)){
            return "包月咨询";
        }
        if (type.equals(SP)){
            return "视频咨询";
        }
        if (type.equals(TY)){
            return "体验咨询";
        }
        return "";
    }
}
