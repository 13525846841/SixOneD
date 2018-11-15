package com.yksj.consultation.station;


import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yksj.consultation.adapter.StationCommentAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.StationCommentBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.healthtalk.net.http.ApiService;

public class StationCommentListActivity extends BaseListActivity {

    private String mStationId;

    @Override
    public void initialize(Bundle bundle) {
        setTitle("评论列表");
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        super.initialize(bundle);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {

    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new StationCommentAdapter();
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.OkHttpStationCommentList(mStationId, pageIndex, createSimpleCallback(StationCommentBean.class));
    }
}
