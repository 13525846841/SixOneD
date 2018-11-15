package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import butterknife.BindView;

import static com.yksj.consultation.doctor.constant.ServiceType.BY;
import static com.yksj.consultation.doctor.constant.ServiceType.DH;
import static com.yksj.consultation.doctor.constant.ServiceType.MZ;
import static com.yksj.consultation.doctor.constant.ServiceType.SP;
import static com.yksj.consultation.doctor.constant.ServiceType.TW;


/**
 * 设置工作室的价格的界面
 */
public class StationPriceSettingActivity extends BaseTitleActivity {

    @BindView(R.id.st_switch)
    SuperTextView mSwitchView;

    @BindView(R.id.price_stv)
    SuperTextView mPriceView;

    public final static String TITLE = "TITLE";
    public final static String PRICE = "PRICE";
    private String mServicePrice = "";//服务价格
    private int mOrderOnOff;//服务开关
    private String mServiceType;//服务角色
    private String mStationId;//服务角色
    private float mPrice;

    public static Intent getCallingIntent(Context context, String servisType, int toggle, float price, String stationId) {
        Intent intent = new Intent(context, StationPriceSettingActivity.class);
        intent.putExtra(Constant.Station.SERVICE_TYPE_ID, servisType);
        intent.putExtra(Constant.Station.ORDER_ON_OFF, toggle);
        intent.putExtra(Constant.Station.PRICE, price);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_set_station_price_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mOrderOnOff = getIntent().getIntExtra(Constant.Station.ORDER_ON_OFF, -1);
        mPrice = getIntent().getFloatExtra(PRICE, 0);
        mServiceType = getIntent().getStringExtra(Constant.Station.SERVICE_TYPE_ID);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        setTitleByType();
        initView();
    }

    /**
     * 根据服务类型设置title
     */
    private void setTitleByType(){
        if (TW.equals(mServiceType)) {
            setTitle("图文咨询");
        } else if (BY.equals(mServiceType)) {
            setTitle("包月咨询");
        } else if (DH.equals(mServiceType)) {
            setTitle("电话咨询");
        } else if (SP.equals(mServiceType)) {
            setTitle("视频咨询");
        } else if (MZ.equals(mServiceType)) {
            setTitle("门诊预约");
        }
    }

    private void initView() {
        setRight("确定", v -> submitData());
        if (mPrice != 0) {
            mPriceView.setRightEditString(String.valueOf(mPrice));
        }
        if (MZ.equals(mServiceType)) {
            findViewById(R.id.price_stv).setVisibility(View.GONE);
        }
        mSwitchView.setSwitchIsChecked(mOrderOnOff == 1);
    }

    /**
     * 是否开通
     */
    private void submitData() {
        mOrderOnOff = mSwitchView.switchIsChecked() ? 1 : 0;
        mServicePrice = mPriceView.getRightEditString().toString();
        if (MZ.equals(mServiceType))
            mServicePrice = "100";
        if (mOrderOnOff == 1) {
            if (TextUtils.isEmpty(mServicePrice)) {
                ToastUtils.showShort("请填写咨询价格");
                return;
            }
        }
        ApiService.OKHttpStationPrice(String.valueOf(mOrderOnOff), mServicePrice, mServiceType, DoctorHelper.getId(), mStationId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    ToastUtils.showShort(response.message);
                    finish();
                }
            }
        }, this);
    }
}
