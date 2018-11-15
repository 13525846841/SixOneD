package com.yksj.healthtalk.utils.sun;

import java.net.URL;

/**
 * Created by hww on 17/9/27.
 * Used for
 */

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
    DefaultProgressMeteringPolicy() {
    }

    public boolean shouldMeterInput(URL var1, String var2) {
        return false;
    }

    public int getProgressUpdateThreshold() {
        return 8192;
    }
}