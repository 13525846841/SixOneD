package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.yksj.consultation.adapter.AdtConsultationManager;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.ServiceListDoctorEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HEKL
 * 
 *         我的服务列表Fragment(包括会诊专家会诊管理)
 */
public class DFgtConsultMyServiceList extends BaseFragment implements OnRefreshListener2<ListView>,
		OnLastItemVisibleListener, OnItemLongClickListener {

	private List<ServiceListDoctorEntity> mList;
	private List<JSONObject> data;
	private ServiceListDoctorEntity mEntity;
	private AdtConsultationManager mAdapter;
	private PullToRefreshListView mRefreshableView;
	private ListView mListView;
	private View mEmptyView;

	private int type;
	private String positionType = null;
	private String customerId = null;
	private int TYPELIST;// 列表类型
	private int TypeList = 16;// 列表类型记录
	private int pageSize = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.e("onCreateView", "onCreateView");
		View view = inflater.inflate(R.layout.consultation_manager_fragment_layout, null);
		initView(view);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {// fragment销毁后保存信息
		LogUtil.e("onSaveInstanceState", "onSaveInstanceState");
		TypeList = TYPELIST;
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {// 加载数据
		LogUtil.e("onStart", "onStart");
		customerId = SmartFoxClient.getLoginUserId();
		if (TypeList == 16) {
			mAdapter = new AdtConsultationManager(getActivity(), positionType, TYPELIST);
			mListView.setAdapter(mAdapter);
			loadData();
		} else if (TypeList != 16) {
			pageSize = 1;
			mAdapter = new AdtConsultationManager(getActivity(), positionType, TypeList);
			mListView.setAdapter(mAdapter);
			loadData();
		}
		super.onStart();
	}

	private void initView(View view) {// 初始化
		mRefreshableView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
		mEmptyView = view.findViewById(R.id.load_faile_layout);
		mListView = mRefreshableView.getRefreshableView();
		mRefreshableView.setOnRefreshListener(this);
		loadingData();
		mAdapter = new AdtConsultationManager(getActivity(), positionType, TYPELIST);
		mListView.setAdapter(mAdapter);
		mRefreshableView.setOnLastItemVisibleListener(this);
	}

	/**
	 *	
	 */
	private void loadingData() {// 列表类型
		TYPELIST = getArguments().getInt("typeList");
		switch (TYPELIST) {
		case 0://专家待会诊
			positionType = "findPatByExpert";
			type = 1;
			break;
		case 1://专家会诊中
			positionType = "findPatByExpert";
			type = 4;
			break;
		case 2://专家已完成
			positionType = "findPatByExpert";
			type = 5;
			break;
		case 10:// 医生待会诊
			positionType = "findPatByAssistant";
			type = 1;
			break;
		case 11:// 医生会诊中
			positionType = "findPatByAssistant";
			type = 6;
			break;
		case 12:// 医生已完成
			positionType = "findPatByAssistant";
			type = 7;
			break;
		}
	}

	/**
	 * 加载助理医生会诊服务列表数据
	 */
	private void loadData() {

		ApiService.doHttpFindMyConsuServiceList(customerId, positionType, type, pageSize,
				new AsyncHttpResponseHandler() {
					@Override
					public void onStart() {
						mRefreshableView.setRefreshing();
						super.onStart();
					}

					@Override
					public void onSuccess(String content) {
						try {
							JSONObject object = new JSONObject(content);
							// if (object.has("error_message")) {
							// ToastUtil.showShort("")
							// }
							if (object.has(positionType)) {
								data = new ArrayList<JSONObject>();
								JSONArray array = object.getJSONArray(positionType);
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									data.add(obj);
								}
								if (!(data.size() == 0)) {
									if (pageSize == 1) {
										mAdapter.removeAll();
										mEmptyView.setVisibility(View.GONE);
									}
									pageSize++;
									mAdapter.addAll(data);
								} else if (data.size() == 0) {// 空数据处理
									if (pageSize == 1) {
										mAdapter.removeAll();
										mEmptyView.setVisibility(View.VISIBLE);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						super.onSuccess(content);
					}

					@Override
					public void onFinish() {
						mRefreshableView.onRefreshComplete();
						super.onFinish();
					}
				});

	}

	@Override
	public void onLastItemVisible() {
		// initData();
	}

	public static Fragment newInstance(String string) {
		Fragment fragment = new Fragment();
		return fragment;
	}

	// 常按删除会诊订单
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		ToastUtil.showShort("删除");
		return true;
	}

	// 下拉
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageSize = 1;
		loadData();

	}

	// 上拉
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData();
	}

}
