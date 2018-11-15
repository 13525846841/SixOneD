package com.library.base.baidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.LogUtils;

/**
 * 百度地图
 * 监听 SDK key 验证以及网络异常广播
 */
public class BaiduCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getAction();
        String message = "";
        if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
            message = "百度地图 验证出错! 错误码 :" + intent.getIntExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0) + " ; 请在 AndroidManifest.xml 文件中检查 key 设置";
        } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
            message = "百度地图 验证成功! 功能可以正常使用";
        } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
            message = "网络出错";
        }
        LogUtils.i(message);
    }
}
