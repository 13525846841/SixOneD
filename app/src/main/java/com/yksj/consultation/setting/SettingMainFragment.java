package com.yksj.consultation.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseFragment;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.ResourceHelper;
import com.library.base.utils.StorageUtils;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.app.AppUpdateManager;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.SharePreHelper;

import org.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * App设置界面
 */
public class SettingMainFragment extends BaseFragment {

    @BindView(R.id.check_updata_stv) SuperTextView mCheckUpdataStv;

    @BindView(R.id.setting_clear_cache) SuperTextView mClearCacheStv;

    @BindView(R.id.wifi_updata_stv) SuperTextView mWifiUpdataStv;

    @Override
    public int createLayoutRes() {
        return R.layout.setting_layout_main;
    }

    @SuppressLint("CheckResult")
    @Override
    public void initialize(View view) {
        super.initialize(view);

        //检测是否有版本更新
        AppUpdateManager.getInstance().isAppUpdate()
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isUpdate) throws Exception {
                                String versionName = AppUtils.getAppVersionName();
                                mCheckUpdataStv.setRightString(String.format("V%s", versionName));
                                if (isUpdate) {
                                    Drawable dotRed = ResourceHelper.getDrawable(R.drawable.shape_dot_red);
                                    mCheckUpdataStv.setRightTvDrawableRight(dotRed);
                                }
                            }
                        });

        //获取以使用的缓存
        String dirSize = getCacheSize();
        mClearCacheStv.setRightString(String.format("当前缓存%s", dirSize));

        //设置是否IWIFI更新
        boolean wifiUpdata = SharePreHelper.isWifiUpdate();
        mWifiUpdataStv.setSwitchIsChecked(wifiUpdata);
        mWifiUpdataStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                SharePreHelper.setWifiUpdate(isChecked);
            }
        });
    }

    /**
     * 获取缓存大小
     * @return
     */
    private String getCacheSize() {
        long byteNum = FileUtils.getDirLength(StorageUtils.getRootPath());
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%.1fGB", (double) byteNum / 1073741824);
        }
    }

    /**
     * 绑定手机
     * @param v
     */
    @OnClick(R.id.bound)
    public void onBound(View v) {
        Intent intent = new Intent(getActivity(), SettingPhoneBound.class);
        intent.putExtra("ALTERBAND", "ALTERBAND");//修改
        startActivityForResult(intent, 2);
    }

    /**
     * 设定密码
     * @param v
     */
    @OnClick(R.id.setting_password)
    public void onRevisePass(View v) {
        Intent intent = new Intent(getActivity(), SettingPassWordUI.class);
        startActivity(intent);
    }

    /**
     * 检查更新
     * @param v
     */
    @OnClick(R.id.check_updata_stv)
    public void onCheckUpdata(View v) {
        AppUpdateManager.getInstance().checkeUpdate(getActivity(), true);
    }

    /**
     * 关于我们
     * @param v
     */
    @OnClick(R.id.setting_update)
    public void onAbout(View v) {
        Intent intent = new Intent(getActivity(), SettingAboutHealthActivity.class);
        startActivity(intent);
    }

    /**
     * 意见反馈
     * @param v
     */
    @OnClick(R.id.setting_feedback)
    public void onFeedback(View v) {
        Intent intent = new Intent(getActivity(), SettingFeedbackUI.class);
        startActivity(intent);
    }

    /**
     * 清理缓存
     * @param v
     */
    @OnClick(R.id.setting_clear_cache)
    public void onClearCache(View v) {
        ConfirmDialog.newInstance("", "是否清理缓存?")
                     .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                         @Override
                         public void onPositiveClick(ConfirmDialog dialog, View v) {
                             super.onPositiveClick(dialog, v);
                             clearAllCache();
                         }
                     }).show(getFragmentManager());
    }

    /**
     * 清理缓存
     */
    @SuppressLint("CheckResult")
    private void clearAllCache() {
        Observable.just(1)
                  .map(new Function<Integer, Boolean>() {
                      @Override
                      public Boolean apply(Integer i) throws Exception {
                          ImageLoader.getInstance().clearDiscCache();
                          return FileUtils.deleteAllInDir(StorageUtils.getRootPath());
                      }
                  })
                  .subscribe(new Consumer<Boolean>() {
                      @Override
                      public void accept(Boolean aBoolean) throws Exception {
                          if (aBoolean) {
                              ToastUtils.showShort("清理完毕");
                              String dirSize = getCacheSize();
                              mClearCacheStv.setRightString(dirSize);
                          }
                      }
                  });
    }
}
