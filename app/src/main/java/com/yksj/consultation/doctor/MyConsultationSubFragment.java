package com.yksj.consultation.doctor;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.adapter.AdtConsultationOrders;
import com.yksj.consultation.bean.ConsultListBean;
import com.yksj.consultation.bean.ListDetails;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * Created by HEKl on 2015/9/15.
 * Used for 会诊订单_
 */
public class MyConsultationSubFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView mRefreshableView;
    private AdtConsultationOrders mAdapter;
    private int TYPELIST;
    private int pageSize = 1;
    private int blankSize;
    private int REFRESH = 0;
    private ConsultListBean bean;
    private String positionId;
    private View mEmptyView;

    public static MyConsultationSubFragment newInstance(int type, int personType) {

        Bundle args = new Bundle();
        args.putInt("typeList", type);
        args.putInt("personType", personType);

        MyConsultationSubFragment fragment = new MyConsultationSubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public int createLayoutRes() {
        return R.layout.fgt_myorders;
    }

    @Override public void initialize(View view) {
        super.initialize(view);
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        pageSize = 1;
        loadData();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.removeAll();
    }

    private void initView(View view) {
        TYPELIST = getArguments().getInt("typeList");
        int personType = getArguments().getInt("personType");
        if (personType == 0) {
            positionId = "findPatByAssistant";
        } else {
            positionId = "findPatByExpert";
        }
        mEmptyView = view.findViewById(R.id.load_faile_layout);
        mRefreshableView = view.findViewById(R.id.pull_refresh_listview);
        ListView mListView = mRefreshableView.getRefreshableView();
        mAdapter = new AdtConsultationOrders(getActivity(), TYPELIST, positionId);
        mListView.setAdapter(mAdapter);
        mRefreshableView.setOnRefreshListener(this);
    }

    /**
     * 会诊列表
     */
    @SuppressWarnings("deprecation")
    private void loadData() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TERMINAL_TYPE", positionId));
        pairs.add(new BasicNameValuePair("TYPE", TYPELIST + ""));
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("PAGESIZE", pageSize + ""));
        pairs.add(new BasicNameValuePair("PAGENUM", "20"));
        pairs.add(new BasicNameValuePair("VALID_MARK", "40"));
        ApiService.OKHttpConsultationList(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                if (!TextUtils.isEmpty(response)) {
                    bean = gson.fromJson(response, ConsultListBean.class);
                    if (bean.getResult().size() == 0) {
                        blankSize = pageSize;
                        if (pageSize == 1) {
                            mAdapter.removeAll();
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            ToastUtil.showShort("没有更多了");
                        }
                    } else if ("0".equals(bean.getCode())) {
                        ToastUtil.showShort(bean.getMessage());
                    } else {
                        ArrayList<ListDetails> success = bean.getResult();
                        if (pageSize == 1) {
                            mEmptyView.setVisibility(View.GONE);
                            mAdapter.removeAll();
                        }
                        pageSize++;
                        mAdapter.addAll(success);
                    }
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mRefreshableView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mRefreshableView.onRefreshComplete();
                if (REFRESH == 1) {
                    ToastUtil.showShort("已更新");
                }
                REFRESH = 0;
            }
        }, this);
    }

    //下拉更新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageSize = 1;
        REFRESH = 1;
        loadData();
    }

    //上拉加载更多
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (blankSize > 0) {
            pageSize = blankSize;
        }
        loadData();
    }
}

