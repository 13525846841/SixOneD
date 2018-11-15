package com.yksj.consultation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;

import com.library.base.event.EExitApp;
import com.library.base.utils.EventManager;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.service.CoreService;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.CustomerInfoHttpResponseHandler;
import com.yksj.healthtalk.utils.PersonInfoUtil;

public class ActivityHelper {

    /**
     * 启动系统网页
     * @param activity
     * @param url
     */
    public static void startWebView(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    /**
     * 强制更新
     */
    public static void forceUpdateApp(Context context, String url) {
        context.stopService(new Intent(context, CoreService.class));
        EventManager.post(new EExitApp());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        try {
            Thread.sleep(800l);
        } catch (Exception e) {
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 下载容联七陌app
     */
    public static void downLoadApp(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到客户资料
     * @param activity
     * @param manager
     * @param id
     */
    public static void startUserInfoActivity(Activity activity, FragmentManager manager, String id) {
        CustomerInfoEntity entity = (CustomerInfoEntity) AppContext.getAppData().cacheInformation.get(id);
        //内存中查找
        if (entity != null) {
            PersonInfoUtil.choiceActivity(entity.getId(), activity, String.valueOf(entity.getRoldid()));
        } else {
            //网络请求
            ApiService.doHttpFindCustomerInfoByCustId(null, null, id, null, new CustomerInfoHttpResponseHandler(activity, manager));
        }
    }
}
