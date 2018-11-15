package com.yksj.healthtalk.manager;


import com.orhanobut.logger.Logger;
import com.yksj.healthtalk.net.socket.IMManager;
import com.yksj.healthtalk.net.socket.SmartControlClient;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 心跳处理
 *
 * @author jack_tang
 */
public class HeartServiceManager extends IMManager {
    private final String TAG = this.getClass().getSimpleName();
    SocketManager mSocketManager = SocketManager.init();

    /**
     * 私有构造方法，防止被实例化
     */
    private HeartServiceManager() {

    }

    /**
     * 使用内部类维护单例
     */
    private static class HeartServiceManagerFactory {
        private static HeartServiceManager instance = new HeartServiceManager();
    }

    public static HeartServiceManager getInstance() {
        return HeartServiceManagerFactory.instance;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return getInstance();
    }


    @Override
    public void doOnStart() {
        Logger.d(TAG, "----------doOnStart");
    }

    @Override
    public void reset() {
        Logger.d(TAG, "----------reset");
    }


    public void sendHeartMsg() {
        if (mSocketManager.isConnected()) {
            mSocketManager.sendSocketParams(null, SmartControlClient.SOCKET_HEART_CODE);
        } else {
            Observable.just(1)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            mSocketManager.connect();
                        }
                    });
        }
    }
}
