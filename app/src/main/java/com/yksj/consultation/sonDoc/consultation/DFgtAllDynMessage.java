package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.adapter.DynamicMesAllAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.EmptyLayout;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.DynamicMessageListEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.FileUtils;
import com.yksj.healthtalk.utils.SharePreHelper;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author HEKL
 * 
 *         动态消息Fragment(全部动态消息、用户动态消息)
 */
public class DFgtAllDynMessage extends BaseFragment implements OnItemClickListener, OnRefreshListener2<ListView> {

	private ListView mListView;
	private DynamicMesAllAdapter mAdapter;
	private PullToRefreshListView mRefreshableView;
	private List<DynamicMessageListEntity> mList;// 动态消息信息实体类列表
	private DynamicMessageListEntity mEntity;// 动态消息信息实体类
	private String consultCenterId = AppContext.APP_CONSULTATION_CENTERID;// 六一健康Id
	private String customerId = SmartFoxClient.getLoginUserId();// 用户Id
	private int type = 0;// 跳转后列表类型(在onSaveInstanceState方法中)
	private int mPageSize = 1;// 加载页数
	private int TYPE;// 列表类型
	private EmptyLayout mEmptyLayout;
	private HashMap<String, String> mAlreadyRead;// 已读

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dynamic_messall_fragment_layout, null);
		initView(view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		LogUtils.e("onActivityCreated", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		LogUtils.e("type", type + "");
		refreshData();
		super.onStart();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		type = TYPE;
		LogUtils.e("type", type + "");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TYPE = getArguments().getInt("type");
	}

	@Override
	public void onStop() {
		mAdapter.removeAll();
		super.onStop();
	}

	private void initView(View view) {
		mAlreadyRead = FileUtils.fatchReadedDynMes();
		mRefreshableView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
		mListView = mRefreshableView.getRefreshableView();
		mRefreshableView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		mEmptyLayout = new EmptyLayout(mActivity, mListView);
		mEmptyLayout.setErrorButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshData();
			}
		});
	}

	// 全部动态消息数据加载
	private void initDataAll() {
		ApiService.doHttpDynamicMessageList(mPageSize, new ObjectHttpResponseHandler() {
			@Override
			public Object onParseResponse(String content) {
				mList = new ArrayList<DynamicMessageListEntity>();
				try {
					JSONObject obj = new JSONObject(content);
					JSONArray array = obj.getJSONArray("findConsuInfoList");
					JSONObject item;
					for (int i = 0; i < array.length(); i++) {
						item = array.getJSONObject(i);
						mEntity = new DynamicMessageListEntity();
						mEntity.setConsultationCenterId(item.optInt("CONSULTATION_CENTER_ID"));
						mEntity.setCustomerId(item.optInt("CUSTOMER_ID"));
						mEntity.setInfoId(item.optInt("INFO_ID"));
						mEntity.setInfoPicture(item.optString("INFO_PICTURE"));
						mEntity.setPublishTime(item.optString("PUBLISH_TIME"));
						mEntity.setStatusTime(item.optString("STATUS_TIME"));
						mEntity.setInfoStaus(item.optString("INFO_STATUS"));
						mEntity.setInfoName(item.optString("INFO_NAME"));
						mList.add(mEntity);
					}
					SharePreHelper.saveNewsReadedId(String.valueOf(mEntity.getInfoId()));
					return mList;
				} catch (JSONException e) {
					return null;
				}
			}

			@Override
			public void onFinish() {
				mRefreshableView.onRefreshComplete();
				super.onFinish();
			}

			@Override
			public void onSuccess(Object response) {
				super.onSuccess(response);
				if (mPageSize == 1) {
					mAdapter.removeAll();
				}
				if (response != null) {
					mPageSize++;
					mAdapter.addAll((List<DynamicMessageListEntity>) response);
				}

				if (mAdapter.getCount() == 0) {
					mEmptyLayout.showEmpty();
				}

			}

			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				mEmptyLayout.showError();
			}
		});

	}

	// 用户动态消息
	private void initDataSpecial() {
		ApiService.doHttpUserDynamicMessageList(consultCenterId, mPageSize, customerId,
				new ObjectHttpResponseHandler() {
					@Override
					public void onStart() {
						mRefreshableView.setRefreshing();
						super.onStart();
					}

					@Override
					public Object onParseResponse(String content) {
						mList = new ArrayList<DynamicMessageListEntity>();
						try {
							JSONObject obj = new JSONObject(content);
							JSONArray array = obj.getJSONArray("findMyConsuInfoList");
							JSONObject item;
							for (int i = 0; i < array.length(); i++) {
								item = array.getJSONObject(i);
								mEntity = new DynamicMessageListEntity();
								mEntity.setConsultationCenterId(item.optInt("CONSULTATION_CENTER_ID"));
								mEntity.setCustomerId(item.optInt("CUSTOMER_ID"));
								mEntity.setInfoId(item.optInt("INFO_ID"));
								mEntity.setInfoPicture(item.optString("INFO_PICTURE"));
								mEntity.setPublishTime(item.optString("PUBLISH_TIME"));
								mEntity.setStatusTime(item.optString("STATUS_TIME"));
								mEntity.setInfoStaus(item.optString("INFO_STATUS"));
								mEntity.setInfoName(item.optString("INFO_NAME"));
								mList.add(mEntity);
							}
							return mList;
						} catch (JSONException e) {
							return null;
						}

					}

					@Override
					public void onFinish() {
						mRefreshableView.onRefreshComplete();
						super.onFinish();
					}

					@Override
					public void onSuccess(Object response) {
						super.onSuccess(response);
						if (mPageSize == 1) {
							mAdapter.removeAll();
						}
						if (response != null) {
							mPageSize++;
							mAdapter.addAll((List<DynamicMessageListEntity>) response);
						}

						if (mAdapter.getCount() == 0) {
							mEmptyLayout.showEmpty();
						}
					}

					@Override
					public void onFailure(Throwable error) {
						super.onFailure(error);
						mEmptyLayout.showError();
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if (TYPE == 1) {
			String infoId = mEntity.getInfoId() + "";
			mAlreadyRead.put("infoId", infoId);
			FileUtils.updateReadedDynMesIds(mAlreadyRead);
			TextView textView = (TextView) view.findViewById(R.id.tv_messtitle);
			textView.setTextColor(getResources().getColor(R.color.color_text_gray));
//		} else if (TYPE == 2) {
//			return;	}
		Intent intent = new Intent(mActivity, DAtyConslutDynMesContent.class);
		intent.putExtra("conId", AppContext.APP_CONSULTATION_CENTERID);
		intent.putExtra("infoId", "" + mAdapter.datas.get(position - 1).getInfoId());
		startActivity(intent);
	}

	// 下拉刷新
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mPageSize = 1;
		if (TYPE == 1) {
			initDataAll();
		} else {
			initDataSpecial();
		}

	}

	// 上拉刷新
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (TYPE == 1) {
			initDataAll();
		} else {
			initDataSpecial();
		}
	}

	/**
	 * 数据刷新
	 */
	public void refreshData() {
		if (type == 0) {// 来自跳转
			mAdapter = new DynamicMesAllAdapter(mActivity, TYPE,mAlreadyRead);
		} else if (type != 0) {// 来自回退
			mAdapter = new DynamicMesAllAdapter(mActivity, type,mAlreadyRead);
			mPageSize = 1;
		}
		mListView.setAdapter(mAdapter);
		switch (TYPE) {
			case 1:
				initDataAll();
				break;
			case 2:
				initDataSpecial();
				break;
		}

	}
}
