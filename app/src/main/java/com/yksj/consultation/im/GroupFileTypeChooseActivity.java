package com.yksj.consultation.im;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.library.base.base.BaseActivity;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.sonDoc.R;

import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 群文件上传文件类型选择
 */
public class GroupFileTypeChooseActivity extends BaseActivity {

    private static final String GROUP_ID = "group_id";

    private String mGroupId;//群Id

    public static Intent getCallingIntent(Context context, String groupId){
        Intent intent = new Intent(context, GroupFileTypeChooseActivity.class);
        intent.putExtra(GROUP_ID, groupId);
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
        mGroupId = getIntent().getStringExtra(GROUP_ID);
    }

    /**
     * 视频选择
     * @param v
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.ib_video)
    public void onVideoChoose(View v){
        RxChooseHelper.videoChoose(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String path) throws Exception {
                              Intent intent = GroupStartUploadActivity.getCallingIntent(GroupFileTypeChooseActivity.this, mGroupId, path, "30");
                              startActivity(intent);
                              finish();
                          }
                      });

    }

    /**
     * 图片选择
     * @param v
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.ib_pic)
    public void onPictureChoose(View v){
        RxChooseHelper.chooseImage(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String path) throws Exception {
                              Intent intent = GroupStartUploadActivity.getCallingIntent(GroupFileTypeChooseActivity.this, mGroupId, path, "20");
                              startActivity(intent);
                              finish();
                          }
                      });
    }

    /**
     * 文档选择
     * @param v
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.ib_file)
    public void onDocmChoose(View v){
        RxChooseHelper.chooseDoc(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String path) throws Exception {
                              Intent intent = GroupStartUploadActivity.getCallingIntent(GroupFileTypeChooseActivity.this, mGroupId, path, "10");
                              startActivity(intent);
                          }
                      });
    }
}
