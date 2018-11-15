package com.yksj.consultation.sonDoc.consultation.salon;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.comm.BaseListFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.consultation.salon.ui.HealthTopicMainUi;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.entity.BaseInfoEntity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 健康话题展示列表的Fragment
 *
 * @author lmk
 */
public class HealthTopicListFragment extends BaseListFragment implements SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener,
        OnRefreshListener2<ListView>, OnClickChildItemListener {

    private PullToRefreshListView mPullListView;
    private ListView mListView;
    private String flagPlacing = "-100000";//第几次加载数据的标记
    private int chargeFlag = 2;//是否收费1-收费，0-免费  2-全部    100表示病友话题的Fragment
    private HealthTopicListAdapter adapter;
    private ArrayList<GroupInfoEntity> datas;
    private String errorMessage;
    private int CHATTINGCODE = 2;
    private GroupInfoEntity cacheGroupInfoEntity;


    public static HealthTopicListFragment newInstance(int sourceType) {
        HealthTopicListFragment fragment = new HealthTopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("source", sourceType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_friend_list_fragment, null);
        mPullListView = (PullToRefreshListView) view.findViewById(R.id.find_friend_pulllist);
        mPullListView.setOnRefreshListener(this);
        mListView = mPullListView.getRefreshableView();
        adapter = new HealthTopicListAdapter(getActivity());
        adapter.setClickListener(this);
        chargeFlag = getArguments().getInt("source");
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SalonHttpUtil.onItemClick(getActivity(), HealthTopicListFragment.this, getChildFragmentManager(), adapter.datas.get(position - 1), true);
            }
        });
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        GroupInfoEntity cacheEntity = null;
        HealthTopicMainUi mainUi = (HealthTopicMainUi) getActivity();
        HashMap<String, BaseInfoEntity> map = mainUi.cacheKeys;
        for (int i = 0; i < adapter.datas.size(); i++) {
            cacheEntity = adapter.datas.get(i);
            if (map.containsKey(cacheEntity.getId()))
                cacheEntity = (GroupInfoEntity) map.get(cacheEntity.getId());
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            flagPlacing = "-100000";
            loadData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        ApiService.doHttpRequestLoadTopic(SmartFoxClient.getLoginUserId(), flagPlacing, chargeFlag,
                new ObjectHttpResponseHandler() {
                    @Override
                    public Object onParseResponse(String content) {
                        if (content != null && !content.contains("error_message")) {//加载导数据
                            return SalonHttpUtil.jsonAnalysisTopicEntitys(getActivity(), content);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Object response) {
                        if (response == null) {
                            ToastUtil.showShort(getResources().getString(R.string.time_out));
                            return;
                        }
                        if ("-100000".equals(flagPlacing)) {
                            adapter.removeAll();
                        }
                        datas = (ArrayList<GroupInfoEntity>) response;
                        adapter.addAll(datas);
                        super.onSuccess(statusCode, response);
                    }

                    @Override
                    public void onStart() {
                        mPullListView.setRefreshing();
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        mPullListView.onRefreshComplete();
                        super.onFinish();
                    }
                });
    }

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        flagPlacing = "-100000";
        loadData();
    }

    //上拉加载更多
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (datas != null && datas.size() > 0) {
            flagPlacing = datas.get(datas.size() - 1).getFlagPlacing();
        }
        loadData();
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
        if ("0".equals(state)) {
        } else if ("-1".equals(state)) {
            ToastUtil.showBasicErrorShortToast(getActivity());
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
            intent1.setClass(getActivity(), ChatActivity.class);
            startActivityForResult(intent1, CHATTINGCODE);
        }
    }

    @Override
    public ListView getListView() {
        return mListView;
    }


    @Override
    public BaseAdapter getAdapter() {
        return adapter;
    }


}
