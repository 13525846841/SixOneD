package com.library.base.utils;

import org.greenrobot.eventbus.EventBus;

public class EventManager {

    public static void register(Object obj) {
        if (!isRegistered(obj)) {
            EventBus.getDefault().register(obj);
        }
    }

    public static void unregister(Object obj) {
        if (isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static boolean isRegistered(Object event) {
        return EventBus.getDefault().isRegistered(event);
    }
}
