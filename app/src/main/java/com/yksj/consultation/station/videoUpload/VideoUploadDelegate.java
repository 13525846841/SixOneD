package com.yksj.consultation.station.videoUpload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.library.base.utils.ResourceHelper;
import com.netease.cloud.nos.android.core.CallRet;
import com.netease.vcloudnosupload.NOSUpload;
import com.netease.vcloudnosupload.NOSUploadHandler;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;

/**
 * 网易云点播视屏上传事物类
 */
public class VideoUploadDelegate {

    private Map<String, NOSUpload.UploadExecutor> excuters = new HashMap<>();

    private static VideoUploadDelegate INSTANCE = null;

    private void VideoUploadHelper() {
    }

    public static VideoUploadDelegate getInstance() {
        if (INSTANCE == null) {
            synchronized (VideoUploadDelegate.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VideoUploadDelegate();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 上传
     * @param context
     * @param uploadPath
     * @param callback
     */
    @SuppressLint("CheckResult")
    public void upload(Context context, String uploadPath, OnVideoUploadCallback callback) {
        File uploadFile = new File(uploadPath);
        String userId = DoctorHelper.getId();
        Observable.just(uploadFile)
                .flatMap(file -> buildSpace(file, userId))
                .flatMap(space -> requestToken(space))
                .flatMap(videoSpace -> uploadInit(context, videoSpace))
                .flatMap(videoSpace -> uploadVideo(context, videoSpace))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> callback.onStart())
                .subscribe(videoSpace -> {
                    switch (videoSpace.next) {
                        case VideoSpace.CANCEL_NEXT:
                            callback.onCancel();
                            break;
                        case VideoSpace.PROGRESS_NEXT:
                            callback.onProgress(videoSpace.current, videoSpace.total);
                            break;
                        case VideoSpace.COMPLETE_NEXT:
                            callback.onComplete(videoSpace.bucket, videoSpace.object,
                                    String.format("http://jdvodocaoh4ht.vod.126.net/%s/%s", videoSpace.bucket, videoSpace.object));
                            break;
                    }
                }, throwable -> {
                    callback.onError(throwable.getMessage());
                });
    }

    /**
     * 删除
     * @param context
     * @param videoId
     */
    public void delet(Context context, String videoId) {

    }

    /**
     * 查询
     * @param videoTag 存储TAG
     */
    @Deprecated
    @SuppressLint("CheckResult")
    public void query(String videoTag, OnVideoQueryCallback callback) {
        VideoSpace videoSpace = new VideoSpace();
        videoSpace.accid = DoctorHelper.getId();
        requestToken(videoSpace)
                .map(space -> formBodyToJson(space, Arrays.asList(videoTag)))
                .flatMap(space -> requestId(space))
                .doOnSubscribe(disposable -> callback.onStart())
                .subscribe(space -> callback.onComplete(space.vid)
                        , throwable -> callback.onError(throwable));
    }

    /**
     * 将视频tag转换成json数据
     * @param space
     * @param tags
     * @return
     */
    private VideoSpace formBodyToJson(VideoSpace space, List<String> tags){
        JSONArray jsonArray = new JSONArray(tags);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("objectNames", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        space.queryBody = jsonObject.toString();
        return space;
    }

    /**
     * 获取视频ID
     * @param space
     * @return
     */
    private Observable<VideoSpace> requestId(VideoSpace space){
        return Observable.create(new ObservableOnSubscribe<VideoSpace>() {
            @Override
            public void subscribe(ObservableEmitter<VideoSpace> emitter) throws Exception {
                ApiService.lectureQueryVideo(space.appKey, space.accid, space.token, space.queryBody, new ApiCallbackWrapper<VideoQueryBean>() {
                    @Override
                    public void onResponse(VideoQueryBean response) {
                        super.onResponse(response);
                        if (response.isSucees()) {
                            String vid = response.ret.list.get(0).vid;
                            space.vid = vid;
                            emitter.onNext(space);
                        }else{
                            emitter.onError(new IllegalStateException());
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * 请求视频上传Token
     * @param videoSpace
     * @return
     */
    private Observable<VideoSpace> requestToken(VideoSpace videoSpace) {
        return Observable.create(new ObservableOnSubscribe<VideoSpace>() {
            @Override
            public void subscribe(ObservableEmitter<VideoSpace> emitter) throws Exception {
                ApiService.lectureToken(videoSpace.accid, new ApiCallbackWrapper<ResponseBean<String>>() {
                    @Override
                    public void onResponse(ResponseBean<String> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            videoSpace.token = response.result;
                            emitter.onNext(videoSpace);
                        } else {
                            emitter.onError(new IllegalArgumentException("服务器返回Token失败"));
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        emitter.onError(e);
                    }
                });
            }
        });
    }

    /**
     * 取消上传
     * @param uploadFile
     */
    public void cancel(String uploadFile) {
        if (excuters.containsKey(uploadFile)) {
            excuters.get(uploadFile).cancel();
        }
    }

    /**
     * 构建上传参数
     * @param file
     * @param accid
     * @return
     */
    @NonNull
    private Observable<VideoSpace> buildSpace(File file, String accid) {
        return Observable.create(new ObservableOnSubscribe<VideoSpace>() {
            @Override
            public void subscribe(ObservableEmitter<VideoSpace> emitter) throws Exception {
                VideoSpace videoSpace = new VideoSpace();
                videoSpace.uploadFile = file;
                videoSpace.fileName = file.getName();
                videoSpace.accid = accid;
                videoSpace.appKey = getAppKey();
                emitter.onNext(videoSpace);
            }
        });
    }

    /**
     * 上传视频
     * @param context
     * @return
     */
    @NonNull
    private Observable<VideoSpace> uploadVideo(Context context, VideoSpace videoSpace) {
        return Observable.create(new ObservableOnSubscribe<VideoSpace>() {
            @Override
            public void subscribe(ObservableEmitter<VideoSpace> emitter) throws Exception {
                NOSUpload nosUpload = getUploadInstance(context, videoSpace);
                File uploadFile = videoSpace.uploadFile;
                String uploadContext = nosUpload.getUploadContext(uploadFile);
                NOSUpload.UploadExecutor executor = nosUpload.putFileByHttp(uploadFile, uploadContext, videoSpace.bucket, videoSpace.object, videoSpace.nosToken, new NOSUploadHandler.UploadCallback() {
                    @Override
                    public void onUploadContextCreate(String oldUploadContext, String newUploadContext) {
                        nosUpload.setUploadContext(uploadFile, newUploadContext);
                        videoSpace.next = VideoSpace.START_NEXT;
                        emitter.onNext(videoSpace);
                    }

                    @Override
                    public void onProcess(long current, long total) {
                        videoSpace.total = total;
                        videoSpace.current = current;
                        videoSpace.next = VideoSpace.PROGRESS_NEXT;
                        emitter.onNext(videoSpace);
                    }

                    @Override
                    public void onSuccess(CallRet callRet) {
                        nosUpload.setUploadContext(uploadFile, "");
                        videoSpace.next = VideoSpace.COMPLETE_NEXT;
                        emitter.onNext(videoSpace);
                        excuters.remove(uploadFile.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(CallRet callRet) {
                        videoSpace.next = VideoSpace.ERROR_NEXT;
                        Exception exception = callRet.getException();
                        emitter.onError(exception);
                        excuters.remove(uploadFile.getAbsolutePath());
                    }

                    @Override
                    public void onCanceled(CallRet callRet) {
                        videoSpace.next = VideoSpace.CANCEL_NEXT;
                        emitter.onNext(videoSpace);
                        excuters.remove(uploadFile.getAbsolutePath());
                    }
                });
                executor.join();
                excuters.put(uploadFile.getAbsolutePath(), executor);
            }
        });
    }

    /**
     * 初始化视频上传
     * @param context
     * @return
     */
    @NonNull
    private Observable<VideoSpace> uploadInit(Context context, VideoSpace videoSpace) {
        return Observable.create(new ObservableOnSubscribe<VideoSpace>() {
            @Override
            public void subscribe(ObservableEmitter<VideoSpace> emitter) throws Exception {
                NOSUpload nosUpload = getUploadInstance(context, videoSpace);
                String fileName = videoSpace.fileName;
                nosUpload.fileUploadInit(fileName, null, -1, -1, null, null, -1, null, new NOSUploadHandler.UploadInitCallback() {
                    @Override
                    public void onSuccess(String nosToken, String bucket, String object) {
                        videoSpace.bucket = bucket;
                        videoSpace.nosToken = nosToken;
                        videoSpace.object = object;
                        emitter.onNext(videoSpace);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Exception exception = new Exception(code + "=" + msg);
                        emitter.onError(exception);
                    }
                });
            }
        });
    }

    private NOSUpload getUploadInstance(Context context, VideoSpace videoSpace) {
        NOSUpload nosUpload = NOSUpload.getInstace(context);
        NOSUpload.Config config = buildConfig(videoSpace);
        nosUpload.setConfig(config);
        return nosUpload;
    }

    private NOSUpload.Config buildConfig(VideoSpace videoSpace) {
        NOSUpload.Config config = new NOSUpload.Config();
        config.appKey = videoSpace.appKey;
        config.accid = videoSpace.accid;
        config.token = videoSpace.token;
        return config;
    }

    private String getAppKey() {
        return ResourceHelper.getString(R.string.nim_appkey);
    }
}
