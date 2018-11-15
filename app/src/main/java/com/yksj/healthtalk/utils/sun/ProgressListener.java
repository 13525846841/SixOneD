package com.yksj.healthtalk.utils.sun;

import com.yksj.healthtalk.utils.EventListener;

/**
 * Created by hww on 17/9/27.
 * Used for
 */

public interface ProgressListener extends EventListener {
    void progressStart(ProgressEvent var1);

    void progressUpdate(ProgressEvent var1);

    void progressFinish(ProgressEvent var1);
}