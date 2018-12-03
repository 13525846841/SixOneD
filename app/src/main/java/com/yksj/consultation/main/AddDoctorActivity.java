package com.yksj.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.umeng.UmengShare;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.sixoneclass.SixOneAddClassActivity;
import com.yksj.consultation.sonDoc.chatting.sixoneclass.group.ContactInfoActivity;

/**
 * 添加朋友界面
 */
public class AddDoctorActivity extends BaseTitleActivity {

    private RelativeLayout rl_phone;
    private RelativeLayout rl_wechat;
    private RelativeLayout rl_qq;
    private EditText mEditText;
    private UMShareListener mListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtils.showShort("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtils.showShort("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtils.showShort("分享取消");
        }
    };

    @Override
    public int createLayoutRes() {
        return R.layout.activity_add_doctor;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("添加好友");
        initView();
    }

    private void initView() {
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        rl_wechat = (RelativeLayout) findViewById(R.id.rl_wechat);
        rl_qq = (RelativeLayout) findViewById(R.id.rl_qq);
        mEditText = (EditText) findViewById(R.id.include_search).findViewById(R.id.edit_search_top);
        mEditText.setFocusable(false);
        mEditText.setOnClickListener(this);
        mEditText.setHint("输入手机号");
        rl_phone.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);
        rl_qq.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_search_top:
                startActivity(new Intent(this, SixOneAddClassActivity.class));
                break;
            case R.id.rl_phone://手机联系人
                startActivity(new Intent(this, ContactInfoActivity.class));
                break;
            case R.id.rl_wechat://微信好友
                sendWX();
                break;
            case R.id.rl_qq://qq好友
                sendQQ();
                break;
        }
    }

    /**
     * qq分享
     */
    private void sendQQ() {
        UmengShare.from(this)
                  .share(SHARE_MEDIA.QQ)
                  .setThumb(R.drawable.ic_launcher)
                  .setTitle(getString(R.string.string_share_title))
                  .setContent(getString(R.string.string_share_content))
                  .setUrl(getString(R.string.string_share_website))
                  .setListener(mListener)
                  .startShare();
    }

    /**
     * 微信分享
     */
    private void sendWX() {
        UmengShare.from(this)
                  .share(SHARE_MEDIA.WEIXIN)
                  .setTitle(getString(R.string.string_share_title))
                  .setThumb(R.drawable.ic_launcher)
                  .setContent(getString(R.string.string_share_content))
                  .setUrl(getString(R.string.string_share_website))
                  .setListener(mListener)
                  .startShare();
    }
}
