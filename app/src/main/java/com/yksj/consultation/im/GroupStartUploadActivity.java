package com.yksj.consultation.im;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.imageLoader.ImageLoader;
import com.luck.picture.lib.PictureVideoPlayActivity;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 群文件上传
 */
public class GroupStartUploadActivity extends BaseTitleActivity {
    private static final String GROUP_ID = "group_id";
    private static final String UPLOAD_PATH = "upload_path";
    private static final String UPLOAD_TYPE = "upload_type";
    private String mGroupId;//群Id
    private String mUploadPath;//上传文件路径
    private String mUploadType;//上传文件类型

    @BindView(R.id.priview_view) ImageView mPriviewView;
    @BindView(R.id.video_play) ImageView mVideoPlayView;

    public static Intent getCallingIntent(Context context, String groupId, String uploadPath, String uploadType) {
        Intent intent = new Intent(context, GroupStartUploadActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(UPLOAD_PATH, uploadPath);
        intent.putExtra(UPLOAD_TYPE, uploadType);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_group_upload;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("文件上传");
        setRight("提交", this::onStartUpload);
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mUploadPath = getIntent().getStringExtra(UPLOAD_PATH);
        mUploadType = getIntent().getStringExtra(UPLOAD_TYPE);

        if (mUploadType.equals("20")) {//图片
            ImageLoader.load(mUploadPath).into(mPriviewView);
        } else if (mUploadType.equals("30")) {//视频
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mUploadPath);
            Bitmap priviewBit = mmr.getFrameAtTime(1000);
            mPriviewView.setImageBitmap(priviewBit);
            mVideoPlayView.setVisibility(View.VISIBLE);
        } else {// 文档
            mPriviewView.setImageResource(R.drawable.image_file_unpressed);
        }
    }

    /**
     * 视频预览
     * @param v
     */
    @OnClick(R.id.video_play)
    public void onVideoPlay(View v){
        Intent intent = new Intent(this, PictureVideoPlayActivity.class);
        intent.putExtra("video_path", mUploadPath);
        startActivity(intent);
    }

    /**
     * 开始上传
     * @param v
     */
    public void onStartUpload(View v) {
        ApiService.groupFileUpload(DoctorHelper.getId(), mGroupId, mUploadPath, mUploadType, new ApiCallbackWrapper<ResponseBean>(true) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    ToastUtils.showShort("上传成功");
                    finish();
                } else {
                    ToastUtils.showShort("上传失败");
                }
            }
        });
    }
}
