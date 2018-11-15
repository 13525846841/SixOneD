package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.DoctorToolsListAdapter;
import com.yksj.consultation.bean.DoctorToolsBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.CommonwealAidAty;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.List;

import butterknife.BindView;

/**
 * 医生工具箱
 */
public class DoctorToolsListActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    private DoctorToolsListAdapter mAdapter;

    public static Intent getCallingIntent(Context context, String doctorId) {
        Intent intent = new Intent(context, DoctorToolsListActivity.class);
        intent.putExtra(Constant.ID, doctorId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doctor_tools;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("工具箱");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(1), getResources().getColor(R.color.divider)));
        mAdapter = new DoctorToolsListAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        String doctorId = getIntent().getStringExtra(Constant.ID);
        if (DoctorHelper.isSelf(doctorId)) {
            setRight("添加", this::onAddTool);
        }
        requestData(doctorId);
    }

    /**
     * 添加工具
     * @param view
     */
    private void onAddTool(View view) {
        Intent intent = DoctorAddToolsActivity.getCallingIntent(DoctorToolsListActivity.this, "add");
        startActivity(intent);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DoctorToolsBean item = mAdapter.getItem(position);
        Intent intent = new Intent(DoctorToolsListActivity.this, CommonwealAidAty.class);
        intent.putExtra(CommonwealAidAty.URL, item.TOOL_URL);
        intent.putExtra(CommonwealAidAty.TITLE, item.TOOL_NAME);
        startActivity(intent);
    }

    private void requestData(String doctorId) {
        ApiService.doctorTools(doctorId, new ApiCallbackWrapper<ResponseBean<List<DoctorToolsBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<DoctorToolsBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mAdapter.setNewData(response.result);
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }
}
