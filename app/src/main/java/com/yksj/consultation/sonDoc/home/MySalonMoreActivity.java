//package com.yksj.consultation.ui.home;
//
//import org.handmark.pulltorefresh.library.PullToRefreshBase;
//import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
//import org.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ExpandableListView;
//import android.widget.ExpandableListView.OnChildClickListener;
//import android.widget.ExpandableListView.OnGroupClickListener;
//
//import com.yksj.healthtalk.db.ChatUserHelper;
//import com.yksj.healthtalk.entity.CustomerInfoEntity;
//import com.yksj.healthtalk.entity.GroupInfoEntity;
//import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
//import com.yksj.healthtalk.net.http.ApiService;
//import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
//import com.yksj.healthtalk.net.socket.SmartFoxClient;
//import com.yksj.healthtalk.services.MessagePushService;
//import com.yksj.consultation.adapter.MySalonMoreListExpandableAdapter;
//import com.yksj.consultation.adapter.MySalonMoreListExpandableAdapter.OnClickDeleteListener;
//import com.yksj.consultation.adapter.MySalonMoreListExpandableAdapter.OnPayClickListener;
//import com.yksj.consultation.adapter.MySalonMoreListExpandableAdapter.onClickGroupHeadListener;
//import com.yksj.consultation.adapter.MySalonMoreListExpandableAdapter.onClickSalonAttentionListener;
//import com.example.libbase.base.BaseActivity;
//import com.yksj.consultation.comm.WaitDialog;
//import com.yksj.consultation.comm.SalonPayActivity;
//import com.yksj.consultation.ui.R;
//import com.yksj.consultation.ui.app.AppData;
//import com.yksj.consultation.ui.app.HTalkApplication;
//import com.yksj.consultation.ui.chatting.ChatActivity;
//import com.yksj.consultation.ui.salon.SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener;
//import com.yksj.consultation.ui.salon.TopicUtils;
//import com.yksj.healthtalk.utils.JsonParseUtils;
//import com.yksj.healthtalk.utils.SalonHttpUtil;
//import com.yksj.healthtalk.utils.ToastUtil;
//
///**
// * 我的话题 更多
// * @author jack_tang
// *
// */
//public class MySalonMoreActivity extends BaseActivity implements OnClickListener,
//OnGroupClickListener, OnChildClickListener, onClickGroupHeadListener,
//OnBuyTicketHandlerListener, OnPayClickListener, OnClickDeleteListener, onClickSalonAttentionListener, OnRefreshListener<ExpandableListView> {
//	private MySalonMoreListExpandableAdapter adapter;
//	public String expand_group_text;// 该组的组名
//	private AppData mAppData;
//	private GroupInfoEntity cacheGroupInfoEntity;
//	private ExpandableListView group_expand;
//	private PullToRefreshExpandableListView refreshView;
//	private View headerView;
//	private ChatUserHelper chatUserHelper;
//	private int groupPosition;// 具体话题的分组position
//	private IntentFilter filter;
//	private CustomerInfoEntity mEntity;
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(MessagePushService.ACTION_MESSAGE)) {
//				if (intent.hasExtra("senderId")) {
//					String id = intent.getStringExtra("senderId");
//					if (mAppData.getInterestGroupIdList().contains(id)) {
//						setAdapter();
//					}
//				}
//			}else if (action.equals(MessagePushService.ACTION_COLLECT_GROUP_NOT)) {
//				WaitDialog.dismiss(getSupportFragmentManager());
//				String result = intent.getStringExtra("result");
//				if (result.equals("0")) {
//					ToastUtil.showShort(MySalonMoreActivity.this, R.string.groupNewFail);
//				} else {
//					SalonHttpUtil.requestUnfollowToSalonResult(MySalonMoreActivity.this,cacheGroupInfoEntity);
//					adapter.notifyDataSetChanged();
//				}
//			} else if (action.equals(MessagePushService.ACTION_COLLECT_GROUP)) {
//				WaitDialog.dismiss(getSupportFragmentManager());
//				String result = intent.getStringExtra("result");
//				if (result.equals("0")) {
//					ToastUtil.showShort(MySalonMoreActivity.this, R.string.groupNewFail);
//				} else {
//					SalonHttpUtil.requestAttentionToSalonResult(MySalonMoreActivity.this,cacheGroupInfoEntity);
//					adapter.notifyDataSetChanged();
//				}
//			}
//
//		}
//	};
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.myfriend_fragment_layout);
//		initialize();
//		initData();
//	}
//
//	private void initData() {
//		chatUserHelper = ChatUserHelper.getInstance();
//		mAppData = HTalkApplication.getAppData();
//		queryInfolay();
//
//		refreshView = (PullToRefreshExpandableListView)findViewById(R.id.hall);
//		refreshView.setOnRefreshListener(this);
//
//		group_expand = (ExpandableListView) refreshView.getRefreshableView();
//
//		headerView = LayoutInflater.from(this).inflate(R.layout.pull_to_refresh_header_edit, null);
//		group_expand.addHeaderView(headerView);
//		group_expand.setGroupIndicator(null);
//		group_expand.setOnChildClickListener(this);
//		group_expand.setOnGroupClickListener(this);
//
//	}
//
//	private void initialize() {
//		findViewById(R.id.title).setVisibility(View.VISIBLE);
//		initializeTitle();
//		titleLeftBtn.setOnClickListener(this);
//		titleTextV.setText("更多");
//	}
//	@Override
//	public void onStart() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(MessagePushService.ACTION_COLLECT_GROUP_NOT);
//		filter.addAction(MessagePushService.ACTION_MESSAGE);
//		filter.addAction(MessagePushService.ACTION_COLLECT_GROUP);
//		this.registerReceiver(receiver, filter);
//		if (refreshView != null) {
//			refreshView.setRefreshing();
//			getGroupData(refreshView);
//		}
//		super.onStart();
//	}
//
//	@Override
//	public void onStop() {
//		this.unregisterReceiver(receiver);
//		super.onStop();
//	}
//
//	private void setAdapter() {
//		if (adapter == null) {
//			adapter = new MySalonMoreListExpandableAdapter(MySalonMoreActivity.this, this);
//			adapter.setIntoInfoListener(this);
//			group_expand.setAdapter(adapter);
//			adapter.setonClickSalonAttentionListener(this);
//			adapter.setonClickGroupHeadListener(this);
//			for (int i = 0; i < adapter.getGroupCount(); i++) {
//				group_expand.expandGroup(i);
//			}
//		} else {
//			adapter.notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	public boolean onChildClick(ExpandableListView parent, View v,
//			int groupPosition, int childPosition, long viewId) {
//		AppData appData = HTalkApplication.getAppData();
//		 if (groupPosition == 0) {
//			cacheGroupInfoEntity =appData.getGroupInfoEntity(mAppData.getbuyFailureGroupIdList().get(childPosition));
//		} else if (groupPosition == 1) {
//			cacheGroupInfoEntity =appData.getGroupInfoEntity(mAppData.getMyBoughtHisGroupIdList().get(childPosition));
//		}
//		SalonHttpUtil.onItemClick(this, this,getSupportFragmentManager(), cacheGroupInfoEntity, true);
//		return true;
//	}
//
//	@Override
//	public boolean onGroupClick(ExpandableListView parent, View v,
//			int groupPosition, long id) {
//		return false;
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.refresh_header_btn:
//			break;
//		case R.id.title_back:
//			onBackPressed();
//			break;
//		}
//	}
//
//	private void queryInfolay() {
//		chatUserHelper.queryGroupInfo(this, mAppData, SmartFoxClient.getLoginUserId());
//	}
//
//	@Override
//	public void onClickFriendHead(GroupInfoEntity entity) {
//		TopicUtils.choiseActivty(entity, this);
//	}
//
//	@Override
//	public void onTicketHandler(String state, GroupInfoEntity entity) {
//		if ("0".equals(state)) {
//		} else if ("-1".equals(state)) {
//			ToastUtil.showShort(this, "服务器出错");
//		} else {
//			Intent intent1 = new Intent();
//			intent1.putExtra(ChatActivity.KEY_PARAME, entity);
//			intent1.setClass(this, ChatActivity.class);
//			this.startActivity(intent1);
////			if (mAppData.getLatelyGroupIdList().contains(entity.getId())) {
////				mAppData.getLatelyGroupIdList().remove(entity.getId());
////			}
////			mAppData.getLatelyGroupIdList().add(0, entity.getId());
////			mAppData.updateCacheInfomation(entity);
////			CollectUtils.updateGroupRecent(chatUserHelper, entity,SmartFoxClient.getLoginUserId());
//		}
//	}
//
//	@Override
//	public void OnPayClick(String payId) {
//		Intent intent = new Intent(this, SalonPayActivity.class);
//		intent.putExtra("payId", payId);
//		startActivity(intent);
//	}
//
//	@Override
//	public void OnClickDelete(final GroupInfoEntity entity, String orderId) {
//		// DeleteGroupOrder?ORDERID
//		ApiService.doHttpDeleteOrder(orderId,
//				new AsyncHttpResponseHandler(this) {
//					@Override
//					public void onSuccess(int statusCode, String content) {
//						if (content.equalsIgnoreCase("Y")) {
//							ToastUtil.showShort(MySalonMoreActivity.this, "删除话题订单成功");
//							if (refreshView != null) {
//								refreshView.setRefreshing();
//								getGroupData(refreshView);
//							}
//						} else {
//							ToastUtil.showShort(MySalonMoreActivity.this, "删除话题订单失败");
//						}
//						super.onSuccess(statusCode, content);
//					}
//				});
//	}
//
//	@Override
//	public void onSalonAttentionClick(View v, GroupInfoEntity mSalonName,
//			int positon) {
//		cacheGroupInfoEntity = SalonHttpUtil.requestAttOrUnfollowToSalon(this,mSalonName);
//	}
//
//	@Override
//	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
//		getGroupData(refreshView);
//	}
//
//	private void getGroupData(
//			final PullToRefreshBase<ExpandableListView> refreshView) {
//		ApiService.doHttpFindGroupsList(SmartFoxClient.getLoginUserId(), new ObjectHttpResponseHandler() {
//			@Override
//			public Object onParseResponse(String cotent) {
//				mAppData.getCreatedGroupIdList().clear();
//				mAppData.getbuyFailureGroupIdList().clear();
//				mAppData.getInterestGroupIdList().clear();
//				mAppData.getMyBoughtGroupIdList().clear();
//				mAppData.getMyBoughtHisGroupIdList().clear();
//				AppData appData = HTalkApplication.getAppData();
//				JsonParseUtils.LoginInitSalon(MySalonMoreActivity.this, cotent, appData,false);
//				return null;
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Object response) {
//				setAdapter();
//				super.onSuccess(statusCode, response);
//			}
//
//			@Override
//			public void onFinish() {
//				refreshView.onRefreshComplete();
//				super.onFinish();
//			}
//		});
//	}
//}
