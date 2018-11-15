package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.FileUtils;
import com.library.base.base.BaseActivity;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.bean.FileType;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;

import io.reactivex.functions.Consumer;

/**
 * 健康讲堂文件上传选择
 */
public class StationUploadLectureChoiceActivity extends BaseActivity {
    public static final int GETFILE = 1101;
    public static final String FILENAME = "file_name";
    public static final String FILETYPE = "file_type";
    public static final String FILEPATH = "file_path";
    private String mStationId;

    public static Intent getCallingIntent(Context context, String stationId) {
        Intent intent = new Intent(context, StationUploadLectureChoiceActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_choice_pic_aty;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setWindowFullScreen();
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置全屏
     */
    private void setWindowFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // 虚拟导航栏透明
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        initView();
    }

    private void initView() {
        initializeTitle();
        findViewById(R.id.ib_video).setOnClickListener(this);
        findViewById(R.id.ib_pic).setOnClickListener(this);
        findViewById(R.id.ib_file).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_video:
                RxChooseHelper.videoChoose(this)
                              .subscribe(new Consumer<String>() {
                                  @Override
                                  public void accept(String s) throws Exception {
                                      //TODO 为实现
                                  }
                              });
                break;
            case R.id.ib_pic:
                RxChooseHelper.chooseImage(this)
                              .subscribe(new Consumer<String>() {
                                  @Override
                                  public void accept(String s) throws Exception {
                                      Intent intent = new Intent();
                                      String name = FileUtils.getFileName(s);
                                      intent.putExtra(StationUploadLectureChoiceActivity.FILENAME, name);
                                      intent.putExtra(StationUploadLectureChoiceActivity.FILEPATH, s);
                                      intent.putExtra(StationUploadLectureChoiceActivity.FILETYPE, FileType.PIC);
                                      setResult(RESULT_OK, intent);
                                      finish();
                                  }
                              });
                break;
            case R.id.ib_file:

                break;
        }
    }
}
