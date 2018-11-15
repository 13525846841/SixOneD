package com.yksj.consultation.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.StationServiceAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.DoctorServiceBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 工作站咨询设置
 */
public class StationServiceActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    private int type;
    private String siteId;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private StationServiceAdapter mAdapter;
    private List<DoctorServiceBean> mDatas;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doc_service_setting;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("咨询设置");
        type = getIntent().getIntExtra(Constant.Station.STATION_HOME_TYPE, -1);
        siteId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        initRecycler();
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mDatas = new ArrayList<>();
        mAdapter = new StationServiceAdapter(mDatas);
        mAdapter.setOnItemClickListener(this);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mDatas.add(0, DoctorServiceBean.valueOf(ServiceType.TW, siteId, DoctorHelper.getId(), 0));
        mDatas.add(1, DoctorServiceBean.valueOf(ServiceType.MZ, siteId, DoctorHelper.getId(), 0));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 加载数据
     */
    private void requestData() {
        ApiService.OKHttpStationService(siteId, new ApiCallbackWrapper<ResponseBean<List<DoctorServiceBean>>>(this) {
            @Override
            public void onResponse(ResponseBean<List<DoctorServiceBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<DoctorServiceBean> result = response.result;
                    if (!result.isEmpty()) {
                        for (DoctorServiceBean serviceBean : result) {
                            if (serviceBean.SERVICE_TYPE_ID.equals(ServiceType.TW)) {
                                mDatas.set(0, serviceBean);
                                mAdapter.notifyDataSetChanged();
                            } else if (serviceBean.SERVICE_TYPE_ID.equals(ServiceType.MZ)) {
                                mDatas.set(1, serviceBean);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        mDatas.clear();
                        mDatas.add(0, DoctorServiceBean.valueOf(ServiceType.TW, siteId, DoctorHelper.getId(), 0));
                        mDatas.add(1, DoctorServiceBean.valueOf(ServiceType.MZ, siteId, DoctorHelper.getId(), 0));
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    LogUtils.e(response.message);
                }
            }
        }, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DoctorServiceBean service = mAdapter.getData().get(position);
        if (type == StationType.STATION_HOME_CREATE) {//工作站站长
            Intent intent = StationPriceSettingActivity.getCallingIntent(this,
                    service.SERVICE_TYPE_ID, service.ORDER_ON_OFF, service.SERVICE_PRICE, siteId);
            startActivity(intent);
        } else {
            //非站长
        }
    }
}
