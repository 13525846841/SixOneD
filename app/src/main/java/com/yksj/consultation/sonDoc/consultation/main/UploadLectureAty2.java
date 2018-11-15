package com.yksj.consultation.sonDoc.consultation.main;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.FileType;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.LoadingProgressFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.StationUploadLectureChoiceActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.filemanager.FileUtils;
import com.yksj.healthtalk.net.http.ApiConnection;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.Mp4ParseUtil;
import com.yksj.healthtalk.utils.ThreadManager;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WeakHandler;
import com.yksj.healthtalk.views.progress.NumberProgressBar;
import com.yksj.healthtalk.views.progress.OnProgressBarListener;

import org.json.JSONException;
import org.json.JSONObject;
import com.library.base.utils.StorageUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 健康讲堂首页
 */
public class UploadLectureAty2 extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    ImageView imageUpload;
    private CheckBox checkBoxW, checkBoxN;
    private EditText editName, editPriceW, editPriceN, editIntrodunce;
    private TextView fileName, textProgress;
    private NumberProgressBar progressBar, progressBar2;
    String name = "";//课件名称
    String priceW = "";//课件站外价格
    String priceN = "";//课件站内价格
    String introduce = "";//简介
    String siteId = "";//工作站ID
    private static final String SITE_ID = "site_id";

    public static final int GET_FILE = 1100;
    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1://小视频
                    progressBar.setProgress((Integer) msg.obj);
                    break;
                case 2://
                    if (mLoadDialog.isShowing()) {
                        mLoadDialog.dismissAllowingStateLoss();
                    }
                    ToastUtil.showShort((String) msg.obj);
                    deleteCache();
                    break;
                case 3://大视频
                    progressBar2.setProgress((Integer) msg.obj);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_main2);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("六一班");
        titleLeftBtn.setOnClickListener(this);
        imageUpload = (ImageView) findViewById(R.id.image_upload);
        checkBoxW = (CheckBox) findViewById(R.id.check1);
        checkBoxN = (CheckBox) findViewById(R.id.check2);
        editName = (EditText) findViewById(R.id.et_name);
        editPriceW = (EditText) findViewById(R.id.et_price_w);
        editPriceN = (EditText) findViewById(R.id.et_price_n);
        editIntrodunce = (EditText) findViewById(R.id.et_introduce);
        progressBar = (NumberProgressBar) findViewById(R.id.loadingProgress);
        progressBar2 = (NumberProgressBar) findViewById(R.id.loadingProgress2);
        fileName = (TextView) findViewById(R.id.file_name);
        textProgress = (TextView) findViewById(R.id.tv_video);
        imageUpload.setOnClickListener(this);
        findViewById(R.id.lecture_upload).setOnClickListener(this);
        checkBoxW.setOnCheckedChangeListener(this);
        checkBoxN.setOnCheckedChangeListener(this);
        editPriceW.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        editPriceN.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        setInputType(editPriceW);
        setInputType(editPriceN);
        progressBar.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if (current == max) {
                    progressBar.setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_video).setVisibility(View.INVISIBLE);
                }
            }
        });
        progressBar2.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if (current == max) {
                    progressBar2.setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_video_e).setVisibility(View.INVISIBLE);
                }
            }
        });
        progressBar.setProgressTextColor(R.color.color_blue);
        progressBar2.setProgressTextColor(R.color.color_blue);
        if (getIntent().hasExtra(SITE_ID))
            siteId = getIntent().getStringExtra(SITE_ID);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.image_upload://选择上传文件
                intent = new Intent(UploadLectureAty2.this, StationUploadLectureChoiceActivity.class);
                startActivityForResult(intent, GET_FILE);
                break;
            case R.id.lecture_upload://上传文件
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确定要提交吗？", "取消", "确定",
                        new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                            @Override
                            public void onDismiss(DialogFragment fragment) {
                            }

                            @Override
                            public void onClick(DialogFragment fragment, View v) {
                                sendData();
                            }
                        });

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GET_FILE) {
            if (data != null) {
                makeFileData(data);
            }
        }
    }

    String file_Name = "";//上传文件名
    String path = "";//上传文件路径
    String fileType = "";//上传文件路径

    private void makeFileData(Intent data) {
        file_Name = data.getStringExtra(StationUploadLectureChoiceActivity.FILENAME);
        path = data.getStringExtra(StationUploadLectureChoiceActivity.FILEPATH);
        fileType = data.getStringExtra(StationUploadLectureChoiceActivity.FILETYPE);
        if (!HStringUtil.isEmpty(file_Name)) {
            fileName.setText(file_Name);
        } else {
            fileName.setText("未知文件");
        }
        if (FileType.VIDEO.equals(fileType)) {
            if (!HStringUtil.isEmpty(path)) {
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                try {
                    media.setDataSource(path);
                } catch (Exception e) {
                    Log.d("aaa", "onBindViewHolder: " + "有了");
                }
                final Bitmap bitmap = media.getFrameAtTime();
                imageUpload.setImageBitmap(bitmap);
                ThreadManager.getInstance().createShortPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //生成缩略图
                            saveFile(bitmap);
                            //生成预览视频
                            makeFile(makeFileTime(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                imageUpload.setImageResource(R.drawable.image_file_mp4);
            }

        } else if (FileType.PIC.equals(fileType)) {
            if (!HStringUtil.isEmpty(path)) {
                ImageLoader
                        .load(path)
                        .placeholder(R.drawable.image_file_pic)
                        .error(R.drawable.image_file_pic)
                        .into(imageUpload);
            } else {
                imageUpload.setImageResource(R.drawable.image_file_pic);
            }

        } else if (FileType.DOC.equals(fileType)) {
            imageUpload.setImageResource(R.drawable.image_file_doc);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isPressed()) {
            if (buttonView == checkBoxW && isChecked) {
                checkBoxN.setChecked(false);
            } else if (buttonView == checkBoxN && isChecked) {
                checkBoxW.setChecked(false);
            }
        }

    }

    /**
     * 上传文件
     */
    private void sendData() {
        String courseClass = "";
        name = editName.getText().toString();
        priceW = editPriceW.getText().toString();
        priceN = editPriceN.getText().toString();
        introduce = editIntrodunce.getText().toString();
//        if (HStringUtil.isEmpty(name)) {
//            editName.setError("标题不能为空");
//            return;
//        }
//
//        if (checkBoxW.isChecked() && HStringUtil.isEmpty(priceW)) {
//            editPriceW.setError("站外价格不能为空");
//            return;
//        }
//        if (checkBoxN.isChecked() && HStringUtil.isEmpty(priceN)) {
//            editPriceN.setError("站内价格不能为空");
//            return;
//        }
//        if (HStringUtil.isEmpty(introduce)) {
//            editIntrodunce.setError("简介不能为空");
//            return;
//        }

        headerFile = new File(path);
        if (FileType.VIDEO.equals(fileType)) {
            courseClass = "30";
            textProgress.setText("视频");
        } else if (FileType.PIC.equals(fileType)) {
            courseClass = "20";
            textProgress.setText("图片");
        } else if (FileType.DOC.equals(fileType)) {
            courseClass = "10";
            textProgress.setText("文档");
        }
        mLoadDialog = LoadingProgressFragmentDialog.showLodingDialog(getSupportFragmentManager(), "上传中...");
        progressBar.setVisibility(View.VISIBLE);
//        if (headerFile != null && LoginBusiness.getInstance().getLoginEntity() != null && !HStringUtil.isEmpty(mStationId)) {
//            Param[] params = new Param[]{
//                    new Param("courseClass", courseClass)//课件类型 10-文件；20-图片；30-视频；
//                    , new Param("courseDesc", introduce)//课件描述
//                    , new Param("courseInList", "1")//医生服务集团内是否可见标记
//                    , new Param("courseInPrice", priceN)//医生服务集团内价格
//                    , new Param("courseListType", "10")//课件生命周期类型：10-默认、永久；20-有时间周期；
//                    , new Param("courseName", name)//课件名称
//                    , new Param("courseOutList", "0")//医生服务集团外是否可见标记
//                    , new Param("curseOutPrice", priceW)//医生服务集团外价格
//                    , new Param("site_id", mStationId)//医生服务集团ID
//                    , new Param("uploadCustomer", DoctorHelper.getId())};
//            try {
//                sendFile(headerFile, params);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        if (headerFile != null && LoginBusiness.getInstance().getLoginEntity() != null && !HStringUtil.isEmpty(siteId)) {
            Param[] params = new Param[]{
                    new Param("file_type", courseClass)//课件类型 10-文件；20-图片；30-视频；
                    ,new Param("group_id", siteId)//课件类型 10-文件；20-图片；30-视频；
                    , new Param("customer_id", DoctorHelper.getId())};
            try {
                sendFile(headerFile, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DoubleBtnFragmentDialog.show(getSupportFragmentManager(), "提示", "尚未编辑完成，您确定要离开吗", "取消", "确定",
                new DoubleBtnFragmentDialog.OnDilaogClickListener() {

                    @Override
                    public void onDismiss(DialogFragment fragment) {
                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        UploadLectureAty2.super.onBackPressed();
                    }
                });
    }

    private File headerFile = null;//上传文件
    private File headerFileMin = null;//上传文件缩略图
    private File videoFileMin = null;//上传文件预览

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiConnection.cancelTag(this);
    }

    private LoadingProgressFragmentDialog mLoadDialog;


    class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private void sendFile(File upLoadFile, Param[] params) {
        Param[] mParams;
        if (params == null) {
            mParams = new Param[0];
        } else {
            mParams = params;
        }

        MultipartBody.Builder builder1 = new MultipartBody.Builder()
                .setType(MultipartBody.ALTERNATIVE);
        for (Param param : mParams) {
            builder1.addPart(okhttp3.Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain;charset=utf-8"), param.value));
        }

        FormBody.Builder paramBuilder = new FormBody.Builder();
        for (Param param : mParams) {
            paramBuilder.add(param.key, param.value);
        }
        builder1.addPart(okhttp3.Headers.of("Content-Disposition", "form-data; name=\"params\""), paramBuilder.build());

        findViewById(R.id.tv_video).setVisibility(View.VISIBLE);
        if (FileType.VIDEO.equals(fileType)) {
            builder1.addFormDataPart("file_pic", file_Name, okhttp3.RequestBody.create(MEDIA_TYPE_PNG, new File(headerFileMin.getPath())));
            if (videoFileMin != null) {
                progressBar2.setVisibility(View.VISIBLE);
                findViewById(R.id.tv_video_e).setVisibility(View.VISIBLE);
                builder1.addFormDataPart("file_min", file_Name
                        , ApiConnection.createCustomRequestBody(MultipartBody.FORM, videoFileMin, new ApiConnection.ProgressListener() {
                            @Override
                            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                                int progress = (int) ((totalBytes - remainingBytes) * 100 / totalBytes);
                                System.out.println(progress + "  ");
                                Message message = Message.obtain();
                                message.what = 1;
                                message.obj = progress;
                                mHandler.sendMessage(message);
                            }
                        }));
            }
        }
        builder1.addFormDataPart("file", upLoadFile.getName(), ApiConnection.createCustomRequestBody(MultipartBody.FORM, videoFileMin, new ApiConnection.ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                int progress = (int) ((totalBytes - remainingBytes) * 100 / totalBytes);
                Message message = Message.obtain();
                message.what = 3;
                message.obj = progress;
                mHandler.sendMessage(message);
            }
        }));

        Request request = new Request
                .Builder()
                .url(AppContext.getApiRepository().UPLOADCLASSROOMFILE) //地址
                .post(builder1.build())
                .build();
        ApiConnection.getClinet().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if ("1".equals(object.optString("code"))) {
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = object.optString("message");
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }

    /**
     * 设置字符过滤
     */
    private void setInputType(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (source.equals(".") && dest.toString().length() == 0) {
                            return "0.";
                        }
                        if (dest.toString().contains(".")) {
                            int index = dest.toString().indexOf(".");
                            int length = dest.toString().substring(index).length();
                            if (length == 3) {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
    }

    private void makeFile(double fileTime) {
        try {
            if (fileTime / 1000 > 10) {
                videoFileMin = StorageUtils.createVideoFile();
                Mp4ParseUtil.cropMp4(path, 0L, 300L, videoFileMin.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件
     *
     * @param bm
     * @throws IOException
     */
    public void saveFile(Bitmap bm) throws IOException {
        String path = getSDPath() + "/bitmapPic/";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        headerFileMin = StorageUtils.createImageFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(headerFileMin));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public static String getRingDuring(String mUri) {
        String duration = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 BrowserSpace/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        LogUtil.e("ryan", "duration " + duration);
        return duration;
    }

    public static Uri getMediaUriFromPath(Context context, String path) {
        Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Video.Media.DISPLAY_NAME + "= ?",
                new String[]{path.substring(path.lastIndexOf("/") + 1)},
                null);

        Uri uri = null;
        if (cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }

    /**
     * 视频文件总时长
     *
     * @param path 文件路径
     * @return
     */
    private double makeFileTime(String path) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);  //recordingFilePath（）为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double time = player.getDuration();
        player.release();//记得释放资源
        return time;//获取音频的时间
    }


    /**
     * 清楚上传残留内存
     */
    private void deleteCache() {
        ThreadManager.getInstance().createShortPool().execute(new Runnable() {
            @Override
            public void run() {
                if (headerFileMin != null) {
                    FileUtils.deleteFile(headerFileMin.getAbsolutePath());
                }
                if (videoFileMin != null) {
                    FileUtils.deleteFile(videoFileMin.getAbsolutePath());
                }

            }
        });
        UploadLectureAty2.this.finish();
    }
}
