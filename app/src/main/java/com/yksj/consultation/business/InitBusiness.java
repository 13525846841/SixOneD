package com.yksj.consultation.business;

import android.app.Application;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.Utils;
import com.library.base.baidu.BaiduLocationHelper;
import com.library.base.imageLoader.GlideLoader;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.umeng.UmengShare;
import com.library.base.utils.ResourceHelper;
import com.library.base.widget.SimpleRefreshFooter;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.yksj.consultation.im.NIMManager;
import com.yksj.consultation.sonDoc.BuildConfig;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.IPDebugHelper;
import com.yksj.healthtalk.utils.AppCashHandler;

/**
 * 初始化业务
 */
public class InitBusiness {

    public void init(Application app) {
        //初始化网易音视频
        NIMManager.init(app);
        //分享初始化
        UmengShare.init(app);
        //初始化工具库
        Utils.init(app);
        //异常上报
//        initBugly(app);
        // 图片加载
        ImageLoader.setLoaderStrategy(new GlideLoader());
        // 初始化刷新Layout
        initRefreshLayout();
        // 初始化调试工具
        initTools(app);
        // 初始化ip选择
        initIpSelect(app);

        initBDMap(app);

        if (ProcessUtils.isMainProcess()) {//是否是主线程
            AppCashHandler.getInstance().init(app);
            //上传异常日志
            if (NetworkUtils.isWifiConnected()) {
                AppCashHandler.getInstance().sendLogToServer();
            }
        }
    }

    /**
     * 初始化ip选择
     * @param app
     */
    private void initIpSelect(Application app) {
        if (BuildConfig.DEBUG) {// debug模式开启IP调试
            IPDebugHelper.getInstance().start(app);
        }
    }

    /**
     * 刷新初始化
     */
    private void initRefreshLayout() {
        // 刷新样式
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            SimpleRefreshHeader refreshHeader = new SimpleRefreshHeader(context);
            return refreshHeader;
        });
        // 加载样式
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            SimpleRefreshFooter refreshFooter = new SimpleRefreshFooter(context);
            return refreshFooter;
        });
    }

    /**
     * leakcanary内存泄漏检测
     * @param app
     */
    private void initLeakCanary(Application app) {
        if (!LeakCanary.isInAnalyzerProcess(app)) {
            LeakCanary.install(app);
        }
    }

    /**
     * 初始化调试工具
     * @param application
     */
    private void initTools(Application application) {
        initLeakCanary(application);
        // 是否是在调试模式
        if (!BuildConfig.DEBUG) {
            return;
        }
    }

    /**
     * 初始化百度地图
     */
    private void initBDMap(Application application) {
        BaiduLocationHelper
                .getInstance(application)
                .initialize()
                .registerReceiver();

    }

    /**
     * 初始化腾讯Bugly异常上报
     * @param app
     */
    private void initBugly(Application app) {
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(app);
        strategy.setUploadProcess(true);
        // 初始化Bugly
        CrashReport.initCrashReport(app, ResourceHelper.getString(R.string.bugly), true, strategy);
    }
}
