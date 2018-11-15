package com.yksj.consultation.agency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.agency.view.AgencyApplyView;
import com.yksj.consultation.bean.AgencyBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

import io.reactivex.functions.Consumer;

/**
 * 入驻申请
 */
public class AgencyApplyActivity extends BaseTitleActivity implements AgencyApplyView.IPresenter {

    private AgencyApplyView mView;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, AgencyApplyActivity.class);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyApplyView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("入驻申请");
    }

    @SuppressLint("CheckResult")
    @Override
    public void openChoosePicture() {
        RxChooseHelper.chooseImage(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              mView.setPicture(s);
                          }
                      });
    }

    @Override
    public void submit(@NotNull AgencyBean agency) {
        if (!checkParames(agency)) {
            return;
        }
        ApiService.agencySubmit(DoctorHelper.getId(),
                agency.name,
                agency.avatar,
                agency.desc,
                String.valueOf(agency.type),
                agency.addressCode,
                agency.address,
                agency.detailAddredd,
                agency.telephone,
                new ApiCallbackWrapper<ResponseBean<String>>(true) {
                    @Override
                    public void onResponse(ResponseBean<String> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            finish();
                        }
                        ToastUtils.showShort(response.result);
                    }
                });
    }

    /**
     * 验证输入的参数
     * @param agency
     * @return
     */
    private boolean checkParames(AgencyBean agency) {
        if (!TextUtils.isEmpty(agency.name)) {
            if (!TextUtils.isEmpty(agency.avatar)) {
                if (!TextUtils.isEmpty(agency.desc)) {
                    if (agency.type != 0) {
                        if (!TextUtils.isEmpty(agency.addressCode)) {
                            if (!TextUtils.isEmpty(agency.address)) {
                                if (!TextUtils.isEmpty(agency.detailAddredd)) {
                                    if (!RegexUtils.isMobileSimple(agency.telephone)) {
                                        return true;
                                    } else {
                                        ToastUtils.showShort("请输入正确的电话");
                                    }
                                } else {
                                    ToastUtils.showShort("请输入地址详情");
                                }
                            } else {
                                ToastUtils.showShort("请输入地址");
                            }
                        } else {
                            ToastUtils.showShort("请选择地区");
                        }
                    } else {
                        ToastUtils.showShort("请选择类型");
                    }
                } else {
                    ToastUtils.showShort("请输入机构介绍");
                }
            } else {
                ToastUtils.showShort("请选择背景图片");
            }
        } else {
            ToastUtils.showShort("请输入机构名称");
        }
        return false;
    }
}
