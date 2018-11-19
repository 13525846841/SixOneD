package com.yksj.consultation.sonDoc.consultation;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.TemplatelibAtyAdapter;
import com.yksj.consultation.bean.FollowTemplateBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import java.util.List;

import butterknife.BindView;

/**
 * 模板库
 */
public class TemplatelibAty extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private TemplatelibAtyAdapter mAdapter;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_templatelib_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("模板库");
        initView();
        requestData();
    }

    private void initView() {
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mRecyclerView.setAdapter(mAdapter = new TemplatelibAtyAdapter());
        mAdapter.setOnItemClickListener(this);
    }

    protected void requestData() {
        ApiService.followTemplateList(DoctorHelper.getId(), new ApiCallbackWrapper<ResponseBean<List<FollowTemplateBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<FollowTemplateBean>> response) {
                super.onResponse(response);
                List<FollowTemplateBean> result = response.templates;
                mAdapter.setNewData(result);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FollowTemplateBean item = mAdapter.getItem(position);
        startActivity(TemplateLibDetailAty.getCallingIntent(this, item.id));
    }
}
