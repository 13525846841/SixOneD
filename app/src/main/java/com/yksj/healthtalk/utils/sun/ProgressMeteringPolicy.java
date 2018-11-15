package com.yksj.healthtalk.utils.sun;

import java.net.URL;

/**
 * Created by hww on 17/9/27.
 * Used for
 */

public interface ProgressMeteringPolicy {
    boolean shouldMeterInput(URL var1, String var2);

    int getProgressUpdateThreshold();
}