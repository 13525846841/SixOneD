package com.yksj.healthtalk.utils.sun;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by hww on 17/9/27.
 * Used for
 */

public class ProgressMonitor {
    private static ProgressMeteringPolicy meteringPolicy = new DefaultProgressMeteringPolicy();
    private static ProgressMonitor pm = new ProgressMonitor();
    private ArrayList<ProgressSource> progressSourceList = new ArrayList();
    private ArrayList<ProgressListener> progressListenerList = new ArrayList();

    public ProgressMonitor() {
    }

    public static synchronized ProgressMonitor getDefault() {
        return pm;
    }

    public static synchronized void setDefault(ProgressMonitor var0) {
        if(var0 != null) {
            pm = var0;
        }

    }

    public static synchronized void setMeteringPolicy(ProgressMeteringPolicy var0) {
        if(var0 != null) {
            meteringPolicy = var0;
        }

    }

    public ArrayList<ProgressSource> getProgressSources() {
        ArrayList var1 = new ArrayList();

        try {
            ArrayList var2 = this.progressSourceList;
            synchronized(this.progressSourceList) {
                Iterator var3 = this.progressSourceList.iterator();

                while(var3.hasNext()) {
                    ProgressSource var4 = (ProgressSource)var3.next();
                    var1.add((ProgressSource)var4.clone());
                }
            }
        } catch (CloneNotSupportedException var7) {
            var7.printStackTrace();
        }

        return var1;
    }

    public synchronized int getProgressUpdateThreshold() {
        return meteringPolicy.getProgressUpdateThreshold();
    }

    public boolean shouldMeterInput(URL var1, String var2) {
        return meteringPolicy.shouldMeterInput(var1, var2);
    }

    public void registerSource(ProgressSource var1) {
        ArrayList var2 = this.progressSourceList;
        synchronized(this.progressSourceList) {
            if(this.progressSourceList.contains(var1)) {
                return;
            }

            this.progressSourceList.add(var1);
        }

        if(this.progressListenerList.size() > 0) {
            var2 = new ArrayList();
            ArrayList var3 = this.progressListenerList;
            synchronized(this.progressListenerList) {
                Iterator var4 = this.progressListenerList.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    var2.add(var4.next());
                }
            }

            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
                ProgressListener var9 = (ProgressListener)var8.next();
                ProgressEvent var5 = new ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
                var9.progressStart(var5);
            }
        }

    }

    public void unregisterSource(ProgressSource var1) {
        ArrayList var2 = this.progressSourceList;
        synchronized(this.progressSourceList) {
            if(!this.progressSourceList.contains(var1)) {
                return;
            }

            var1.close();
            this.progressSourceList.remove(var1);
        }

        if(this.progressListenerList.size() > 0) {
            var2 = new ArrayList();
            ArrayList var3 = this.progressListenerList;
            synchronized(this.progressListenerList) {
                Iterator var4 = this.progressListenerList.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    var2.add(var4.next());
                }
            }

            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
                ProgressListener var9 = (ProgressListener)var8.next();
                ProgressEvent var5 = new ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
                var9.progressFinish(var5);
            }
        }

    }

    public void updateProgress(ProgressSource var1) {
        ArrayList var2 = this.progressSourceList;
        synchronized(this.progressSourceList) {
            if(!this.progressSourceList.contains(var1)) {
                return;
            }
        }

        if(this.progressListenerList.size() > 0) {
            var2 = new ArrayList();
            ArrayList var3 = this.progressListenerList;
            synchronized(this.progressListenerList) {
                Iterator var4 = this.progressListenerList.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    var2.add(var4.next());
                }
            }

            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
                ProgressListener var9 = (ProgressListener)var8.next();
                ProgressEvent var5 = new ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
                var9.progressUpdate(var5);
            }
        }

    }

    public void addProgressListener(ProgressListener var1) {
        ArrayList var2 = this.progressListenerList;
        synchronized(this.progressListenerList) {
            this.progressListenerList.add(var1);
        }
    }

    public void removeProgressListener(ProgressListener var1) {
        ArrayList var2 = this.progressListenerList;
        synchronized(this.progressListenerList) {
            this.progressListenerList.remove(var1);
        }
    }
}