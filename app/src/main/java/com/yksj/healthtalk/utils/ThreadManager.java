package com.yksj.healthtalk.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理线程池
 *
 * @author itcast
 */
public class ThreadManager {
    private ThreadManager() {

    }

    private static ThreadManager instance = new ThreadManager();
    private ThreadPoolProxy socketPool;
    private ThreadPoolProxy shortPool;
    private ThreadPoolProxy chatPool;

    public static ThreadManager getInstance() {
        return instance;
    }


    private static final int count = Runtime.getRuntime().availableProcessors() * 3 + 2;
    // 联网比较耗时
    // cpu的核数*2+1
    public synchronized ThreadPoolProxy createLongPool() {
        if (socketPool == null) {
            socketPool = new ThreadPoolProxy(5, 15, 5000L);
        }
        return socketPool;
    }

    // 操作本地文件
    public synchronized ThreadPoolProxy createShortPool() {
        if (shortPool == null) {
            shortPool = new ThreadPoolProxy(3, 10, 5000L);
        }
        return shortPool;
    }  // 操作聊天列表
    public synchronized ThreadPoolProxy createChatListPool() {
        if (chatPool == null) {
            chatPool = new ThreadPoolProxy(5, 10, 5000L);
        }
        return chatPool;
    }

    public class ThreadPoolProxy {
        private ThreadPoolExecutor pool;
        private int corePoolSize;
        private int maximumPoolSize;
        private long time;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.time = time;

        }

        /**
         * 执行任务
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (pool == null) {
                // 创建线程池
                /*
                 * 1. 线程池里面管理多少个线程2. 如果排队满了, 额外的开的线程数3. 如果线程池没有要执行的任务 存活多久4.
				 * 时间的单位 5 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到LinkedBlockingQueue对象中 排队
				 */
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                        time, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(10));
            }
            pool.execute(runnable); // 调用线程池 执行异步任务
        }

        /**
         * 取消任务
         *
         * @param runnable
         */
        public void cancel(Runnable runnable) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(runnable); // 取消异步任务
            }
        }
    }

    /**
     * 所有线程挂起
     */
    public void shutDown() {
        if (socketPool != null) socketPool.pool.shutdown();
        if (shortPool != null) shortPool.pool.shutdown();
        if (chatPool != null) chatPool.pool.shutdown();

    }

}