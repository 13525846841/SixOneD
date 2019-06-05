package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.umeng.UmengShare;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.BarcodeBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.ZoomImgeDialogFragment;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.lang.ref.WeakReference;

import butterknife.BindView;

/**
 * 二维码
 */
public class BarCodeActivity extends BaseTitleActivity {

    @BindView(R.id.image_head)
    ImageView mAvatarView;

    @BindView(R.id.tv_name)
    TextView mNameView;

    @BindView(R.id.tv_user_title)
    TextView mUserTitleView;

    @BindView(R.id.tv_hospital_office)
    TextView mHospitalOfficeView;

    @BindView(R.id.account_sixone)
    TextView mAccount;

    @BindView(R.id.iv_ad)
    ImageView imageQR;//二维码

    @BindView(R.id.capture_layout)
    View mCaptureLay;

    @BindView(R.id.description_tv)
    TextView mDescView;

    private BarcodeBean mBarcodeBean;

    public static Intent getCallingIntent(Context context, String userId, String name, String hospital, String office, String title) {
        Intent intent = new Intent(context, BarCodeActivity.class);
        intent.putExtra(Constant.USER_ID, userId);
        intent.putExtra(Constant.USER_NAME, name);
        intent.putExtra(Constant.USER_HOSPITAL, hospital);
        intent.putExtra(Constant.USER_OFFICE, office);
        intent.putExtra(Constant.USER_TITLE, title);
        return intent;
    }

    private static Space mSpace;

    public static Space from(Context context) {
        mSpace = new Space(context);
        return mSpace;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_bar_code;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);

        setTitle(TextUtils.isEmpty(mSpace.name) ? "二维码名片" : mSpace.name);
//        setRight("分享", this::onShareClick);
//
        ImageLoader.load(mSpace.avatarPath).into(mAvatarView);

        if (!TextUtils.isEmpty(mSpace.name)) {
            mNameView.setText(mSpace.name);
        }
        if (!TextUtils.isEmpty(mSpace.office)) {
            mUserTitleView.setVisibility(View.VISIBLE);
            mUserTitleView.setText(mSpace.office);
        }
        if (!TextUtils.isEmpty(mSpace.hospital) && !TextUtils.isEmpty(mSpace.secendOffice)) {
            mHospitalOfficeView.setVisibility(View.VISIBLE);
            mHospitalOfficeView.setText(mSpace.hospital + "   |   " + mSpace.secendOffice);
        }
        if (!TextUtils.isEmpty(mSpace.id)) {
            mAccount.setText(mSpace.id);
        }
        if (!TextUtils.isEmpty(mSpace.description)) {
            mDescView.setText(mSpace.description);
        }
        if (!TextUtils.isEmpty(mSpace.qrPath)) {
            ImageLoader.load(mSpace.qrPath).into(imageQR);
        } else {
            requestData();
        }
    }

    /**
     * 触发分享
     * @param view
     */
    private void onShareClick(View view) {
        UmengShare.from(this)
                .share()
                .setTitle(getString(R.string.string_share_title))
                .setContent(getString(R.string.string_share_content))
                .setThumb(R.drawable.ic_launcher)
                .setImage(captureView(mCaptureLay))
                .showDialog(this);
    }

    /**
     * 截取view图片
     * @param v
     * @return
     */
    private Bitmap captureView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void requestData() {
        ApiService.doGetBarCode(mSpace.id, new ApiCallbackWrapper<BarcodeBean>(true) {
            @Override
            public void onResponse(BarcodeBean response) {
                super.onResponse(response);
                if (response != null && response.isSuccess()) {
                    mBarcodeBean = response;
                    ImageLoader.load(response.path).into(imageQR);
                    String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + LoginBusiness.getInstance().getLoginEntity().getNormalHeadIcon();
                    ImageLoader.loadAvatar(url).into(mAvatarView);
                    mAccount.setText(response.customer_account);
                } else {
                    ToastUtils.showShort("数据加载失败");
                }
            }
        }, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ad:
                ZoomImgeDialogFragment.show(mBarcodeBean.path, getSupportFragmentManager());
                break;
        }
    }

    public static class Space {
        private String qrPath;
        private String name;
        private String title;
        private String office;
        private String hospital;
        private String secendOffice;
        private String avatarPath;
        private String id;
        private String description;
        private WeakReference<Context> context;

        public Space(Context context) {
            this.context = new WeakReference(context);
        }

        public Space setQrPath(String qrPath) {
            this.qrPath = qrPath;
            return this;
        }

        public Space setDescription(String desc) {
            this.description = desc;
            return this;
        }

        public Space setId(String doctorId) {
            this.id = doctorId;
            return this;
        }

        public Space setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
            return this;
        }

        public Space setName(String name) {
            this.name = name;
            return this;
        }

        public Space setTitle(String title) {
            this.title = title;
            return this;
        }

        public Space setHospital(String hospital) {
            this.hospital = hospital;
            return this;
        }

        public Space setOffice(String office) {
            this.office = office;
            return this;
        }

        public Space setSecendOffice(String secendOffice) {
            this.secendOffice = secendOffice;
            return this;
        }

        public void toStart() {
            if (context.get() != null) {
                Intent intent = new Intent(context.get(), BarCodeActivity.class);
                context.get().startActivity(intent);
            }
        }
    }
}
