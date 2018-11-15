package com.yksj.consultation.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.library.base.utils.BeanCacheHelper;
import com.library.base.utils.EventManager;
import com.yksj.consultation.bean.DoctorInfoBean;
import com.yksj.consultation.event.EDoctorUpdata;
import com.yksj.healthtalk.utils.MD5Utils;

public class DoctorHelper {

    private static DoctorInfoBean mDoctorInfo;

    /**
     * 保存登陆信息
     * @param doctorInfo
     */
    public static void saveLoginInfo(DoctorInfoBean doctorInfo){
        mDoctorInfo = doctorInfo;
        BeanCacheHelper.save(Utils.getApp(), doctorInfo);
        EDoctorUpdata event = new EDoctorUpdata();
        event.doctorInfoBean = mDoctorInfo;
        EventManager.post(event);
    }

    /**
     * 退出
     */
    public static void quit(){
        mDoctorInfo = null;
        BeanCacheHelper.remove(Utils.getApp(), DoctorInfoBean.class);
    }

    /**
     * 获取医生实体类
     * @return
     */
    public static DoctorInfoBean getDoctorInfo(){
        if (mDoctorInfo == null){
            mDoctorInfo = BeanCacheHelper.load(Utils.getApp(), DoctorInfoBean.class);
            return mDoctorInfo;
        }
        return mDoctorInfo;
    }

    /**
     * 获取审核状态
     * @return
     */
    public static String getReviewStatus(){
        if (mDoctorInfo == null){
            return "0";
        }
        return mDoctorInfo.roleId;
    }

    /**
     * 是否有登陆
     * @return
     */
    public static boolean hasLoagin(){
        return getDoctorInfo() != null;
    }

    /**
     * 设置工作站Id
     * @param stationId
     */
    @Deprecated
    public static void setStationId(String stationId){
        DoctorInfoBean doctorInfo = getDoctorInfo();
        doctorInfo.site_ID = stationId;
        BeanCacheHelper.save(Utils.getApp(), doctorInfo);
    }

    /**
     * 根据Id判断是否是自己
     * @param id
     * @return
     */
    public static boolean isSelf(String id){
        return getId().equals(id);
    }

    /**
     * 是否是专家
     * @return
     */
    public static boolean isExpert(){
        String doctorPosition = getDoctorInfo().position;
        return !TextUtils.equals("0", doctorPosition);
    }

    /**
     * 获取账号
     * @return
     */
    public static String getAccount(){
        return getDoctorInfo().customerAccounts;
    }

    /**
     * im tokeng
     * @return
     */
    public static String getNimToken(){
        return getDoctorInfo().token;
    }

    /**
     * 获取姓名
     * @return
     */
    public static String getNickName(){
        return getDoctorInfo().doctorRealName;
    }

    /**
     * 获取医生Id
     * @return
     */
    public static String getId(){
        return String.valueOf(getDoctorInfo().customerId);
    }

    /**
     * 获取MD5加密Id
     * @return
     */
    public static String getMD5Id(){
        return MD5Utils.getMD5(getId());
    }

    /**
     * 首页医生订单提示
     * @return
     */
    public static String getHomeMsgType(){
        String type;
        if (DoctorHelper.isExpert()) {
            type = "homePageInfoExpert";
        } else {
            type = "homePageInfoAssi";
        }
        return type;
    }
}
