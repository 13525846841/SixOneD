package com.yksj.consultation.sonDoc.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.DoctorOrderAdapter;
import com.yksj.consultation.bean.OutpatientOrderBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.AtyOutPatientDetail;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import java.util.List;

import butterknife.BindView;
import okhttp3.Request;

/**
 * 医生订单
 * @author jack_tang
 */
public class MyOutpatientSubFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.refresh_layout) SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout) View mEmptyView;

    private String mType;
    private int mPageIndex = 1; //页数
    private DoctorOrderAdapter mAdapter;

    public static MyOutpatientSubFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);

        MyOutpatientSubFragment fragment = new MyOutpatientSubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int createLayoutRes() {
        return R.layout.doctor_order_fragment_layout;
    }

    @Override public void initialize(View view) {
        super.initialize(view);
        initView(view);
        mType = getArguments().getString("type");
    }

    private void initView(View view) {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData(false))
                .setEnableLoadMore(false)
                .autoRefresh();
        mRecyclerView.setAdapter(mAdapter = new DoctorOrderAdapter(getContext()));
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(() -> requestData(true), mRecyclerView);
    }

    private void requestData(boolean isMore) {
        /**
         * 我的订单（医生端） 192.168.16.157:8899/DuoMeiHealth/FindMyPatientDetails32
         customerId 我的id  type 列表序号  pageSize 第几页  pageNum 每页几条 date（格式：201303）日期
         serviceTypeId 服务类型   (目前已完成列表无日期，服务类型筛选date=0，serviceTypeId=0即可)
         列表序号 0-服务中 1-待服务  3-已完成 4-待支付  7-全部
         */
        if (!isMore) {
            mPageIndex = 1;
        }
        ApiService.outpatientOrder(DoctorHelper.getId(), mType, mPageIndex, new ApiCallbackWrapper<ResponseBean<List<OutpatientOrderBean>>>() {
            @Override public void onResponse(ResponseBean<List<OutpatientOrderBean>> response) {
                super.onResponse(response);
                if (response != null && response.isSuccess()) {
                    List<OutpatientOrderBean> result = response.result;
                    if (mAdapter.isLoading()) {
                        if (result == null || result.isEmpty()) {
                            ToastUtils.showShort("没有更多数据");
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.addData(result);
                            mAdapter.loadMoreComplete();
                        }
                    } else {
                        if (result == null || result.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter.setNewData(result);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                    mPageIndex++;
                } else {
                    mRefreshLayout.finishRefresh();
                    mAdapter.loadMoreComplete();
                    ToastUtils.showShort("数据加载错误");
                }
            }

            @Override public void onError(Request request, Exception e) {
                super.onError(request, e);
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OutpatientOrderBean item = mAdapter.getItem(position);
        Intent intent = new Intent(getContext(), AtyOutPatientDetail.class);
        intent.putExtra("ORIDERID", item.ORDER_ID);
        startActivity(intent);
    }
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position,
//                            long id) {
//        CustomerInfoEntity entity = new CustomerInfoEntity();
//        JSONObject jsonObject = mAdapter.getDatas().get(position - 1);
//        String uid = jsonObject.optString("customerId");
//        //昵称
//        if (!HStringUtil.isEmpty(jsonObject.optString("remarksName", ""))) {
//            entity.setName(jsonObject.optString("remarksName", ""));
//        } else {
//            entity.setName(jsonObject.optString("customerNickname", ""));
//        }
//        entity.setId(uid);
//        FriendHttpUtil.chatFromPerson(getActivity(), entity);
//    }
}
