package com.library.base.baidu;

/**
 * 百度地图工具类
 * 作用:1-获取坐标
 */

import android.content.Context;
import android.content.IntentFilter;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;

import java.lang.ref.WeakReference;

public class BaiduLocationHelper {

    //地址扫描时间间隔
    private static final int DEFAULT_SCAN_SPAN = 5000;
    private WeakReference<Context> mContext;
    private static BaiduLocationHelper INSTANCE;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private OnLocationChangeListener mChangeListener;
    private boolean mAutoNotify;
    private BaiduCheckReceiver mReceiver;

    private BaiduLocationHelper(Context app) {
        mContext = new WeakReference<>(app);
        mReceiver = new BaiduCheckReceiver();
    }

    public static BaiduLocationHelper getInstance(Context app) {
        if (INSTANCE == null) {
            synchronized (BaiduLocationHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BaiduLocationHelper(app);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化建议放在application中
     */
    public BaiduLocationHelper initialize() {
        SDKInitializer.initialize(mContext.get());
        return this;
    }

    /**
     * 注册 SDK 广播监听者
     */
    public BaiduLocationHelper registerReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE);
        mContext.get().registerReceiver(mReceiver, iFilter);
        return this;
    }

    public BaiduLocationHelper unregisterReceiver() {
        if (mReceiver != null) {
            mContext.get().unregisterReceiver(mReceiver);
        }
        return this;
    }

    /**
     * 开始定位
     * @return
     */
    public BaiduLocationHelper startLocation() {
        configParams();
        mLocationClient.start();
        mLocationClient.requestLocation();
        mLocationClient.registerLocationListener(mLocationListener);
        return this;
    }

    /**
     * 停止定位
     * @return
     */
    public BaiduLocationHelper stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            mChangeListener = null;
            mLocationClient.unRegisterLocationListener(mLocationListener);
            release();
        }
        return this;
    }

    /**
     * 初始化参数
     */
    private void configParams() {
        mLocationClient = new LocationClient(mContext.get());
        mLocationClient.restart();
        mLocationListener = new MyLocationListener();
        LocationClientOption clientOption = new LocationClientOption();
        clientOption.setOpenGps(true);
        if (mAutoNotify) {
            clientOption.setOpenAutoNotifyMode();
        } else {
            clientOption.setScanSpan(DEFAULT_SCAN_SPAN);
        }
        clientOption.setCoorType(CoordinateType.BD09LL);
        clientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(clientOption);
    }

    /**
     * 设置位置变化监听
     * @param listener
     * @return
     */
    public BaiduLocationHelper setChangeListener(OnLocationChangeListener listener) {
        this.mChangeListener = listener;
        return this;
    }

    /**
     * 设置自动监听位置变化
     * @param autoNotify
     * @return
     */
    public BaiduLocationHelper setAutoNotify(boolean autoNotify) {
        this.mAutoNotify = autoNotify;
        return this;
    }

    /**
     * 销毁
     */
    public void release() {
        mLocationClient = null;
        mLocationListener = null;
        mChangeListener = null;
    }

    /**
     * 百度地图位置监听
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (mChangeListener != null) {
                int type = location.getLocType();
                if (BDLocation.TypeNetWorkLocation == type
                        || BDLocation.TypeCacheLocation == type
                        || BDLocation.TypeOffLineLocation == type
                        || BDLocation.TypeGpsLocation == type) {
                    if (mAutoNotify) {//自动通知位置
                        mChangeListener.onLocationChange(INSTANCE, location);
                    } else {
                        //没有自动通知位置，就结束位置监听
                        mChangeListener.onLocationChange(INSTANCE, location);
                        stopLocation();
                    }
                }
            }
        }
    }

    /**
     * 调用定位时要实现此接口处理回调
     */
    public interface OnLocationChangeListener {
        void onLocationChange(BaiduLocationHelper helper, BDLocation location);
    }
}
