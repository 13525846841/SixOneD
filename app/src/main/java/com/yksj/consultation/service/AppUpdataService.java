package com.yksj.consultation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.library.base.utils.StorageUtils;
import com.yksj.consultation.bean.AppUpdataInfoBean;
import com.yksj.healthtalk.net.http.ApiConnection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * APK更新Service
 */
public class AppUpdataService extends Service {

    private static final String UPDATA_INFO = "updata_info";
    private String mDownloadUrl;
    private OkHttpClient mHttpClient;
    private File mSaveFile;
    private String mVersionCode;
    private AppUpdataInfoBean mUpdataInfo;
    private Runnable mDownloadRun = new Runnable() {
        @Override public void run() {
            try {
                Request request = buildRequest(mDownloadUrl);
                Response response = mHttpClient.newCall(request).execute();
                InputStream is = response.body().byteStream();
                saveDownloadApk(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static Intent getCallingIntent(Context context, AppUpdataInfoBean infoBean) {
        Intent intent = new Intent(context, AppUpdataService.class);
        intent.putExtra(UPDATA_INFO, infoBean);
        return intent;
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();
        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConnection.DEFAULT_CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mUpdataInfo = intent.getParcelableExtra(UPDATA_INFO);
        mDownloadUrl = mUpdataInfo.downloadUrl;
        mVersionCode = mUpdataInfo.version;
        if (TextUtils.isEmpty(mDownloadUrl)) {
            stopSelf();
            return;
        }
        String saveName = EncryptUtils.encryptMD5ToString(String.format("lyjk-%s", mVersionCode)) + ".apk";
        mSaveFile = new File(StorageUtils.getDownCachePath(), saveName);
        if (mSaveFile.exists() && mSaveFile.length() == mUpdataInfo.length) {
            installApk();
            stopSelf();
            return;
        }

        new Thread(mDownloadRun).start();
    }

    /**
     * 构建下载
     * @param url
     * @return
     */
    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    /**
     * 安装APK
     */
    private void installApk() {
        if (mUpdataInfo.isNowInstall) {
            Intent intent = IntentUtils.getInstallAppIntent(mSaveFile, true);
            startActivity(intent);
            stopSelf();
        }
    }

    /**
     * 保存下载的APK文件
     * @param is
     */
    private void saveDownloadApk(InputStream is) {
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(mSaveFile));
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            installApk();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public void onDestroy() {
        mHttpClient = null;
        super.onDestroy();
    }
}
