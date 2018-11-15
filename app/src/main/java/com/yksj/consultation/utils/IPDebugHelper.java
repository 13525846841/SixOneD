package com.yksj.consultation.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;

import com.library.base.dialog.SelectorDialog;
import com.yksj.consultation.app.AppContext;

/**
 * 服务器调试IP帮助类
 */
public class IPDebugHelper implements SensorEventListener {
    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 5000;
    private static final int UPTATE_INTERVAL_TIME = 42;
    private Context context;
    private FragmentActivity activity;
    private long lastUpdateTime;
    private float lastX, lastY, lastZ;
    private SensorManager sm;
    private Sensor sensor;
    private SelectorDialog ipChooseDialog;

    private static IPDebugHelper INSTANCE = null;
    private void IPDebugHelper(){}
    public static IPDebugHelper getInstance(){
        if (INSTANCE == null) {
            synchronized (IPDebugHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new IPDebugHelper();
                }
            }
        }
        return INSTANCE;
    }

    public IPDebugHelper() {
    }

    public void start(Context context){
        initialaze(context);
        unregister();
        register();
    }

    private void initialaze(Context context) {
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (activity == null){
            return;
        }
    }

    private void register() {
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregister() {
        sm.unregisterListener(this, sensor);
    }

    public void release(){
        unregister();
        sensor = null;
        activity = null;
        context = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME) return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        if (speed > SPEED_SHRESHOLD) {
            showIpDialog();
        }
    }

    private void showIpDialog() {
        if (ipChooseDialog == null){
            String[] urls = new String[]{"http://192.168.1.161:8080", "http://220.194.46.204:80"};
            ipChooseDialog = SelectorDialog.newInstance(urls);
            ipChooseDialog.setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                @Override
                public void onItemClick(SelectorDialog dialog, int position) {
                    AppContext.getApiRepository().setupWebRoot(urls[position]);
                }
            });
        }
        if (!ipChooseDialog.isAdded()) {
            ipChooseDialog.show(activity.getSupportFragmentManager());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
