package com.yksj.consultation.sonDoc.consultation;

/**
 * Created by Administrator on 2016/4/26.
 * Used for
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.yksj.consultation.sonDoc.R;


/**
 * 本实例演示如何在Android中播放网络上的视频，这里牵涉到视频传输协议，视频编解码等知识点
 *
 * @author Administrator
 *         Android当前支持两种协议来传输视频流一种是Http协议，另一种是RTSP协议
 *         Http协议最常用于视频下载等，但是目前还不支持边传输边播放的实时流媒体
 *         同时，在使用Http协议 传输视频时，需要根据不同的网络方式来选择合适的编码方式，
 *         比如对于GPRS网络，其带宽只有20kbps,我们需要使视频流的传输速度在此范围内。
 *         比如，对于GPRS来说，如果多媒体的编码速度是400kbps，那么对于一秒钟的视频来说，就需要20秒的时间。这显然是无法忍受的
 *         Http下载时，在设备上进行缓存，只有当缓存到一定程度时，才能开始播放。
 *         <p/>
 *         所以，在不需要实时播放的场合，我们可以使用Http协议
 *         <p/>
 *         RTSP：Real Time Streaming Protocal，实时流媒体传输控制协议。
 *         使用RTSP时，流媒体的格式需要是RTP。
 *         RTSP和RTP是结合使用的，RTP单独在Android中式无法使用的。
 *         <p/>
 *         RTSP和RTP就是为实时流媒体设计的，支持边传输边播放。
 *         <p/>
 *         同样的对于不同的网络类型（GPRS，3G等），RTSP的编码速度也相差很大。根据实际情况来
 *         <p/>
 *         使用前面介绍的三种方式，都可以播放网络上的视频，唯一不同的就是URI
 *         <p/>
 *         本例中使用VideoView来播放网络上的视频
 */
public class InternetVideoDemo extends Activity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        this.getWindow().setBackgroundDrawableResource(R.color.black);

        setContentView(R.layout.internet_video_aty);
        Uri uri = Uri.parse(getIntent().getStringExtra("url"));





//        Uri uri = Uri.parseDate(Environment.getExternalStorageDirectory().getPathForUri()+"/Test_Movie.m4v");
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Log.v("URI:::::::::", uri.toString());
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);


//        Uri uri = Uri.parseDate("http://192.168.1.113:9090/DuoMeiHealth/HeadDownLoadServlet.do?path=record/recordImg/3783/237233-3.mp4");
//        Uri uri = Uri.parseDate("http://220.194.46.204/dmys/DownLoadfile?path=2.mp4");


//        VideoView videoView = (VideoView) findViewById(R.id.vv_show);
//        videoView.setMediaController(new MediaController(this));
//        videoView.setVideoURI(uri);
//        //videoView.startRecorder();
//        videoView.requestFocus();
//        videoView.startRecorder();

//        WebView mWebView = (WebView) findViewById(R.id.vv_show);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setSupportMultipleWindows(true);
//        mWebView.getSettings().setSupportZoom(true);
//        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        mWebView.requestFocus();
//        if (HStringUtil.isEmpty(url)) {
//            url = ApiService.getmHttpUrls().PUBLICDONATE;
//        }
//        mWebView.loadUrl(uri.toString());

//        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//            Activity activty=this;
//            ActivityCompat.requestPermissions(activty,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    CODE_FOR_WRITE_PERMISSION);
//            return;
//        }


    }


}
